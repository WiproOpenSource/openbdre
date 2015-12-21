package com.wipro.ats.bdre.imcrawler.model;

import com.wipro.ats.bdre.imcrawler.jpa.Docidsdb;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

public class DocidsDBDaoTest {
    private static final Logger LOGGER = Logger.getLogger(DocidsDBDaoTest.class);

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    @Autowired
    DocidsDBDao docidsDBDao;

    Docidsdb docidsdb;
    @Ignore
    @Test
    public void testList() throws Exception {
        List<Docidsdb> docidLists = docidsDBDao.list(0,4);
        for (Docidsdb docidList: docidLists){
            LOGGER.info(docidList.getUrl());
        }
    }
    @Ignore
    @Test
    public void testTotalRecordCount() throws Exception {

    }
    @Ignore
    @Test
    public void testInsert() throws Exception {
        Docidsdb docidsdb = new Docidsdb();
        docidsdb.setUrl("test1");
        docidsDBDao.insert(docidsdb);
        LOGGER.info("url has been set");
    }
    @Ignore
    @Test
    public void testGet() throws Exception {
        docidsdb = docidsDBDao.get(1);
        LOGGER.info(docidsdb.getUrl());
    }
    @Ignore
    @Test
    public void testUpdate() throws Exception {

    }
    @Ignore
    @Test
    public void testDelete() throws Exception {
        docidsDBDao.delete(1);
        LOGGER.info("*******deleted********");
    }
}