package com.wipro.ats.bdre.md.api;

import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.dao.PluginConfigDAO;
import com.wipro.ats.bdre.md.dao.jpa.PluginConfigId;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import java.util.List;

/**
 * Created by cloudera on 6/6/16.
 */
public class PluginConfig extends MetadataAPIBase {
    private static final Logger LOGGER = Logger.getLogger(ProcessLog.class);

    @Autowired
    PluginConfigDAO pluginConfigDAO;

    public PluginConfig() {
        AutowireCapableBeanFactory acbFactory = getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    public Object execute(String[] params) {
        return null;
    }

    public void insert(com.wipro.ats.bdre.md.pm.beans.PluginConfig pluginConfig){

    }


    public List<String> distinctPluginUniqueIdList()
    {
        return pluginConfigDAO.distinctPluginConfig();
    }
    public com.wipro.ats.bdre.md.dao.jpa.PluginConfig get(PluginConfigId pluginConfigId)
    {
        return pluginConfigDAO.get(pluginConfigId);
    }

    public List<String> getWithConfig(String pluginUniQueId,String configGroup)
    {
        return pluginConfigDAO.getWithConfig(pluginUniQueId,configGroup);
    }
}
