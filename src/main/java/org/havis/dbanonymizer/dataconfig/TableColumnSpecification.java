package org.havis.dbanonymizer.dataconfig;

import com.opencsv.bean.CsvBindByName;

public class TableColumnSpecification {
    @CsvBindByName(column = "Table", required = true)
    private String tableName;

    @CsvBindByName(column = "Column", required = true)
    private String columnName;

    @CsvBindByName(column = "Type", required = true)
    private ColumnType columnType;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public ColumnType getColumnType() {
        return columnType;
    }

    public void setColumnType(ColumnType columnType) {
        this.columnType = columnType;
    }

    @Override
    public String toString() {
        return "TableColumnSpecification{" +
            "tableName='" + tableName + '\'' +
            ", columnName='" + columnName + '\'' +
            ", columnType=" + columnType +
            '}';
    }
}
