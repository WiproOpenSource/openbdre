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

import java.util.ArrayList;
import java.util.List;

//import com.sleepycat.je.Cursor;
//import com.sleepycat.je.DatabaseEntry;
//import com.sleepycat.je.Environment;
//import com.sleepycat.je.OperationStatus;
import javax.jdo.Transaction;

import com.wipro.ats.bdre.imcrawler.url.WebURL;
import com.wipro.ats.bdre.imcrawler.model.PMF;
import com.wipro.ats.bdre.imcrawler.model.PendingURLsDB;
import com.wipro.ats.bdre.imcrawler.model.WebURLsDB;
import org.datanucleus.store.rdbms.query.ForwardQueryResult;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

/**
 * @author Yasser Ganjisaffar
 */
public class WorkQueues {
    //    private final Database urlsDB;
//    private WebURLsDB webURLsDB = new WebURLsDB();
    private String dbName;
    //    private final Environment env;
    PersistenceManager manager = PMF.getInstance().getPersistenceManager();
    private final boolean resumable;

//    private final WebURLTupleBinding webURLBinding;

    protected final Object mutex = new Object();

    public WorkQueues(String dbName, boolean resumable) {
//        this.env = env;
        this.dbName = dbName;
        this.resumable = resumable;
//        DatabaseConfig dbConfig = new DatabaseConfig();
//        dbConfig.setAllowCreate(true);
//        dbConfig.setTransactional(resumable);
//        dbConfig.setDeferredWrite(!resumable);
//        urlsDB = env.openDatabase(null, dbName, dbConfig);
//        webURLBinding = new WebURLTupleBinding();
    }

//    protected Transaction beginTransaction() {
//        return resumable ? env.beginTransaction(null, null) : null;
//    }

//    protected static void commit(Transaction tnx) {
//        if (tnx != null) {
//            tnx.commit();
//        }
//    }

//    protected Cursor openCursor(Transaction txn) {
//        return urlsDB.openCursor(txn, null);
//    }

