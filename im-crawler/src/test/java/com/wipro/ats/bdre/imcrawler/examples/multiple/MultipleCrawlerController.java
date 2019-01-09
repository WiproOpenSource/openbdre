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

package com.wipro.ats.bdre.imcrawler.examples.multiple;

import com.wipro.ats.bdre.BaseStructure;
import com.wipro.ats.bdre.imcrawler.crawler.CrawlConfig;
import com.wipro.ats.bdre.imcrawler.crawler.CrawlController;
import com.wipro.ats.bdre.imcrawler.fetcher.PageFetcher;
import com.wipro.ats.bdre.imcrawler.robotstxt.RobotstxtConfig;
import com.wipro.ats.bdre.imcrawler.robotstxt.RobotstxtServer;
import org.apache.commons.cli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Yasser Ganjisaffar
 */

public class MultipleCrawlerController extends BaseStructure {
    private static final Logger logger = LoggerFactory.getLogger(MultipleCrawlerController.class);

    private static final String[][] PARAMS_STRUCTURE = {
            {"r", "root-folder", "rootFolder that contains intermediate crawl data"},
            {"p", "parent-process-id", "Process Id of the process to begin"},
            {"i", "instance-exec-id", "Instance Exec Id for the process"},
    };

    public static void main(String[] args) throws Exception {
        CommandLine commandLine = new MultipleCrawlerController().getCommandLine(args, PARAMS_STRUCTURE);
        String rootFolder = commandLine.getOptionValue("root-folder");
        logger.debug("root Folder path is " + rootFolder);
        int pid = Integer.parseInt(commandLine.getOptionValue("parent-process-id"));
        logger.debug("processId is " + pid);
        int instanceExecid = Integer.parseInt(commandLine.getOptionValue("instance-exec-id"));
        logger.debug("instanceExec Id is " + instanceExecid);

//    if (args.length != 3) {
//      logger.info("Needed parameters: ");
//      logger.info("\t rootFolder (it will contain intermediate crawl data)");
//
//      logger.info("\t Process Id of the process to begin");
//      logger.info("\t Instance Exec Id for the process");
//      return;
//    }

    /*
     * crawlStorageFolder is a folder where intermediate crawl data is
     * stored.
     */
        String crawlStorageFolder = rootFolder;

        CrawlConfig config1 = new CrawlConfig();
        CrawlConfig config2 = new CrawlConfig();

    /*
     * The two crawlers should have different storage folders for their
     * intermediate data
     */
        config1.setCrawlStorageFolder(crawlStorageFolder + "/crawler1");
        config2.setCrawlStorageFolder(crawlStorageFolder + "/crawler2");

        config1.setPolitenessDelay(1000);
        config2.setPolitenessDelay(2000);

        config1.setMaxPagesToFetch(50);
        config2.setMaxPagesToFetch(100);

    /*
     * We will use different PageFetchers for the two crawlers.
     */
        PageFetcher pageFetcher1 = new PageFetcher(config1);
        PageFetcher pageFetcher2 = new PageFetcher(config2);

    /*
     * We will use the same RobotstxtServer for both of the crawlers.
     */
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher1);

        CrawlController controller1 = new CrawlController(config1, pageFetcher1, robotstxtServer, pid, instanceExecid);
        CrawlController controller2 = new CrawlController(config2, pageFetcher2, robotstxtServer, pid, instanceExecid);

        String[] crawler1Domains = {"http://www.ics.uci.edu/", "http://www.cnn.com/"};
        String[] crawler2Domains = {"http://en.wikipedia.org/"};

        controller1.setCustomData(crawler1Domains);
        controller2.setCustomData(crawler2Domains);

        controller1.addSeed("http://www.ics.uci.edu/");
        controller1.addSeed("http://www.cnn.com/");
        controller1.addSeed("http://www.ics.uci.edu/~lopes/");
        controller1.addSeed("http://www.cnn.com/POLITICS/");

        controller2.addSeed("http://en.wikipedia.org/wiki/Main_Page");
        controller2.addSeed("http://en.wikipedia.org/wiki/Obama");
        controller2.addSeed("http://en.wikipedia.org/wiki/Bing");

    /*
     * The first crawler will have 5 concurrent threads and the second
     * crawler will have 7 threads.
     */
        controller1.startNonBlocking(BasicCrawler.class, 5);
        controller2.startNonBlocking(BasicCrawler.class, 7);

        controller1.waitUntilFinish();
        logger.info("Crawler 1 is finished.");

        controller2.waitUntilFinish();
        logger.info("Crawler 2 is finished.");
    }
}