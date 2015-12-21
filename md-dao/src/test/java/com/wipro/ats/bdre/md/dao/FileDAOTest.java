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

import com.wipro.ats.bdre.md.beans.FileInfo;
import com.wipro.ats.bdre.md.dao.jpa.Batch;
import com.wipro.ats.bdre.md.dao.jpa.File;
import com.wipro.ats.bdre.md.dao.jpa.FileId;
import com.wipro.ats.bdre.md.dao.jpa.Servers;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Date;
import java.util.List;

/**
 * Created by MR299389 on 10/29/2015.
 */
public class FileDAOTest {
    private static final Logger LOGGER = Logger.getLogger(FileDAOTest.class);

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    @Autowired
    FileDAO fileDAO;
    @Autowired
    BatchDAO batchDAO;
    @Autowired
    ServersDAO serversDAO;

    @Test
    public void testList() throws Exception {
        LOGGER.info("Size of File is atleast:" + fileDAO.list(0, 10).size());
    }

    @Test
    public void testTotalRecordCount() throws Exception {
        LOGGER.info("Total Size of File is:" + fileDAO.totalRecordCount());
    }

    @Test
    @Ignore
    public void testGet() throws Exception {
        FileId fileId = new FileId();
        fileId.setBatchId(1L);
        fileId.setCreationTs(new Date());
        fileId.setFileSize(1L);
        fileId.setServerId(1);
        fileId.setPath("");
        LOGGER.info("File(1) size is:" + fileDAO.get(fileId).getId().getFileSize());

    }

    @Test
    public void testInsertUpdateAndDelete() throws Exception {
        File file = new File();
        FileId fileId = new FileId();
        fileId.setBatchId(0L);
        fileId.setCreationTs(new Date());
        fileId.setFileSize(1L);
        fileId.setServerId(123461);
        fileId.setPath("Test path");
        fileId.setFileHash("Hash");
        Batch batch = batchDAO.get((long) 0);
        Servers servers = serversDAO.get(123461);
        file.setId(fileId);
        file.setBatch(batch);
        file.setServers(servers);
        fileId = fileDAO.insert(file);
        LOGGER.info("File is added with BatchId:" + fileId.getBatchId());
        //Yet to test update,get and delete method
        //fileDAO.update(file);
        //fileId=fileDAO.get(fileId).getId();
        //LOGGER.info("Updated path is:"+fileId.getPath());
        fileDAO.delete(fileId.getBatchId());
        // LOGGER.info("Deleted File Entry with BatchId:"+fileId.getBatchId());
    }


    @Test
    public void testGetFiles() throws Exception {
        List<FileInfo> fileInfos = fileDAO.getFiles(0, 1);
        for (FileInfo fileInfo : fileInfos) {
            LOGGER.info("file Info is " + fileInfo);
        }
    }

    @Ignore
    @Test
    public void testGetFile() throws Exception {
        com.wipro.ats.bdre.md.beans.table.File file = new com.wipro.ats.bdre.md.beans.table.File();
        file.setBatchId((long) 0);
        com.wipro.ats.bdre.md.beans.table.File returnedFile = fileDAO.getFile(file);
        LOGGER.info("Path is " + returnedFile.getPath());
    }

    @Test
    public void testInsert() {
        com.wipro.ats.bdre.md.beans.table.File file = new com.wipro.ats.bdre.md.beans.table.File();
        file.setFileSize((long) 1);
        file.setCreationTS(new Date());
        file.setBatchId((long) 0);
        file.setFileHash("test");
        file.setPath("testPath");
        file.setServerId(123461);
        com.wipro.ats.bdre.md.beans.table.File returnedFile = new com.wipro.ats.bdre.md.beans.table.File();
        returnedFile = fileDAO.insert(file);
        //file.setPath("new path");
        // fileDAO.update(file);
        fileDAO.delete(file.getBatchId());
    }
}
