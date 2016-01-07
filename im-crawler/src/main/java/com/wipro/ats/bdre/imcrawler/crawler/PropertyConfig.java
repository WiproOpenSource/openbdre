package com.wipro.ats.bdre.imcrawler.crawler;

import com.wipro.ats.bdre.md.api.GetProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by AS294216 on 11-09-2015.
 */
public class PropertyConfig {
    private static final Logger logger = LoggerFactory.getLogger(PropertyConfig.class);
    private static PropertyConfig propertyConfig = null;
    private static final String CONFIG_GROUP = "crawler";
    private int politenessDelay;
    private int maxDepthOfCrawling;
    private int maxPagesToFetch;
    private int includeBinaryContentInCrawling;
    private int resumableCrawling;
    private String userAgentString;
    private String proxyHost;
    private int proxyPort;
    private String outputPath;
    private String proxyUserName;
    private String proxyPassword;
    private String url;
    private String urlsToSearch;
    private String urlsNotToSearch;
    private int numMappers;

    public Integer getNumMappers() {
        return numMappers;
    }

    public String getUrlsToSearch() {
        return urlsToSearch;
    }

    public String getUrlsNotToSearch() {
        return urlsNotToSearch;
    }

    public String getUrl() {
        return url;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public String getProxyPassword() {
        return proxyPassword;
    }

    public int getPolitenessDelay() {
        return politenessDelay;
    }

    public int getMaxDepthOfCrawling() {
        return maxDepthOfCrawling;
    }

    public int getMaxPagesToFetch() {
        return maxPagesToFetch;
    }

    public int isIncludeBinaryContentInCrawling() {
        return includeBinaryContentInCrawling;
    }

    public int isResumableCrawling() {
        return resumableCrawling;
    }

    public String getUserAgentString() {
        return userAgentString;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public String getProxyUserName() {
        return proxyUserName;
    }


    private PropertyConfig(int processId) {
        String pid = new Integer(processId).toString();
        GetProperties getProperties = new GetProperties();
        java.util.Properties listForParams = getProperties.getProperties(pid.toString(), CONFIG_GROUP);

        if (listForParams.getProperty("politenessDelay") != null) {
            politenessDelay = Integer.parseInt(listForParams.getProperty("politenessDelay"));
        } else {
            politenessDelay = 1000;
        }
        if (listForParams.getProperty("maxDepthOfCrawling") != null) {
            maxDepthOfCrawling = Integer.parseInt(listForParams.getProperty("maxDepthOfCrawling"));
        } else {
            maxDepthOfCrawling = 10;
        }
        if (listForParams.getProperty("maxPagesToFetch") != null) {
            maxPagesToFetch = Integer.parseInt(listForParams.getProperty("maxPagesToFetch"));
        } else {
            maxPagesToFetch = 5;
        }
        if (listForParams.getProperty("includeBinaryContentInCrawling") != null) {
            includeBinaryContentInCrawling = Integer.parseInt(listForParams.getProperty("includeBinaryContentInCrawling"));
        } else {
            includeBinaryContentInCrawling = 0;
        }
        if (listForParams.getProperty("resumableCrawling") != null) {
            resumableCrawling = Integer.parseInt(listForParams.getProperty("resumableCrawling"));
        } else {
            resumableCrawling = 0;
        }
        if (listForParams.getProperty("userAgentString") != null) {
            userAgentString = listForParams.getProperty("userAgentString");
        } else {
            userAgentString = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36";
        }
        if(listForParams.getProperty("proxyHost")!=null){
            proxyHost = listForParams.getProperty("proxyHost");
        }
        if(listForParams.getProperty("proxyPort")!=null){
            proxyPort = Integer.parseInt(listForParams.getProperty("proxyPort"));
        }
        if(listForParams.getProperty("proxyUsername")!=null){
            proxyUserName = listForParams.getProperty("proxyUsername");
        }
        if(listForParams.getProperty("proxyPassword")!=null){
            proxyPassword = listForParams.getProperty("proxyPassword");
        }

        outputPath = listForParams.getProperty("outputPath");
        url = listForParams.getProperty("url");
        urlsToSearch = listForParams.getProperty("urlsToSearch");
        urlsNotToSearch = listForParams.getProperty("urlsNotToSearch");
        numMappers = Integer.parseInt(listForParams.getProperty("numberOfMappers"));
        //ignoreImages = Boolean.parseBoolean(listForParams.getProperty("ignoreImage"));


        logger.info("Config parameters:- number of mappers: "+ numMappers+" PolitenessDelay: " + politenessDelay + " maxDepthOfCrawling: " + maxDepthOfCrawling +
                " maxPagesToFetch: " + maxPagesToFetch + " includeBinaryContentInCrawling: " + includeBinaryContentInCrawling +
                " resumableCrawling: " + resumableCrawling + " userAgentString: " + userAgentString + " proxyHost: " + proxyHost +
                " proxyPort: " + proxyPort + " proxyUserName: " + proxyUserName + " url(comma seperated): " + url +
                " urlsToSearch Pattern: "+ urlsToSearch+ " urlsNotToSearch Pattern: "+urlsNotToSearch + " outputPath: "+outputPath);
    }


    public static PropertyConfig getPropertyConfig(int pid) {
        if (propertyConfig == null)
            propertyConfig = new PropertyConfig(pid);
        return propertyConfig;
    }

}
