package de.wedasoft.libraries.genericsqlrepository;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GenericSqlRepositoryTest {

    @Nested
    class createAndInitializeNewDto {

        private static class TestRepository extends GenericSqlRepository<Example> {

            @Override
            public String getJdbcUrl() {
                return "jdbc:test";
            }

            @Override
            public String getUsername() {
                return "user";
            }

            @Override
            public String getPassword() {
                return "pass";
            }

            @Override
            public int getTimeoutInSeconds() {
                return 30;
            }

        }

        @Test
        void mapsAllSupportedTypes() throws Exception {
            TestRepository repository = new TestRepository();
            Map<String, Object> values = new HashMap<>();
            values.put("id_column_name", "7");
            values.put("name_column_name", "Mammoth");
            values.put("byte_value_column_name", (byte) 11);
            values.put("short_value_column_name", (short) 22);
            values.put("int_value_column_name", 33);
            values.put("long_value_column_name", 44L);
            values.put("float_value_column_name", 5.5f);
            values.put("double_value_column_name", 6.25d);
            values.put("big_decimal_value_column_name", new BigDecimal("7.75"));
            values.put("boolean_value_column_name", true);
            values.put("character_value_column_name", 'Z');
            values.put("string_value_column_name", "Another value");
            values.put("local_date_value_column_name", LocalDate.of(2024, 2, 29));
            values.put("local_date_time_value_column_name", LocalDateTime.of(2024, 3, 1, 12, 34, 56));
            values.put("date_value_column_name", Date.valueOf("2024-03-02"));
            values.put("instant_value_column_name", Instant.parse("2024-03-01T12:34:56Z"));
            ResultSet resultSet = createResultSet(values);

            Method method = GenericSqlRepository.class.getDeclaredMethod("createAndInitializeNewDto", ResultSet.class);
            method.setAccessible(true);

            Example dto = (Example) method.invoke(repository, resultSet);

            assertNotNull(dto);
            assertEquals(7L, dto.getId());
            assertEquals("Mammoth", dto.getName());
            assertEquals(Byte.valueOf((byte) 11), dto.getByteValue());
            assertEquals(Short.valueOf((short) 22), dto.getShortValue());
            assertEquals(Integer.valueOf(33), dto.getIntegerValue());
            assertEquals(Long.valueOf(44L), dto.getLongValue());
            assertEquals(Float.valueOf(5.5f), dto.getFloatValue());
            assertEquals(Double.valueOf(6.25d), dto.getDoubleValue());
            assertEquals(new BigDecimal("7.75"), dto.getBigDecimalValue());
            assertEquals(Boolean.TRUE, dto.getBooleanValue());
            assertEquals('Z', dto.getCharacterValue());
            assertEquals("Another value", dto.getStringValue());
            assertEquals(LocalDate.of(2024, 2, 29), dto.getLocalDateValue());
            assertEquals(LocalDateTime.of(2024, 3, 1, 12, 34, 56), dto.getLocalDateTimeValue());
            assertEquals(Date.valueOf("2024-03-02"), dto.getUtilDateValue());
            assertEquals(Instant.parse("2024-03-01T12:34:56Z"), dto.getInstantValue());
        }

        private ResultSet createResultSet(Map<String, Object> values) {
            return (ResultSet) Proxy.newProxyInstance(
                    ResultSet.class.getClassLoader(),
                    new Class<?>[]{ResultSet.class},
                    (_, method, args) -> {
                        String columnName = args != null && args.length > 0 ? (String) args[0] : null;
                        Object value = columnName != null ? values.get(columnName) : null;
                        switch (method.getName()) {
                            case "getString" -> {
                                if (value == null) {
                                    return null;
                                }
                                if (value instanceof Character) {
                                    return String.valueOf(value);
                                }
                                return value.toString();
                            }
                            case "getByte" -> {
                                if (value == null) {
                                    return (byte) 0;
                                }
                                if (value instanceof Number) {
                                    return ((Number) value).byteValue();
                                }
                                return Byte.valueOf(value.toString());
                            }
                            case "getShort" -> {
                                if (value == null) {
                                    return (short) 0;
                                }
                                if (value instanceof Number) {
                                    return ((Number) value).shortValue();
                                }
                                return Short.valueOf(value.toString());
                            }
                            case "getInt" -> {
                                if (value == null) {
                                    return 0;
                                }
                                if (value instanceof Number) {
                                    return ((Number) value).intValue();
                                }
                                return Integer.valueOf(value.toString());
                            }
                            case "getLong" -> {
                                if (value == null) {
                                    return 0L;
                                }
                                if (value instanceof Number) {
                                    return ((Number) value).longValue();
                                }
                                return Long.valueOf(value.toString());
                            }
                            case "getFloat" -> {
                                if (value == null) {
                                    return 0f;
                                }
                                if (value instanceof Number) {
                                    return ((Number) value).floatValue();
                                }
                                return Float.valueOf(value.toString());
                            }
                            case "getDouble" -> {
                                if (value == null) {
                                    return 0d;
                                }
                                if (value instanceof Number) {
                                    return ((Number) value).doubleValue();
                                }
                                return Double.valueOf(value.toString());
                            }
                            case "getBoolean" -> {
                                if (value == null) {
                                    return false;
                                }
                                if (value instanceof Boolean) {
                                    return value;
                                }
                                return Boolean.valueOf(value.toString());
                            }
                            case "getObject", "getBigDecimal" -> {
                                return value;
                            }
                            case "getDate" -> {
                                return switch (value) {
                                    case null -> null;
                                    case LocalDate localDate -> Date.valueOf(localDate);
                                    case java.util.Date date -> new Date(date.getTime());
                                    default -> Date.valueOf(value.toString());
                                };
                            }
                            case "getTimestamp" -> {
                                return switch (value) {
                                    case null -> null;
                                    case Timestamp _ -> value;
                                    case LocalDateTime localDateTime -> Timestamp.valueOf(localDateTime);
                                    case Instant instant -> Timestamp.from(instant);
                                    case java.util.Date date -> new Timestamp(date.getTime());
                                    default -> Timestamp.valueOf(value.toString());
                                };
                            }
                            case "wasNull" -> {
                                return false;
                            }
                        }
                        return null;
                    });
        }
    }

}
