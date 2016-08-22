package com.wipro.ats.bdre;

import com.wipro.ats.bdre.md.dao.ProcessDAO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by cloudera on 8/16/16.
 */
public class GetParentProcessType {
    private static final Logger LOGGER =  Logger.getLogger(GetParentProcessType.class);

    @Autowired
    ProcessDAO processDAO;

    public GetParentProcessType() {
        /* Hibernate Auto-Wire */
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    public String getParentProcessTypeId(Integer pid){
        LOGGER.info("pid is "+pid);
        LOGGER.info(processDAO.getParentProcessTypeId(pid));
        return processDAO.getParentProcessTypeId(pid);
    }
}
