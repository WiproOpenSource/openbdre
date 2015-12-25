package com.wipro.ats.bdre.imcrawler.model;

import com.wipro.ats.bdre.imcrawler.jpa.Pendingurlsdb;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

public class PendingUrlsDBDaoTest {
    private static final Logger LOGGER = Logger.getLogger(PendingUrlsDBDaoTest.class);

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    @Autowired
    PendingUrlsDBDao pendingUrlsDBDao;

    @Ignore
    @Test
    public void testList() throws Exception {
        List<Pendingurlsdb> pendingUrlLists = pendingUrlsDBDao.list(0,3);
        for (Pendingurlsdb pendingUrlList:pendingUrlLists){
            LOGGER.info(pendingUrlList.getUrl());
        }
    }
//
//    @Ignore
//    @Test
//    public void testGet() throws Exception {
//        Pendingurlsdb pendingurlsdb = pendingUrlsDBDao.get(5);
//        LOGGER.info("******* get() result"+pendingurlsdb.getUniqid());
//    }
//    @Ignore
//    @Test
//    public void testInsert() throws Exception {
//        Pendingurlsdb pendingurlsdb = new Pendingurlsdb();
//        pendingurlsdb.setAnchor("anchor");
//        pendingurlsdb.setDepth((short) 1);
//        pendingurlsdb.setDocid(2);
//        pendingurlsdb.setDomain("domain");
//        pendingurlsdb.setInstanceExecid(new Long (3));
//        pendingurlsdb.setParentDocid(3);
//        pendingurlsdb.setParentUrl("parenturl");
//        pendingurlsdb.setPath("ssh");
//        pendingurlsdb.setPid(new Long(6));
//        pendingurlsdb.setPriority((byte) 1);
//        pendingurlsdb.setSubDomain("subdomain");
//        pendingurlsdb.setTag("tag");
//        pendingurlsdb.setUniqid(new Long(4));
//        pendingurlsdb.setUrl("url3");
//        pendingUrlsDBDao.insert(pendingurlsdb);
//        LOGGER.info("********insertion done************");
//    }
//    @Ignore
//    @Test
//    public void testDelete() throws Exception {
//        pendingUrlsDBDao.delete(4);
//        LOGGER.info("******deleted********");
//    }
}