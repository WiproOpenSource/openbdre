package com.wipro.ats.bdre.md.beans.table;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by cloudera on 5/31/17.
 */
public class Connections {
    @NotNull
    @Size(max = 45)
    private String connectionName;

    @NotNull
    @Size(max = 45)
    private String connectionType;

    @Size(max=1024)
    private String description;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getCounter() {
        return counter;
    }

    public void setCounter(Integer counter) {
        this.counter = counter;
    }

    private Integer page;
    private Integer counter;

    public String getConnectionName() {
        return connectionName;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    public String getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }



}
