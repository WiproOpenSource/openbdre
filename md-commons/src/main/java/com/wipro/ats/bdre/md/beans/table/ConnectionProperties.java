package com.wipro.ats.bdre.md.beans.table;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by cloudera on 5/31/17.
 */
public class ConnectionProperties {
    @NotNull
    @Size(max = 45)
    private String connectionName;

    @NotNull
    @Size(max = 128)
    private String propKey;

    @NotNull
    @Size(max = 128)
    private String configGroup;

    @NotNull
    @Size(max = 1024)
    private String propValue;

    @NotNull
    @Size(max = 1024)
    private String description;
    private Integer page;

    public Integer getCounter() {
        return counter;
    }

    public void setCounter(Integer counter) {
        this.counter = counter;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    private Integer counter;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getConnectionName() {
        return connectionName;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    public String getPropKey() {
        return propKey;
    }

    public void setPropKey(String propKey) {
        this.propKey = propKey;
    }

    public String getConfigGroup() {
        return configGroup;
    }

    public void setConfigGroup(String configGroup) {
        this.configGroup = configGroup;
    }

    public String getPropValue() {
        return propValue;
    }

    public void setPropValue(String propValue) {
        this.propValue = propValue;
    }


}
