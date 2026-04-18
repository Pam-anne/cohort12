package app.dao;

import app.database.DatabaseConnection;
import app.database.SchemaManager;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Generic data access object. All SQL is generated at runtime from the
 * entity class definition via reflection — no per-entity subclass is needed.
 */
public class BaseDAO<T> {

    private final Class<T> entityClass;
    private final String tableName;
    private final List<Field> fields;

    public BaseDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
        this.tableName = SchemaManager.tableName(entityClass);
        this.fields = SchemaManager.persistentFields(entityClass);
    }

    public void insert(T entity) {
        String sql = buildInsertSql();
        try (PreparedStatement ps = DatabaseConnection.get()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            for (int i = 0; i < fields.size(); i++) {
                Object value = fields.get(i).get(entity);
                ps.setObject(i + 1, toSqlValue(value));
            }

            ps.executeUpdate();
        } catch (SQLException | IllegalAccessException e) {
            throw new RuntimeException("Insert failed for " + tableName + ": " + e.getMessage(), e);
        }
    }

    public List<T> findAll() {
        List<T> result = new ArrayList<>();
        String sql = buildSelectSql();

        try (PreparedStatement ps = DatabaseConnection.get().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(mapRow(rs));
            }
        } catch (SQLException | IllegalAccessException | InstantiationException e) {
            throw new RuntimeException("Select failed for " + tableName + ": " + e.getMessage(), e);
        }

        return result;
    }

    public T findBy(String columnName, Object value) {
        String sql = selectColumns() + " FROM \"" + tableName + "\""
                + " WHERE \"" + columnName + "\" = ? LIMIT 1";

        try (PreparedStatement ps = DatabaseConnection.get().prepareStatement(sql)) {
            ps.setObject(1, value);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
                return null;
            }
        } catch (SQLException | IllegalAccessException | InstantiationException e) {
            throw new RuntimeException("findBy " + columnName + " failed for "
                    + tableName + ": " + e.getMessage(), e);
        }
    }

    private T mapRow(ResultSet rs) throws SQLException, IllegalAccessException, InstantiationException {
        T instance = entityClass.newInstance();
        for (Field f : fields) {
            Object value = rs.getObject(SchemaManager.columnName(f));
            if (value != null) {
                f.set(instance, coerce(value, f.getType()));
            }
        }
        return instance;
    }

    private String buildInsertSql() {
        StringBuilder cols = new StringBuilder();
        StringBuilder vals = new StringBuilder();
        for (int i = 0; i < fields.size(); i++) {
            if (i > 0) {
                cols.append(", ");
                vals.append(", ");
            }
            cols.append('"').append(SchemaManager.columnName(fields.get(i))).append('"');
            vals.append('?');
        }
        return "INSERT INTO \"" + tableName + "\" (" + cols + ") VALUES (" + vals + ")";
    }

    private String buildSelectSql() {
        return selectColumns() + " FROM \"" + tableName + "\" ORDER BY id";
    }

    private String selectColumns() {
        StringBuilder cols = new StringBuilder();
        for (int i = 0; i < fields.size(); i++) {
            if (i > 0) cols.append(", ");
            cols.append('"').append(SchemaManager.columnName(fields.get(i))).append('"');
        }
        return "SELECT " + cols;
    }

    private Object toSqlValue(Object value) {
        if (value == null) return null;
        if (value instanceof Enum) return ((Enum<?>) value).name();
        if (value instanceof java.util.Date && !(value instanceof java.sql.Date)
                && !(value instanceof java.sql.Timestamp)) {
            return new java.sql.Timestamp(((java.util.Date) value).getTime());
        }
        return value;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Object coerce(Object dbValue, Class<?> targetType) {
        if (targetType.isInstance(dbValue)) return dbValue;
        if (targetType.isEnum() && dbValue instanceof String) {
            return Enum.valueOf((Class<Enum>) targetType, (String) dbValue);
        }
        if (targetType == java.util.Date.class && dbValue instanceof java.sql.Timestamp) {
            return new java.util.Date(((java.sql.Timestamp) dbValue).getTime());
        }
        return dbValue;
    }
}
