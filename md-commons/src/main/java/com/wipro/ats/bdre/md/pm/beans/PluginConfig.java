package com.wipro.ats.bdre.md.pm.beans;

/**
 * Created by cloudera on 6/5/16.
 */
public class PluginConfig {
    private String wfdJspFrag;
    private String deployScript;
    private String execScript;

    public String getWfdJspFrag() {
        return wfdJspFrag;
    }

    public void setWfdJspFrag(String wfdJspFrag) {
        this.wfdJspFrag = wfdJspFrag;
    }

    public String getDeployScript() {
        return deployScript;
    }

    public void setDeployScript(String deployScript) {
        this.deployScript = deployScript;
    }

    public String getExecScript() {
        return execScript;
    }

    public void setExecScript(String execScript) {
        this.execScript = execScript;
    }
}
