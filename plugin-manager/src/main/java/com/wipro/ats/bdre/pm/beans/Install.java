package com.wipro.ats.bdre.pm.beans;

import java.util.List;

/**
 * Created by cloudera on 6/1/16.
 */
public class Install {
    public List<FS> getFs() {
        return fs;
    }

    public void setFs(List<FS> fs) {
        this.fs = fs;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    private List<FS> fs;
    private Metadata metadata;
}
