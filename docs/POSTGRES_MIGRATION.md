# Postgres Migration — Implementation Notes

This document explains how the Cohort12 web application was moved off
session-scoped, in-memory storage onto a real PostgreSQL database, and how each
of the five stated requirements is satisfied by the code that was added or
changed.

---

## 1. What the app looked like before

The application is a generic Jakarta EE web app built around a reflection-based
framework (`BaseAction<T>`, `BaseListAction<T>`, and `@Cohort12Form` / `@Cohort12FormField`
annotations). Every entity (`Person`, `School`, `Trainer`) was persisted only
inside the user's `HttpSession` under keys like `Person_DB`, `School_DB`,
`Trainer_DB`.

Consequences of the old design:

- Data disappeared on session timeout, server restart, or log out.
- Each user saw their own "database".
- No concept of a real schema, so adding a new entity had no persistence cost.

The PostgreSQL JDBC driver (`org.postgresql:postgresql:42.7.10`) was already
declared in `pom.xml`, but was unused.

---

## 2. What was added

Five new files and two rewrites. Directory layout after the change:

```
src/main/
├── java/app/
│   ├── action/
│   │   ├── BaseAction.java         (rewritten — DAO instead of HttpSession)
│   │   └── BaseListAction.java     (rewritten — reads via DAO)
│   ├── dao/
│   │   └── BaseDAO.java            (new — generic CRUD via reflection)
│   ├── database/
│   │   ├── DatabaseConnection.java (new — JDBC singleton)
│   │   └── SchemaManager.java      (new — DB + table DDL from annotations)
│   └── listener/
│       └── DatabaseInitializer.java (new — @WebListener bootstrap)
└── resources/
    └── db.properties               (new — host/port/user/password/dbname)
```

All existing action classes (`PersonRegister`, `PersonList`, `SchoolRegister`,
`SchoolList`, `TrainerRegister`, `TrainerList`) and all model classes
(`Person`, `School`, `Trainer`) are **unchanged**. The refactor is contained in
the generic base classes and the new database layer.

---

## 3. Requirement-by-requirement walkthrough

### Requirement 1 — Convert DB session to RDBMS Postgres

**Before:** `BaseAction.doPost()` did:

```java
List<T> register = (List<T>) session.getAttribute(this.dbName());
register.add(serializeForm(req.getParameterMap()));
session.setAttribute(this.dbName(), register);
```

**After:** `BaseAction.doPost()` does:

```java
T entity = serializeForm(req.getParameterMap());
dao().insert(entity);
```

And `BaseListAction.doGet()` reads through `returnData()` which now calls
`dao().findAll()` — a real `SELECT` against PostgreSQL.

Every write and every read now goes through JDBC. The `HttpSession` is no
longer used for data storage (it still exists for login/session tracking —
that concern is left untouched in `LoginFilter`).

**Where to look:**
- `app/action/BaseAction.java:82-95` — DAO-backed `doPost`.
- `app/action/BaseAction.java:161-164` — DAO-backed `returnData()`.
- `app/action/BaseListAction.java:38` — list view reads from Postgres.

---

### Requirement 2 — Singleton JDBC connection via ServletContextListener

**`app/database/DatabaseConnection.java`** is a classic singleton:

- A single private static `Connection` field.
- `init()` opens the connection exactly once (guarded by `synchronized`).
- `get()` returns the same `Connection` to every caller, and throws if called
  before `init()` ran — so a misconfigured deploy fails fast with a clear error.
- `close()` is called on shutdown.

The instance is created on **application startup** by
`app/listener/DatabaseInitializer.java`, which is annotated `@WebListener` so
the servlet container auto-registers it without any `<listener>` entry in
`web.xml`. Its `contextInitialized()` hook runs once per WAR deployment, in
this order:

1. `SchemaManager.ensureDatabaseExists()` — creates the DB if missing.
2. `DatabaseConnection.init()` — opens the singleton connection.
3. For every registered entity, `SchemaManager.ensureTable(entity)`.

`contextDestroyed()` calls `DatabaseConnection.close()` so the pool is released
cleanly on undeploy.

**Why this satisfies "singleton on startup":** exactly one `Connection` object
exists for the lifetime of the deployed application, created in the listener,
shared by every servlet/DAO, closed on undeploy.

---

### Requirement 3 — Auto-create the database if it does not exist

You cannot run `CREATE DATABASE ... IF NOT EXISTS` from within the database
you are creating — PostgreSQL doesn't allow it, and you wouldn't have a
connection there yet anyway. The pattern used in
`SchemaManager.ensureDatabaseExists()` is the standard workaround:

1. Open a short-lived "admin" JDBC connection to the built-in `postgres`
   maintenance database.
2. Query `pg_database` with a parameterised statement to check whether
   `db.name` from `db.properties` already exists.
3. If it does not, run `CREATE DATABASE "<db.name>"`.
4. Close the admin connection.

