package com.wipro.ats.bdre.md.beans;

import com.wipro.ats.bdre.md.rest.beans.ProcessExport;

/**
 * Created by PREM on 4/16/16.
 */
public class SameProcessCodeProcess {

    ProcessExport processExport;
    Integer parentProcessId;
    String processCreaterName;

    public Integer getParentProcessId() {
        return parentProcessId;
    }

    public void setParentProcessId(Integer parentProcessId) {
        this.parentProcessId = parentProcessId;
    }

    public String getProcessCreaterName() {
        return processCreaterName;
    }

    public void setProcessCreaterName(String processCreaterName) {
        this.processCreaterName = processCreaterName;
    }

    public ProcessExport getProcessExport() {
        return processExport;
    }

    public void setProcessExport(ProcessExport processExport) {
        this.processExport = processExport;
    }
}
