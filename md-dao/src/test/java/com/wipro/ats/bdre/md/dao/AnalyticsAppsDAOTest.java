package com.wipro.ats.bdre.md.dao;


import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.Assert.*;

/**
 * Created by cloudera on 4/20/16.
 */
public class AnalyticsAppsDAOTest {


    private static final Logger LOGGER = Logger.getLogger(AppDeploymentQueueDAOTest.class);
    @Autowired
    AnalyticsAppsDAO analyticsAppsDAO;

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }


    @Ignore
    @Test
    public void testInsert() throws Exception {
        AnalyticsAppsDAO analyticsAppsDAO = new AnalyticsAppsDAO();
    }

    @Ignore
    @Test
    public void testListIndustries() throws Exception{
        LOGGER.info(analyticsAppsDAO.listIndustries());
    }


    @Ignore
    @Test
    public void testListApp() throws Exception{
        LOGGER.info(analyticsAppsDAO.listApps("Banking","cat1"));
    }
}