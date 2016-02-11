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

import com.wipro.ats.bdre.md.dao.jpa.Pendingurlsdb;
import com.wipro.ats.bdre.md.dao.jpa.Weburlsdb;
import com.wipro.ats.bdre.imcrawler.model.PendingUrlsDBDao;
import com.wipro.ats.bdre.imcrawler.model.WebUrlsDBDao;
import com.wipro.ats.bdre.imcrawler.url.WebURL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Yasser Ganjisaffar modified by AS294216
 */
public class WorkQueues {
    private String dbName;
    //PersistenceManager manager = PMF.getInstance().getPersistenceManager();
    private final boolean resumable;
    protected final Object mutex = new Object();

    public WorkQueues(String dbName, boolean resumable) {
        this.dbName = dbName;
        this.resumable = resumable;
        /*Hibernate autowire*/
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    @Autowired
    WebUrlsDBDao webUrlsDBDao;
    @Autowired
    PendingUrlsDBDao pendingUrlsDBDao;

    public List<WebURL> get(int max, String database, int pid) {
        synchronized (mutex) {
            List<WebURL> results = new ArrayList<>(max);

            //get all rows limit max
            if (database.equals("InProcessPagesDB")) {
                //Query query = manager.newQuery(WebURLsDB.class);
                //ForwardQueryResult data = (ForwardQueryResult) query.execute();
                Long totalSize = webUrlsDBDao.totalRecordCount();
                if (max <= totalSize) {
//                    for (int i = 1; i <= max; i++) {
                    List<Weburlsdb> weburlsdbList = webUrlsDBDao.list(0,max);
//                        Weburlsdb info = webUrlsDBDao.get(i);
                    for(Weburlsdb info:weburlsdbList) {
                        if (info.getPid() == pid) {
                            WebURL webURL = new WebURL();
                            webURL.setURL(info.getUrl());
                            webURL.setAnchor(info.getAnchor());
                            webURL.setDepth(info.getDepth());
                            webURL.setDocid(info.getDocid());
                            webURL.setDomain(info.getDomain());
                            webURL.setParentDocid(info.getParentdocid());
                            webURL.setParentUrl(info.getParenturl());
                            webURL.setPath(info.getPath());
                            webURL.setPriority(info.getPriority().byteValue());
                            webURL.setSubDomain(info.getSubdomain());
                            webURL.setTag(info.getTag());
                            results.add(webURL);
                        }
                    }
//                    }
                } else {
//                    for (int i = 1; i <= totalSize; i++) {
                    List<Weburlsdb> weburlsdbList = webUrlsDBDao.list(0,totalSize.intValue());
//                        Weburlsdb info = webUrlsDBDao.get(i);
                    for(Weburlsdb info:weburlsdbList) {
                        if (info.getPid() == pid) {
                            WebURL webURL = new WebURL();
                            webURL.setURL(info.getUrl());
                            webURL.setAnchor(info.getAnchor());
                            webURL.setDepth(info.getDepth());
                            webURL.setDocid(info.getDocid());
                            webURL.setDomain(info.getDomain());
                            webURL.setParentDocid(info.getParentdocid());
                            webURL.setParentUrl(info.getParenturl());
                            webURL.setPath(info.getPath());
                            webURL.setPriority(info.getPriority().byteValue());
                            webURL.setSubDomain(info.getSubdomain());
                            webURL.setTag(info.getTag());
                            results.add(webURL);
                        }
                    }
//                    }
                }
            } else if (database.equals("PendingURLsDB")) {
                //Query query = manager.newQuery(PendingURLsDB.class);
                //ForwardQueryResult data = (ForwardQueryResult) query.execute();
                Long totalSize = pendingUrlsDBDao.totalRecordCount();
                if (max <= totalSize) {
//                    for (int i = 1; i <= max; i++) {
                    List<Pendingurlsdb> pendingurlsdbList = pendingUrlsDBDao.list(0,max);
//                        Pendingurlsdb info = pendingUrlsDBDao.get(i);
                    for (Pendingurlsdb info:pendingurlsdbList) {
                        if (info.getPid() == pid) {
                            WebURL webURL = new WebURL();
                            webURL.setURL(info.getUrl());
                            webURL.setAnchor(info.getAnchor());
                            webURL.setDepth(info.getDepth());
                            webURL.setDocid(info.getDocid());
                            webURL.setDomain(info.getDomain());
                            webURL.setParentDocid(info.getParentdocid());
                            webURL.setParentUrl(info.getParenturl());
                            webURL.setPath(info.getPath());
                            webURL.setPriority(info.getPriority().byteValue());
                            webURL.setSubDomain(info.getSubdomain());
                            webURL.setTag(info.getTag());
                            results.add(webURL);
                        }
                    }
//                    }
                } else {
//                    for (int i = 1; i <= totalSize; i++) {
                    List<Pendingurlsdb> pendingurlsdbList = pendingUrlsDBDao.list(0,totalSize.intValue());
//                        Pendingurlsdb info = pendingUrlsDBDao.get(i);
                    for (Pendingurlsdb info:pendingurlsdbList) {
                        if (info.getPid() == pid) {
                            WebURL webURL = new WebURL();
                            webURL.setURL(info.getUrl());
                            webURL.setAnchor(info.getAnchor());
                            webURL.setDepth(info.getDepth());
                            webURL.setDocid(info.getDocid());
                            webURL.setDomain(info.getDomain());
                            webURL.setParentDocid(info.getParentdocid());
                            webURL.setParentUrl(info.getParenturl());
                            webURL.setPath(info.getPath());
                            webURL.setPriority(info.getPriority().byteValue());
                            webURL.setSubDomain(info.getSubdomain());
                            webURL.setTag(info.getTag());
                            results.add(webURL);
                        }
                    }
//                    }
                }
            }
            return results;
        }
    }

    public void delete(int count, String database) {
        synchronized (mutex) {
            //delete rows from starting in DB until count
            /*  Transaction tx = manager.currentTransaction();
                tx.begin();   */
            if (database.equals("InProcessPagesDB")) {
                /*  Query query = manager.newQuery(WebURLsDB.class);
                    ForwardQueryResult data = (ForwardQueryResult) query.execute();  */
                List<Weburlsdb> weburlsdbList = webUrlsDBDao.list(0,count);
                for (Weburlsdb weburlsdb:weburlsdbList) {
//                for (int i = 1; i <= count; i++) {
                    /* WebURLsDB info = (WebURLsDB) data.get(i);
                       manager.deletePersistent(info);  */
                    webUrlsDBDao.delete(weburlsdb.getUniqid().intValue());
                }
//                }
            } else if (database.equals("PendingURLsDB")) {
                /*  Query query = manager.newQuery(PendingURLsDB.class);
                    ForwardQueryResult data = (ForwardQueryResult) query.execute();  */
                List<Pendingurlsdb> pendingurlsdbList = pendingUrlsDBDao.list(0,count);
                for (Pendingurlsdb pendingurlsdb:pendingurlsdbList) {
//                for (int i = 1; i <= count; i++) {
                    /*  PendingURLsDB info = (PendingURLsDB) data.get(i);
                        manager.deletePersistent(info);  */
                    pendingUrlsDBDao.delete(pendingurlsdb.getUniqid().intValue());
                }
//                }
            }
            //tx.commit();
        }
    }

    public void put(WebURL url, String database, int pid, long instanceExecid) {
        /*  Transaction tx = manager.currentTransaction();
            tx.begin();  */
        if (database.equals("InProcessPagesDB")) {
            if (url.getURL().length() < 2000) {
                Weburlsdb webURLsDB = new Weburlsdb();
                long lpid = (long) pid;
                webURLsDB.setPid(lpid);
                webURLsDB.setInstanceexecid(instanceExecid);
                webURLsDB.setUrl(url.getURL());
                webURLsDB.setAnchor(url.getAnchor());
                webURLsDB.setDepth(url.getDepth());
                webURLsDB.setDocid(url.getDocid());
                webURLsDB.setDomain(url.getDomain());
                webURLsDB.setParentdocid(url.getParentDocid());
                webURLsDB.setParenturl(url.getParentUrl());
                webURLsDB.setPath(url.getPath());
                webURLsDB.setPriority(new Integer(url.getPriority()));
                webURLsDB.setSubdomain(url.getSubDomain());
                webURLsDB.setTag(url.getTag());
                webUrlsDBDao.insert(webURLsDB);
                //manager.makePersistent(webURLsDB);
            }
        } else if (database.equals("PendingURLsDB")) {
            if (url.getURL().length() < 2000) {
                Pendingurlsdb pendingurlsdb = new Pendingurlsdb();
                long lpid = (long) pid;
                pendingurlsdb.setPid(lpid);
                pendingurlsdb.setInstanceexecid(instanceExecid);
                pendingurlsdb.setUrl(url.getURL());
                pendingurlsdb.setAnchor(url.getAnchor());
                pendingurlsdb.setDepth(url.getDepth());
                pendingurlsdb.setDocid(url.getDocid());
                pendingurlsdb.setDomain(url.getDomain());
                pendingurlsdb.setParentdocid(url.getParentDocid());
                pendingurlsdb.setParenturl(url.getParentUrl());
                pendingurlsdb.setPath(url.getPath());
                pendingurlsdb.setPriority(new Integer(url.getPriority()));
                pendingurlsdb.setSubdomain(url.getSubDomain());
                pendingurlsdb.setTag(url.getTag());
                pendingUrlsDBDao.insert(pendingurlsdb);
                //manager.makePersistent(pendingURLsDB);
            }
        }
        //tx.commit();
    }

    public long getLength(String database) {
        if (database.equals("InProcessPagesDB")) {
            /*  Query query = manager.newQuery(WebURLsDB.class);
                ForwardQueryResult data = (ForwardQueryResult) query.execute();  */
            return webUrlsDBDao.totalRecordCount();
        } else if (database.equals("PendingURLsDB")) {
            /*  Query query = manager.newQuery(PendingURLsDB.class);
                ForwardQueryResult data = (ForwardQueryResult) query.execute();  */
            return pendingUrlsDBDao.totalRecordCount();
        } else {
            return 0;
        }
    }

    public void close() {
//        urlsDB.close();
//        manager.close();
    }
}