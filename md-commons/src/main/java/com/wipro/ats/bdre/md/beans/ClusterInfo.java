package com.wipro.ats.bdre.md.beans;

/**
 * Created by SU324335 on 4/20/2016.
 */
public class ClusterInfo {
    private String nameNodeHostName;
    private String nameNodePort;
    private String jobTrackerHostName;
    private String jobTrackerPort;
    private String hiveHostName;
    private String clusterName;
    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }



    public String getNameNodePort() {
        return nameNodePort;
    }

    public void setNameNodePort(String nameNodePort) {
        this.nameNodePort = nameNodePort;
    }

    public String getNameNodeHostName() {
        return nameNodeHostName;
    }

    public void setNameNodeHostName(String nameNodeHostName) {
        this.nameNodeHostName = nameNodeHostName;
    }

    public String getJobTrackerHostName() {
        return jobTrackerHostName;
    }

    public void setJobTrackerHostName(String jobTrackerHostName) {
        this.jobTrackerHostName = jobTrackerHostName;
    }

    public String getJobTrackerPort() {
        return jobTrackerPort;
    }

    public void setJobTrackerPort(String jobTrackerPort) {
        this.jobTrackerPort = jobTrackerPort;
    }

    public String getHiveHostName() {
        return hiveHostName;
    }

    public void setHiveHostName(String hiveHostName) {
        this.hiveHostName = hiveHostName;
    }


}
