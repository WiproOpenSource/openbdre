/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wipro.ats.bdre.imcrawler.frontier;

import java.util.HashMap;
import java.util.Map;

import com.wipro.ats.bdre.imcrawler.crawler.CrawlConfig;
import com.wipro.ats.bdre.imcrawler.crawler.Configurable;
import com.wipro.ats.bdre.imcrawler.jpa.Statisticsdb;
import com.wipro.ats.bdre.imcrawler.model.StatisticsDBDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Yasser Ganjisaffar modified by AS294216
 */
public class Counters extends Configurable {
    private static final Logger logger = LoggerFactory.getLogger(Counters.class);
    @Autowired
    StatisticsDBDao statisticsDBDao;
    //PersistenceManager manager = PMF.getInstance().getPersistenceManager();

    public static class ReservedCounterNames {
        public static final String SCHEDULED_PAGES = "Scheduled-Pages";
        public static final String PROCESSED_PAGES = "Processed-Pages";
    }

    private static final String DATABASE_NAME = "Statistics";
    //protected StatisticsDB statisticsDB1 = new StatisticsDB();
    protected final Object mutex = new Object();
    protected Map<String, Long> counterValues;
    public Counters(CrawlConfig config) {
        super(config);
        /*Hibernate Auto-wire*/
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);

        this.counterValues = new HashMap<>();
    /*
     * When crawling is set to be resumable, we have to keep the statistics
     * in a transactional database to make sure they are not lost if crawler
     * is crashed or terminated unexpectedly.
     */
        if (config.isResumableCrawling()) {
            /*  Transaction tx = manager.currentTransaction();
                tx.begin();
                Query query = manager.newQuery(StatisticsDB.class);
                ForwardQueryResult data = (ForwardQueryResult) query.execute();  */
            Long totalSize = statisticsDBDao.totalRecordCount();
            for (int i = 1; i <= totalSize; i++) {
                Statisticsdb info = statisticsDBDao.get(i);
                counterValues.put(info.getName(), info.getValue());
            }
            //tx.commit();
        }
    }

    public long getValue(String name) {
        synchronized (mutex) {
            Long value = counterValues.get(name);
            if (value == null) {
                return 0;
            }
            return value;
        }
    }

    public void setValue(String name, long value) {
        synchronized (mutex) {
            /*  Transaction tx = manager.currentTransaction();
                tx.begin();
            statisticsDB1.setValue(value);
            statisticsDB1.setName(name);
            manager.makePersistent(statisticsDB1);
            tx.commit();  */
            Statisticsdb statisticsdb = new Statisticsdb();
            statisticsdb.setValue(value);
            statisticsdb.setName(name);
            statisticsDBDao.insert(statisticsdb);
        }
    }

    public void increment(String name) {
        increment(name, 1);
    }

    public void increment(String name, long addition) {
        synchronized (mutex) {
            long prevValue = getValue(name);
            setValue(name, prevValue + addition);
        }
    }
    @Deprecated
    public void close() {

    }
}