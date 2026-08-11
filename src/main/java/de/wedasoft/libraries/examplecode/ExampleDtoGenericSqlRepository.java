package de.wedasoft.libraries.examplecode;

import de.wedasoft.libraries.genericsqlrepository.GenericSqlRepository;

import java.sql.SQLException;
import java.util.List;

public class ExampleDtoGenericSqlRepository extends GenericSqlRepository<ExampleDto> {

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

    public List<ExampleDto> findAll() throws SQLException {
        return super.findAll();
    }

    public ExampleDto findById(Long id) throws SQLException {
        return executeSelect(String.format("SELECT * FROM %s WHERE id = %s;", getTableName(), id))
                .stream()
                .findFirst()
                .orElse(null);
    }

    public List<ExampleDto> findByName(String name) throws SQLException {
        return executeSelect(String.format("SELECT * FROM %s WHERE name = %s;", getTableName(), asSqlStringLiteral(name)));
    }

}
