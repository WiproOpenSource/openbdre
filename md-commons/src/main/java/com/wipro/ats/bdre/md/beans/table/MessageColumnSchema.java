package com.wipro.ats.bdre.md.beans.table;

/**
 * Created by cloudera on 5/31/17.
 */
public class MessageColumnSchema {

    String columnName;
    String dataType;
    int counter;

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }


    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }


}
