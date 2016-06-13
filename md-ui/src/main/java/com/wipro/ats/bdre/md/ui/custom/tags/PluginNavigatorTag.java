package com.wipro.ats.bdre.md.ui.custom.tags;

/**
 * Created by cloudera on 6/7/16.
 */
import com.wipro.ats.bdre.md.dao.InstalledPluginsDAO;
import com.wipro.ats.bdre.md.dao.PluginConfigDAO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.util.List;

public class PluginNavigatorTag extends SimpleTagSupport {
    private static final Logger LOGGER = Logger.getLogger(PluginNavigatorTag.class);
    @Autowired
    InstalledPluginsDAO installedPluginsDAO;
    @Autowired
    PluginConfigDAO pluginConfigDAO;

    String jobManagementJSON;
    String metadataManagementJSON;
    String masterJSON;
    String jobDefinitionsJSON;
    String runControlJSON;
    String dataIngestionJSON;

    public  PluginNavigatorTag() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    public String getDataIngestionJSON() {
        String diJson="";
        List<String> pluginValueList=pluginConfigDAO.getWithKeyAndConfigGroup("navigation","Data Ingestion");
        LOGGER.info("plugin values List "+pluginValueList.toString());
        if (pluginValueList.size()!=0)
            diJson=pluginValueList.toString();
        else{
            diJson="[]";
        }
        return diJson;    }

    public void setDataIngestionJSON(String dataIngestionJSON) {
        this.dataIngestionJSON = dataIngestionJSON;
    }

    public String getJobManagementJSON(){
        String jmJson="";
        List<String> pluginValueList=pluginConfigDAO.getWithKeyAndConfigGroup("navigation","Job Management");
        LOGGER.info("plugin values List "+pluginValueList.toString());
        if (pluginValueList.size()!=0)
            jmJson=pluginValueList.toString();
        else{
            jmJson="[]";
        }
        return jmJson;
    }

    public void setJobManagementJSON(String jobManagementJSON) {
        this.jobManagementJSON = jobManagementJSON;
    }

    public String getMetadataManagementJSON() {

        String mmJson="";
        List<String> pluginValueList=pluginConfigDAO.getWithKeyAndConfigGroup("navigation","Metadata Management");
        LOGGER.info("plugin values List "+pluginValueList.toString());
        if (pluginValueList.size()!=0)
            mmJson=pluginValueList.toString();
        else{
            mmJson="[]";
        }
        return mmJson;
    }
    public void setMetadataManagementJSON(String metadataManagementJSON) {
        this.metadataManagementJSON = metadataManagementJSON;
    }

    public String getMasterJSON() {
        String mJson="";
        List<String> pluginValueList=pluginConfigDAO.getWithKeyAndConfigGroup("navigation","master");
        LOGGER.info("plugin values List "+pluginValueList.toString());
        if (pluginValueList.size()!=0)
            mJson=pluginValueList.toString();
        else{
            mJson="[]";
        }
        return mJson;
    }

    public void setMasterJSON(String masterJSON) {
        this.masterJSON = masterJSON;
    }


    public String getRunControlJSON() {
        String rcJson="";
        List<String> pluginValueList=pluginConfigDAO.getWithKeyAndConfigGroup("navigation","Run Control");
        LOGGER.info("plugin values List "+pluginValueList.toString());
        if (pluginValueList.size()!=0)
            rcJson=pluginValueList.toString();
        else{
            rcJson="[]";
        }
        return rcJson;
    }

    public void setRunControlJSON(String runControlJSON) {
        this.runControlJSON = runControlJSON;
    }

    public String getJobDefinitionsJSON() {
        String jdJson="";
        List<String> pluginValueList=pluginConfigDAO.getWithKeyAndConfigGroup("navigation","Job Definitions");
        LOGGER.info("plugin values List "+pluginValueList.toString());
        if (pluginValueList.size()!=0)
            jdJson=pluginValueList.toString();
        else{
            jdJson="[]";
        }
        return jdJson;
    }

    public void setJobDefinitionsJSON(String jobDefinitionsJSON) {
        this.jobDefinitionsJSON = jobDefinitionsJSON;
    }


}