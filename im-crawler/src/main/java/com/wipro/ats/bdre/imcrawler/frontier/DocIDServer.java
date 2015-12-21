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
import com.wipro.ats.bdre.imcrawler.model.DocIDsDB;
import com.wipro.ats.bdre.imcrawler.model.PMF;
import org.datanucleus.store.rdbms.query.ForwardQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;
import java.util.List;

/**
 * @author Yasser Ganjisaffar
 */

public class DocIDServer extends Configurable {
    private static final Logger logger = LoggerFactory.getLogger(DocIDServer.class);
    PersistenceManager manager = PMF.getInstance().getPersistenceManager();

    //  private final Database docIDsDB;
    private DocIDsDB docIDsDB = new DocIDsDB();
    private static final String DATABASE_NAME = "DocIDs";

    private final Object mutex = new Object();

    private int lastDocID;

    public DocIDServer(CrawlConfig config) {
        super(config);
//    DatabaseConfig dbConfig = new DatabaseConfig();
//    dbConfig.setAllowCreate(true);
//    dbConfig.setTransactional(config.isResumableCrawling());
//    dbConfig.setDeferredWrite(!config.isResumableCrawling());
//    lastDocID = 0;
//    docIDsDB = env.openDatabase(null, DATABASE_NAME, dbConfig);
        if (config.isResumableCrawling()) {
            int docCount = getDocCount();
            if (docCount > 0) {
                logger.info("Loaded {} URLs that had been detected in previous crawl.", docCount);
                lastDocID = getLastDocID();
            }
        }
    }

    /**
     * Returns the docid of an already seen url.
     *
     * @param url the URL for which the docid is returned.
     * @return the docid of the url if it is seen before. Otherwise -1 is returned.
     */
    public int getDocId(String url) {
        synchronized (mutex) {
            //search in DB for that url
      /*
      OperationStatus result = null;
      DatabaseEntry value = new DatabaseEntry();
      try {
        DatabaseEntry key = new DatabaseEntry(url.getBytes());

        result = docIDsDB.get(null, key, value, null);

      } catch (Exception e) {
        logger.error("Exception thrown while getting DocID", e);
        return -1;
      }

      if ((result == OperationStatus.SUCCESS) && (value.getData().length > 0)) {
        return Util.byteArray2Int(value.getData());
      }
*/

            Query query = manager.newQuery(DocIDsDB.class);
            try {
                ForwardQueryResult data = (ForwardQueryResult) query.execute();
                for (int i = 0; i < data.size(); i++) {
                    DocIDsDB info = (DocIDsDB) data.get(i);
                    if (info.getUrl().equals(url)) {
                        return info.getDocId();
                    }
                }
            } catch (ClassCastException e) {
                logger.debug("---ClassCastException---");
                return -1;
            }
            //     Object data = new Object();

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

//        docIDsDB.put(null, new DatabaseEntry(url.getBytes()), new DatabaseEntry(Util.int2ByteArray(lastDocID)));

                Transaction tx = manager.currentTransaction();
                tx.begin();
//          logger.debug("lastDocId is:"+lastDocID);
                //auto_incrementing the docid field in DB
//        ++lastDocID;
                if (url.length() < 3000) {
//          docIDsDB.setDocId(lastDocID);
                    Query q = manager.newQuery("javax.jdo.query.SQL", "INSERT INTO DOCIDSDB (URL) VALUES (?)");
                    Long results = (Long) q.execute(url);
                } else {
//          docIDsDB.setDocId(lastDocID);
                    Query q = manager.newQuery("javax.jdo.query.SQL", "INSERT INTO DOCIDSDB (URL) VALUES (?)");
                    Long results = (Long) q.execute("--long-url--");

                }
//          manager.makePersistent(docIDsDB);
                tx.commit();

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

//      docIDsDB.put(null, new DatabaseEntry(url.getBytes()), new DatabaseEntry(Util.int2ByteArray(docId)));
            Transaction tx = manager.currentTransaction();
            tx.begin();
//      docIDsDB.setDocId(docId);
            Query q = manager.newQuery("javax.jdo.query.SQL", "INSERT INTO DOCIDSDB (URL) VALUES (?)");
            Long results = (Long) q.execute(url);
//      docIDsDB.setUrl(url);
//      manager.makePersistent(docIDsDB);
            tx.commit();

            lastDocID = getLastDocID();
        }
    }

    public boolean isSeenBefore(String url) {
        return getDocId(url) != -1;
    }

    public final int getDocCount() {
        //get rows in DB
    /*try {
      return (int) docIDsDB.count();
    } catch (DatabaseException e) {
      logger.error("Exception thrown while getting DOC Count", e);
      return -1;
    }*/

        Transaction tx = manager.currentTransaction();
        tx.begin();
        Query query = manager.newQuery(DocIDsDB.class);
        ForwardQueryResult data = (ForwardQueryResult) query.execute();
        tx.commit();

        return data.size();

    }

    public final int getLastDocID() {
        //get lastDocid

        Transaction tx = manager.currentTransaction();
        tx.begin();
        Query query = manager.newQuery(DocIDsDB.class);
        ForwardQueryResult data = (ForwardQueryResult) query.execute();
        int tobereturnedDocId;
        if (data.size() > 0) {
            DocIDsDB info = (DocIDsDB) data.get(data.size() - 1);
            tobereturnedDocId = info.getDocId();
        } else {
            tobereturnedDocId = -1;
        }
        tx.commit();
        return tobereturnedDocId;
    }

    public void close() {
//    try {
//      docIDsDB.close();
//    } catch (DatabaseException e) {
//      logger.error("Exception thrown while closing DocIDServer", e);
//    }
//    manager.close();
    }
}