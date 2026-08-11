package de.wedasoft.libraries.genericsqlrepository;

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class GenericSqlRepository<DtoClass> {

    public abstract String getJdbcUrl();

    public abstract String getUsername();

    public abstract String getPassword();

    public abstract int getTimeoutInSeconds();

    public String getTableName() {
        GenericSqlRepositoryTable tableAnnotation = getDtoClass().getAnnotation(GenericSqlRepositoryTable.class);
        if (tableAnnotation == null) {
            throw new IllegalStateException("DTO class '%s' must be annotated with @GenericSqlRepositoryTable"
                    .formatted(getDtoClass().getName()));
        }
        return tableAnnotation.name();
    }

    public List<DtoClass> executeSelect(String sqlSelect) throws SQLException {
        //noinspection SqlSourceToSinkFlow
        try (
                Connection connection = getConnection();
                Statement statement = createStatement(connection);
                ResultSet resultSet = statement.executeQuery(sqlSelect)) {
            List<DtoClass> dtos = new ArrayList<>();
            while (resultSet.next()) {
                dtos.add(createAndInitializeNewDto(resultSet));
            }
            return dtos;
        } catch (SQLException exception) {
            throw new SQLException("Error executing SELECT '" + sqlSelect + "' on JDBC-URL: " + getJdbcUrl(), exception);
        } catch (InvocationTargetException | NoSuchMethodException |
                 InstantiationException | IllegalAccessException exception) {
            throw new IllegalStateException("Unable to create DTO instance for '%s'"
                    .formatted(getDtoClass().getName()), exception);
        }
    }

    private Statement createStatement(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        statement.setQueryTimeout(getTimeoutInSeconds());
        return statement;
    }

    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(getJdbcUrl(), getUsername(), getPassword());
    }

    protected String asSqlStringLiteral(final String value) {
        return "'" + value.replace("'", "''") + "'";
    }

    protected String asSqlDate(final LocalDate localDate) {
        return "DATE(" + asSqlStringLiteral(localDate.toString()) + ")";
    }

    public List<DtoClass> findAll() throws SQLException {
        return executeSelect("SELECT * FROM " + getTableName());
    }

    private Class<DtoClass> getDtoClass() {
        Type superClass = getClass().getGenericSuperclass();
        @SuppressWarnings("unchecked")
        Class<DtoClass> clazz = (Class<DtoClass>) ((ParameterizedType) superClass).getActualTypeArguments()[0];
        return clazz;
    }

    private DtoClass createAndInitializeNewDto(final ResultSet resultSet)
            throws InvocationTargetException, InstantiationException,
            IllegalAccessException, NoSuchMethodException, SQLException {

        Constructor<DtoClass> constructor = getDtoClass().getDeclaredConstructor();
        constructor.setAccessible(true);
        DtoClass dto = constructor.newInstance();
        for (Field field : dto.getClass().getDeclaredFields()) {
            final String dbColumnName = field.getAnnotation(GenericSqlRepositoryColumn.class) == null
                    ? field.getName()
                    : field.getAnnotation(GenericSqlRepositoryColumn.class).name();
            field.setAccessible(true);
            setFieldValue(dto, field, resultSet, dbColumnName);
            field.setAccessible(false);
        }
        return dto;
    }

    private void setFieldValue(
            final DtoClass dto,
            final Field field,
            final ResultSet resultSet,
            final String dbColumnName)
            throws IllegalAccessException, SQLException {

        Class<?> fieldType = field.getType();
        Object dbColumnValue = readColumnValue(resultSet, dbColumnName, fieldType);

        if (dbColumnValue == null) {
            if (isPrimitiveDatatype(field)) {
                throw new IllegalStateException("Null values aren't allowed for data type '%s'. Affected attribute: %s"
                        .formatted(field.getType().getSimpleName(), field.getName()));
            } else {
                field.set(dto, null);
                return;
            }
        }

        field.set(dto, dbColumnValue);
    }

    private Object readColumnValue(
            final ResultSet resultSet,
            final String dbColumnName,
            final Class<?> fieldType)
            throws SQLException {

        /* basic types */
        if (fieldType.equals(Byte.class) || fieldType.equals(byte.class)) {
            return resultSet.wasNull() ? null : resultSet.getByte(dbColumnName);
        }
        if (fieldType.equals(Short.class) || fieldType.equals(short.class)) {
            return resultSet.wasNull() ? null : resultSet.getShort(dbColumnName);
        }
        if (fieldType.equals(Integer.class) || fieldType.equals(int.class)) {
            return resultSet.wasNull() ? null : resultSet.getInt(dbColumnName);
        }
        if (fieldType.equals(Long.class) || fieldType.equals(long.class)) {
            return resultSet.wasNull() ? null : resultSet.getLong(dbColumnName);
        }
        if (fieldType.equals(Float.class) || fieldType.equals(float.class)) {
            return resultSet.wasNull() ? null : resultSet.getFloat(dbColumnName);
        }
        if (fieldType.equals(Double.class) || fieldType.equals(double.class)) {
            return resultSet.wasNull() ? null : resultSet.getDouble(dbColumnName);
        }
        if (fieldType.equals(Boolean.class) || fieldType.equals(boolean.class)) {
            return resultSet.wasNull() ? null : resultSet.getBoolean(dbColumnName);
        }
        if (fieldType.equals(Character.class) || fieldType.equals(char.class)) {
            String value = resultSet.getString(dbColumnName);
            final char defaultChar = 0;
            return value == null ? null : (value.isEmpty() ? defaultChar : value.charAt(0));
        }

        /* advanced types */
        if (fieldType.equals(String.class)) {
            return resultSet.getString(dbColumnName);
        }
        if (fieldType.equals(BigDecimal.class)) {
            return resultSet.getBigDecimal(dbColumnName);
        }

        /* date types */
        if (fieldType.equals(LocalDate.class)) {
            return resultSet.getObject(dbColumnName, LocalDate.class);
        }
        if (fieldType.equals(LocalDateTime.class)) {
            return resultSet.getObject(dbColumnName, LocalDateTime.class);
        }
        if (fieldType.equals(java.util.Date.class)) {
            return resultSet.getObject(dbColumnName, java.sql.Date.class);
        }
        if (fieldType.equals(Instant.class)) {
            return resultSet.getObject(dbColumnName, Instant.class);
        }

        /* end */
        throw new IllegalStateException("Datatype isn't supported: " + fieldType.getSimpleName());
    }

    private boolean isPrimitiveDatatype(Field field) {
        Class<?> fieldType = field.getType();
        return fieldType.equals(byte.class) || fieldType.equals(short.class)
               || fieldType.equals(int.class) || fieldType.equals(long.class)
               || fieldType.equals(float.class) || fieldType.equals(double.class)
               || fieldType.equals(boolean.class) || fieldType.equals(char.class);
    }

}
