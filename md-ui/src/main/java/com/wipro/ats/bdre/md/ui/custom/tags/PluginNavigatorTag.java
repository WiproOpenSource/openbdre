package com.wipro.ats.bdre.md.ui.custom.tags;

/**
 * Created by cloudera on 6/7/16.
 */
import com.wipro.ats.bdre.md.dao.InstalledPluginsDAO;
import com.wipro.ats.bdre.md.dao.PluginConfigDAO;
import com.wipro.ats.bdre.md.dao.jpa.InstalledPlugins;
import com.wipro.ats.bdre.md.dao.jpa.PluginConfig;
import com.wipro.ats.bdre.md.dao.jpa.PluginConfigId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.*;
import java.io.*;
import java.util.Date;

public class PluginNavigatorTag extends SimpleTagSupport {
    @Autowired
    InstalledPluginsDAO installedPluginsDAO;
    @Autowired
    PluginConfigDAO pluginConfigDAO;

    public void setJobManagementJSON(String jobManagementJSON) {
        this.jobManagementJSON = jobManagementJSON;
    }

    String jobManagementJSON;
    public PluginNavigatorTag() {
        /* Hibernate Auto-Wire */
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    public String getJobManagementJSON(){
        String jmJson="";
        PluginConfigId pluginConfigId = new PluginConfigId();
        pluginConfigId.setPluginKey(0);
        pluginConfigId.setPluginUniqueId("Test-1");
        PluginConfig pluginConfig=pluginConfigDAO.get(pluginConfigId);
        jmJson=pluginConfig.getPluginValue();
        return jmJson;
    }
}