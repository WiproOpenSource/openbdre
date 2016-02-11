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
import com.wipro.ats.bdre.md.beans.GetETLDriverInfo;
import com.wipro.ats.bdre.md.dao.jpa.Batch;
import com.wipro.ats.bdre.md.dao.jpa.File;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SH324337 on 11/3/2015.
 */
@Transactional
@Service
public class GetETLInfoDAO {
    private static final Logger LOGGER = Logger.getLogger(GetETLInfoDAO.class);
    @Autowired
    SessionFactory sessionFactory;

    public List<GetETLDriverInfo> getETLInfo(long minBatchId, long maxBatchId) throws Exception {
        Session session = sessionFactory.openSession();
        List<GetETLDriverInfo> getETLDriverInfoList = new ArrayList<GetETLDriverInfo>();
        try {
            session.beginTransaction();
            List<Batch> batchList = new ArrayList<Batch>();
            List<File> fileList = new ArrayList<File>();

            for (Long i = minBatchId; i <= maxBatchId; i++) {

                Batch batchSizeList = (Batch) session.get(Batch.class, i);
                if (batchSizeList != null) {
                    batchList.add(batchSizeList);
                }
            }
            if (batchList.size() != 0) {
                Criteria FileCriteria = session.createCriteria(File.class).add(Restrictions.in("batch", batchList));
                fileList = FileCriteria.list();
            }
            for (File file : fileList) {
                GetETLDriverInfo getETLDriverInfo = new GetETLDriverInfo();
                getETLDriverInfo.setFileList(String.valueOf(file.getBatch().getBatchId()) + "," + String.valueOf(file.getServers().getServerId()) + "," + file.getId().getPath() + "," + String.valueOf(file.getId().getFileSize() + "," + file.getId().getFileHash() + "," + file.getId().getCreationTs()));
                getETLDriverInfoList.add(getETLDriverInfo);

            }
            session.getTransaction().commit();
            return getETLDriverInfoList;

        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.info("Error Occured " + e);
            return null;
        } finally {
            session.close();
        }
    }
}
