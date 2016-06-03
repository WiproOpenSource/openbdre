package com.wipro.ats.bdre.md.pm.beans;

import java.util.List;

/**
 * Created by cloudera on 6/1/16.
 */
public class Metadata {
    private List<DataList> dataList;

    public List<DataList> getDataList() {
        return dataList;
    }

    public void setDataList(List<DataList> dataList) {
        this.dataList = dataList;
    }
}
