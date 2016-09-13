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

package com.wipro.ats.bdre.md.rest.util;

import com.wipro.ats.bdre.md.dao.jpa.*;
import com.wipro.ats.bdre.md.dao.jpa.Process;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by AR288503 on 1/2/2016.
 */
public class Dao2TableUtil {

    private static final Logger LOGGER = Logger.getLogger(Dao2TableUtil.class);

    private Dao2TableUtil(){

    }
    private static com.wipro.ats.bdre.md.beans.table.Process jpa2TableProcess(com.wipro.ats.bdre.md.dao.jpa.Process jpaProcess) {
        com.wipro.ats.bdre.md.beans.table.Process tableProcess = new com.wipro.ats.bdre.md.beans.table.Process();
        tableProcess.setProcessId(jpaProcess.getProcessId());
        tableProcess.setProcessName(jpaProcess.getProcessName());
        tableProcess.setAddTS(jpaProcess.getAddTs());
        tableProcess.setEditTS(jpaProcess.getEditTs());
        tableProcess.setCanRecover(jpaProcess.getCanRecover());
        tableProcess.setBusDomainId(jpaProcess.getBusDomain().getBusDomainId());
        tableProcess.setBatchPattern(jpaProcess.getBatchCutPattern());
        tableProcess.setProcessTypeId(jpaProcess.getProcessType().getProcessTypeId());
        tableProcess.setWorkflowId(jpaProcess.getWorkflowType().getWorkflowId());
        tableProcess.setDeleteFlag(jpaProcess.getDeleteFlag());
        tableProcess.setDescription(jpaProcess.getDescription());
        tableProcess.setEnqProcessId(jpaProcess.getEnqueuingProcessId());
        if(jpaProcess.getProcess()!=null)
            tableProcess.setParentProcessId(jpaProcess.getProcess().getProcessId());
        return tableProcess;
    }

    public static List<com.wipro.ats.bdre.md.beans.table.Process> jpaList2TableProcessList(List<com.wipro.ats.bdre.md.dao.jpa.Process> jpaProcessList) {
        List<com.wipro.ats.bdre.md.beans.table.Process> tableProcessList = new ArrayList<com.wipro.ats.bdre.md.beans.table.Process>();
        for (com.wipro.ats.bdre.md.dao.jpa.Process jpaProcess : jpaProcessList) {
            tableProcessList.add(jpa2TableProcess(jpaProcess));
        }
        return tableProcessList;
    }

    public static com.wipro.ats.bdre.md.beans.table.Properties jpa2TableProperties(Properties jpaProperties){
        com.wipro.ats.bdre.md.beans.table.Properties tableProperties=new com.wipro.ats.bdre.md.beans.table.Properties();
        tableProperties.setProcessId(jpaProperties.getProcess().getProcessId());
        tableProperties.setDescription(jpaProperties.getDescription());
        tableProperties.setConfigGroup(jpaProperties.getConfigGroup());
        tableProperties.setKey(jpaProperties.getId().getPropKey());
        tableProperties.setValue(jpaProperties.getPropValue());
        return tableProperties;
    }
    public static Process buildJPAProcess(Integer processTypeId, String name, String desc, Integer workflowTypeId,Integer busDomainId) {
        Process daoProcess = new Process();
        ProcessType daoProcessType = new ProcessType();
        daoProcessType.setProcessTypeId(processTypeId);
        daoProcess.setProcessType(daoProcessType);
        LOGGER.info("workflow type id is "+workflowTypeId);
        if (workflowTypeId != null) {
            com.wipro.ats.bdre.md.dao.jpa.WorkflowType daoWorkflowType = new com.wipro.ats.bdre.md.dao.jpa.WorkflowType();
            daoWorkflowType.setWorkflowId(workflowTypeId);
            daoProcess.setWorkflowType(daoWorkflowType);
        }
        else
        {
            com.wipro.ats.bdre.md.dao.jpa.WorkflowType daoWorkflowType = new com.wipro.ats.bdre.md.dao.jpa.WorkflowType();
            daoWorkflowType.setWorkflowId(1);
            daoProcess.setWorkflowType(daoWorkflowType);
        }
        com.wipro.ats.bdre.md.dao.jpa.BusDomain daoBusDomain = new com.wipro.ats.bdre.md.dao.jpa.BusDomain();
        daoBusDomain.setBusDomainId(busDomainId);
        daoProcess.setBusDomain(daoBusDomain);
        ProcessTemplate daoProcessTemplate = new ProcessTemplate();
        daoProcessTemplate.setProcessTemplateId(0);
        daoProcess.setProcessTemplate(daoProcessTemplate);
        daoProcess.setNextProcessId("0");

        daoProcess.setDescription(desc);
        daoProcess.setProcessName(name);
        daoProcess.setCanRecover(true);
        daoProcess.setDeleteFlag(false);
        daoProcess.setEnqueuingProcessId(0);
        daoProcess.setAddTs(new Date());
        daoProcess.setEditTs(new Date());
        PermissionType permissionType=new PermissionType();
        permissionType.setPermissionTypeId(7);
        daoProcess.setPermissionTypeByUserAccessId(permissionType);
        PermissionType permissionType1=new PermissionType();
        permissionType1.setPermissionTypeId(4);
        daoProcess.setPermissionTypeByGroupAccessId(permissionType1);
        PermissionType permissionType2=new PermissionType();
        permissionType2.setPermissionTypeId(0);
        daoProcess.setPermissionTypeByOthersAccessId(permissionType2);
        return daoProcess;
    }


    public static Properties buildJPAProperties(String configGrp, String key, String value, String desc) {
        Properties properties = new Properties();
        try {
            Process process = new Process();
            properties.setProcess(process);
            properties.setConfigGroup(configGrp);
            properties.setPropValue(value);
            PropertiesId propertiesId = new PropertiesId();
            propertiesId.setPropKey(key);
            properties.setId(propertiesId);
            properties.setDescription(desc);

        } catch (Exception e) {
            LOGGER.error(e);
        }
        return properties;
    }
}