    public List<WebURL> get(int max, String database, int pid) {
        synchronized (mutex) {
            List<WebURL> results = new ArrayList<>(max);
            /*
            DatabaseEntry key = new DatabaseEntry();
            DatabaseEntry value = new DatabaseEntry();
            Transaction txn = beginTransaction();
            try (Cursor cursor = openCursor(txn)) {
                OperationStatus result = cursor.getFirst(key, value, null);
                int matches = 0;
                while ((matches < max) && (result == OperationStatus.SUCCESS)) {
                    if (value.getData().length > 0) {
                        //convert value to URL
                        results.add(webURLBinding.entryToObject(value));
                        matches++;
                    }
                    result = cursor.getNext(key, value, null);
                }
            }
            commit(txn);
            */
            //get all rows limit max
            if (database.equals("InProcessPagesDB")) {
                Query query = manager.newQuery(WebURLsDB.class);
                ForwardQueryResult data = (ForwardQueryResult) query.execute();
                if (max <= data.size()) {
                    for (int i = 0; i < max; i++) {
                        WebURLsDB info = (WebURLsDB) data.get(i);
                        if (info.getPid() == pid) {
                            WebURL webURL = new WebURL();
                            webURL.setURL(info.getUrl());
                            webURL.setAnchor(info.getAnchor());
                            webURL.setDepth(info.getDepth());
                            webURL.setDocid(info.getDocid());
                            webURL.setDomain(info.getDomain());
                            webURL.setParentDocid(info.getParentDocid());
                            webURL.setParentUrl(info.getParentUrl());
                            webURL.setPath(info.getPath());
                            webURL.setPriority(info.getPriority());
                            webURL.setSubDomain(info.getSubDomain());
                            webURL.setTag(info.getTag());
                            results.add(webURL);
                        }
                    }
                } else {
                    for (int i = 0; i < data.size(); i++) {
                        WebURLsDB info = (WebURLsDB) data.get(i);
                        if (info.getPid() == pid) {
                            WebURL webURL = new WebURL();
                            webURL.setURL(info.getUrl());
                            webURL.setAnchor(info.getAnchor());
                            webURL.setDepth(info.getDepth());
                            webURL.setDocid(info.getDocid());
                            webURL.setDomain(info.getDomain());
                            webURL.setParentDocid(info.getParentDocid());
                            webURL.setParentUrl(info.getParentUrl());
                            webURL.setPath(info.getPath());
                            webURL.setPriority(info.getPriority());
                            webURL.setSubDomain(info.getSubDomain());
                            webURL.setTag(info.getTag());
                            results.add(webURL);
                        }
                    }
                }
            } else if (database.equals("PendingURLsDB")) {
                Query query = manager.newQuery(PendingURLsDB.class);
                ForwardQueryResult data = (ForwardQueryResult) query.execute();
                if (max <= data.size()) {
                    for (int i = 0; i < max; i++) {
                        PendingURLsDB info = (PendingURLsDB) data.get(i);
                        if (info.getPid() == pid) {
                            WebURL webURL = new WebURL();
                            webURL.setURL(info.getUrl());
                            webURL.setAnchor(info.getAnchor());
                            webURL.setDepth(info.getDepth());
                            webURL.setDocid(info.getDocid());
                            webURL.setDomain(info.getDomain());
                            webURL.setParentDocid(info.getParentDocid());
                            webURL.setParentUrl(info.getParentUrl());
                            webURL.setPath(info.getPath());
                            webURL.setPriority(info.getPriority());
                            webURL.setSubDomain(info.getSubDomain());
                            webURL.setTag(info.getTag());
                            results.add(webURL);
                        }
                    }
                } else {
                    for (int i = 0; i < data.size(); i++) {
                        PendingURLsDB info = (PendingURLsDB) data.get(i);
                        if (info.getPid() == pid) {
                            WebURL webURL = new WebURL();
                            webURL.setURL(info.getUrl());
                            webURL.setAnchor(info.getAnchor());
                            webURL.setDepth(info.getDepth());
                            webURL.setDocid(info.getDocid());
                            webURL.setDomain(info.getDomain());
                            webURL.setParentDocid(info.getParentDocid());
                            webURL.setParentUrl(info.getParentUrl());
                            webURL.setPath(info.getPath());
                            webURL.setPriority(info.getPriority());
                            webURL.setSubDomain(info.getSubDomain());
                            webURL.setTag(info.getTag());
                            results.add(webURL);
                        }
                    }
                }
            }
            return results;
        }
    }

    public void delete(int count, String database) {
        synchronized (mutex) {
            //delete rows from starting in DB until count
            /*
            DatabaseEntry key = new DatabaseEntry();
            DatabaseEntry value = new DatabaseEntry();
            Transaction txn = beginTransaction();
            try (Cursor cursor = openCursor(txn)) {
                OperationStatus result = cursor.getFirst(key, value, null);
                int matches = 0;
                while ((matches < count) && (result == OperationStatus.SUCCESS)) {
                    cursor.delete();
                    matches++;
                    result = cursor.getNext(key, value, null);
                }
            }
            commit(txn);
            */
            Transaction tx = manager.currentTransaction();
            tx.begin();
            if (database.equals("InProcessPagesDB")) {
                Query query = manager.newQuery(WebURLsDB.class);
                ForwardQueryResult data = (ForwardQueryResult) query.execute();
                for (int i = 0; i < count; i++) {
                    WebURLsDB info = (WebURLsDB) data.get(i);
                    manager.deletePersistent(info);
                }
            } else if (database.equals("PendingURLsDB")) {
                Query query = manager.newQuery(PendingURLsDB.class);
                ForwardQueryResult data = (ForwardQueryResult) query.execute();
                for (int i = 0; i < count; i++) {
                    PendingURLsDB info = (PendingURLsDB) data.get(i);
                    manager.deletePersistent(info);
                }
            }
            tx.commit();
        }
    }

