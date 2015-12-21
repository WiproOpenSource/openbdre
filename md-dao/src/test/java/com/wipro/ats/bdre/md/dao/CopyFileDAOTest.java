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

import com.wipro.ats.bdre.md.beans.CopyFileInfo;
import com.wipro.ats.bdre.md.beans.FileInfo;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by PR324290 on 11/5/2015.
 */
public class CopyFileDAOTest {

    private static final Logger LOGGER = Logger.getLogger(CopyFileDAOTest.class);

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    @Autowired
    CopyFileDAO copyFileDAO;

    @Test
    public void copyFileTest() {
        CopyFileInfo copyFileInfo = new CopyFileInfo();
        copyFileInfo.setDestBatchId(Long.valueOf(0));
        copyFileInfo.setDestPrefix("Test123");
        copyFileInfo.setDestServerId(123461);
        copyFileInfo.setSourceBatchId(Long.valueOf(0));
        FileInfo fileInfo = copyFileDAO.copyFile(copyFileInfo);
        LOGGER.info("fileInfo is " + fileInfo);
    }
}


