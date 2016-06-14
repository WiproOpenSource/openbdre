package com.wipro.ats.bdre.md.pm.beans;

import java.util.List;

/**
 * Created by cloudera on 6/1/16.
 */
public class Install {
    private List<FS> fs;
    private Metadata metadata;
    private UIWAR uiWar;
    private RestWar restWar;

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


    public UIWAR getUiWar() {
        return uiWar;
    }

    public void setUiWar(UIWAR uiWar) {
        this.uiWar = uiWar;
    }

    public RestWar getRestWar() {
        return restWar;
    }

    public void setRestWar(RestWar restWar) {
        this.restWar = restWar;
    }
}
