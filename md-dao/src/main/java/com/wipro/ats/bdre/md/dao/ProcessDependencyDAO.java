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
import com.wipro.ats.bdre.md.beans.ProcessDependencyInfo;
import com.wipro.ats.bdre.md.dao.jpa.Process;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MI294210 on 11/3/2015.
 */
@Transactional
@Service

public class ProcessDependencyDAO {

    private static final Logger LOGGER = Logger.getLogger(ProcessDependencyDAO.class);
    @Autowired
    SessionFactory sessionFactory;
    private static final String DELETEFLAG="deleteFlag";



    public List<ProcessDependencyInfo> listUD(Integer parentProcessId) {
        List<ProcessDependencyInfo> upstreamDownstreamProcessList = new ArrayList<ProcessDependencyInfo>();
        Session session = sessionFactory.openSession();
        try {

            session.beginTransaction();

            //fetching the parent process
            Process passedProcess = (Process) session.get(Process.class, parentProcessId);
            Process parentProcess = null;

            if (passedProcess != null) {
                parentProcess = passedProcess.getProcess();
            }


            //check for valid parent process
            if (passedProcess == null || parentProcess != null) {
                throw new MetadataException("Invalid parent-process. pid=:" + parentProcessId);
            } else {
                //adding process to List
                ProcessDependencyInfo processInfo = new ProcessDependencyInfo();
                processInfo.setProcessId(passedProcess.getProcessId());
                processInfo.setBusDomainId(passedProcess.getBusDomain().getBusDomainId());
                processInfo.setBatchPattern(passedProcess.getBatchCutPattern());
                processInfo.setCanRecover(passedProcess.getCanRecover());
                processInfo.setProcessName(passedProcess.getProcessName());
                processInfo.setDescription(passedProcess.getDescription());
                processInfo.setProcessTypeId(passedProcess.getProcessType().getProcessTypeId());
                processInfo.setParentProcessId(null);
                processInfo.setEnqProcessId(passedProcess.getEnqueuingProcessId());
                processInfo.setNextProcessIds(passedProcess.getNextProcessId());
                processInfo.setAddTS((Timestamp) passedProcess.getAddTs());
                processInfo.setRowType("P");

                LOGGER.info(processInfo);
                upstreamDownstreamProcessList.add(processInfo);


                //Listing downstream processes
                Criteria checkDownstreamParentProcesses = session.createCriteria(Process.class).add(Restrictions.or(Restrictions.eq("enqueuingProcessId", parentProcessId.toString()),Restrictions.like("enqueuingProcessId","%,"+parentProcessId.toString()),Restrictions.like("enqueuingProcessId",parentProcessId.toString()+",%"),Restrictions.like("enqueuingProcessId","%,"+parentProcessId.toString()+",%")))
                        .add(Restrictions.eq(DELETEFLAG, false)).setProjection(Projections.property("process"));
                List<Integer> downstreamParentProcessIdList = new ArrayList<Integer>();
                if (!checkDownstreamParentProcesses.list().isEmpty()) {
                    List<Process> downstreamParentProcessList = checkDownstreamParentProcesses.list();
                    for (Process p : downstreamParentProcessList) {
                        if (p!=null)
                        downstreamParentProcessIdList.add(p.getProcessId());
                    }
                    Criteria checkDownstreamProcesses = session.createCriteria(Process.class).add(Restrictions.in("processId", downstreamParentProcessIdList));
                    List<Process> downstreamProcessList = new ArrayList<Process>();
                    if (!checkDownstreamProcesses.list().isEmpty()) {
                        downstreamProcessList = checkDownstreamProcesses.list();

                        for (Process downProcess : downstreamProcessList) {
                            ProcessDependencyInfo downProcessInfo = new ProcessDependencyInfo();
                            downProcessInfo.setProcessId(downProcess.getProcessId());
                            downProcessInfo.setBusDomainId(downProcess.getBusDomain().getBusDomainId());
                            downProcessInfo.setBatchPattern(downProcess.getBatchCutPattern());
                            downProcessInfo.setCanRecover(downProcess.getCanRecover());
                            downProcessInfo.setProcessName(downProcess.getProcessName());
                            downProcessInfo.setDescription(downProcess.getDescription());
                            downProcessInfo.setProcessTypeId(downProcess.getProcessType().getProcessTypeId());
                            downProcessInfo.setParentProcessId(null);
                            downProcessInfo.setEnqProcessId(downProcess.getEnqueuingProcessId());
                            downProcessInfo.setNextProcessIds(downProcess.getNextProcessId());
                            downProcessInfo.setAddTS((Timestamp) downProcess.getAddTs());
                            downProcessInfo.setRowType("D");
                            LOGGER.info(downProcessInfo);

                            upstreamDownstreamProcessList.add(downProcessInfo);
                        }
                    }

                } else {
                    LOGGER.info("No downstream processes present");
                }
                //Listing upstream processes
                Criteria checkUpstreamParentProcesses = session.createCriteria(Process.class).add(Restrictions.eq("process", passedProcess)).add(Restrictions.eq(DELETEFLAG, false)).setProjection(Projections.property("enqueuingProcessId"));

                List<String> upstreamParentProcessListString=checkUpstreamParentProcesses.list();

                List<Integer> upstreamParentProcessList = new ArrayList<Integer>();
                for (String temp:upstreamParentProcessListString)
                {
                    LOGGER.info(temp);
                    String[] arrayUp=temp.split(",");
                    for (int i=0;i<arrayUp.length;i++)
                     upstreamParentProcessList.add(Integer.parseInt(arrayUp[i]));
                }
                if (!upstreamParentProcessList.isEmpty()) {
                   // upstreamParentProcessList = checkUpstreamParentProcesses.list();

                    Criteria checkUpstreamProcesses = session.createCriteria(Process.class).add(Restrictions.in("processId", upstreamParentProcessList)).add(Restrictions.eq(DELETEFLAG, false));
                    List<Process> upstreamProcessList = new ArrayList<Process>();
                    if (!checkUpstreamProcesses.list().isEmpty()) {
                        upstreamProcessList = checkUpstreamProcesses.list();

                        for (Process upProcess : upstreamProcessList) {
                            ProcessDependencyInfo upProcessInfo = new ProcessDependencyInfo();
                            upProcessInfo.setProcessId(upProcess.getProcessId());
                            upProcessInfo.setBusDomainId(upProcess.getBusDomain().getBusDomainId());
                            upProcessInfo.setBatchPattern(upProcess.getBatchCutPattern());
                            upProcessInfo.setCanRecover(upProcess.getCanRecover());
                            upProcessInfo.setProcessName(upProcess.getProcessName());
                            upProcessInfo.setDescription(upProcess.getDescription());
                            upProcessInfo.setProcessTypeId(upProcess.getProcessType().getProcessTypeId());
                            upProcessInfo.setParentProcessId(null);
                            upProcessInfo.setEnqProcessId(upProcess.getEnqueuingProcessId());
                            upProcessInfo.setNextProcessIds(upProcess.getNextProcessId());
                            upProcessInfo.setAddTS((Timestamp) upProcess.getAddTs());
                            upProcessInfo.setRowType("U");
                            LOGGER.info(upProcessInfo);

                            upstreamDownstreamProcessList.add(upProcessInfo);
                        }
                    } else {
                        LOGGER.info("No upstream processes present");

                    }
                }
                session.getTransaction().commit();

            }
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error("Error occurred", e);
        } finally {
            session.close();
        }
        return upstreamDownstreamProcessList;
    }
}