Only after this does the singleton connect to the real application database.
This means the first time you deploy the WAR, the database is created for you;
every subsequent deploy is a no-op.

**Where to look:** `app/database/SchemaManager.java:21-50`.

---

### Requirement 4 — Auto-create tables from models/entities

Driven entirely by reflection on the model classes — no hard-coded DDL and no
JPA required. For every class registered in
`DatabaseInitializer.ENTITIES`, `SchemaManager.ensureTable(...)` does the
following:

1. Table name = `clazz.getSimpleName().toLowerCase()`, so `Person` →
   `person`, `School` → `school`, `Trainer` → `trainer`.
2. Iterate over every declared field that is **not** static, transient, or
   synthetic — these become columns. Column name is the field name lowercased.
3. Map each field's Java type to a Postgres type in `sqlType(...)`:
   - `String` → `VARCHAR(255)`
   - `int`/`Integer` → `INTEGER`, `long`/`Long` → `BIGINT`
   - `double`/`Double` → `DOUBLE PRECISION`, `float`/`Float` → `REAL`
   - `boolean`/`Boolean` → `BOOLEAN`
   - `BigDecimal` → `NUMERIC(19,4)`
   - `java.util.Date` → `TIMESTAMP`, `java.sql.Date` → `DATE`
   - enums → `VARCHAR(64)`
   - anything else falls back to `VARCHAR(255)`
4. Prepend an auto-incrementing surrogate key: `id SERIAL PRIMARY KEY`.
5. Wrap everything in `CREATE TABLE IF NOT EXISTS ...`, so it is safe to run
   on every startup.

Concretely, at startup the server logs:

```
[SchemaManager] Ensured table: person
[SchemaManager] Ensured table: school
[SchemaManager] Ensured table: trainer
```

And Postgres ends up with tables like (for `Person`):

```sql
CREATE TABLE IF NOT EXISTS person (
    id         SERIAL PRIMARY KEY,
    name       VARCHAR(255),
    nationalid VARCHAR(255),
    address    VARCHAR(255),
    age        INTEGER
)
```

**Adding a new entity later:** create the model class and add it to the
`ENTITIES` list in `DatabaseInitializer`. Nothing else is needed — no manual
migration, no DAO subclass, no SQL.

**Where to look:**
- `app/database/SchemaManager.java:52-101`
- `app/listener/DatabaseInitializer.java:25-30`

---

### Requirement 5 — Dynamically generated queries

`app/dao/BaseDAO.java` is a single generic class, `BaseDAO<T>`, that builds
every SQL statement at runtime from the entity's fields. There are **no
per-entity DAO subclasses** — `new BaseDAO<>(Person.class)`,
`new BaseDAO<>(School.class)`, and `new BaseDAO<>(Trainer.class)` all work out
of the box, and a new entity added tomorrow works without any new SQL.

How it generates the queries:

- **On construction**, it calls `SchemaManager.persistentFields(clazz)` — the
  same reflection pass used to build the `CREATE TABLE` DDL — and caches the
  list. DAO and schema stay in lockstep automatically.

- **`insert(T entity)`** builds:

  ```sql
  INSERT INTO <table> (<col1>, <col2>, ...) VALUES (?, ?, ...)
  ```

  Parameters are bound in the same order, pulled off the entity via
  `Field.get(entity)`. Enum values are serialised as their `name()`, and
  `java.util.Date` is coerced to `java.sql.Timestamp`.

- **`findAll()`** builds:

  ```sql
  SELECT <col1>, <col2>, ... FROM <table> ORDER BY id
  ```

  For each row it does `entityClass.newInstance()` and assigns each field back
  via `Field.set(instance, value)`, coercing enums and timestamps as needed.

Because the field list is derived by reflection, the SQL automatically picks
up any new field you add to a model — no DAO change required.

**Where to look:**
- `app/dao/BaseDAO.java:58-79` — `buildInsertSql` / `buildSelectSql`.
- `app/dao/BaseDAO.java:29-64` — `insert` / `findAll`.
- `BaseAction.dao()` lazily constructs a `BaseDAO<T>` for its generic type
  parameter, so every concrete action (`PersonRegister`, `SchoolList`, …) gets
  the right DAO for free.

---

## 4. How the pieces fit together at runtime

### Application startup

```
WildFly starts the WAR
   ↓
DatabaseInitializer.contextInitialized()   [@WebListener]
   ↓
SchemaManager.ensureDatabaseExists()       → connects to 'postgres', CREATE DATABASE if needed
   ↓
DatabaseConnection.init()                  → opens singleton Connection to cohort12db
   ↓
SchemaManager.ensureTable(Person.class)    → CREATE TABLE IF NOT EXISTS person (...)
SchemaManager.ensureTable(School.class)    → CREATE TABLE IF NOT EXISTS school (...)
SchemaManager.ensureTable(Trainer.class)   → CREATE TABLE IF NOT EXISTS trainer (...)
   ↓
Application is ready.
```

