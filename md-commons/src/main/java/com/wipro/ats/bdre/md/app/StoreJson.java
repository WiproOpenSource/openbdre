package com.wipro.ats.bdre.md.app;

import java.util.List;

/**
 * Created by cloudera on 3/10/16.
 */
public class StoreJson {
    private String name;
    private String id;
    private List<AppValues> columns;

    public List<AppValues> getColumns() {
        return columns;
    }

    public void setColumns(List<AppValues> columns) {
        this.columns = columns;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



}
