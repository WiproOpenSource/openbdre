package com.wipro.ats.bdre.md.api;

import com.wipro.ats.bdre.exception.MetadataException;
import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.beans.GetPropertiesInfo;
import com.wipro.ats.bdre.md.dao.ConnectionPropertiesDAO;
import com.wipro.ats.bdre.md.dao.ConnectionsDAO;
import com.wipro.ats.bdre.md.dao.jpa.ConnectionProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import java.util.List;
import java.util.Properties;

/**
 * Created by cloudera on 6/8/17.
 */
public class GetConnectionProperties extends MetadataAPIBase {

    @Autowired
    private ConnectionPropertiesDAO connectionPropertiesDAO;

    public GetConnectionProperties() {
        AutowireCapableBeanFactory acbFactory = getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    public Properties getConnectionProperties(String connectionName, String configGroup) {
        Properties savedProperties = new Properties();
        List<ConnectionProperties> connectionProperties = connectionPropertiesDAO.getConnectionPropertiesForConfig(connectionName,configGroup);
        try {
            for (ConnectionProperties info : connectionProperties) {
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
