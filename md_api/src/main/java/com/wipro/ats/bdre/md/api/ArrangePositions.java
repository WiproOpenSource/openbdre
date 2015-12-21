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
package com.wipro.ats.bdre.md.api;

import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.beans.PositionsInfo;
import com.wipro.ats.bdre.md.beans.table.Process;
import com.wipro.ats.bdre.md.dao.ProcessDAO;
import com.wipro.ats.bdre.md.dao.PropertiesDAO;
import com.wipro.ats.bdre.md.dao.jpa.PropertiesId;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by RI294200 on 10/6/2015.
 */
public class ArrangePositions extends MetadataAPIBase {
    public ArrangePositions() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    private static final Logger LOGGER = Logger.getLogger(ArrangePositions.class);


    private static final Integer DELTAX = 200;
    private static final Integer DELTAY = 100;
    @Autowired
    private PropertiesDAO propertiesDAO;
    @Autowired
    private ProcessDAO processDAO;

    public Map<String, PositionsInfo> getListPositionInfo(Integer processId) {
        //List<PositionsInfo> listPositionInfo = new ArrayList<PositionsInfo>();
        Map<String, PositionsInfo> positionsInfoMap = new HashMap<String, PositionsInfo>();
        //Level and list of nodes map
        Map<Integer, List<PositionsInfo>> rankPositionsInfoMap = new HashMap<Integer, List<PositionsInfo>>();
        try {


            // List<Properties> propertiesList = new ArrayList<Properties>();
            //fetching parent process and adding it to the top of the list of processes that include sub-processes also.
            //process = s.selectOne("call_procedures.GetProcess", process);
            com.wipro.ats.bdre.md.dao.jpa.Process jpaProcess = processDAO.get(processId);

            Process process = new Process();
            if (jpaProcess != null) {
                process.setDescription(jpaProcess.getDescription());
                process.setAddTS(jpaProcess.getAddTs());
                process.setBatchPattern(jpaProcess.getBatchCutPattern());
                if (jpaProcess.getBusDomain() != null)
                    process.setBusDomainId(jpaProcess.getBusDomain().getBusDomainId());
                process.setEditTS(jpaProcess.getEditTs());
                process.setEnqProcessId(jpaProcess.getEnqueuingProcessId());
                process.setProcessId(jpaProcess.getProcessId());
                process.setNextProcessIds(jpaProcess.getNextProcessId());
                process.setProcessName(jpaProcess.getProcessName());
                process.setTableAddTS(jpaProcess.getAddTs().toString());
                process.setTableEditTS(jpaProcess.getEditTs().toString());
                if (jpaProcess.getWorkflowType() != null)
                    process.setWorkflowId(jpaProcess.getWorkflowType().getWorkflowId());
                if (jpaProcess.getProcessType() != null)
                    process.setProcessTypeId(jpaProcess.getProcessType().getProcessTypeId());
                process.setCanRecover(jpaProcess.getCanRecover());
                if (jpaProcess.getProcessTemplate() != null)
                    process.setProcessTemplateId(jpaProcess.getProcessTemplate().getProcessTemplateId());
                if (jpaProcess.getProcess() != null)
                    process.setParentProcessId(jpaProcess.getProcess().getProcessId());
            }

            //  List<Process> processes = s.selectList("call_procedures.GetSubProcesses", process);
            List<com.wipro.ats.bdre.md.dao.jpa.Process> jpaSubProcesses = processDAO.subProcesslist(processId);
            List<Process> processes = new ArrayList<Process>();
            for (com.wipro.ats.bdre.md.dao.jpa.Process jpaSubProcess : jpaSubProcesses) {
                Process subProcess = new Process();
                subProcess.setDescription(jpaSubProcess.getDescription());
                subProcess.setAddTS(jpaSubProcess.getAddTs());
                subProcess.setBatchPattern(jpaSubProcess.getBatchCutPattern());
                if (jpaSubProcess.getBusDomain() != null)
                    subProcess.setBusDomainId(jpaSubProcess.getBusDomain().getBusDomainId());
                subProcess.setEditTS(jpaSubProcess.getEditTs());
                subProcess.setEnqProcessId(jpaSubProcess.getEnqueuingProcessId());
                subProcess.setProcessId(jpaSubProcess.getProcessId());
                subProcess.setNextProcessIds(jpaSubProcess.getNextProcessId());
                subProcess.setProcessName(jpaSubProcess.getProcessName());
                subProcess.setTableAddTS(jpaSubProcess.getAddTs().toString());
                subProcess.setTableEditTS(jpaSubProcess.getEditTs().toString());
                if (jpaSubProcess.getWorkflowType() != null)
                    subProcess.setWorkflowId(jpaSubProcess.getWorkflowType().getWorkflowId());
                if (jpaSubProcess.getProcessType() != null)
                    subProcess.setProcessTypeId(jpaSubProcess.getProcessType().getProcessTypeId());
                subProcess.setCanRecover(jpaSubProcess.getCanRecover());
                if (jpaProcess.getProcessTemplate() != null)
                    subProcess.setProcessTemplateId(jpaSubProcess.getProcessTemplate().getProcessTemplateId());
                if (jpaProcess.getProcess() != null)
                    subProcess.setParentProcessId(jpaProcess.getProcess().getProcessId());

                processes.add(subProcess);
            }


            processes.add(0, process);
            //get all processes and set in a map
            for (Process p : processes) {
                PositionsInfo positionsInfo = new PositionsInfo();
                positionsInfo.setProcessId(p.getProcessId());
                positionsInfo.setLevel(0);
                positionsInfo.setParentProcessId(processId);
                positionsInfoMap.put(p.getProcessId().toString(), positionsInfo);
            }
            //set the children for each node
            for (Process p : processes) {
                PositionsInfo thisPositionInfo = positionsInfoMap.get(p.getProcessId().toString());
                String[] nextProcesses = p.getNextProcessIds().split(",");
                List<PositionsInfo> children = new ArrayList<PositionsInfo>();
                for (String nextProcess : nextProcesses) {
                    if (nextProcess.equals("0") || nextProcess.equals(processId.toString())) continue;
                    children.add(positionsInfoMap.get(nextProcess));
                }
                thisPositionInfo.setChildren(children);
            }

            setLevels(positionsInfoMap.get(processId.toString()));

            for (PositionsInfo positionsInfo : positionsInfoMap.values()) {
                int level = positionsInfo.getLevel();
                List<PositionsInfo> positionsInfoInGivenLevelList = rankPositionsInfoMap.get(level);
                if (positionsInfoInGivenLevelList == null)
                    positionsInfoInGivenLevelList = new ArrayList<PositionsInfo>();
                positionsInfoInGivenLevelList.add(positionsInfo);
                rankPositionsInfoMap.put(level, positionsInfoInGivenLevelList);
            }
            arrangePositions(rankPositionsInfoMap);
            savePositionsInDB(rankPositionsInfoMap);
            LOGGER.info("Saved properties into DB");

        } catch (Exception e) {
            LOGGER.error("error occurred", e);
        }
        return positionsInfoMap;
    }

