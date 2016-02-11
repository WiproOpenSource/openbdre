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
import com.wipro.ats.bdre.md.beans.ProcessAncestorsInfo;
import com.wipro.ats.bdre.md.beans.table.Process;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by MI294210 on 11/24/2015.
 */
@Transactional
@Service
public class ProcessAncestorsDAO {
    private static final Logger LOGGER = Logger.getLogger(ProcessAncestorsDAO.class);
    @Autowired
    SessionFactory sessionFactory;

    public List<Process> listUpstreams(Integer processId) {
        List<Process> upstreamProcessList = new ArrayList<Process>();
        Session session = sessionFactory.openSession();

        try {
            session.beginTransaction();
            Criteria checkParentProcess = session.createCriteria(com.wipro.ats.bdre.md.dao.jpa.Process.class).add(Restrictions.eq("processId", processId)).add(Restrictions.isNull("process.processId"));
            Integer parentProcessCount = checkParentProcess.list().size();
            LOGGER.info("Parent process count:" + parentProcessCount);
            if (parentProcessCount == 0) {
                throw new MetadataException("Invalid parent process:" + processId);
            }

            Criteria checkEnqueuingProcesses = session.createCriteria(com.wipro.ats.bdre.md.dao.jpa.Process.class).add(Restrictions.eq("process.processId", processId)).add(Restrictions.eq("deleteFlag", false)).setProjection(Projections.distinct(Projections.property("enqueuingProcessId")));
            List<Integer> enqueuingProcessIdList = checkEnqueuingProcesses.list();

            LOGGER.info("Number of enqueuing process count:" + checkEnqueuingProcesses.list().size());
            if (enqueuingProcessIdList.size() != 0) {
                Criteria fetchEnqueuingProcessList = session.createCriteria(com.wipro.ats.bdre.md.dao.jpa.Process.class).add(Restrictions.eq("deleteFlag", false)).add(Restrictions.in("processId", enqueuingProcessIdList));
                List<com.wipro.ats.bdre.md.dao.jpa.Process> enqueuingProcessList = fetchEnqueuingProcessList.list();
                Integer enqProcessListCount = fetchEnqueuingProcessList.list().size();
                LOGGER.info("No. of upstream processes:" + enqProcessListCount);


                //Mapping jpa beans to normal md beans
                for (com.wipro.ats.bdre.md.dao.jpa.Process jpaProcess : enqueuingProcessList) {
                    Process process = new Process();
                    process.setProcessId(jpaProcess.getProcessId());
                    process.setProcessName(jpaProcess.getProcessName());
                    process.setProcessTypeId(jpaProcess.getProcessType().getProcessTypeId());
                    process.setBusDomainId(jpaProcess.getBusDomain().getBusDomainId());
                    process.setWorkflowId(jpaProcess.getWorkflowType().getWorkflowId());
                    process.setAddTS(jpaProcess.getAddTs());
                    process.setBatchPattern(jpaProcess.getBatchCutPattern());
                    process.setCanRecover(jpaProcess.getCanRecover());
                    process.setDescription(jpaProcess.getDescription());
                    process.setEditTS(jpaProcess.getEditTs());
                    process.setEnqProcessId(jpaProcess.getEnqueuingProcessId());
                    process.setNextProcessIds(jpaProcess.getNextProcessId());
                    if (jpaProcess.getProcess() != null) {
                        process.setParentProcessId(jpaProcess.getProcess().getProcessId());
                    }
                    if (jpaProcess.getProcessTemplate() != null) {
                        process.setProcessTemplateId(jpaProcess.getProcessTemplate().getProcessTemplateId());
                    }
                    process.setCounter(enqProcessListCount);
                    upstreamProcessList.add(process);

                }


            }
            session.getTransaction().commit();
            return upstreamProcessList;

        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error("Error occurred");
            throw new MetadataException(e);
        } finally {
            session.close();
        }

    }

    public ProcessAncestorsInfo fetchDetails(Integer processId) {
        ProcessAncestorsInfo processAncestorsInfo = new ProcessAncestorsInfo();
        Session session = sessionFactory.openSession();

        try {
            session.beginTransaction();

            Criteria checkParentProcess = session.createCriteria(com.wipro.ats.bdre.md.dao.jpa.Process.class).add(Restrictions.eq("processId", processId)).add(Restrictions.isNull("process.processId"));
            Integer parentProcessCount = checkParentProcess.list().size();
            LOGGER.info("Parent process count:" + parentProcessCount);
            if (parentProcessCount == 0) {
                throw new MetadataException("Invalid parent process:" + processId);
            }

            Criteria fetchMaxEditTs = session.createCriteria(com.wipro.ats.bdre.md.dao.jpa.Process.class).add(Restrictions.or(Restrictions.eq("processId", processId), Restrictions.eq("process.processId", processId))).setProjection(Projections.max("editTs"));
            Criteria fetchMaxInsertTs = session.createCriteria(com.wipro.ats.bdre.md.dao.jpa.ProcessDeploymentQueue.class).add(Restrictions.eq("process.processId", processId)).setProjection(Projections.max("insertTs"));
            Criteria fetchMaxSuccessTs = session.createCriteria(com.wipro.ats.bdre.md.dao.jpa.ProcessDeploymentQueue.class).add(Restrictions.eq("process.processId", processId)).add(Restrictions.eq("deployStatus.deployStatusId", (short) 3)).setProjection(Projections.max("endTs"));
            Criteria fetchMaxFailTs = session.createCriteria(com.wipro.ats.bdre.md.dao.jpa.ProcessDeploymentQueue.class).add(Restrictions.eq("process.processId", processId)).add(Restrictions.eq("deployStatus.deployStatusId", (short) 4)).setProjection(Projections.max("endTs"));
            Criteria fetchMaxDeploymentId = session.createCriteria(com.wipro.ats.bdre.md.dao.jpa.ProcessDeploymentQueue.class).add(Restrictions.eq("process.processId", processId)).setProjection(Projections.max("deploymentId"));

            Date editTs = (Date) fetchMaxEditTs.list().get(0);
            LOGGER.info("edit Ts:" + editTs);
            Date deployInsertTs = (Date) fetchMaxInsertTs.list().get(0);
            LOGGER.info(" deployInsertTs:" + deployInsertTs);
            Date deploySuccessTs = (Date) fetchMaxSuccessTs.list().get(0);
            LOGGER.info(" deploySuccessTs:" + deploySuccessTs);
            Date deployFailTs = (Date) fetchMaxFailTs.list().get(0);
            LOGGER.info(" deployFailTs:" + deployFailTs);
            Long deployId = (Long) fetchMaxDeploymentId.list().get(0);
            LOGGER.info("deployId:" + deployId);

            session.getTransaction().commit();

            processAncestorsInfo.setProcessId(processId);
            processAncestorsInfo.setEditTs(editTs);
            if (deployId != null) {
                processAncestorsInfo.setDeployId(deployId.intValue());
            }
            processAncestorsInfo.setDeployInsertTs(deployInsertTs);
            processAncestorsInfo.setDeploySuccessTs(deploySuccessTs);
            processAncestorsInfo.setDeployFailTs(deployFailTs);

            return processAncestorsInfo;
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            throw new MetadataException(e);
        } finally {
            session.close();
        }
    }

}
