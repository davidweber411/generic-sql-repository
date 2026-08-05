package de.wedasoft.libraries.genericsqlrepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.ResultSet;
import java.time.ZoneId;
import java.util.Map;
import org.junit.jupiter.api.Test;

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
                    if ("getDate".equals(method.getName())) {
                        return null;
                    }
                    if ("getTimestamp".equals(method.getName())) {
                        return null;
                    }
                    if ("getBigDecimal".equals(method.getName())) {
                        return null;
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

        @Override
        public ZoneId getDefaultZoneId() {
            return ZoneId.of("UTC");
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

        @Override
        public ZoneId getDefaultZoneId() {
            return ZoneId.of("UTC");
        }
    }

    private static class ExampleWithoutTableAnnotation {
    }
}
