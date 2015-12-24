/*
 * Copyright 2015 Wipro Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wipro.ats.bdre.md.dao;

import com.wipro.ats.bdre.md.beans.table.Properties;
import com.wipro.ats.bdre.md.beans.table.PropertiesTemplate;
import com.wipro.ats.bdre.md.dao.jpa.ProcessTemplate;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

/**
 * Created by PR324290 on 10/29/2015.
 */
public class ProcessTemplateDAOTest {

    private static final Logger LOGGER = Logger.getLogger(ProcessTemplateDAOTest.class);

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }


    @Autowired
    ProcessTemplateDAO processTemplateDAO;

    @Test
    public void testList() throws Exception {

        LOGGER.info("Size of ProcessTemplate is atleast:" + processTemplateDAO.list(0, 10, 1).size());
    }

    @Test
    public void testTotalRecordCount() throws Exception {
        LOGGER.info("Size of ProcessTemplate is:" + processTemplateDAO.totalRecordCount());
    }

    @Ignore
    @Test
    public void testGet() throws Exception {

        LOGGER.info("ProcessTemplate(0) type:" + processTemplateDAO.get(0).getDescription());
    }


    @Test
    public void testInsertUpdateAndDelete() throws Exception {
        ProcessTemplate processTemplate = processTemplateDAO.get(0);
        int processTemplateId = (Integer) processTemplateDAO.insert(processTemplate);
        LOGGER.info("New ProcessTemplate added with ID:" + processTemplateId);

        processTemplateDAO.update(processTemplate);
        processTemplate = processTemplateDAO.get(processTemplateId);
        LOGGER.info("Updated processTemplate with type:" + processTemplate.getDescription());
        ;
        processTemplateDAO.delete(processTemplateId);
        LOGGER.info("ProcessTemplate Deleted with ID:" + processTemplateId);
    }

    @Test
    public void testSelectPTList() throws Exception {
        List<com.wipro.ats.bdre.md.beans.table.ProcessTemplate> returnedList = processTemplateDAO.selectPTList(0);
        LOGGER.info("size of returned list is " + returnedList.size());
    }

    @Test
    public void testSelectPPListForTemplateId() throws Exception {
        List<com.wipro.ats.bdre.md.beans.table.Process> returnedList = processTemplateDAO.selectPPListForTemplateId(0);
        LOGGER.info("size of returned list is " + returnedList.size());
    }

    @Test
    public void testListSubProcessTemplates() throws Exception {
        List<com.wipro.ats.bdre.md.beans.table.ProcessTemplate> returnedList = processTemplateDAO.listSubProcessTemplates(0);
        LOGGER.info("size of returned list is " + returnedList.size());
    }

    @Test
    public void testSelectNextForPid() throws Exception {
        com.wipro.ats.bdre.md.beans.table.Process process = processTemplateDAO.selectNextForPid(2, 0);
        LOGGER.info("process fetched:" + process.getProcessId());
    }


    @Test
    public void testSelectMissingPropListForP() throws Exception {
        List<Properties> propertiesList = processTemplateDAO.selectMissingPropListForP(1, 0);
        if (propertiesList != null)
            LOGGER.info("missing properties list size :" + propertiesList.size());
    }


    @Test
    public void testSelectMissingPropListForT() throws Exception {
        List<PropertiesTemplate> propertiesTempList = processTemplateDAO.selectMissingPropListForT(2, 1, 0);
        if (propertiesTempList != null)
            LOGGER.info("missing properties list size :" + propertiesTempList.size());
    }

    @Test
    public void testSelectMissingSubPList() throws Exception {
        List<com.wipro.ats.bdre.md.beans.table.ProcessTemplate> returnedList = processTemplateDAO.selectMissingSubPList(10802, null);
        LOGGER.info("size of returned list is " + returnedList.size());
    }

    @Test
    public void testSelectMissingSubTList() throws Exception {
        List<com.wipro.ats.bdre.md.beans.table.ProcessTemplate> returnedList = processTemplateDAO.selectMissingSubTList(10802, null);
        LOGGER.info("size of returned list is " + returnedList.size());

    }
}
