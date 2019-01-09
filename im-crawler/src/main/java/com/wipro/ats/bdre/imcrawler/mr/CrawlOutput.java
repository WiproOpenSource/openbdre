package com.wipro.ats.bdre.imcrawler.mr;

import java.util.Arrays;

/**
 * Created by AS294216 on 9/24/15.
 */
public class CrawlOutput {
    int docid;
    String url;
    String domain;
    String path;
    String subDomain;
    String parentUrl;
    String anchor;
    String html;
    byte[] binary;

    public CrawlOutput(int docid, String url, String domain, String path, String subDomain, String parentUrl, String anchor, String html, byte[] binary) {
        this.docid = docid;
        this.url = url;
        this.domain = domain;
        this.path = path;
        this.subDomain = subDomain;
        this.parentUrl = parentUrl;
        this.anchor = anchor;
        this.html = html;
        this.binary = binary;
    }

    @Override
    public String toString() {
        return "CrawlOutput{" +
                "docid=" + docid +
                ", url='" + url + '\'' +
                ", domain='" + domain + '\'' +
                ", path='" + path + '\'' +
                ", subDomain='" + subDomain + '\'' +
                ", parentUrl='" + parentUrl + '\'' +
                ", anchor='" + anchor + '\'' +
                ", html='" + html + '\'' +
                ", binary=" + Arrays.toString(binary) +
                '}';
    }

    public int getDocid() {
        return docid;
    }

    public void setDocid(int docid) {
        this.docid = docid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSubDomain() {
        return subDomain;
    }

    public void setSubDomain(String subDomain) {
        this.subDomain = subDomain;
    }

    public String getParentUrl() {
        return parentUrl;
    }

    public void setParentUrl(String parentUrl) {
        this.parentUrl = parentUrl;
    }

    public String getAnchor() {
        return anchor;
    }

    public void setAnchor(String anchor) {
        this.anchor = anchor;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public byte[] getBinary() {
        return binary;
    }

    public void setBinary(byte[] binary) {
        this.binary = binary;
    }
}
