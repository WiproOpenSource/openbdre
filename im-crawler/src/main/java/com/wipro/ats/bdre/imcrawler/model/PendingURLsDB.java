package com.wipro.ats.bdre.imcrawler.model;

import javax.jdo.annotations.*;

/**
 * Created by AS294216 on 05-09-2015.
 */

@PersistenceCapable(identityType = IdentityType.DATASTORE)
@DatastoreIdentity(strategy = IdGeneratorStrategy.IDENTITY, column = "uniqid")
public class PendingURLsDB {
    @Persistent
    @Column(length = 3000)
    private String url;
    @Persistent
    private int docid;
    @Persistent
    private int pid;
    @Persistent
    private long instanceExecid;
    @Persistent
    private int parentDocid;
    @Persistent
    private String parentUrl;
    @Persistent
    private short depth;
    @Persistent
    private String domain;
    @Persistent
    private String subDomain;
    @Persistent
    @Column(length = 1000)
    private String path;
    @Persistent
    private String anchor;
    @Persistent
    private byte priority;
    @Persistent
    private String tag;

    public PendingURLsDB() {
        super();
    }

    @Override
    public String toString() {
        return "PendingURLsDB{" +
                "url='" + url + '\'' +
                ", docid=" + docid +
                ", pid=" + pid +
                ", instanceExecid=" + instanceExecid +
                ", parentDocid=" + parentDocid +
                ", parentUrl='" + parentUrl + '\'' +
                ", depth=" + depth +
                ", domain='" + domain + '\'' +
                ", subDomain='" + subDomain + '\'' +
                ", path='" + path + '\'' +
                ", anchor='" + anchor + '\'' +
                ", priority=" + priority +
                ", tag='" + tag + '\'' +
                '}';
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public long getInstanceExecid() {
        return instanceExecid;
    }

    public void setInstanceExecid(long instanceExecid) {
        this.instanceExecid = instanceExecid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getDocid() {
        return docid;
    }

    public void setDocid(int docid) {
        this.docid = docid;
    }

    public int getParentDocid() {
        return parentDocid;
    }

    public void setParentDocid(int parentDocid) {
        this.parentDocid = parentDocid;
    }

    public String getParentUrl() {
        return parentUrl;
    }

    public void setParentUrl(String parentUrl) {
        this.parentUrl = parentUrl;
    }

    public short getDepth() {
        return depth;
    }

    public void setDepth(short depth) {
        this.depth = depth;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getSubDomain() {
        return subDomain;
    }

    public void setSubDomain(String subDomain) {
        this.subDomain = subDomain;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAnchor() {
        return anchor;
    }

    public void setAnchor(String anchor) {
        this.anchor = anchor;
    }

    public byte getPriority() {
        return priority;
    }

    public void setPriority(byte priority) {
        this.priority = priority;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}