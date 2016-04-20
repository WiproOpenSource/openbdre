package com.wipro.ats.bdre.md.beans.table;

import javax.validation.constraints.NotNull;

/**
 * Created by cloudera on 4/20/16.
 */
public class AnalyticsApps {
    @NotNull
    private Integer process;
    @NotNull
    private String industryName;
    @NotNull
    private String categoryName;
    @NotNull
    private String appDescription;
    @NotNull
    private String appName;
    @NotNull
    private String questionsJson;
    @NotNull
    private String dashboardUrl;
    @NotNull
    private String ddpUrl;
    private Long analyticAppsId;

    public Long getAnalyticAppsId() {
        return analyticAppsId;
    }

    public void setAnalyticAppsId(Long analyticAppsId) {
        this.analyticAppsId = analyticAppsId;
    }

    public Integer getProcess() {
        return process;
    }

    public void setProcess(Integer process) {
        this.process = process;
    }

    public String getIndustryName() {
        return industryName;
    }

    public void setIndustryName(String industryName) {
        this.industryName = industryName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getAppDescription() {
        return appDescription;
    }

    public void setAppDescription(String appDescription) {
        this.appDescription = appDescription;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getQuestionsJson() {
        return questionsJson;
    }

    public void setQuestionsJson(String questionsJson) {
        this.questionsJson = questionsJson;
    }

    public String getDashboardUrl() {
        return dashboardUrl;
    }

    public void setDashboardUrl(String dashboardUrl) {
        this.dashboardUrl = dashboardUrl;
    }

    public String getDdpUrl() {
        return ddpUrl;
    }

    public void setDdpUrl(String ddpUrl) {
        this.ddpUrl = ddpUrl;
    }



}
