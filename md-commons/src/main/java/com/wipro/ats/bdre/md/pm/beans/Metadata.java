package com.wipro.ats.bdre.md.pm.beans;

import java.util.List;

/**
 * Created by cloudera on 6/1/16.
 */
public class Metadata {
    private List<Insert> insert;

    public List<Insert> getInsert() {
        return insert;
    }

    public void setInsert(List<Insert> insert) {
        this.insert = insert;
    }
}
