### Description

This class can be used to create a loosely coupled connection to a database table.

### Features

- Data can be read from a third party database without batting an eyelid.
- Your application can be started, even if the remote database is not available.

### Usage

##### Step 1: Import classes

Copy the classes `GenericSqlRepository`, `GenericSqlRepositoryTable` and `GenericSqlRepositoryColumn` into your
project.

##### Step 2: Create DTO class for the row

Create the DTO class which represents the row in the third party database:

```
@GenericSqlRepositoryTable(name = "example_table_name_in_db")
public class ExampleDto {
    
    @GenericSqlRepositoryColumn(name = "id_column_name")
    private Long id;
    
    ...

}
```

##### Step 3: Create the repository class

Example below:

```
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

```

##### Step 4: Profit!

Profit!