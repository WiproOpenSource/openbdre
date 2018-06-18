package com.wipro.ats.bdre.md.api;

import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.dao.MessagesDAO;
import com.wipro.ats.bdre.md.dao.jpa.Messages;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

/**
 * Created by cloudera on 5/24/17.
 */
public class StreamingMessagesAPI extends MetadataAPIBase {

    private static final Logger LOGGER = Logger.getLogger(StreamingMessagesAPI.class);
    @Autowired
    MessagesDAO messagesDAO;

    public StreamingMessagesAPI() {
        AutowireCapableBeanFactory acbFactory = getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }


    public Messages getMessage(String messageName){
        return messagesDAO.get(messageName);
    }

    @Override
    public Object execute(String[] params) {
        return null;
    }
}
