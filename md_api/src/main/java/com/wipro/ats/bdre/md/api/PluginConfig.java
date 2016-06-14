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

    public void insert(List<com.wipro.ats.bdre.md.pm.beans.PluginConfig> pluginConfigs,String pluginUniqueId){
        for (com.wipro.ats.bdre.md.pm.beans.PluginConfig pluginConfig : pluginConfigs){
            com.wipro.ats.bdre.md.dao.jpa.PluginConfig pluginConfigJPA = new com.wipro.ats.bdre.md.dao.jpa.PluginConfig();
            pluginConfigJPA.setConfigGroup(pluginConfig.getConfigGroup());
            pluginConfigJPA.setPluginValue(pluginConfig.getValue());
            PluginConfigId pluginConfigId = new PluginConfigId();
            pluginConfigId.setPluginUniqueId(pluginUniqueId);
            pluginConfigId.setPluginKey(pluginConfig.getKey());
            pluginConfigJPA.setId(pluginConfigId);
            pluginConfigDAO.insert(pluginConfigJPA);

        }
    }


    public List<String> distinctPluginUniqueIdList(String configGroup)
    {
        return pluginConfigDAO.distinctPluginConfig(configGroup);
    }
    public com.wipro.ats.bdre.md.dao.jpa.PluginConfig get(PluginConfigId pluginConfigId)
    {
        return pluginConfigDAO.get(pluginConfigId);
    }

    public List<String> getWithConfig(String pluginUniQueId,String configGroup)
    {
        return pluginConfigDAO.getWithConfig(pluginUniQueId,configGroup);
    }

    public List<String> listPluginKeys(String pluginUniqueId,String configGroup)
    {
        return pluginConfigDAO.listPluginKeys(pluginUniqueId,configGroup);
    }
}
