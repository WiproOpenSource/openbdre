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

import com.wipro.ats.bdre.imcrawler.crawler.Configurable;
import com.wipro.ats.bdre.imcrawler.crawler.CrawlConfig;
import com.wipro.ats.bdre.md.dao.jpa.Docidsdb;
import com.wipro.ats.bdre.imcrawler.model.DocidsDBDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import java.util.List;


/**
 * @author Yasser Ganjisaffar modified by AS294216
 */

public class DocIDServer extends Configurable {
    private static final Logger logger = LoggerFactory.getLogger(DocIDServer.class);
    //PersistenceManager manager = PMF.getInstance().getPersistenceManager();
    private Docidsdb docIDsDB = new Docidsdb();
    private static final String DATABASE_NAME = "DocIDs";
    private final Object mutex = new Object();
    private int lastDocID;

    public DocIDServer(CrawlConfig config) {
        super(config);
        /*Hibernate Auto-wire*/
        AutowireCapableBeanFactory acbFactory = getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);

        if (config.isResumableCrawling()) {
            int docCount = getDocCount();
            if (docCount > 0) {
                logger.info("Loaded {} URLs that had been detected in previous crawl.", docCount);
                lastDocID = getLastDocID();
            }
        }
    }

    @Autowired
    DocidsDBDao docidsDBDao;

    /**
     * Returns the docid of an already seen url.
     *
     * @param url the URL for which the docid is returned.
     * @return the docid of the url if it is seen before. Otherwise -1 is returned.
     */
    public int getDocId(String url) {
        synchronized (mutex) {
            //search in DB for that url
            //Query query = manager.newQuery(DocIDsDB.class);
            try {
                //ForwardQueryResult data = (ForwardQueryResult) query.execute();
                Long totalSize = docidsDBDao.totalRecordCount();
//                for (int i = 1; i <= totalSize; i++) {
                List<Docidsdb> docidsdbList = docidsDBDao.list(0, totalSize.intValue());
                for (Docidsdb info:docidsdbList) {
//                    Docidsdb info = docidsDBDao.get(i);
                    if (info.getUrl().equals(url)) {
                        return info.getDocid().intValue();
                    }
                }
//                }
            } catch (ClassCastException e) {
                logger.debug("---ClassCastException---");
                return -1;
            }
            return -1;
        }
    }

    public int getNewDocID(String url) {
        synchronized (mutex) {

            try {
                // Make sure that we have not already assigned a docid for this URL
                int docID = getDocId(url);
                if (docID > 0) {
                    return docID;
                }
                /*  Transaction tx = manager.currentTransaction();
                tx.begin();  */
                if (url.length() < 3000) {
                    /*  Query q = manager.newQuery("javax.jdo.query.SQL", "INSERT INTO DOCIDSDB (URL) VALUES (?)");
                        Long results = (Long) q.execute(url);  */
                    Docidsdb docidsdb = new Docidsdb();
                    docidsdb.setUrl(url);
                    docidsDBDao.insert(docidsdb);
                } else {
                    /*  Query q = manager.newQuery("javax.jdo.query.SQL", "INSERT INTO DOCIDSDB (URL) VALUES (?)");
                        Long results = (Long) q.execute("--long-url--");  */
                    Docidsdb docidsdb = new Docidsdb();
                    docidsdb.setUrl("--long-url--");
                    docidsDBDao.insert(docidsdb);
                    logger.info("Long URL (>3000 characters) can't enter");
                }
                //tx.commit();
                lastDocID = getLastDocID();
                return lastDocID;
            } catch (Exception e) {
                logger.error("Exception thrown while getting new DocID", e);
                return -1;
            }
        }
    }

    public void addUrlAndDocId(String url, int docId) throws Exception {
        synchronized (mutex) {
            if (docId <= lastDocID) {
                throw new Exception("Requested doc id: " + docId + " is not larger than: " + lastDocID);
            }

            // Make sure that we have not already assigned a docid for this URL
            int prevDocid = getDocId(url);
            if (prevDocid > 0) {
                if (prevDocid == docId) {
                    return;
                }
                throw new Exception("Doc id: " + prevDocid + " is already assigned to URL: " + url);
            }
            /*  Transaction tx = manager.currentTransaction();
                tx.begin();
                Query q = manager.newQuery("javax.jdo.query.SQL", "INSERT INTO DOCIDSDB (URL) VALUES (?)");
                Long results = (Long) q.execute(url);
                tx.commit();  */
            Docidsdb docidsdb = new Docidsdb();
            docidsdb.setUrl(url);
            docidsDBDao.insert(docidsdb);
            lastDocID = getLastDocID();
        }
    }

    public boolean isSeenBefore(String url) {
        return getDocId(url) != -1;
    }

    public final int getDocCount() {
        //get rows in DB
        /*  Transaction tx = manager.currentTransaction();
            tx.begin();
            Query query = manager.newQuery(DocIDsDB.class);
            ForwardQueryResult data = (ForwardQueryResult) query.execute();
            tx.commit();  */
        Long totalSize = docidsDBDao.totalRecordCount();
        return totalSize.intValue();
    }

    public final int getLastDocID() {
        //get lastDocid

        /*  Transaction tx = manager.currentTransaction();
            tx.begin();
            Query query = manager.newQuery(DocIDsDB.class);
            ForwardQueryResult data = (ForwardQueryResult) query.execute();  */
        Long totalSize = docidsDBDao.totalRecordCount();
        Integer intTotalSize = new Integer(totalSize.intValue());
        int tobereturnedDocId;
        if (totalSize > 0) {
            tobereturnedDocId = docidsDBDao.getLastElement().getDocid().intValue();
        } else {
            tobereturnedDocId = -1;
        }
        //tx.commit();
        return tobereturnedDocId;
    }
    @Deprecated
    public void close() {
    //deprecated
    }
}