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

import com.wipro.ats.bdre.exception.MetadataException;
import com.wipro.ats.bdre.md.beans.CopyFileInfo;
import com.wipro.ats.bdre.md.beans.FileInfo;
import com.wipro.ats.bdre.md.dao.jpa.Batch;
import com.wipro.ats.bdre.md.dao.jpa.File;
import com.wipro.ats.bdre.md.dao.jpa.FileId;
import com.wipro.ats.bdre.md.dao.jpa.Servers;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by PR324290 on 11/3/2015.
 */
@Transactional
@Component
public class CopyFileDAO {


    private static final Logger LOGGER = Logger.getLogger(CopyFileDAO.class);
    @Autowired
    SessionFactory sessionFactory;

    public FileInfo copyFile(CopyFileInfo copyFileInfo) {
        Session session = sessionFactory.openSession();

        try {
            session.beginTransaction();

            if (copyFileInfo.getDestBatchId() == null || copyFileInfo.getDestServerId() == null || copyFileInfo.getSourceBatchId() == null) {
                LOGGER.info("Null params except for prefix not allowed");
                throw new MetadataException("Null params except for prefix not allowed");
                //this function should not be executed

            }


            //deleting the file

            Criteria deletingFileCriteria = session.createCriteria(File.class).add(Restrictions.eq("id.serverId", copyFileInfo.getDestServerId())).add(Restrictions.eq("id.batchId", copyFileInfo.getDestBatchId()));
            List<File> deletingFiles = deletingFileCriteria.list();

            if (deletingFiles != null) {
                for (File deletingFile : deletingFiles) {
                    session.delete(deletingFile);
                    LOGGER.info("creation time of the file deleted is " + deletingFile.getId().getCreationTs());

                }
            }

            //adding the file into the database
            Batch sourceBatch = (Batch) session.get(Batch.class, copyFileInfo.getSourceBatchId());
            LOGGER.info("sourceBatch" + sourceBatch.getBatchId());
            Servers destServer = (Servers) session.get(Servers.class, copyFileInfo.getDestServerId());
            Batch destBatch = (Batch) session.get(Batch.class, copyFileInfo.getDestBatchId());

            Criteria addingFileCriteria = session.createCriteria(File.class).add(Restrictions.eq("id.batchId", copyFileInfo.getSourceBatchId()));
            LOGGER.info("files count:" + addingFileCriteria.list().size());
            List<File> addingFiles = addingFileCriteria.list();
            for (File addingFile : addingFiles) {
                LOGGER.info(" IDPath" + addingFile.getId().getPath() + "Batchid" + addingFile.getBatch().getBatchId());
                FileId addingFileId = addingFile.getId();
                addingFileId.setPath(copyFileInfo.getDestPrefix() + addingFileId.getPath());
                addingFileId.setBatchId(destBatch.getBatchId());
                addingFileId.setServerId(destServer.getServerId());


                File file = new File();
                file.setServers(destServer);
                file.setBatch(destBatch);
                file.setId(addingFileId);
                LOGGER.info("adding file id is " + file.getId().getBatchId());

                LOGGER.info("adding file dest batch id : " + addingFile.getBatch().getBatchId());
                LOGGER.info("adding file server id" + addingFile.getServers().getServerId());


                //LOGGER.info("File added with Path: "+addingFile.getId().getPath());

                session.save(file);

            }

            // Returning fileInfo joining Servers and File
            LOGGER.info("return dest server id is " + destServer.getServerId());
            LOGGER.info("return batch id is " + destBatch.getBatchId());
            Criteria returningFilesCriteria = session.createCriteria(File.class).add(Restrictions.eq("batch", destBatch)).add(Restrictions.eq("servers", destServer));
            // Criteria returningServersCriteria = session.createCriteria(Servers.class);
            returningFilesCriteria.setMaxResults(1);
            List<File> returningFiles = returningFilesCriteria.list();

            LOGGER.info(returningFiles.size() + " return file size");
            File returningFile = new File();
            if (returningFiles.size() != 0)
                returningFile = returningFiles.get(0);
            LOGGER.info("Returning:" + returningFile);

            FileInfo fileInfo = new FileInfo();

            if (returningFile.getBatch() != null && returningFile != null)
                fileInfo.setBatchId(returningFile.getBatch().getBatchId());
            if (returningFile.getId() != null && returningFile != null) {
                fileInfo.setFileHash(returningFile.getId().getFileHash());
                fileInfo.setFilePath(returningFile.getId().getPath());
                fileInfo.setFileSize(returningFile.getId().getFileSize());
            }
            destServer = (Servers) session.get(Servers.class, copyFileInfo.getDestServerId());
            if (destServer != null) {
                fileInfo.setServerIP(destServer.getServerIp());
                fileInfo.setServerName(destServer.getServerName());
                fileInfo.setServerType(destServer.getServerType());
                fileInfo.setSshPrivateKey(destServer.getSshPrivateKey());
                fileInfo.setUsername(destServer.getLoginUser());
                fileInfo.setPassword(destServer.getLoginPassword());
            }
            session.getTransaction().commit();
            return fileInfo;

        } catch (MetadataException e) {
            session.getTransaction().rollback();
            throw new MetadataException(e);

        } finally {
            session.close();
        }

    }
}
