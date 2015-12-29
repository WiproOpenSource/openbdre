package com.wipro.ats.bdre.imcrawler.model;

import com.wipro.ats.bdre.md.dao.jpa.Statisticsdb;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

public class StatisticsDBDaoTest {
    private static final Logger LOGGER = Logger.getLogger(StatisticsDBDaoTest.class);

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    @Autowired
    StatisticsDBDao statisticsDBDao;

    Statisticsdb statisticsdb;
    @Ignore
    @Test
    public void testList() throws Exception {
        List<Statisticsdb> statisticsdbLists = statisticsDBDao.list(0,2);
        for (Statisticsdb statisticsdblist: statisticsdbLists) {
            LOGGER.info("******"+statisticsdblist.getUniqid());
        }
    }
    @Ignore
    @Test
    public void testGet() throws Exception {
        statisticsdb = statisticsDBDao.get(2);
        LOGGER.info("*******get() "+statisticsdb.getUniqid());
    }
    @Ignore
    @Test
    public void testInsert() throws Exception {
        statisticsdb = new Statisticsdb();
        statisticsdb.setName("alpha");
        statisticsdb.setUniqid(3L);
        statisticsdb.setValue(5L);
        statisticsDBDao.insert(statisticsdb);
        LOGGER.info("********** data inserted");
    }
    @Ignore
    @Test
    public void testDelete() throws Exception {
        statisticsDBDao.delete(1);
        LOGGER.info("*********deleted");
    }
}