    /*
     * The key that is used for storing URLs determines the order
     * they are crawled. Lower key values results in earlier crawling.
     * Here our keys are 6 bytes. The first byte comes from the URL priority.
     * The second byte comes from depth of crawl at which this URL is first found.
     * The rest of the 4 bytes come from the docid of the URL. As a result,
     * URLs with lower priority numbers will be crawled earlier. If priority
     * numbers are the same, those found at lower depths will be crawled earlier.
     * If depth is also equal, those found earlier (therefore, smaller docid) will
     * be crawled earlier.
     */
//    protected static DatabaseEntry getDatabaseEntryKey(WebURL url) {
//        byte[] keyData = new byte[6];
//        keyData[0] = url.getPriority();
//        keyData[1] = ((url.getDepth() > Byte.MAX_VALUE) ? Byte.MAX_VALUE : (byte) url.getDepth());
//        Util.putIntInByteArray(url.getDocid(), keyData, 2);
//        return new DatabaseEntry(keyData);
//    }

    public void put(WebURL url, String database, int pid, long instanceExecid) {
        /*
        DatabaseEntry value = new DatabaseEntry();
        webURLBinding.objectToEntry(url, value);
        Transaction txn = beginTransaction();
        urlsDB.put(txn, getDatabaseEntryKey(url), value);
        commit(txn);*/

        Transaction tx = manager.currentTransaction();
        tx.begin();
        if (database.equals("InProcessPagesDB")) {
            if (url.getURL().length() < 2000) {
                WebURLsDB webURLsDB = new WebURLsDB();
                webURLsDB.setPid(pid);
                webURLsDB.setInstanceExecid(instanceExecid);
                webURLsDB.setUrl(url.getURL());
                webURLsDB.setAnchor(url.getAnchor());
                webURLsDB.setDepth(url.getDepth());
                webURLsDB.setDocid(url.getDocid());
                webURLsDB.setDomain(url.getDomain());
                webURLsDB.setParentDocid(url.getParentDocid());
                webURLsDB.setParentUrl(url.getParentUrl());
                webURLsDB.setPath(url.getPath());
                webURLsDB.setPriority(url.getPriority());
                webURLsDB.setSubDomain(url.getSubDomain());
                webURLsDB.setTag(url.getTag());
                manager.makePersistent(webURLsDB);
            }
        } else if (database.equals("PendingURLsDB")) {
            if (url.getURL().length() < 2000) {
                PendingURLsDB webURLsDB = new PendingURLsDB();
                webURLsDB.setPid(pid);
                webURLsDB.setInstanceExecid(instanceExecid);
                webURLsDB.setUrl(url.getURL());
                webURLsDB.setAnchor(url.getAnchor());
                webURLsDB.setDepth(url.getDepth());
                webURLsDB.setDocid(url.getDocid());
                webURLsDB.setDomain(url.getDomain());
                webURLsDB.setParentDocid(url.getParentDocid());
                webURLsDB.setParentUrl(url.getParentUrl());
                webURLsDB.setPath(url.getPath());
                webURLsDB.setPriority(url.getPriority());
                webURLsDB.setSubDomain(url.getSubDomain());
                webURLsDB.setTag(url.getTag());
                manager.makePersistent(webURLsDB);
            }
        }
        tx.commit();
    }

    public long getLength(String database) {
        if (database.equals("InProcessPagesDB")) {
            Query query = manager.newQuery(WebURLsDB.class);
            ForwardQueryResult data = (ForwardQueryResult) query.execute();
            return (long) data.size();
        } else if (database.equals("PendingURLsDB")) {
            Query query = manager.newQuery(PendingURLsDB.class);
            ForwardQueryResult data = (ForwardQueryResult) query.execute();
            return (long) data.size();
        } else {
            return 0;
        }
//        return urlsDB.count();
    }

    public void close() {
//        urlsDB.close();
//        manager.close();
    }
}