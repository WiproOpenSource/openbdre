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


import com.wipro.ats.bdre.imcrawler.model.PMF;
import com.wipro.ats.bdre.imcrawler.model.WebURLsDB;
import com.wipro.ats.bdre.imcrawler.url.WebURL;
import org.datanucleus.store.rdbms.query.ForwardQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.Byte;

import javax.jdo.Transaction;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

/**
 * This class maintains the list of pages which are
 * assigned to crawlers but are not yet processed.
 * It is used for resuming a previous crawl.
 *
 * @author Yasser Ganjisaffar
 */
public class InProcessPagesDB extends WorkQueues {
    private static final Logger logger = LoggerFactory.getLogger(InProcessPagesDB.class);

    private static final String DATABASE_NAME = "InProcessPagesDB";
    public String database;                                 //pass the DB name to its object
    PersistenceManager manager = PMF.getInstance().getPersistenceManager();

    public InProcessPagesDB() {
        super(DATABASE_NAME, true);
        this.database = DATABASE_NAME;
        long docCount = getLength(DATABASE_NAME);
        if (docCount > 0) {
            logger.info("Loaded {} URLs that have been in process in the previous crawl.", docCount);
        }
    }

    public boolean removeURL(WebURL webUrl, int pid) {
        synchronized (mutex) {
      /*
      DatabaseEntry key = getDatabaseEntryKey(webUrl);
      DatabaseEntry value = new DatabaseEntry();
      Transaction txn = beginTransaction();
      try (Cursor cursor = openCursor(txn)) {
        OperationStatus result = cursor.getSearchKey(key, value, null);

        if (result == OperationStatus.SUCCESS) {
          result = cursor.delete();
          if (result == OperationStatus.SUCCESS) {
            return true;
          }
        }
      } finally {
        commit(txn);
      }
      */


            Transaction tx = manager.currentTransaction();
            tx.begin();
            Query query = manager.newQuery(WebURLsDB.class);
            ForwardQueryResult data = (ForwardQueryResult) query.execute();
            for (int i = 0; i < data.size(); i++) {
                WebURLsDB info = (WebURLsDB) data.get(i);
                Byte infobyte = new Byte(info.getPriority());
                Byte weburlbyte = new Byte(webUrl.getPriority());
                if ((webUrl.getDocid() == info.getDocid()) && (pid == info.getPid())) {
                    if ((webUrl.getDepth() == info.getDepth()) && (infobyte.compareTo(weburlbyte)) == 0) {
                        manager.deletePersistent(info);
                        tx.commit();

                        return true;
                    }
                }
            }
            tx.commit();


        }
        return false;
    }
}