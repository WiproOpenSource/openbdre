package com.wipro.ats.bdre.augen;

import java.util.Enumeration;
import java.util.List;

import com.wipro.ats.bdre.BaseStructure;
import com.wipro.ats.bdre.md.api.GetProcess;
import com.wipro.ats.bdre.md.api.GetProperties;
import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.beans.ProcessInfo;
import com.wipro.ats.bdre.md.dao.AnalyticsAppsDAO;
import com.wipro.ats.bdre.md.dao.ProcessDAO;
import com.wipro.ats.bdre.md.dao.jpa.AnalyticsApps;
import com.wipro.ats.bdre.md.dao.jpa.Process;
import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

/**
 * Created by cloudera on 4/15/16.
 */
public class AnalyticUIGeneratorMain extends MetadataAPIBase {
    @Autowired
    AnalyticsAppsDAO analyticsAppsDAO;
    @Autowired
    ProcessDAO processDAO;


    private static final Logger LOGGER = Logger.getLogger(AnalyticUIGeneratorMain.class);
    private static final String[][] PARAMS_STRUCTURE = {
            {"p", "parent-process-id", "Process Id of the process to begin"},
            {"u", "username", "Username"}

    };
    public AnalyticUIGeneratorMain() {
        AutowireCapableBeanFactory acbFactory = getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    public static void main(String[] args)  {
        AnalyticUIGeneratorMain analyticUIGeneratorMain = new AnalyticUIGeneratorMain();
        analyticUIGeneratorMain.table2DAOAnalyticUI(args);


    }
    @Override
    public Object execute(String[] param){
        return null;
    }

    public void table2DAOAnalyticUI(String[] args){
        CommandLine commandLine = new AnalyticUIGeneratorMain().getCommandLine(args, PARAMS_STRUCTURE);
        String pid = commandLine.getOptionValue("parent-process-id");
        LOGGER.debug("processId is " + pid);
        String username = commandLine.getOptionValue("username");
        LOGGER.debug("username is " + username);
        processDAO.securityCheck(Integer.parseInt(pid),username, "execute");
        String outputFile = "";


        //Getting sub-process for process-id
        List<ProcessInfo> processInfos = new GetProcess().execute(new String[]{"--parent-process-id", pid , "--username" , username});
        // Getting properties related with flume action for every process
        StringBuilder addFlumeProperties = new StringBuilder();
        com.wipro.ats.bdre.md.dao.jpa.AnalyticsApps analyticsApps = new com.wipro.ats.bdre.md.dao.jpa.AnalyticsApps();

        for (ProcessInfo processInfo : processInfos) {
            if (processInfo.getParentProcessId() == 0) {
                Process process = new Process();
                process.setProcessId(processInfo.getProcessId());
                analyticsApps.setProcess(process);
                continue;
            }
            GetProperties getProperties = new GetProperties();
            java.util.Properties flumeProperties = getProperties.getProperties(processInfo.getProcessId().toString(), "analytics-app");
            Enumeration e = flumeProperties.propertyNames();

            if (!flumeProperties.isEmpty()) {
                while (e.hasMoreElements()) {
                    String key = (String) e.nextElement();
                    if ("app-desc".equals(key)){
                        analyticsApps.setAppDescription(flumeProperties.getProperty(key));
                    }else if("app-name".equals(key)){
                        analyticsApps.setAppName(flumeProperties.getProperty(key));
                    }else if("category".equals(key)){
                        analyticsApps.setCategoryName(flumeProperties.getProperty(key));
                    }else if("dashboard-url".equals(key)){
                        analyticsApps.setDashboardUrl(flumeProperties.getProperty(key));
                    }else if("ddp-url".equals(key)){
                        analyticsApps.setDdpUrl(flumeProperties.getProperty(key));
                    }else if("industry".equals(key)){
                        analyticsApps.setIndustryName(flumeProperties.getProperty(key));
                    }else if("questions-json".equals(key)){
                        analyticsApps.setQuestionsJson(flumeProperties.getProperty(key));
                    }

                }
                analyticsApps.setAppImage("~/bdre-wfd/" + processInfo.getProcessId());


            }
        }
        AnalyticUIGeneratorMain analyticUIGeneratorMain = new AnalyticUIGeneratorMain();
        analyticUIGeneratorMain.insertIntoAnalyticTable(analyticsApps);
    }

    public void insertIntoAnalyticTable(AnalyticsApps analyticsApps){
        analyticsAppsDAO.insert(analyticsApps);
    }
}
