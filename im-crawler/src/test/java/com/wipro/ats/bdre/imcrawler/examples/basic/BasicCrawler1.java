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

import com.wipro.ats.bdre.imcrawler.crawler.Page;
import com.wipro.ats.bdre.imcrawler.crawler.WebCrawler;
import com.wipro.ats.bdre.imcrawler.parser.BinaryParseData;
import com.wipro.ats.bdre.imcrawler.parser.HtmlParseData;
import com.wipro.ats.bdre.imcrawler.url.WebURL;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.Header;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author Yasser Ganjisaffar
 */
public class BasicCrawler1 extends WebCrawler {
    public static String cmdvar;
    private static final Pattern IMAGE_EXTENSIONS = Pattern.compile(".*\\.(bmp|gif|jpg|png)$");

    /**
     * You should implement this function to specify whether the given url
     * should be crawled or not (based on your crawling logic).
     */
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        // Ignore the url if it has an extension that matches our defined set of image extensions.
        if (IMAGE_EXTENSIONS.matcher(href).matches()) {
            return false;
        }

        // Only accept the url if it is in the "www.ics.uci.edu" domain and protocol is "http".
        return true;
    }

    /**
     * This function is called when a page is fetched and ready to be processed
     * by your program.
     */
    @Override
    public void visit(Page page) {
        int docid = page.getWebURL().getDocid();
        String url = page.getWebURL().getURL();
        String domain = page.getWebURL().getDomain();
        String path = page.getWebURL().getPath();
        String subDomain = page.getWebURL().getSubDomain();
        String parentUrl = page.getWebURL().getParentUrl();
        String anchor = page.getWebURL().getAnchor();

        logger.debug("Docid: {}", docid);
        logger.info("URL: {}", url);
        logger.debug("Domain: '{}'", domain);
        logger.debug("Sub-domain: '{}'", subDomain);
        logger.debug("Path: '{}'", path);
        logger.debug("Parent page: {}", parentUrl);
        logger.debug("Anchor text: {}", anchor);

        //instead of storing to file store it somewhere from
        //which mapper can take the values
        //check when no threads running; pass that as condition to
        //return false in nextKeyValue funtion
        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String text = htmlParseData.getText();
            String html = htmlParseData.getHtml();

            File file = new File(docid + "_" + cmdvar);
            try {
                FileUtils.writeStringToFile(file, html);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Set<WebURL> links = htmlParseData.getOutgoingUrls();

            logger.debug("Text length: {}", text.length());
            logger.debug("Html length: {}", html.length());
            logger.debug("Number of outgoing links: {}", links.size());
        } else if (page.getParseData() instanceof BinaryParseData) {
            BinaryParseData binaryParseData = new BinaryParseData();
            //content of the page in binary format sent to be transformed
            //and get Parsed binary content or null
            binaryParseData.setBinaryContent(page.getContentData());
            String html = binaryParseData.getHtml();
            //check for filename and extension
            String baseName = FilenameUtils.getBaseName(url);
            String extension = FilenameUtils.getExtension(url);
            File file;
            if (baseName != null && extension != null) {
                file = new File(baseName + "_" + cmdvar + "." + extension);
            } else if (baseName != null && extension == null) {
                file = new File(baseName + "_" + cmdvar);
            } else {
                file = new File(docid + "_" + cmdvar);
            }

            try {
                FileUtils.writeStringToFile(file, html);
            } catch (IOException e) {
                e.printStackTrace();
            }
            logger.debug("parsed Html length(null if not parsed): {}", html.length());
        }

        Header[] responseHeaders = page.getFetchResponseHeaders();
        if (responseHeaders != null) {
            logger.debug("Response headers:");
            for (Header header : responseHeaders) {
                logger.debug("\t{}: {}", header.getName(), header.getValue());
            }
        }

        logger.debug("=============");
    }
}
