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

import com.wipro.ats.bdre.exception.MetadataException;
import com.wipro.ats.bdre.md.beans.FileInfo;
import com.wipro.ats.bdre.md.dao.jpa.Batch;
import com.wipro.ats.bdre.md.dao.jpa.File;
import com.wipro.ats.bdre.md.dao.jpa.FileId;
import com.wipro.ats.bdre.md.dao.jpa.Servers;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MR299389 on 10/29/2015.
 */
@Transactional
@Service
public class FileDAO {
    private static final Logger LOGGER = Logger.getLogger(FileDAO.class);
    @Autowired
    SessionFactory sessionFactory;
    FileId fileId = new FileId();
    public List<File> list(Integer pageNum, Integer numResults) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(File.class).addOrder(Order.desc("id.batchId"));
        criteria.setFirstResult(pageNum);
        criteria.setMaxResults(numResults);
        List<File> files = criteria.list();
        session.getTransaction().commit();
        session.close();
        return files;
    }

    public Long totalRecordCount() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        long size = session.createCriteria(File.class).list().size();
        session.getTransaction().commit();
        session.close();
        return size;
    }

    public File get(FileId fileId) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        File file = (File) session.get(File.class, fileId);
        session.getTransaction().commit();
        session.close();
        return file;
    }

    public FileId insert(File file) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            fileId = (FileId) session.save(file);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.info(" Error Occured " + e);
            return null;
        } finally {
            session.close();
        }
        return fileId;

    }

    //File cannot be updated since all the fields are included in embeddeLd.

    public com.wipro.ats.bdre.md.beans.table.File update(com.wipro.ats.bdre.md.beans.table.File tableFile) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            Criteria fetchFileList = session.createCriteria(File.class).add(Restrictions.eq("batch.batchId", tableFile.getBatchId()));
            File jpaFile = (File) fetchFileList.list().get(0);
            fileId.setPath(tableFile.getPath());
            fileId.setFileSize(tableFile.getFileSize());
            fileId.setFileHash(tableFile.getFileHash());
            fileId.setCreationTs(tableFile.getCreationTS());
            fileId.setServerId(tableFile.getServerId());
            fileId.setBatchId(tableFile.getBatchId());
            jpaFile.setId(fileId);
            Servers servers = new Servers();
            servers.setServerId(tableFile.getServerId());
            jpaFile.setServers(servers);
            Batch batch = new Batch();
            batch.setBatchId(tableFile.getBatchId());
            jpaFile.setBatch(batch);
            session.update(jpaFile);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.info("Error  Occured " + e);
            return null;
        } finally {
            session.close();
        }
        return tableFile;
    }

    public void delete(Long batchId) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            Criteria criteria = session.createCriteria(File.class).add(Restrictions.eq("batch.batchId", batchId));
            List<File> jpaFileList = criteria.list();
            for (File jpaFile : jpaFileList) {
                session.delete(jpaFile);
            }

            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.info("error occured " + e);

        } finally {
            session.close();
        }
    }


    public String getPath(Long batchId) {
        Session session = sessionFactory.openSession();
        String path="";
        try {
            session.beginTransaction();
            Criteria criteria = session.createCriteria(File.class).add(Restrictions.eq("batch.batchId", batchId));
            List<File> jpaFileList = criteria.list();
            File file=jpaFileList.get(0);
            path=file.getId().getPath();

            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.info("error occured " + e);

        } finally {
            session.close();
        }
        return path;
    }

    public List<com.wipro.ats.bdre.md.beans.FileInfo> getFiles(long minBatchId, long maxBatchId) {
        Session session = sessionFactory.openSession();
        List<FileInfo> fileInfos = new ArrayList<FileInfo>();
        try {
            session.beginTransaction();
            Criteria joinFileServers = session.createCriteria(File.class, "f").createAlias("f.servers", "s").add(Restrictions.between("f.batch.batchId", minBatchId, maxBatchId));
            joinFileServers.addOrder(Order.asc("f.batch.batchId"));

            List<File> joinedFileServerList = joinFileServers.list();
            LOGGER.info(joinedFileServerList.toString());
            for (File file : joinedFileServerList) {
                FileInfo fileInfo = new FileInfo();
                fileInfo.setBatchId(file.getBatch().getBatchId());
                fileInfo.setFilePath(file.getId().getPath());
                fileInfo.setFileHash(file.getId().getFileHash());
                fileInfo.setFileSize(file.getId().getFileSize());
                fileInfo.setServerIP(file.getServers().getServerIp());
                fileInfo.setServerName(file.getServers().getServerName());
                fileInfo.setServerType(file.getServers().getServerType());
                fileInfo.setSshPrivateKey(file.getServers().getSshPrivateKey());
                fileInfo.setUsername(file.getServers().getLoginUser());
                fileInfo.setPassword(file.getServers().getLoginPassword());
                fileInfo.setServerMetaInfo(file.getServers().getServerMetainfo());

                fileInfos.add(fileInfo);
                LOGGER.info("fileInfo added with Batch ID:" + fileInfo.getBatchId());
            }
            session.getTransaction().commit();
            return fileInfos;
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.info("Error Occured " + e);
            return fileInfos;
        } finally {
            session.close();
        }

    }


    public com.wipro.ats.bdre.md.beans.table.File getFile(com.wipro.ats.bdre.md.beans.table.File file) {
        Session session = sessionFactory.openSession();
        com.wipro.ats.bdre.md.beans.table.File tableFile = new com.wipro.ats.bdre.md.beans.table.File();
        try {
            session.beginTransaction();
            Criteria countCriteria = session.createCriteria(File.class);
            int counter = countCriteria.list().size();
            Batch jpaBatch = (Batch) session.get(Batch.class, file.getBatchId());
            Criteria criteria = session.createCriteria(File.class).add(Restrictions.eq("batch", jpaBatch));
            File jpaFile = (File) criteria.uniqueResult();
            tableFile = new com.wipro.ats.bdre.md.beans.table.File();
            if (jpaFile != null && jpaFile.getId() != null && jpaFile.getBatch() != null && jpaFile.getServers() != null) {
                tableFile.setBatchId(jpaFile.getBatch().getBatchId());
                tableFile.setServerId(jpaFile.getServers().getServerId());
                tableFile.setPath(jpaFile.getId().getPath());
                tableFile.setFileSize(jpaFile.getId().getFileSize());
                tableFile.setFileHash(jpaFile.getId().getFileHash());
                tableFile.setCreationTS(jpaFile.getId().getCreationTs());
            }
            tableFile.setCounter(counter);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.info("Error Occured" + e);
            return null;
        } finally {
            session.close();
        }
        return tableFile;
    }

    public com.wipro.ats.bdre.md.beans.table.File insert(com.wipro.ats.bdre.md.beans.table.File tableFile) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();

            fileId.setPath(tableFile.getPath());
            fileId.setFileSize(tableFile.getFileSize());
            fileId.setFileHash(tableFile.getFileHash());
            fileId.setCreationTs(tableFile.getCreationTS());
            fileId.setServerId(tableFile.getServerId());
            fileId.setBatchId(tableFile.getBatchId());

            File file = new File();
            file.setId(fileId);

            Batch batch = new Batch();
            batch.setBatchId(tableFile.getBatchId());
            file.setBatch(batch);

            Servers servers = new Servers();
            servers.setServerId(tableFile.getServerId());
            file.setServers(servers);

            session.save(file);

            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.info("Error occured " + e);
            return null;
        } finally {
            session.close();
        }
        return tableFile;
    }

}
