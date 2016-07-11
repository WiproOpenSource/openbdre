package com.wipro.ats.bdre.md.beans.table;

/**
 * Created by cloudera on 4/6/16.
 */
public class PermissionType {
    private Integer permissionTypeId;
    private String permissionTypeName;
    private  Integer counter;
    public Integer getCounter() {
        return counter;
    }

    public void setCounter(Integer counter) {
        this.counter = counter;
    }


    public Integer getPermissionTypeId() {
        return permissionTypeId;
    }


    public void setPermissionTypeId(Integer permissionTypeId) {
        this.permissionTypeId = permissionTypeId;
    }

    public String getPermissionTypeName() {
        return permissionTypeName;
    }

    public void setPermissionTypeName(String permissionTypeName) {
        this.permissionTypeName = permissionTypeName;
    }




}
