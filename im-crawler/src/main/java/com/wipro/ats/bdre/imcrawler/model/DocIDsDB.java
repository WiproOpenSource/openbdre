package com.wipro.ats.bdre.imcrawler.model;

import javax.jdo.annotations.*;
import javax.persistence.GenerationType;


/**
 * Created by AS294216 on 05-09-2015.
 */

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class DocIDsDB {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private int docId;

    @Persistent
    @Column(length = 3000)
    private String url;

    public DocIDsDB() {
        super();
    }

    public int getDocId() {
        return docId;
    }

    public void setDocId(int docId) {
        this.docId = docId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "DocIDsDB{" +
                "docId=" + docId +
                ", url='" + url + '\'' +
                '}';
    }
}