package com.wipro.ats.bdre.md.app;

import java.util.List;

/**
 * Created by cloudera on 3/10/16.
 */
public class StoreJson {
    private String name;
    private String id;

    public List<AppValues> getAppValuesList() {
        return appValuesList;
    }

    public void setAppValuesList(List<AppValues> appValuesList) {
        this.appValuesList = appValuesList;
    }

    private List<AppValues> appValuesList;

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
