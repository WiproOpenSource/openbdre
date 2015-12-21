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

package com.wipro.ats.bdre.imcrawler.examples.shutdown;

import com.wipro.ats.bdre.BaseStructure;
import com.wipro.ats.bdre.imcrawler.crawler.CrawlConfig;
import com.wipro.ats.bdre.imcrawler.crawler.CrawlController;
import com.wipro.ats.bdre.imcrawler.fetcher.PageFetcher;
import com.wipro.ats.bdre.imcrawler.robotstxt.RobotstxtConfig;
import org.apache.commons.cli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wipro.ats.bdre.imcrawler.robotstxt.RobotstxtServer;

/**
 * @author Yasser Ganjisaffar
 */

public class ControllerWithShutdown extends BaseStructure {
    private static final Logger logger = LoggerFactory.getLogger(ControllerWithShutdown.class);

    private static final String[][] PARAMS_STRUCTURE = {
            {"r", "root-folder", "rootFolder that contains intermediate crawl data"},
            {"t", "num-threads", "No. of concurrent threads to be run"},
            {"p", "parent-process-id", "Process Id of the process to begin"},
            {"i", "instance-exec-id", "Instance Exec Id for the process"},
    };

    public static void main(String[] args) throws Exception {
        CommandLine commandLine = new ControllerWithShutdown().getCommandLine(args, PARAMS_STRUCTURE);
        String rootFolder = commandLine.getOptionValue("root-folder");
        logger.debug("root Folder path is " + rootFolder);
        String numThread = commandLine.getOptionValue("num-threads");
        logger.debug("numberOfCrawlers (number of concurrent threads) " + numThread);
        int pid = Integer.parseInt(commandLine.getOptionValue("parent-process-id"));
        logger.debug("processId is " + pid);
        int instanceExecid = Integer.parseInt(commandLine.getOptionValue("instance-exec-id"));
        logger.debug("instanceExec Id is " + instanceExecid);

//    if (args.length != 4) {
//      logger.info("Needed parameters: ");
//      logger.info("\t rootFolder (it will contain intermediate crawl data)");
//      logger.info("\t numberOfCrawlers (number of concurrent threads)");
//      logger.info("\t Process Id of the process to begin");
//      logger.info("\t Instance Exec Id for the process");
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

        config.setPolitenessDelay(1000);

        // Unlimited number of pages can be crawled.
        config.setMaxPagesToFetch(-1);

    /*
     * Instantiate the controller for this crawl.
     */
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer, pid, instanceExecid);

    /*
     * For each crawl, you need to add some seed urls. These are the first
     * URLs that are fetched and then the crawler starts following links
     * which are found in these pages
     */
        controller.addSeed("http://www.ics.uci.edu/~welling/");
        controller.addSeed("http://www.ics.uci.edu/~lopes/");
        controller.addSeed("http://www.ics.uci.edu/");

    /*
     * Start the crawl. This is a blocking operation, meaning that your code
     * will reach the line after this only when crawling is finished.
     */
        controller.startNonBlocking(BasicCrawler.class, numberOfCrawlers);

        // Wait for 30 seconds
        Thread.sleep(30 * 1000);

        // Send the shutdown request and then wait for finishing
        controller.shutdown();
        controller.waitUntilFinish();
    }
}