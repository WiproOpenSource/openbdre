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


import com.wipro.ats.bdre.imcrawler.jpa.Weburlsdb;
import com.wipro.ats.bdre.imcrawler.model.WebUrlsDBDao;
import com.wipro.ats.bdre.imcrawler.url.WebURL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;


/**
 * This class maintains the list of pages which are
 * assigned to crawlers but are not yet processed.
 * It is used for resuming a previous crawl.
 *
 * @author Yasser Ganjisaffar modified by AS294216
 */
public class InProcessPagesDB extends WorkQueues {
    private static final Logger logger = LoggerFactory.getLogger(InProcessPagesDB.class);

    private static final String DATABASE_NAME = "InProcessPagesDB";
    public String database;                                 //pass the DB name to its object
    //PersistenceManager manager = PMF.getInstance().getPersistenceManager();

    public InProcessPagesDB() {
        super(DATABASE_NAME, true);
        /*Hibernate Auto-wire*/
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);

        this.database = DATABASE_NAME;
        long docCount = getLength(DATABASE_NAME);
        if (docCount > 0) {
            logger.info("Loaded {} URLs that have been in process in the previous crawl.", docCount);
        }
    }

    @Autowired
    WebUrlsDBDao webUrlsDBDao;

    public boolean removeURL(WebURL webUrl, int pid) {
        synchronized (mutex) {
            //Transaction tx = manager.currentTransaction();
            //tx.begin();
            //Query query = manager.newQuery(WebURLsDB.class);
            //ForwardQueryResult data = (ForwardQueryResult) query.execute();
            Long totalSize = webUrlsDBDao.totalRecordCount();
            List<Weburlsdb> weburlsdbList = webUrlsDBDao.list(0,totalSize.intValue());
//            for (int i = 1; i <= totalSize; i++) {
//                Weburlsdb info = (Weburlsdb) webUrlsDBDao.get(i);
            for (Weburlsdb info:weburlsdbList) {
                Byte infobyte = new Byte(info.getPriority().byteValue());
                Byte weburlbyte = new Byte(webUrl.getPriority());
                if ((webUrl.getDocid() == info.getDocid()) && (pid == info.getPid())) {
                    if ((webUrl.getDepth() == info.getDepth()) && (infobyte.compareTo(weburlbyte)) == 0) {
                        webUrlsDBDao.delete(info.getUniqid().intValue());
                        //manager.deletePersistent(info);
                        //tx.commit();
                        return true;
                    }
                }
            }
//            }
            //tx.commit();
        }
        return false;
    }
}