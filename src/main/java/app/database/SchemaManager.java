package app.database;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public final class SchemaManager {

    private SchemaManager() {
    }

    public static void ensureDatabaseExists() throws SQLException {
        Properties props = DatabaseConnection.getProperties();
        String dbName = props.getProperty("db.name");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");

        String adminUrl = DatabaseConnection.jdbcUrl("postgres");

        try (Connection adminConn = DriverManager.getConnection(adminUrl, user, password)) {
            if (databaseExists(adminConn, dbName)) {
                System.out.println("[SchemaManager] Database '" + dbName + "' already exists.");
                return;
            }

            try (Statement stmt = adminConn.createStatement()) {
                stmt.executeUpdate("CREATE DATABASE \"" + dbName + "\"");
                System.out.println("[SchemaManager] Database '" + dbName + "' created.");
            }
        }
    }

    private static boolean databaseExists(Connection conn, String dbName) throws SQLException {
        String sql = "SELECT 1 FROM pg_database WHERE datname = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dbName);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public static void ensureTable(Class<?> entityClass) throws SQLException {
        String tableName = tableName(entityClass);
        List<Field> fields = persistentFields(entityClass);

        if (fields.isEmpty()) {
            System.out.println("[SchemaManager] No persistent fields on "
                    + entityClass.getSimpleName() + ", skipping table creation.");
            return;
        }

        StringBuilder ddl = new StringBuilder();
        ddl.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (");
        ddl.append("id SERIAL PRIMARY KEY");

        for (Field f : fields) {
            ddl.append(", ").append(columnName(f))
                    .append(' ').append(sqlType(f.getType()));
        }

        ddl.append(")");

        try (Statement stmt = DatabaseConnection.get().createStatement()) {
            stmt.executeUpdate(ddl.toString());
            System.out.println("[SchemaManager] Ensured table: " + tableName);
        }
    }

    public static String tableName(Class<?> entityClass) {
        return entityClass.getSimpleName().toLowerCase();
    }

    public static String columnName(Field field) {
        return field.getName().toLowerCase();
    }

    public static List<Field> persistentFields(Class<?> entityClass) {
        List<Field> result = new ArrayList<>();
        for (Field f : entityClass.getDeclaredFields()) {
            int mods = f.getModifiers();
            if (Modifier.isStatic(mods) || Modifier.isTransient(mods) || f.isSynthetic()) {
                continue;
            }
            f.setAccessible(true);
            result.add(f);
        }
        return result;
    }

    private static String sqlType(Class<?> javaType) {
        if (javaType == String.class) return "VARCHAR(255)";
        if (javaType == int.class || javaType == Integer.class) return "INTEGER";
        if (javaType == long.class || javaType == Long.class) return "BIGINT";
        if (javaType == short.class || javaType == Short.class) return "SMALLINT";
        if (javaType == double.class || javaType == Double.class) return "DOUBLE PRECISION";
        if (javaType == float.class || javaType == Float.class) return "REAL";
        if (javaType == boolean.class || javaType == Boolean.class) return "BOOLEAN";
        if (javaType == BigDecimal.class) return "NUMERIC(19,4)";
        if (javaType == java.sql.Date.class) return "DATE";
        if (javaType == Date.class || javaType == java.sql.Timestamp.class) return "TIMESTAMP";
        if (javaType.isEnum()) return "VARCHAR(64)";
        return "VARCHAR(255)";
    }
}
