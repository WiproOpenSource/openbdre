package com.wipro.ats.bdre.imcrawler.model;

import javax.jdo.annotations.*;

/**
 * Created by AS294216 on 05-09-2015.
 */

@PersistenceCapable(identityType = IdentityType.DATASTORE)
@DatastoreIdentity(strategy = IdGeneratorStrategy.IDENTITY, column = "uniqid")
public class StatisticsDB {


    @Persistent
    private long value;

    @Persistent
    private String name;

    public StatisticsDB() {
        super();
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "StatisticsDB{" +
                "value=" + value +
                ", name='" + name + '\'' +
                '}';
    }
}