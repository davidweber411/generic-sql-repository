package de.wedasoft.libraries.genericsqlrepository;

import java.time.ZoneId;
import java.time.ZoneOffset;

public class ExampleGenericSqlRepository extends GenericSqlRepository<Example> {

    @Override
    public String getJdbcUrl() {
        return "jdbc-url";
    }

    @Override
    public String getUsername() {
        return "database-user-name";
    }

    @Override
    public String getPassword() {
        return "database-user-password";
    }

    @Override
    public ZoneId getDefaultZoneId() {
        return ZoneOffset.UTC; // default zone id in database here
    }

}