    private void savePositionsInDB(Map<Integer,List<PositionsInfo>> rankPositionsInfoMap) {

        java.util.Date date= new java.util.Date();

        LOGGER.info("start time save positions in db"+date);
        try {
            List<PositionsInfo> allPositionsInfoList=new ArrayList<PositionsInfo>();
            for(int level: rankPositionsInfoMap.keySet()){
                List<PositionsInfo> positionsInfoList=rankPositionsInfoMap.get(level);

                //adding levelWise list to allList
                allPositionsInfoList.addAll(positionsInfoList);
            }

            if(allPositionsInfoList.size()>0) {
                //calling updateArrangePositions
                propertiesDAO.updateArrangePositions(allPositionsInfoList.get(0).getProcessId(),allPositionsInfoList);
                java.util.Date date1= new java.util.Date();
                LOGGER.info("close time save positions in db"+date);
            }
        } catch (Exception e) {
            LOGGER.error("error occurred in addToDatabase function",e);
        }
    }

    private void setLevels(PositionsInfo referenceNode) {
        List<PositionsInfo> childNodes = referenceNode.kids();
        //List<String> nextIds = Arrays.asList(referenceNode.nextProcessIds.split(","));
        for (PositionsInfo childNode : childNodes) {
            int parentLevel = referenceNode.getLevel();
            int childLevel = childNode.getLevel();

            if (childLevel <= parentLevel) {
                childLevel = parentLevel + 1;
            }
            childNode.setLevel(childLevel);
            if ((childNode.kids().size() == 1 && childNode.kids().get(0).getProcessId() == childNode.kids().get(0).getParentProcessId()) || childNode.kids().size() == 0) {
                LOGGER.debug("No more children to process");
            } else {
                setLevels(childNode);
            }
        }

    }

    private void arrangePositions(Map<Integer, List<PositionsInfo>> rankPositionsInfoMap) {
        int maxLevelDepth = 0;
        for (int level : rankPositionsInfoMap.keySet()) {
            List<PositionsInfo> positionsInfoList = rankPositionsInfoMap.get(level);
            int listLen = positionsInfoList.size();
            maxLevelDepth = listLen > maxLevelDepth ? listLen : maxLevelDepth;
        }
        int startX = 0;
        for (int level : rankPositionsInfoMap.keySet()) {

            List<PositionsInfo> positionsInfoList = rankPositionsInfoMap.get(level);
            int currentDepth = positionsInfoList.size();
            int startY = (maxLevelDepth - currentDepth) * DELTAY / 2;
            for (PositionsInfo positionsInfo : positionsInfoList) {
                positionsInfo.setxPos(startX);
                positionsInfo.setyPos(startY);
                startY += DELTAY;
            }
            startX += DELTAX;
        }
        rankPositionsInfoMap.get(0).get(0).setxPos(startX);
    }



    @Override
    public Object execute(String[] params) {
        return null;
    }
}
