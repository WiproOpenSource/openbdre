package com.wipro.ats.bdre.md.dao;

import com.wipro.ats.bdre.md.dao.jpa.*;
import com.wipro.ats.bdre.md.dao.jpa.Process;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.Assert.assertEquals;

/**
 * Created by SU324335 on 3/8/2016.
 */
public class AppDeploymentQueueStatusDAOTest {
    private static final Logger LOGGER = Logger.getLogger(AppDeploymentQueueStatusDAOTest.class);

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    @Autowired
    AppDeploymentQueueStatusDAO appDeploymentQueueStatusDAO;

    @Ignore
    @Test
    public void testInsertUpdateAndDelete() throws Exception {
        AppDeploymentQueueStatus adqStatus=new AppDeploymentQueueStatus();
        adqStatus.setDescription("Merged");
        adqStatus.setAppDeployStatusId((short) 3);

        Short adqId = appDeploymentQueueStatusDAO.insert(adqStatus);
        LOGGER.info("Adq is added with Id:" + adqId);

        adqStatus.setDescription("updated Merged");
        appDeploymentQueueStatusDAO.update(adqStatus);
        adqStatus = appDeploymentQueueStatusDAO.get(adqId);
        assertEquals("updated Merged",adqStatus.getDescription());
        LOGGER.info("Updated Description is:" + adqStatus.getDescription());
        appDeploymentQueueStatusDAO.delete(adqId);
        LOGGER.info("Deleted AdqStatus Entry with ID" + adqStatus.getAppDeployStatusId());
    }


}
