package com.wipro.ats.bdre.md.api;

import com.wipro.ats.bdre.exception.MetadataException;
import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.dao.ConfigurationPropertiesDAO;
import com.wipro.ats.bdre.md.dao.ConnectionPropertiesDAO;
import com.wipro.ats.bdre.md.dao.ConnectionsDAO;
import com.wipro.ats.bdre.md.dao.jpa.ConfigurationProperties;
import com.wipro.ats.bdre.md.dao.jpa.ConnectionProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import java.util.List;
import java.util.Properties;

/**
 * Created by cloudera on 6/8/17.
 */
public class GetConfigurationProperties extends MetadataAPIBase {

    @Autowired
    private ConfigurationPropertiesDAO configurationPropertiesDAO;

    public GetConfigurationProperties() {
        AutowireCapableBeanFactory acbFactory = getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    public Properties getConfigurationPropertiesByMessage(String messageName) {
        Properties savedProperties = new Properties();
        List<ConfigurationProperties> connectionProperties = configurationPropertiesDAO.getConfigurationPropertiesByMessageName(messageName);
        try {
            for (ConfigurationProperties info : connectionProperties) {
                System.out.println("info.getId().getPropKey() = " + info.getId().getPropKey());
                savedProperties.setProperty(info.getId().getPropKey(), info.getPropValue());
            }
        } catch (Exception e) {
            throw new MetadataException(e);
        }
        return savedProperties;
    }


    @Override
    public Object execute(String[] params) {
        return null;
    }

}
