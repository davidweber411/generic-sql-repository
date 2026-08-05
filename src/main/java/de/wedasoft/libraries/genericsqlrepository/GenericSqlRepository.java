package de.wedasoft.libraries.genericsqlrepository;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public abstract class GenericSqlRepository<DtoClass> {

    public abstract String getJdbcUrl();

    public abstract String getUsername();

    public abstract String getPassword();

    public abstract ZoneId getDefaultZoneId();

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
                Statement statement = connection.createStatement();
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

        DtoClass dto = getDtoClass().getConstructor().newInstance();
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

        String dbColumnValue = resultSet.getString(dbColumnName);
        if (dbColumnValue == null) {
            if (isPrimitiveDatatype(field)) {
                throw new IllegalStateException("Null values aren't allowed for data type '%s'. Affected attribute: %s"
                        .formatted(field.getType().getSimpleName(), field.getName()));
            } else {
                field.set(dto, null);
                return;
            }
        }

        Class<?> fieldType = field.getType();
        if (fieldType.equals(Byte.class) || fieldType.equals(byte.class)) {
            field.set(dto, Byte.valueOf(dbColumnValue));
        } else if (fieldType.equals(Short.class) || fieldType.equals(short.class)) {
            field.set(dto, Short.valueOf(dbColumnValue));
        } else if (fieldType.equals(Integer.class) || fieldType.equals(int.class)) {
            field.set(dto, Integer.valueOf(dbColumnValue));
        } else if (fieldType.equals(Long.class) || fieldType.equals(long.class)) {
            field.set(dto, Long.valueOf(dbColumnValue));
        } else if (fieldType.equals(Float.class) || fieldType.equals(float.class)) {
            field.set(dto, Float.valueOf(dbColumnValue));
        } else if (fieldType.equals(Double.class) || fieldType.equals(double.class)) {
            field.set(dto, Double.valueOf(dbColumnValue));
        } else if (fieldType.equals(BigDecimal.class)) {
            field.set(dto, resultSet.getBigDecimal(dbColumnName));
        } else if (fieldType.equals(Boolean.class) || fieldType.equals(boolean.class)) {
            field.set(dto, Boolean.valueOf(dbColumnValue));
        } else if (fieldType.equals(Character.class) || fieldType.equals(char.class)) {
            final char defaultChar = 0;
            field.set(dto, dbColumnValue.isEmpty() ? defaultChar : dbColumnValue.charAt(0));
        } else if (fieldType.equals(String.class)) {
            field.set(dto, dbColumnValue);
        } else if (fieldType.equals(LocalDate.class)) {
            Date dbColumnValueForLocalDate = resultSet.getDate(dbColumnName);
            field.set(dto, dbColumnValueForLocalDate == null ? null : dbColumnValueForLocalDate.toLocalDate());
        } else if (fieldType.equals(LocalDateTime.class)) {
            Timestamp timestamp = resultSet.getTimestamp(dbColumnName);
            field.set(dto, timestamp == null ? null : timestamp.toLocalDateTime());
        } else if (fieldType.equals(java.util.Date.class)) {
            field.set(dto, resultSet.getTimestamp(dbColumnName));
        } else if (fieldType.equals(Instant.class)) {
            Timestamp timestamp = resultSet.getTimestamp(dbColumnName);
            field.set(dto, timestamp == null ? null : timestamp.toInstant().atZone(getDefaultZoneId()).toInstant());
        } else {
            throw new IllegalStateException("Datatype isn't supported: " + field.getType().getSimpleName());
        }
    }

    private boolean isPrimitiveDatatype(Field field) {
        Class<?> fieldType = field.getType();
        return fieldType.equals(byte.class) || fieldType.equals(short.class)
               || fieldType.equals(int.class) || fieldType.equals(long.class)
               || fieldType.equals(float.class) || fieldType.equals(double.class)
               || fieldType.equals(boolean.class) || fieldType.equals(char.class);
    }

}
