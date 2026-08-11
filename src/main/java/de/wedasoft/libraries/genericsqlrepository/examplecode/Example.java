package de.wedasoft.libraries.genericsqlrepository.examplecode;

import de.wedasoft.libraries.genericsqlrepository.GenericSqlRepositoryColumn;
import de.wedasoft.libraries.genericsqlrepository.GenericSqlRepositoryTable;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@GenericSqlRepositoryTable(name = "example_table_name_in_db")
public class Example {

    @GenericSqlRepositoryColumn(name = "id_column_name")
    private Long id;

    @GenericSqlRepositoryColumn(name = "name_column_name")
    private String name;

    @GenericSqlRepositoryColumn(name = "byte_value_column_name")
    private Byte byteValue;

    @GenericSqlRepositoryColumn(name = "short_value_column_name")
    private Short shortValue;

    @GenericSqlRepositoryColumn(name = "int_value_column_name")
    private Integer integerValue;

    @GenericSqlRepositoryColumn(name = "long_value_column_name")
    private Long longValue;

    @GenericSqlRepositoryColumn(name = "float_value_column_name")
    private Float floatValue;

    @GenericSqlRepositoryColumn(name = "double_value_column_name")
    private Double doubleValue;

    @GenericSqlRepositoryColumn(name = "big_decimal_value_column_name")
    private BigDecimal bigDecimalValue;

    @GenericSqlRepositoryColumn(name = "boolean_value_column_name")
    private Boolean booleanValue;

    @GenericSqlRepositoryColumn(name = "character_value_column_name")
    private Character characterValue;

    @GenericSqlRepositoryColumn(name = "string_value_column_name")
    private String stringValue;

    @GenericSqlRepositoryColumn(name = "local_date_value_column_name")
    private LocalDate localDateValue;

    @GenericSqlRepositoryColumn(name = "local_date_time_value_column_name")
    private LocalDateTime localDateTimeValue;

    @GenericSqlRepositoryColumn(name = "date_value_column_name")
    private java.util.Date utilDateValue;

    @GenericSqlRepositoryColumn(name = "instant_value_column_name")
    private Instant instantValue;

    public Example() {
    }

    public Example(Long id, String name, Byte byteValue, Short shortValue, Integer integerValue, Long longValue,
                   Float floatValue, Double doubleValue, BigDecimal bigDecimalValue, Boolean booleanValue,
                   Character characterValue, String stringValue, LocalDate localDateValue,
                   LocalDateTime localDateTimeValue, java.util.Date utilDateValue, Instant instantValue) {
        this.id = id;
        this.name = name;
        this.byteValue = byteValue;
        this.shortValue = shortValue;
        this.integerValue = integerValue;
        this.longValue = longValue;
        this.floatValue = floatValue;
        this.doubleValue = doubleValue;
        this.bigDecimalValue = bigDecimalValue;
        this.booleanValue = booleanValue;
        this.characterValue = characterValue;
        this.stringValue = stringValue;
        this.localDateValue = localDateValue;
        this.localDateTimeValue = localDateTimeValue;
        this.utilDateValue = utilDateValue;
        this.instantValue = instantValue;
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

    public Byte getByteValue() {
        return byteValue;
    }

    public void setByteValue(Byte byteValue) {
        this.byteValue = byteValue;
    }

    public Short getShortValue() {
        return shortValue;
    }

    public void setShortValue(Short shortValue) {
        this.shortValue = shortValue;
    }

    public Integer getIntegerValue() {
        return integerValue;
    }

    public void setIntegerValue(Integer integerValue) {
        this.integerValue = integerValue;
    }

    public Long getLongValue() {
        return longValue;
    }

    public void setLongValue(Long longValue) {
        this.longValue = longValue;
    }

    public Float getFloatValue() {
        return floatValue;
    }

    public void setFloatValue(Float floatValue) {
        this.floatValue = floatValue;
    }

    public Double getDoubleValue() {
        return doubleValue;
    }

    public void setDoubleValue(Double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public BigDecimal getBigDecimalValue() {
        return bigDecimalValue;
    }

    public void setBigDecimalValue(BigDecimal bigDecimalValue) {
        this.bigDecimalValue = bigDecimalValue;
    }

    public Boolean getBooleanValue() {
        return booleanValue;
    }

    public void setBooleanValue(Boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    public Character getCharacterValue() {
        return characterValue;
    }

    public void setCharacterValue(Character characterValue) {
        this.characterValue = characterValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public LocalDate getLocalDateValue() {
        return localDateValue;
    }

    public void setLocalDateValue(LocalDate localDateValue) {
        this.localDateValue = localDateValue;
    }

    public LocalDateTime getLocalDateTimeValue() {
        return localDateTimeValue;
    }

    public void setLocalDateTimeValue(LocalDateTime localDateTimeValue) {
        this.localDateTimeValue = localDateTimeValue;
    }

    public java.util.Date getUtilDateValue() {
        return utilDateValue;
    }

    public void setUtilDateValue(java.util.Date utilDateValue) {
        this.utilDateValue = utilDateValue;
    }

    public Instant getInstantValue() {
        return instantValue;
    }

    public void setInstantValue(Instant instantValue) {
        this.instantValue = instantValue;
    }
}
