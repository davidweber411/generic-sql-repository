package de.wedasoft.libraries.genericsqlrepository;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.ResultSet;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GenericSqlRepositoryTest {

    @Test
    void createAndInitializeNewDtoUsesColumnAnnotationsAndDefaultConstructor() throws Exception {
        TestRepository repository = new TestRepository();
        ResultSet resultSet = createResultSet(Map.of(
                "id_column_name", "7",
                "name_column_name", "Mammoth"));

        Method method = GenericSqlRepository.class.getDeclaredMethod("createAndInitializeNewDto", ResultSet.class);
        method.setAccessible(true);

        Example dto = (Example) method.invoke(repository, resultSet);

        assertNotNull(dto);
        assertEquals(7L, dto.getId());
        assertEquals("Mammoth", dto.getName());
    }

    @Test
    void createAndInitializeNewDtoUsesJdbcByteGetter() throws Exception {
        ByteTestRepository repository = new ByteTestRepository();
        ResultSet resultSet = createResultSet(Map.of(
                "byte_value_column_name", (byte) 7));

        Method method = GenericSqlRepository.class.getDeclaredMethod("createAndInitializeNewDto", ResultSet.class);
        method.setAccessible(true);

        ExampleWithByte dto = (ExampleWithByte) method.invoke(repository, resultSet);

        assertNotNull(dto);
        assertEquals(7, dto.getByteValue());
    }

    @Test
    void getTableNameRequiresTableAnnotation() {
        MissingTableAnnotationRepository repository = new MissingTableAnnotationRepository();

        IllegalStateException exception = assertThrows(IllegalStateException.class, repository::getTableName);

        assertTrue(exception.getMessage().contains("@GenericSqlRepositoryTable"));
    }

    private ResultSet createResultSet(Map<String, Object> values) {
        return (ResultSet) Proxy.newProxyInstance(
                ResultSet.class.getClassLoader(),
                new Class<?>[]{ResultSet.class},
                (proxy, method, args) -> {
                    if ("getString".equals(method.getName())) {
                        return values.get(args[0]);
                    }
                    if ("getByte".equals(method.getName())) {
                        Object value = values.get(args[0]);
                        if (value instanceof Number) {
                            return ((Number) value).byteValue();
                        }
                        return Byte.valueOf(value.toString());
                    }
                    if ("getShort".equals(method.getName())) {
                        Object value = values.get(args[0]);
                        if (value instanceof Number) {
                            return ((Number) value).shortValue();
                        }
                        return Short.valueOf(value.toString());
                    }
                    if ("getInt".equals(method.getName())) {
                        Object value = values.get(args[0]);
                        if (value instanceof Number) {
                            return ((Number) value).intValue();
                        }
                        return Integer.valueOf(value.toString());
                    }
                    if ("getLong".equals(method.getName())) {
                        Object value = values.get(args[0]);
                        if (value instanceof Number) {
                            return ((Number) value).longValue();
                        }
                        return Long.valueOf(value.toString());
                    }
                    if ("getFloat".equals(method.getName())) {
                        Object value = values.get(args[0]);
                        if (value instanceof Number) {
                            return ((Number) value).floatValue();
                        }
                        return Float.valueOf(value.toString());
                    }
                    if ("getDouble".equals(method.getName())) {
                        Object value = values.get(args[0]);
                        if (value instanceof Number) {
                            return ((Number) value).doubleValue();
                        }
                        return Double.valueOf(value.toString());
                    }
                    if ("getBoolean".equals(method.getName())) {
                        Object value = values.get(args[0]);
                        if (value instanceof Boolean) {
                            return value;
                        }
                        return Boolean.valueOf(value.toString());
                    }
                    if ("getDate".equals(method.getName())) {
                        return null;
                    }
                    if ("getTimestamp".equals(method.getName())) {
                        return null;
                    }
                    if ("getBigDecimal".equals(method.getName())) {
                        return null;
                    }
                    if ("wasNull".equals(method.getName())) {
                        return false;
                    }
                    return null;
                });
    }

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
    }

    private static class MissingTableAnnotationRepository extends GenericSqlRepository<ExampleWithoutTableAnnotation> {

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

    }

    private static class ByteTestRepository extends GenericSqlRepository<ExampleWithByte> {

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

    }

    private static class ExampleWithoutTableAnnotation {
    }

    @GenericSqlRepositoryTable(name = "example_with_byte")
    private static class ExampleWithByte {

        @GenericSqlRepositoryColumn(name = "byte_value_column_name")
        private byte byteValue;

        public byte getByteValue() {
            return byteValue;
        }

        public void setByteValue(byte byteValue) {
            this.byteValue = byteValue;
        }
    }
}