### User submits the "Register Person" form

```
POST /register_person
   ↓
PersonRegister (extends BaseAction<Person>)
   ↓
BaseAction.doPost()
   ↓
serializeForm(requestMap) → Person instance populated via BeanUtils
   ↓
dao().insert(person)
   ↓
BaseDAO builds:  INSERT INTO person (name, nationalid, address, age) VALUES (?, ?, ?, ?)
   ↓
Redirect to ./person_lists
```

### User opens the "Persons" list

```
GET /person_lists
   ↓
PersonList (extends BaseListAction<Person>)
   ↓
BaseListAction.doGet()
   ↓
returnData() → BaseDAO.findAll()
   ↓
SELECT name, nationalid, address, age FROM person ORDER BY id
   ↓
Cohort12Framework.htmlTable(writer, Person.class, rows)
```

### Application shutdown

```
WildFly undeploys the WAR
   ↓
DatabaseInitializer.contextDestroyed()
   ↓
DatabaseConnection.close()  → singleton Connection closed
```

---

## 5. Configuration

All database settings live in `src/main/resources/db.properties`:

```
db.host=localhost
db.port=5432
db.name=cohort12db
db.user=postgres
db.password=postgres
```

Change these to match your environment before deploying. The file is packaged
into the WAR (`WEB-INF/classes/db.properties`) and read via the classloader by
`DatabaseConnection.loadProperties()`.

**Prerequisite:** Postgres must be running and the `db.user` account must have
permission to `CREATE DATABASE` — on a default local install, the `postgres`
superuser already has it.

---

## 6. Design decisions and their trade-offs

- **Single `Connection`, not a pool.** The requirement asked for a singleton
  JDBC connection created on startup, so that's what was built. On a real
  production deployment you would replace this with a `DataSource` (HikariCP
  or the WildFly-managed one) without changing any other file — only the
  internals of `DatabaseConnection` need updating.

- **Explicit `ENTITIES` list in `DatabaseInitializer`.** Classpath scanning
  would remove this list, but pulls in a reflection library (Reflections,
  Spring, etc.). An explicit list is three lines of code and makes it obvious
  which classes are persisted. Adding an entity is a one-line change.

- **All declared fields are persisted.** Not just `@Cohort12FormField`-annotated
  ones. This matches the user's mental model — "the model class is the schema"
  — and keeps fields like `Person.address` and `Person.age` (which don't show
  up in the form) part of the row.

- **`CREATE TABLE IF NOT EXISTS` — no migrations.** On first deploy the table
  is created; on subsequent deploys, existing tables are left alone. Adding a
  **new field** to an existing model will not alter the live table. When that
  matters, a migration tool like Flyway should be introduced. For a cohort
  training project it's an acceptable simplification.

- **Field order drives column order.** Because reflection returns declared
  fields in source order, the DDL and the generated `INSERT` use the same
  order — and therefore `PreparedStatement` bindings are always consistent.

---

## 7. Verifying the change

```bash
mvn clean compile       # compiles cleanly (27 source files)
mvn clean package       # produces target/cohort12.war
```

Deploy the WAR to WildFly (`mvn wildfly:deploy`) with Postgres running.
Expected startup log lines:

```
[DatabaseInitializer] Bootstrapping database layer...
[SchemaManager] Database 'cohort12db' created.              (first run only)
[DatabaseConnection] Singleton connection opened for database: cohort12db
[SchemaManager] Ensured table: person
[SchemaManager] Ensured table: school
[SchemaManager] Ensured table: trainer
[DatabaseInitializer] Database layer ready.
```

From `psql`:

```sql
\c cohort12db
\dt                     -- person, school, trainer should be listed
\d person               -- columns: id, name, nationalid, address, age
SELECT * FROM person;   -- rows populated via /register_person
```

---

## 8. Requirement checklist

| # | Requirement | Satisfied by |
|---|-------------|--------------|
| 1 | Convert DB session → RDBMS Postgres | `BaseAction` / `BaseListAction` now call `BaseDAO`; `HttpSession` no longer stores data |
| 2 | Singleton JDBC via `ServletContextListener` on startup | `DatabaseConnection` singleton + `DatabaseInitializer` `@WebListener` |
| 3 | Auto-create database if missing | `SchemaManager.ensureDatabaseExists()` (connects to `postgres`, `CREATE DATABASE` if absent) |
| 4 | Auto-create tables for models/entities | `SchemaManager.ensureTable(...)` reflects fields, maps Java → SQL types, `CREATE TABLE IF NOT EXISTS` |
| 5 | Dynamically generated queries | `BaseDAO<T>` builds `INSERT` / `SELECT` from the reflected field list — no hand-written SQL anywhere |
