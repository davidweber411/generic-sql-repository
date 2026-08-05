package de.wedasoft.libraries.genericsqlrepository;

@GenericSqlRepositoryTable(name = "example_table_name_in_db")
public class Example {

    @GenericSqlRepositoryColumn(name = "id_column_name")
    private Long id;

    @GenericSqlRepositoryColumn(name = "text_column_name")
    private String text;

    public Example(Long id, String text) {
        this.id = id;
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
