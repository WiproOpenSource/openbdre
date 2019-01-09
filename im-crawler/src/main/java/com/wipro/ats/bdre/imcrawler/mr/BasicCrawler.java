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

package com.wipro.ats.bdre.imcrawler.mr;

import com.wipro.ats.bdre.imcrawler.crawler.Page;
import com.wipro.ats.bdre.imcrawler.crawler.PropertyConfig;
import com.wipro.ats.bdre.imcrawler.crawler.WebCrawler;
import com.wipro.ats.bdre.imcrawler.parser.BinaryParseData;
import com.wipro.ats.bdre.imcrawler.parser.HtmlParseData;
import com.wipro.ats.bdre.imcrawler.url.WebURL;
import org.apache.hadoop.io.Text;
import org.apache.http.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author Yasser Ganjisaffar
 */
public class BasicCrawler extends WebCrawler {
    private static final Logger logger = LoggerFactory.getLogger(BasicCrawler.class);
    private PropertyConfig propertyConfig;
    private static Pattern urlPattern = null;
    private static Pattern urlNotPattern = null;

    public BasicCrawler(int pid) {
        propertyConfig = PropertyConfig.getPropertyConfig(pid);
        urlPattern = Pattern.compile(propertyConfig.getUrlsToSearch());
        urlNotPattern = Pattern.compile(propertyConfig.getUrlsNotToSearch());
        logger.debug("inside BasicCrawler------regex pattern to search----------"+propertyConfig.getUrlsToSearch());
        logger.debug("inside BasicCrawler------regex pattern not to search----------"+propertyConfig.getUrlsNotToSearch());
    }
    public BasicCrawler() {

    }

    /**
     * You should implement this function to specify whether the given url
     * should be crawled or not (based on your crawling logic).
     */
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        // Ignore the url if it has an extension that matches our defined set of image extensions.
        //ignore url if it matches this pattern
        if (urlNotPattern.matcher(href).matches()) {
            return false;
        }
        //visit url if it matches this pattern
        if (urlPattern.matcher(href).matches()) {
            return true;
        }
        //if no search pattern given
        if(propertyConfig.getUrlsToSearch()==null){
            return true;
        }

        // as it doesnt lie within our requirements
        return false;
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

//      File file = new File(docid+"_"+cmdvar);
//      try {
//        FileUtils.writeStringToFile(file,html);
//      } catch (IOException e) {
//        e.printStackTrace();
//      }
            //byte[] b = html.getBytes(Charset.forName("UTF-8"));
            //BytesWritable bytesWritable = new BytesWritable(b);
            byte[] b = new byte[]{0};
            CrawlOutput crawlOutput = new CrawlOutput(docid, url, domain, path, subDomain, parentUrl, anchor, html, b);
            Text htmlString = new Text(html);
            //CrawlRecordReader.valueList.add(htmlString);
            CrawlRecordReader.fullList.add(crawlOutput);

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
//      String baseName = FilenameUtils.getBaseName(url);
//      String extension = FilenameUtils.getExtension(url);
//      File file;
//      if(baseName!=null && extension!=null) {
//        file = new File(baseName+"_"+cmdvar+ "." + extension);
//      } else if (baseName!=null && extension==null) {
//        file = new File(baseName+"_"+cmdvar);
//      } else {
//        file = new File(docid+"_"+cmdvar);
//      }
//      try {
//        FileUtils.writeStringToFile(file,html);
//      } catch (IOException e) {
//        e.printStackTrace();
//      }

            byte[] b = html.getBytes(Charset.forName("UTF-8"));
            //BytesWritable bytesWritable = new BytesWritable(b);
            //  Text htmlToText = new Text(html);
            CrawlOutput crawlOutput = new CrawlOutput(docid, url, domain, path, subDomain, parentUrl, anchor, "null", b);
            Text htmlString = new Text(html);
            //CrawlRecordReader.valueList.add(htmlString);
            CrawlRecordReader.fullList.add(crawlOutput);


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
