package com.wipro.ats.bdre.md.api;

import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.dao.ConnectionsDAO;
import com.wipro.ats.bdre.md.dao.jpa.Connections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

/**
 * Created by cloudera on 8/16/17.
 */
public class GetConnections extends MetadataAPIBase {
    @Autowired
    private ConnectionsDAO connectionsDAO;

    public GetConnections() {
        AutowireCapableBeanFactory acbFactory = getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    public Connections getConnection(String conName) {
        return connectionsDAO.get(conName);
    }

    @Override
    public Object execute(String[] params) {
        return null;
    }
}
