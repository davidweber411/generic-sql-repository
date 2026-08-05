package de.wedasoft.libraries.genericsqlrepository;

@GenericSqlRepositoryTable(name = "example_table_name_in_db")
public class Example {

    @GenericSqlRepositoryColumn(name = "id_column_name")
    private Long id;

    @GenericSqlRepositoryColumn(name = "name_column_name")
    private String name;

    public Example(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
