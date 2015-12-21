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
import com.wipro.ats.bdre.imcrawler.model.StatisticsDB;
import com.wipro.ats.bdre.imcrawler.model.PMF;
import org.datanucleus.store.rdbms.query.ForwardQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jdo.Transaction;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

/**
 * @author Yasser Ganjisaffar
 */
public class Counters extends Configurable {
    private static final Logger logger = LoggerFactory.getLogger(Counters.class);
    PersistenceManager manager = PMF.getInstance().getPersistenceManager();

    public static class ReservedCounterNames {
        public static final String SCHEDULED_PAGES = "Scheduled-Pages";
        public static final String PROCESSED_PAGES = "Processed-Pages";
    }

    private static final String DATABASE_NAME = "Statistics";
    //  protected Database statisticsDB = null;
    protected StatisticsDB statisticsDB1 = new StatisticsDB();
//  protected Environment env;

    protected final Object mutex = new Object();

    protected Map<String, Long> counterValues;

    public Counters(CrawlConfig config) {
        super(config);

//    this.env = env;
        this.counterValues = new HashMap<>();

    /*
     * When crawling is set to be resumable, we have to keep the statistics
     * in a transactional database to make sure they are not lost if crawler
     * is crashed or terminated unexpectedly.
     */
        if (config.isResumableCrawling()) {
      /*
//      DatabaseConfig dbConfig = new DatabaseConfig();
//      dbConfig.setAllowCreate(true);
//      dbConfig.setTransactional(true);
//      dbConfig.setDeferredWrite(false);
      statisticsDB = env.openDatabase(null, DATABASE_NAME, dbConfig);

      OperationStatus result;
      DatabaseEntry key = new DatabaseEntry();
      DatabaseEntry value = new DatabaseEntry();
      Transaction tnx = env.beginTransaction(null, null);
      Cursor cursor = statisticsDB.openCursor(tnx, null);
      //Moves the cursor to the first key/data pair of the database, and returns that pair.
      result = cursor.getFirst(key, value, null);
    //put name and value in counterValues for all data in DB
      while (result == OperationStatus.SUCCESS) {
        if (value.getData().length > 0) {
          String name = new String(key.getData());
          long counterValue = Util.byteArray2Long(value.getData());
          counterValues.put(name, counterValue);
        }
        //Moves the cursor to the next key/data pair and returns that pair.
        result = cursor.getNext(key, value, null);
      }
      cursor.close();
      tnx.commit();
      */

            Transaction tx = manager.currentTransaction();
            tx.begin();
            Query query = manager.newQuery(StatisticsDB.class);
            ForwardQueryResult data = (ForwardQueryResult) query.execute();
            for (int i = 0; i < data.size(); i++) {
                StatisticsDB info = (StatisticsDB) data.get(i);
                counterValues.put(info.getName(), info.getValue());
            }
            tx.commit();

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
    /*  try {
        counterValues.put(name, value);
        if (statisticsDB != null) {
          Transaction txn = env.beginTransaction(null, null);
          statisticsDB.put(txn, new DatabaseEntry(name.getBytes()), new DatabaseEntry(Util.long2ByteArray(value)));
        }
      } catch (Exception e) {
        logger.error("Exception setting value", e);
      }*/

            Transaction tx = manager.currentTransaction();
            tx.begin();
            statisticsDB1.setValue(value);
            statisticsDB1.setName(name);
            manager.makePersistent(statisticsDB1);
            tx.commit();

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

    public void close() {
//    try {
//      if (statisticsDB != null) {
//        statisticsDB.close();
//      }
//    } catch (DatabaseException e) {
//      logger.error("Exception thrown while trying to close statisticsDB", e);
//    }
//    manager.close();
    }
}