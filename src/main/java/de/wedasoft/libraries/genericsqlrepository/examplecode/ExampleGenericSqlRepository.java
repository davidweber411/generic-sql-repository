package de.wedasoft.libraries.genericsqlrepository.examplecode;

import de.wedasoft.libraries.genericsqlrepository.GenericSqlRepository;

import java.sql.SQLException;
import java.util.List;

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
    public int getTimeoutInSeconds() {
        return 30;
    }

    public List<Example> findAll() throws SQLException {
        return super.findAll();
    }

    public Example findById(Long id) throws SQLException {
        return executeSelect(String.format("SELECT * FROM %s WHERE id = %s;", getTableName(), id))
                .stream()
                .findFirst()
                .orElse(null);
    }

    public List<Example> findByName(String name) throws SQLException {
        return executeSelect(String.format("SELECT * FROM %s WHERE name = %s;", getTableName(), asSqlStringLiteral(name)));
    }

}
