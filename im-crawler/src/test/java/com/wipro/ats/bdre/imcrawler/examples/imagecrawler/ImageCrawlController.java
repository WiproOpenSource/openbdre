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

package com.wipro.ats.bdre.imcrawler.examples.imagecrawler;

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
public class ImageCrawlController extends BaseStructure {
    private static final Logger logger = LoggerFactory.getLogger(ImageCrawlController.class);
    private static final String[][] PARAMS_STRUCTURE = {
            {"r", "root-folder", "rootFolder that contains intermediate crawl data"},
            {"t", "num-threads", "No. of concurrent threads to be run"},
            {"p", "parent-process-id", "Process Id of the process to begin"},
            {"i", "instance-exec-id", "Instance Exec Id for the process"},
            {"s", "storage-folder", "storageFolder (a folder for storing downloaded images)"}
    };

    public static void main(String[] args) throws Exception {
        CommandLine commandLine = new ImageCrawlController().getCommandLine(args, PARAMS_STRUCTURE);
        String rootFolder = commandLine.getOptionValue("root-folder");
        logger.debug("root Folder path is " + rootFolder);
        String numThread = commandLine.getOptionValue("num-threads");
        logger.debug("numberOfCrawlers (number of concurrent threads) " + numThread);
        int pid = Integer.parseInt(commandLine.getOptionValue("parent-process-id"));
        logger.debug("processId is " + pid);
        int instanceExecid = Integer.parseInt(commandLine.getOptionValue("instance-exec-id"));
        logger.debug("instanceExec Id is " + instanceExecid);
        String storageFolder = commandLine.getOptionValue("storage-folder");
        logger.debug("storage Folder path is " + storageFolder);

//    if (args.length < 5) {
//      logger.info("Needed parameters: ");
//      logger.info("\t rootFolder (it will contain intermediate crawl data)");
//      logger.info("\t numberOfCralwers (number of concurrent threads)");
//        logger.info("\t Process Id of the process to begin");
//        logger.info("\t Instance Exec Id for the process");
//      logger.info("\t storageFolder (a folder for storing downloaded images)");
//      return;
//    }

//    String rootFolder = args[0];
        int numberOfCrawlers = Integer.parseInt(numThread);
//    String storageFolder = args[2];

        CrawlConfig config = new CrawlConfig();

        config.setCrawlStorageFolder(rootFolder);

    /*
     * Since images are binary content, we need to set this parameter to
     * true to make sure they are included in the crawl.
     */
        config.setIncludeBinaryContentInCrawling(true);

        String[] crawlDomains = {"http://uci.edu/"};

        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer, pid, instanceExecid);
        for (String domain : crawlDomains) {
            controller.addSeed(domain);
        }

        ImageCrawler.configure(crawlDomains, storageFolder);

        controller.start(ImageCrawler.class, numberOfCrawlers);
    }
}