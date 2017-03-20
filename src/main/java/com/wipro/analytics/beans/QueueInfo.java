package com.wipro.analytics.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by cloudera on 3/19/17.
 */
public class QueueInfo implements Serializable {
    private String queueName;
    private double maxCapacity;
    private int usedMemory;
    private int usedCores;
    private int numContainers;
    private String queueState;
    private int maxApplications;
    private int numApplications;
    private int numActiveApplications;
    private int numPendingApplications;
    private String queueType;
    private String users;

    public int getUsedMemory() {
        return usedMemory;
    }

    public void setUsedMemory(int usedMemory) {
        this.usedMemory = usedMemory;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public double getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(double maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public int getUsedCores() {
        return usedCores;
    }

    public void setUsedCores(int usedCores) {
        this.usedCores = usedCores;
    }

    public int getNumContainers() {
        return numContainers;
    }

    public void setNumContainers(int numContainers) {
        this.numContainers = numContainers;
    }

    public String getQueueState() {
        return queueState;
    }

    public void setQueueState(String queueState) {
        this.queueState = queueState;
    }

    public int getMaxApplications() {
        return maxApplications;
    }

    public void setMaxApplications(int maxApplications) {
        this.maxApplications = maxApplications;
    }

    public int getNumApplications() {
        return numApplications;
    }

    public void setNumApplications(int numApplications) {
        this.numApplications = numApplications;
    }

    public int getNumActiveApplications() {
        return numActiveApplications;
    }

    public void setNumActiveApplications(int numActiveApplications) {
        this.numActiveApplications = numActiveApplications;
    }

    public int getNumPendingApplications() {
        return numPendingApplications;
    }

    public void setNumPendingApplications(int numPendingApplications) {
        this.numPendingApplications = numPendingApplications;
    }

    public String getQueueType() {
        return queueType;
    }

    public void setQueueType(String queueType) {
        this.queueType = queueType;
    }

    public String getUsers() {
        return users;
    }

    public void setUsers(String users) {
        this.users = users;
    }

    @Override
    public String toString(){
        return
                queueName+ '\t'
                + maxCapacity+ '\t'
                + usedMemory+ '\t'
                + usedCores+ '\t'
                + numContainers+ '\t'
                + queueState+ '\t'
                + maxApplications+ '\t'
                + numApplications+ '\t'
                + numActiveApplications+ '\t'
                + numPendingApplications+ '\t'
                + queueType+ '\t'
                + users;
    }

}
