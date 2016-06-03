package com.wipro.ats.bdre.md.pm.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cloudera on 6/1/16.
 */
public class DataList {
    private String tableName;

    public List<ArrayList<Object>> getData() {
        return data;
    }

    public void setData(List<ArrayList<Object>> data) {
        this.data = data;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    private List<ArrayList<Object>> data;
}
