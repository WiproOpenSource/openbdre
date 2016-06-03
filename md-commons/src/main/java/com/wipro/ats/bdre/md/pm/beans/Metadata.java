package com.wipro.ats.bdre.md.pm.beans;

import java.util.List;

/**
 * Created by cloudera on 6/1/16.
 */
public class Metadata {
    private List<DataList> insert;

    private List<DataList> update;

    public List<DataList> getDelete() {
        return delete;
    }

    public void setDelete(List<DataList> delete) {
        this.delete = delete;
    }

    public List<DataList> getUpdate() {
        return update;
    }

    public void setUpdate(List<DataList> update) {
        this.update = update;
    }

    public List<DataList> getInsert() {
        return insert;
    }

    public void setInsert(List<DataList> insert) {
        this.insert = insert;
    }

    private List<DataList> delete;


}
