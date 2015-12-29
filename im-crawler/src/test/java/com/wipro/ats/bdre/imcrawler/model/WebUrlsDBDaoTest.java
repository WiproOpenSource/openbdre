package com.wipro.ats.bdre.imcrawler.model;

import com.wipro.ats.bdre.md.dao.jpa.Weburlsdb;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

public class WebUrlsDBDaoTest {
    private static final Logger LOGGER = Logger.getLogger(WebUrlsDBDaoTest.class);

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    @Autowired
    WebUrlsDBDao webUrlsDBDao;

    Weburlsdb weburlsdb;

    @Ignore
    @Test
    public void testList() throws Exception {
        List<Weburlsdb> weburlsdbLists = webUrlsDBDao.list(0,2);
        for (Weburlsdb weburlsdbList: weburlsdbLists){
            LOGGER.info(weburlsdbList.getUrl());
        }
    }
    @Ignore
    @Test
    public void testGet() throws Exception {
        weburlsdb = webUrlsDBDao.get(2);
        LOGGER.info("******* get() "+weburlsdb.getUrl());
    }
    @Ignore
    @Test
    public void testInsert() throws Exception {
        weburlsdb = new Weburlsdb();
        weburlsdb.setAnchor("anchor");
        weburlsdb.setDepth(1);
        weburlsdb.setDocid(2);
        weburlsdb.setDomain("domain");
        weburlsdb.setInstanceexecid(new Long (3));
        weburlsdb.setParentdocid(3);
        weburlsdb.setParenturl("parenturl");
        weburlsdb.setPath("ssh");
        weburlsdb.setPid(new Long(6));
        weburlsdb.setPriority(1);
        weburlsdb.setSubdomain("subdomain");
        weburlsdb.setTag("tag");
        weburlsdb.setUniqid(new Long(4));
        weburlsdb.setUrl("url1");
        webUrlsDBDao.insert(weburlsdb);
        LOGGER.info("********insertion done************");
    }
    @Ignore
    @Test
    public void testDelete() throws Exception {
        webUrlsDBDao.delete(1);
        LOGGER.info("******* deleted row");
    }
}