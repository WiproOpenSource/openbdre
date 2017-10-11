/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wipro.ats.bdre.md.rest.ext;

import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.beans.CrawlerInfo;
import com.wipro.ats.bdre.md.beans.table.Process;
import com.wipro.ats.bdre.md.dao.ProcessDAO;
import com.wipro.ats.bdre.md.dao.UserRolesDAO;
import com.wipro.ats.bdre.md.dao.jpa.Users;
import com.wipro.ats.bdre.md.rest.RestWrapper;
import com.wipro.ats.bdre.md.rest.util.BindingResultError;
import com.wipro.ats.bdre.md.rest.util.Dao2TableUtil;
import com.wipro.ats.bdre.md.rest.util.DateConverter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by AS294216 on 01-10-2015.
 */
@Controller
@RequestMapping("/crawler")
@Scope("session")
public class CrawlerAPI extends MetadataAPIBase {
    private static final Logger LOGGER = Logger.getLogger(CrawlerAPI.class);
    private static final String CRAWLER = "crawler";

    @Autowired
    private ProcessDAO processDAO;
    @Autowired
    UserRolesDAO userRolesDAO;
    @RequestMapping(value = {"/", ""}, method = RequestMethod.POST)

    @ResponseBody
    public RestWrapper createCrawlerProcess(@ModelAttribute("crawlerInfo")
                                     @Valid CrawlerInfo crawlerInfo, BindingResult bindingResult, Principal principal) {

        RestWrapper restWrapper = null;

        if (bindingResult.hasErrors()) {
            BindingResultError bindingResultError = new BindingResultError();
            return bindingResultError.errorMessage(bindingResult);
        }


        //making process
        com.wipro.ats.bdre.md.dao.jpa.Process parentProcess = Dao2TableUtil.buildJPAProcess(28, crawlerInfo.getProcessName(), crawlerInfo.getProcessDescription(), crawlerInfo.getWorkflowTypeId(),crawlerInfo.getBusDomainId());
        Users users=new Users();
        users.setUsername(principal.getName());
        parentProcess.setUsers(users);
        parentProcess.setUserRoles(userRolesDAO.minUserRoleId(principal.getName()));
        com.wipro.ats.bdre.md.dao.jpa.Process childProcess = Dao2TableUtil.buildJPAProcess(29, "child of " + crawlerInfo.getProcessName(), "SubProcess of " + crawlerInfo.getProcessDescription(), 0,crawlerInfo.getBusDomainId());
        List<com.wipro.ats.bdre.md.dao.jpa.Properties> childProps=new ArrayList<>();

        //inserting in properties table
        com.wipro.ats.bdre.md.dao.jpa.Properties jpaProperties = Dao2TableUtil.buildJPAProperties(CRAWLER, "politenessDelay", crawlerInfo.getPolitenessDelay().toString(), "Delay between requests");
        childProps.add(jpaProperties);
        jpaProperties = Dao2TableUtil.buildJPAProperties(CRAWLER, "maxDepthOfCrawling", crawlerInfo.getMaxDepthOfCrawling().toString(), "Depth of crawling");
        childProps.add(jpaProperties);
        jpaProperties = Dao2TableUtil.buildJPAProperties(CRAWLER, "maxPagesToFetch", crawlerInfo.getMaxPagesToFetch().toString(), "no. of pages to fetch");
        childProps.add(jpaProperties);
        jpaProperties = Dao2TableUtil.buildJPAProperties(CRAWLER, "includeBinaryContentInCrawling", crawlerInfo.getIncludeBinaryContentInCrawling().toString(), "to include binary content");
        childProps.add(jpaProperties);
        jpaProperties = Dao2TableUtil.buildJPAProperties(CRAWLER, "resumableCrawling", crawlerInfo.getResumableCrawling().toString(), "set resumable crawling");
        childProps.add(jpaProperties);
        jpaProperties = Dao2TableUtil.buildJPAProperties(CRAWLER, "userAgentString", crawlerInfo.getUserAgentString(), "User agent string");
        childProps.add(jpaProperties);
        jpaProperties = Dao2TableUtil.buildJPAProperties(CRAWLER, "outputPath", crawlerInfo.getOutputPath(), "HDFS output path");
        childProps.add(jpaProperties);

        if (crawlerInfo.getProxyHost() != null && crawlerInfo.getProxyHost() != "") {
            jpaProperties = Dao2TableUtil.buildJPAProperties(CRAWLER, "proxyHost", crawlerInfo.getProxyHost(), "Proxy Host");
            childProps.add(jpaProperties);
            if (crawlerInfo.getProxyPort() != null && crawlerInfo.getProxyPort() != 0) {
                jpaProperties = Dao2TableUtil.buildJPAProperties(CRAWLER, "proxyPort", crawlerInfo.getProxyPort().toString(), "Proxy Port");
                childProps.add(jpaProperties);
                if (crawlerInfo.getProxyUserName() != null && crawlerInfo.getProxyUserName() != "" && crawlerInfo.getProxyPassword() != null && crawlerInfo.getProxyPassword() != "") {
                    jpaProperties = Dao2TableUtil.buildJPAProperties(CRAWLER, "proxyUsername", crawlerInfo.getProxyUserName(), "Proxy Username");
                    childProps.add(jpaProperties);
                    jpaProperties = Dao2TableUtil.buildJPAProperties(CRAWLER, "proxyPassword", crawlerInfo.getProxyPassword(), "Proxy Password");
                    childProps.add(jpaProperties);
                }

            }

        }
        jpaProperties = Dao2TableUtil.buildJPAProperties(CRAWLER, "url", crawlerInfo.getUrl(), "Base Url to crawl");
        childProps.add(jpaProperties);
        jpaProperties = Dao2TableUtil.buildJPAProperties(CRAWLER, "urlsToSearch", crawlerInfo.getUrlsToSearch(), "urls to include in search");
        childProps.add(jpaProperties);
        jpaProperties = Dao2TableUtil.buildJPAProperties(CRAWLER, "urlsNotToSearch", crawlerInfo.getUrlsNotToSearch(), "urls not to include in search");
        childProps.add(jpaProperties);
        jpaProperties = Dao2TableUtil.buildJPAProperties(CRAWLER, "numberOfMappers", crawlerInfo.getNumMappers().toString(), "number of mappers to run");
        childProps.add(jpaProperties);

        List<com.wipro.ats.bdre.md.dao.jpa.Process> processList = processDAO.createOneChildJob(parentProcess,childProcess,null,childProps);
        List<Process> tableProcessList = Dao2TableUtil.jpaList2TableProcessList(processList);
        Integer counter = tableProcessList.size();
        for (Process process:tableProcessList) {
            process.setCounter(counter);
            process.setTableAddTS(DateConverter.dateToString(process.getAddTS()));
            process.setTableEditTS(DateConverter.dateToString(process.getEditTS()));
        }
        restWrapper = new RestWrapper(tableProcessList, RestWrapper.OK);
        LOGGER.info("Process and Properties for crawler process inserted by" + principal.getName());
        return restWrapper;
    }


    @Override
    public Object execute(String[] params) {
        return null;
    }
}
