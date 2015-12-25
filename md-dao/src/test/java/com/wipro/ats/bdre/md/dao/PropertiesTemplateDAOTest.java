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

package com.wipro.ats.bdre.md.dao;


import com.wipro.ats.bdre.md.dao.jpa.ProcessTemplate;
import com.wipro.ats.bdre.md.dao.jpa.PropertiesTemplate;
import com.wipro.ats.bdre.md.dao.jpa.PropertiesTemplateId;
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
 * Created by MR299389 on 10/28/2015.
 */
public class PropertiesTemplateDAOTest {
    private static final Logger LOGGER = Logger.getLogger(PropertiesTemplateDAOTest.class);

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    @Autowired
    PropertiesTemplateDAO propertiesTemplateDAO;
    @Autowired
    ProcessTemplateDAO processTemplateDAO;

    @Test
    public void testList() throws Exception {
        LOGGER.info("Size of PropertiesTemplate is atleast:" + propertiesTemplateDAO.list(0, 10).size());
    }

    @Test
    public void testTotalRecordCount() throws Exception {
        LOGGER.info("Total Size of PropertiesTemplate is:" + propertiesTemplateDAO.totalRecordCount());
    }

    @Test
    @Ignore
    public void testGet() throws Exception {
        PropertiesTemplateId propertiesTemplateId = new PropertiesTemplateId();
        propertiesTemplateId.setProcessTemplateId(1);
        propertiesTemplateId.setPropTempKey("");
        LOGGER.info("Description of PropertiesTemplateId(1) is:" + propertiesTemplateDAO.get(propertiesTemplateId).getDescription());

    }

    @Test
    public void testInsertUpdateAndDelete() throws Exception {
        PropertiesTemplateId propertiesTemplateId = new PropertiesTemplateId();
        propertiesTemplateId.setProcessTemplateId(0);
        propertiesTemplateId.setPropTempKey("Test Key");
        ProcessTemplate processTemplate = processTemplateDAO.get(0);
        PropertiesTemplate propertiesTemplate = new PropertiesTemplate();
        propertiesTemplate.setDescription("Testing");
        propertiesTemplate.setConfigGroup("Test CG");
        propertiesTemplate.setId(propertiesTemplateId);
        propertiesTemplate.setPropTempValue("Test Value");
        propertiesTemplate.setProcessTemplate(processTemplate);
        propertiesTemplateId = propertiesTemplateDAO.insert(propertiesTemplate);
        LOGGER.info("PropertiesTemplate is added with Id:" + propertiesTemplateId.getProcessTemplateId());
        propertiesTemplate.setDescription("Updated Description");
        propertiesTemplateDAO.update(propertiesTemplate);
        propertiesTemplate = propertiesTemplateDAO.get(propertiesTemplateId);
        LOGGER.info("Updated Description is:" + propertiesTemplate.getDescription());
        propertiesTemplateDAO.delete(propertiesTemplateId);
        LOGGER.info("Deleted PropertiesTemplate Entry with ID:" + propertiesTemplateId.getProcessTemplateId());
    }


    @Test
    public void insertAndUpdateTest() throws Exception {
        com.wipro.ats.bdre.md.beans.table.PropertiesTemplate newPropertiesTemplate = new com.wipro.ats.bdre.md.beans.table.PropertiesTemplate();
        newPropertiesTemplate.setConfigGroup("Test");
        newPropertiesTemplate.setKey("key test1234");
        newPropertiesTemplate.setDescription("description test");
        newPropertiesTemplate.setValue("value test");
        newPropertiesTemplate.setParentProcessTemplateId(0);
        newPropertiesTemplate.setProcessTemplateId(0);

        com.wipro.ats.bdre.md.beans.table.PropertiesTemplate propertiesTemplate = propertiesTemplateDAO.insertProcessTemplate(newPropertiesTemplate);
        LOGGER.info("Properties Template returned when inserted is " + propertiesTemplate);

        newPropertiesTemplate.setConfigGroup("Test");
        propertiesTemplate = propertiesTemplateDAO.updateProcessTemplate(newPropertiesTemplate);
        LOGGER.info("Properties Template returned when updated is " + propertiesTemplate);
        PropertiesTemplateId propertiesTemplateId = new PropertiesTemplateId();
        propertiesTemplateId.setProcessTemplateId(0);
        propertiesTemplateId.setPropTempKey("key test1234");
        propertiesTemplateDAO.delete(propertiesTemplateId);
    }


    @Test
    public void listPropertyTemplateTest() throws Exception {
        List<com.wipro.ats.bdre.md.beans.table.PropertiesTemplate> tableProperties = propertiesTemplateDAO.listPropertyTemplate(0, 10);
        for (com.wipro.ats.bdre.md.beans.table.PropertiesTemplate propertiesTemplate : tableProperties) {
            LOGGER.info("properties Template:" + propertiesTemplate);
        }
    }

    @Test
    public void listPropertiesTemplateTest() throws Exception {
        List<com.wipro.ats.bdre.md.beans.table.PropertiesTemplate> tableProperties = propertiesTemplateDAO.listPropertiesTemplateBean(0);
        for (com.wipro.ats.bdre.md.beans.table.PropertiesTemplate propertiesTemplate : tableProperties) {
            LOGGER.info("properties Template:" + propertiesTemplate);
        }
    }

    @Test
    public void deletePropertiesTemplateTest() throws Exception {
        int processTemplateId = 0;
        propertiesTemplateDAO.deletePropertiesTemplate(processTemplateId);
        LOGGER.info("ProcessTemplateId of deleted Properties Template is" + processTemplateId);
    }

    @Test
    public void deletePropertyTemplateTest() throws Exception {
        int processTemplateId = 0;
        String key = "key test1234";
        propertiesTemplateDAO.deletePropertyTemplate(processTemplateId, key);
        LOGGER.info("ProcessTemplateId of deleted Properties Template is" + processTemplateId);
    }

}
