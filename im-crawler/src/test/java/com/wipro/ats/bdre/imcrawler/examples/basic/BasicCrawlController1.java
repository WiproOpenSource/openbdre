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

package com.wipro.ats.bdre.imcrawler.examples.basic;

import com.wipro.ats.bdre.BaseStructure;
import com.wipro.ats.bdre.imcrawler.crawler.CrawlConfig;
import com.wipro.ats.bdre.imcrawler.crawler.CrawlController;
import com.wipro.ats.bdre.imcrawler.fetcher.PageFetcher;
import com.wipro.ats.bdre.imcrawler.robotstxt.RobotstxtConfig;
import com.wipro.ats.bdre.imcrawler.robotstxt.RobotstxtServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Yasser Ganjisaffar
 */
public class BasicCrawlController1 extends BaseStructure implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(BasicCrawlController1.class);

    String rootFolder;
    String numThread;
    int pid;
    int instanceExecid;
    String cmdvar;

    public BasicCrawlController1(String rootFolder, String numThread, int pid, int instanceExecid, String cmdvar) {
        this.rootFolder = rootFolder;
        this.cmdvar = cmdvar;
        this.numThread = numThread;
        this.instanceExecid = instanceExecid;
        this.pid = pid;
    }

    //    private static final String[][] PARAMS_STRUCTURE = {
//            {"r", "root-folder", "rootFolder that contains intermediate crawl data"},
//            {"t", "num-threads", "No. of concurrent threads to be run"},
//            {"p", "parent-process-id", "Process Id of the process to begin"},
//            {"i", "instance-exec-id", "Instance Exec Id for the process"},
//            {"c", "cmnd-variable", "test value to append to filename"},
//    };

    public void run() {
//      CommandLine commandLine = new BasicCrawlController().getCommandLine(args, PARAMS_STRUCTURE);
//      String rootFolder = commandLine.getOptionValue("root-folder");
//      logger.debug("root Folder path is " + rootFolder);
//      String numThread = commandLine.getOptionValue("num-threads");
//      logger.debug("numberOfCrawlers (number of concurrent threads) " + numThread);
//      String processid = commandLine.getOptionValue("parent-process-id");
//      logger.debug("processId is " + processid);
//      int pid = Integer.parseInt(processid);
//      String iEid = commandLine.getOptionValue("instance-exec-id");
//      logger.debug("instanceExec Id is " + iEid);
//      int instanceExecid = Integer.parseInt(iEid);
//      String cmdvar = commandLine.getOptionValue("cmnd-variable");
//      logger.debug("test value to append to filename " + cmdvar);
//        BasicCrawler.cmdvar = cmdvar;


        BasicCrawler1.cmdvar = cmdvar;

//    if (args.length != 4) {
//      logger.info("Needed parameters: ");
//      logger.info("\t rootFolder (it will contain intermediate crawl data)");
//      logger.info("\t numberOfCrawlers (number of concurrent threads)");
//        logger.info("\t Process Id of the process to begin");
//        logger.info("\t Instance Exec Id for the process");
//      return;
//    }

    /*
     * crawlStorageFolder is a folder where intermediate crawl data is
     * stored.
     */
        String crawlStorageFolder = rootFolder;

    /*
     * numberOfCrawlers shows the number of concurrent threads that should
     * be initiated for crawling.
     */
        int numberOfCrawlers = Integer.parseInt(numThread);

        CrawlConfig config = new CrawlConfig();

        config.setCrawlStorageFolder(crawlStorageFolder);
//      PropertyConfig.setParams(pid, "crawlConfig");

    /*
     * Be polite: Make sure that we don't send more than 1 request per
     * second (1000 milliseconds between requests).
     */
        config.setPolitenessDelay(400);

    /*
     * You can set the maximum crawl depth here. The default value is -1 for
     * unlimited depth
     */
        config.setMaxDepthOfCrawling(3);

    /*
     * You can set the maximum number of pages to crawl. The default value
     * is -1 for unlimited number of pages
     */
        config.setMaxPagesToFetch(100);

        /**
         * Do you want crawler4j to crawl also binary data ?
         * example: the contents of pdf, or the metadata of images etc
         */
        config.setIncludeBinaryContentInCrawling(false);

    /*
     * Do you need to set a proxy? If so, you can use:
     * config.setProxyHost("proxyserver.example.com");
     * config.setProxyPort(8080);
     *
     * If your proxy also needs authentication:
     * config.setProxyUsername(username); config.getProxyPassword(password);
     */
/*TODO: 1) add static class to set config properties(done)
        1.1) modify package name (done)
        1.2) main class takes from properties table(done)
        1.3) static class sets values taken from properties table(done)
        2) 2.1) add else to clause for saving files (done)
            2.2) add filename+extension from url (done)
        3) check weburlsDB storing logic (done)
        4) dont create auto tables (done)
        5) (test data generation) no. of mappers to be used by input based on volume(no reducer)
        6) regex pattern added to shouldvisit policy so it searches those url which are added
        7) add process id and instanceexec id in the WeburlsDB and PendingURLsDB table (done)
        8) while fetching comparison check process id too (done)
        9) extend base structure class and add PARAMS_STRUCTURE(done)
        10) dynamically create persistence unit (not working)
        11) test same code in 3 cmd windows to check conflicts (done)
        12) make docid field in docidsDB auto_increment (done)
        16) modify log4j to cycle the log (done, based on file size)
        hadoop
        12) input split generation
        13) input format generation
        14) record reader generation (K(byte writable),V(Textwritable))
        15) avro output format

* */
    /*
     * This config parameter can be used to set your crawl to be resumable
     * (meaning that you can resume the crawl from a previously
     * interrupted/crashed crawl). Note: if you enable resuming feature and
     * want to start a fresh crawl, you need to delete the contents of
     * rootFolder manually.
     */
        config.setResumableCrawling(true);
        config.setUserAgentString("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");
        config.setProxyHost("proxy1.wipro.com");
        config.setProxyPort(8080);
        config.setProxyUsername("as294216");
        config.setProxyPassword("Mikasa@AOT3741");

    /*
     * Instantiate the controller for this crawl.
     */
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = null;
        try {
            controller = new CrawlController(config, pageFetcher, robotstxtServer, pid, instanceExecid);
        } catch (Exception e) {
            logger.debug("unable to create CrawlController object");
            e.printStackTrace();
        }

    /*
     * For each crawl, you need to add some seed urls. These are the first
     * URLs that are fetched and then the crawler starts following links
     * which are found in these pages
     */
//    controller.addSeed("https://www.zomato.com/bangalore");
        controller.addSeed("http://www.dmoz.org/");
        controller.addSeed("http://www.indianexpress.com/");


    /*
     * Start the crawl. This is a blocking operation, meaning that your code
     * will reach the line after this only when crawling is finished.
     */
        controller.start(BasicCrawler1.class, numberOfCrawlers);
    }
}