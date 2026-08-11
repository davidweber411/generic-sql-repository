package de.wedasoft.libraries.genericsqlrepository;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GenericSqlRepositoryTableAnnotationTest {

    @Test
    void tableRequiresAnnotation() {
        MissingTableAnnotationRepository repository = new MissingTableAnnotationRepository();

        IllegalStateException exception = assertThrows(IllegalStateException.class, repository::getTableName);

        assertTrue(exception.getMessage().contains("@GenericSqlRepositoryTable"));
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
        public int getTimeoutInSeconds() {
            return 30;
        }
    }

    private static class ExampleWithoutTableAnnotation {
    }

}
