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

package com.wipro.ats.bdre.imcrawler.examples.localdata;

import com.wipro.ats.bdre.imcrawler.crawler.CrawlConfig;
import com.wipro.ats.bdre.imcrawler.crawler.Page;
import com.wipro.ats.bdre.imcrawler.fetcher.PageFetchResult;
import com.wipro.ats.bdre.imcrawler.fetcher.PageFetcher;
import com.wipro.ats.bdre.imcrawler.parser.HtmlParseData;
import com.wipro.ats.bdre.imcrawler.parser.ParseData;
import com.wipro.ats.bdre.imcrawler.parser.Parser;
import com.wipro.ats.bdre.imcrawler.url.WebURL;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is a demonstration of how crawler4j can be used to download a
 * single page and extract its title and text.
 */
public class Downloader {
    private static final Logger logger = LoggerFactory.getLogger(Downloader.class);

    private final Parser parser;
    private final PageFetcher pageFetcher;

    public Downloader() {
        CrawlConfig config = new CrawlConfig();

        parser = new Parser(config);
        pageFetcher = new PageFetcher(config);
    }

    public static void main(String[] args) {
        Downloader downloader = new Downloader();
        downloader.processUrl("http://en.wikipedia.org/wiki/Main_Page/");
        downloader.processUrl("http://www.yahoo.com/");
    }

    public void processUrl(String url) {
        logger.debug("Processing: {}", url);
        Page page = download(url);
        if (page != null) {
            ParseData parseData = page.getParseData();
            if (parseData != null) {
                if (parseData instanceof HtmlParseData) {
                    HtmlParseData htmlParseData = (HtmlParseData) parseData;
                    logger.debug("Title: {}", htmlParseData.getTitle());
                    logger.debug("Text length: {}", htmlParseData.getText().length());
                    logger.debug("Html length: {}", htmlParseData.getHtml().length());
                }
            } else {
                logger.warn("Couldn't parse the content of the page.");
            }
        } else {
            logger.warn("Couldn't fetch the content of the page.");
        }
        logger.debug("==============");
    }

    private Page download(String url) {
        WebURL curURL = new WebURL();
        curURL.setURL(url);
        PageFetchResult fetchResult = null;
        try {
            fetchResult = pageFetcher.fetchPage(curURL);
            if (fetchResult.getStatusCode() == HttpStatus.SC_OK) {
                Page page = new Page(curURL);
                fetchResult.fetchContent(page);
                parser.parse(page, curURL.getURL());
                return page;
            }
        } catch (Exception e) {
            logger.error("Error occurred while fetching url: " + curURL.getURL(), e);
        } finally {
            if (fetchResult != null) {
                fetchResult.discardContentIfNotConsumed();
            }
        }
        return null;
    }
}