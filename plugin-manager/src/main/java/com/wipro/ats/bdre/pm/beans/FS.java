package com.wipro.ats.bdre.pm.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cloudera on 6/1/16.
 */
public class FS {
    private String aaction;
    private String sourceLocation;
    private String destinationLocation;
    private String permission;
    private boolean copy;

    public String getAaction() {
        return aaction;
    }

    public void setAaction(String aaction) {
        this.aaction = aaction;
    }

    public String getSourceLocation() {
        return sourceLocation;
    }

    public void setSourceLocation(String sourceLocation) {
        this.sourceLocation = sourceLocation;
    }

    public String getDestinationLocation() {
        return destinationLocation;
    }

    public void setDestinationLocation(String destinationLocation) {
        this.destinationLocation = destinationLocation;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public boolean isCopy() {
        return copy;
    }

    public void setCopy(boolean copy) {
        this.copy = copy;
    }
}
