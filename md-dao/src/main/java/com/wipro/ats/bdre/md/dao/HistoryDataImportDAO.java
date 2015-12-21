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
import com.wipro.ats.bdre.md.beans.table.IntermediateInfo;
import com.wipro.ats.bdre.md.dao.jpa.*;
import com.wipro.ats.bdre.md.dao.jpa.Process;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by KA294215 on 03-11-2015.
 */
@Transactional
@Service
public class HistoryDataImportDAO {
    private static final Logger LOGGER = Logger.getLogger(HistoryDataImportDAO.class);
    @Autowired
    SessionFactory sessionFactory;

    public List<com.wipro.ats.bdre.md.beans.table.Process> historyDataImport(IntermediateInfo intermediateInfo) throws Exception {
        Session session = sessionFactory.openSession();
        List<com.wipro.ats.bdre.md.beans.table.Process> createdProcesses = new ArrayList<com.wipro.ats.bdre.md.beans.table.Process>();

        try {
            session.beginTransaction();
            Criteria numOfTableCriteria = session.createCriteria(Intermediate.class).add(Restrictions.like("id.interKey", "baseDDL_%"))
                    .add(Restrictions.eq("id.uuid", intermediateInfo.getUuid()));
            List<Intermediate> intermediateList = numOfTableCriteria.list();

            int flag = 0;
            int numOfTableToIngest = 0;
            LOGGER.info("number of table is " + intermediateList.size());
            for (int i = 1; i <= intermediateList.size(); i++) {
                IntermediateId getInterValue = new IntermediateId();
                getInterValue.setInterKey("ingestOnly_" + i);
                getInterValue.setUuid(intermediateInfo.getUuid());
                Criteria interValueCriteria = session.createCriteria(Intermediate.class).add(Restrictions.eq("id", getInterValue));
                Intermediate interValueRow = (Intermediate) interValueCriteria.list().get(0);
                if ("false".equals(interValueRow.getInterValue())) {
                    flag = 1;
                    numOfTableToIngest += 1;
                }
            }
            IntermediateId getBusDomain = new IntermediateId();
            getBusDomain.setInterKey("busdomainid");
            getBusDomain.setUuid(intermediateInfo.getUuid());
            Criteria getBusDomainCriteria = session.createCriteria(Intermediate.class).add(Restrictions.eq("id", getBusDomain));
            Intermediate busDomainRow = (Intermediate) getBusDomainCriteria.list().get(0);
            Integer busDomainId = Integer.parseInt(busDomainRow.getInterValue());
            BusDomain busDomain = new BusDomain();
            busDomain.setBusDomainId(busDomainId);

            ProcessType dataLoadProcessType = new ProcessType();
            dataLoadProcessType.setProcessTypeId(5);
            ProcessType dataImportProcessType = new ProcessType();
            dataImportProcessType.setProcessTypeId(4);
            ProcessType childDataImportProcessType = new ProcessType();
            childDataImportProcessType.setProcessTypeId(13);
            ProcessType file2RawType = new ProcessType();
            file2RawType.setProcessTypeId(6);
            ProcessType raw2StageType = new ProcessType();
            raw2StageType.setProcessTypeId(7);
            ProcessType stage2BaseType = new ProcessType();
            stage2BaseType.setProcessTypeId(8);


            WorkflowType oozieType = new WorkflowType();
            oozieType.setWorkflowId(1);
            WorkflowType actionType = new WorkflowType();
            actionType.setWorkflowId(0);

            Process nullProcess = new Process();
            nullProcess.setProcessId(null);
            List<Integer> parentProcessIdList = new ArrayList<Integer>();
            Process dataLoadParent = new Process();


            String nextProcessForDataLoadParent = "";
            String nextProcessForF2R = "";
            String nextProcessForR2S = "";


            if (flag == 1) {
                dataLoadParent.setDescription("Data Load Parent");
                dataLoadParent.setAddTs(new Date());
                dataLoadParent.setProcessName("Data Load");
                dataLoadParent.setBusDomain(busDomain);
                dataLoadParent.setProcessType(dataLoadProcessType);
                dataLoadParent.setNextProcessId("");
                dataLoadParent.setCanRecover(false);
                dataLoadParent.setEnqueuingProcessId(0);
                dataLoadParent.setBatchCutPattern(null);
                dataLoadParent.setDeleteFlag(false);
                dataLoadParent.setWorkflowType(oozieType);
                session.save(dataLoadParent);
                LOGGER.info("the inserted data load parent is " + dataLoadParent.getProcessId());
                parentProcessIdList.add(dataLoadParent.getProcessId());
            }
            for (int i = 1; i <= intermediateList.size(); i++) {
                Process dataImportProcess = new Process();
                dataImportProcess.setDescription("Data Import" + i);
                dataImportProcess.setAddTs(new Date());
                dataImportProcess.setProcessName("Data Import" + i);
                dataImportProcess.setBusDomain(busDomain);
                dataImportProcess.setProcessType(dataImportProcessType);
                dataImportProcess.setNextProcessId("");
                dataImportProcess.setCanRecover(false);
                dataImportProcess.setEnqueuingProcessId(0);
                dataImportProcess.setBatchCutPattern(null);
                dataImportProcess.setWorkflowType(oozieType);
                dataImportProcess.setDeleteFlag(false);
                session.save(dataImportProcess);
                LOGGER.info("the inserted data import parent is " + dataImportProcess.getProcessId());
                parentProcessIdList.add(dataImportProcess.getProcessId());

                if (i == 1) {
                    //something special to be done
                }

                Process childDataImportProcess = new Process();
                childDataImportProcess.setDescription("Child Of  Data Import");
                childDataImportProcess.setProcessName("child of  data import");
                childDataImportProcess.setProcess(dataImportProcess);
                childDataImportProcess.setBusDomain(busDomain);
                childDataImportProcess.setProcessType(childDataImportProcessType);
                childDataImportProcess.setEnqueuingProcessId(0);
                childDataImportProcess.setNextProcessId(dataImportProcess.getProcessId().toString());
                childDataImportProcess.setWorkflowType(actionType);
                childDataImportProcess.setDeleteFlag(false);
                session.save(childDataImportProcess);
                LOGGER.info("the inserted data import is " + childDataImportProcess.getProcessId());
                dataImportProcess.setNextProcessId(childDataImportProcess.getProcessId().toString());
                session.update(dataImportProcess);


                IntermediateId intermediateIdDB = new IntermediateId();
                intermediateIdDB.setUuid(intermediateInfo.getUuid());
                intermediateIdDB.setInterKey("db");

                Criteria dbValueCriteria = session.createCriteria(Intermediate.class).add(Restrictions.eq("id", intermediateIdDB));
                Intermediate dbValue = (Intermediate) dbValueCriteria.list().get(0);

                PropertiesId propertiesIdDB = new PropertiesId();
                propertiesIdDB.setProcessId(childDataImportProcess.getProcessId());
                propertiesIdDB.setPropKey(dbValue.getId().getInterKey());
                Properties dbProperties = new Properties();
                dbProperties.setId(propertiesIdDB);
                dbProperties.setConfigGroup("imp-common");
                dbProperties.setPropValue(dbValue.getInterValue());
                dbProperties.setDescription("properties for  data import");
                session.save(dbProperties);


                IntermediateId intermediateIdDriver = new IntermediateId();
                intermediateIdDriver.setUuid(intermediateInfo.getUuid());
                intermediateIdDriver.setInterKey("driver");

                Criteria driverValueCriteria = session.createCriteria(Intermediate.class).add(Restrictions.eq("id", intermediateIdDriver));
                Intermediate driverValue = (Intermediate) driverValueCriteria.list().get(0);

                PropertiesId propertiesIdDriver = new PropertiesId();
                propertiesIdDriver.setProcessId(childDataImportProcess.getProcessId());
                propertiesIdDriver.setPropKey(driverValue.getId().getInterKey());
                Properties driverProperties = new Properties();
                driverProperties.setId(propertiesIdDriver);
                driverProperties.setConfigGroup("imp-common");
                driverProperties.setPropValue(driverValue.getInterValue());
                driverProperties.setDescription("properties for  data import");
                session.save(driverProperties);

                IntermediateId intermediateIdPassword = new IntermediateId();
                intermediateIdPassword.setUuid(intermediateInfo.getUuid());
                intermediateIdPassword.setInterKey("password");

                Criteria passwordValueCriteria = session.createCriteria(Intermediate.class).add(Restrictions.eq("id", intermediateIdPassword));
                Intermediate passwordValue = (Intermediate) passwordValueCriteria.list().get(0);

                PropertiesId propertiesIdPassword = new PropertiesId();
                propertiesIdPassword.setProcessId(childDataImportProcess.getProcessId());
                propertiesIdPassword.setPropKey(passwordValue.getId().getInterKey());
                Properties passwordProperties = new Properties();
                passwordProperties.setId(propertiesIdPassword);
                passwordProperties.setConfigGroup("imp-common");
                passwordProperties.setPropValue(passwordValue.getInterValue());
                passwordProperties.setDescription("properties for  data import");
                session.save(passwordProperties);


                IntermediateId intermediateIdUserName = new IntermediateId();
                intermediateIdUserName.setUuid(intermediateInfo.getUuid());
                intermediateIdUserName.setInterKey("username");

                Criteria userNameValueCriteria = session.createCriteria(Intermediate.class).add(Restrictions.eq("id", intermediateIdUserName));
                Intermediate userNameValue = (Intermediate) userNameValueCriteria.list().get(0);

                PropertiesId propertiesIdUserName = new PropertiesId();
                propertiesIdUserName.setProcessId(childDataImportProcess.getProcessId());
                propertiesIdUserName.setPropKey(userNameValue.getId().getInterKey());
                Properties userNameProperties = new Properties();
                userNameProperties.setId(propertiesIdUserName);
                userNameProperties.setConfigGroup("imp-common");
                userNameProperties.setPropValue(userNameValue.getInterValue());
                userNameProperties.setDescription("properties for  data import");
                session.save(userNameProperties);


                IntermediateId intermediateIdRawName = new IntermediateId();
                intermediateIdRawName.setUuid(intermediateInfo.getUuid());
                intermediateIdRawName.setInterKey("rawTableName_" + i);

                Criteria rawNameValueCriteria = session.createCriteria(Intermediate.class).add(Restrictions.eq("id", intermediateIdRawName));
                Intermediate rawNameValue = (Intermediate) rawNameValueCriteria.list().get(0);

                PropertiesId propertiesIdRawName = new PropertiesId();
                propertiesIdRawName.setProcessId(childDataImportProcess.getProcessId());
                propertiesIdRawName.setPropKey(rawNameValue.getId().getInterKey());
                Properties rawNameProperties = new Properties();
                rawNameProperties.setId(propertiesIdRawName);
                rawNameProperties.setConfigGroup("imp-common");
                rawNameProperties.setPropValue(rawNameValue.getInterValue());
                rawNameProperties.setDescription("properties for  data import");
                session.save(rawNameProperties);

                PropertiesId propertiesIdFileLayout = new PropertiesId();
                propertiesIdFileLayout.setProcessId(childDataImportProcess.getProcessId());
                propertiesIdFileLayout.setPropKey("file.layout");
                Properties fileLayoutProperties = new Properties();
                fileLayoutProperties.setId(propertiesIdFileLayout);
                fileLayoutProperties.setConfigGroup("imp-common");
                fileLayoutProperties.setPropValue("textFile");
                fileLayoutProperties.setDescription("properties for  data import");
                session.save(fileLayoutProperties);


                PropertiesId propertiesIdImport = new PropertiesId();
                propertiesIdImport.setProcessId(childDataImportProcess.getProcessId());
                propertiesIdImport.setPropKey("'import'");
                Properties importProperties = new Properties();
                importProperties.setId(propertiesIdImport);
                importProperties.setConfigGroup("imp-common");
                importProperties.setPropValue("1");
                importProperties.setDescription("properties for  data import");
                session.save(importProperties);


                PropertiesId propertiesIdMappers = new PropertiesId();
                propertiesIdMappers.setProcessId(childDataImportProcess.getProcessId());
                propertiesIdMappers.setPropKey("''mappers''");
                Properties mapeersProperties = new Properties();
                mapeersProperties.setId(propertiesIdMappers);
                mapeersProperties.setConfigGroup("imp-common");
                mapeersProperties.setPropValue("1");
                mapeersProperties.setDescription("properties for  data import");
                session.save(mapeersProperties);


                IntermediateId intermediateIdColumnList = new IntermediateId();
                intermediateIdColumnList.setUuid(intermediateInfo.getUuid());
                intermediateIdColumnList.setInterKey("columnList_" + i);

                Criteria columnListValueCriteria = session.createCriteria(Intermediate.class).add(Restrictions.eq("id", intermediateIdColumnList));
                Intermediate columnListValue = (Intermediate) columnListValueCriteria.list().get(0);

                PropertiesId propertiesIdColumnList = new PropertiesId();
                propertiesIdColumnList.setProcessId(childDataImportProcess.getProcessId());
                propertiesIdColumnList.setPropKey("columns'");
                Properties columnListProperties = new Properties();
                columnListProperties.setId(propertiesIdColumnList);
                columnListProperties.setConfigGroup("imp-common");
                columnListProperties.setPropValue(columnListValue.getInterValue());
                columnListProperties.setDescription("properties for  data import");
                session.save(columnListProperties);


                IntermediateId intermediateIdIncrementType = new IntermediateId();
                intermediateIdIncrementType.setUuid(intermediateInfo.getUuid());
                intermediateIdIncrementType.setInterKey("incrementType_" + i);

                Criteria incrementTypeValueCriteria = session.createCriteria(Intermediate.class).add(Restrictions.eq("id", intermediateIdIncrementType));
                Intermediate incrementTypeValue = (Intermediate) incrementTypeValueCriteria.list().get(0);

                PropertiesId propertiesIdIncrementType = new PropertiesId();
                propertiesIdIncrementType.setProcessId(childDataImportProcess.getProcessId());
                propertiesIdIncrementType.setPropKey("incr.mode");
                Properties incrementTypeProperties = new Properties();
                incrementTypeProperties.setId(propertiesIdIncrementType);
                incrementTypeProperties.setConfigGroup("imp-common");
                incrementTypeProperties.setPropValue(incrementTypeValue.getInterValue());
                incrementTypeProperties.setDescription("properties for  data import");
                session.save(incrementTypeProperties);


                IntermediateId intermediateIdCheckCol = new IntermediateId();
                intermediateIdCheckCol.setUuid(intermediateInfo.getUuid());
                intermediateIdCheckCol.setInterKey("primaryKeyColumn_" + i);

                Criteria checkColValueCriteria = session.createCriteria(Intermediate.class).add(Restrictions.eq("id", intermediateIdCheckCol));
                Intermediate checkColValue = (Intermediate) checkColValueCriteria.list().get(0);

                PropertiesId propertiesIdCheckCol = new PropertiesId();
                propertiesIdCheckCol.setProcessId(childDataImportProcess.getProcessId());
                propertiesIdCheckCol.setPropKey("check.col");
                Properties checkColProperties = new Properties();
                checkColProperties.setId(propertiesIdCheckCol);
                checkColProperties.setConfigGroup("imp-common");
                checkColProperties.setPropValue(checkColValue.getInterValue());
                checkColProperties.setDescription("properties for  data import");
                session.save(checkColProperties);

                String ingestOnlyCount = "ingestOnly_" + i;
                IntermediateId intermediateIdLoadOrNot = new IntermediateId();
                intermediateIdLoadOrNot.setUuid(intermediateInfo.getUuid());
                intermediateIdLoadOrNot.setInterKey(ingestOnlyCount);

                Criteria loadOrNotCriteria = session.createCriteria(Intermediate.class).add(Restrictions.eq("id", intermediateIdLoadOrNot));
                Intermediate loadOrNotValue = (Intermediate) loadOrNotCriteria.list().get(0);
                LOGGER.debug("loadOrNotValue " + loadOrNotValue.getInterValue());
                if ("false".equals(loadOrNotValue.getInterValue())) {
                    Process file2Raw = new Process();
                    file2Raw.setDescription("'File2Raw'");
                    file2Raw.setAddTs(new Date());
                    file2Raw.setProcessName("Data Load-F2R");
                    file2Raw.setBusDomain(busDomain);
                    file2Raw.setProcessType(file2RawType);
                    file2Raw.setNextProcessId("");
                    file2Raw.setProcess(dataLoadParent);
                    file2Raw.setCanRecover(false);
                    file2Raw.setEnqueuingProcessId(dataImportProcess.getProcessId());
                    file2Raw.setBatchCutPattern(null);
                    file2Raw.setDeleteFlag(false);
                    file2Raw.setWorkflowType(actionType);
                    session.save(file2Raw);

                    nextProcessForDataLoadParent += file2Raw.getProcessId().toString() + ",";

                    Process raw2Stage = new Process();
                    raw2Stage.setDescription("''Raw2Stage''");
                    raw2Stage.setAddTs(new Date());
                    raw2Stage.setProcessName("Data Load-R2S");
                    raw2Stage.setBusDomain(busDomain);
                    raw2Stage.setProcessType(raw2StageType);
                    raw2Stage.setProcess(dataLoadParent);
                    raw2Stage.setCanRecover(false);
                    raw2Stage.setNextProcessId("");
                    raw2Stage.setEnqueuingProcessId(dataImportProcess.getProcessId());
                    raw2Stage.setBatchCutPattern(null);
                    raw2Stage.setDeleteFlag(false);
                    raw2Stage.setWorkflowType(actionType);
                    session.save(raw2Stage);

                    nextProcessForF2R += raw2Stage.getProcessId().toString() + ",";

                    Process stage2Base = new Process();
                    stage2Base.setDescription("'''Stage2Base'''");
                    stage2Base.setAddTs(new Date());
                    stage2Base.setProcessName("Data Load-S2B");
                    stage2Base.setBusDomain(busDomain);
                    stage2Base.setProcessType(stage2BaseType);
                    stage2Base.setProcess(dataLoadParent);
                    stage2Base.setCanRecover(false);
                    stage2Base.setEnqueuingProcessId(0);
                    stage2Base.setBatchCutPattern(null);
                    stage2Base.setDeleteFlag(false);
                    stage2Base.setNextProcessId("");
                    stage2Base.setWorkflowType(actionType);
                    stage2Base.setNextProcessId(dataLoadParent.getProcessId().toString());
                    session.save(stage2Base);

                    nextProcessForR2S += stage2Base.getProcessId() + ",";

                    IntermediateId intermediateIdRawTableName = new IntermediateId();
                    intermediateIdRawTableName.setUuid(intermediateInfo.getUuid());
                    intermediateIdRawTableName.setInterKey("rawTableName_" + i);

                    Criteria rawTableNameCriteria = session.createCriteria(Intermediate.class).add(Restrictions.eq("id", intermediateIdRawTableName));
                    Intermediate rawTableName = (Intermediate) rawTableNameCriteria.list().get(0);

                    IntermediateId intermediateIdRawTableDDL = new IntermediateId();
                    intermediateIdRawTableDDL.setUuid(intermediateInfo.getUuid());
                    intermediateIdRawTableDDL.setInterKey("rawDDL_" + i);

                    Criteria rawTableDDLCriteria = session.createCriteria(Intermediate.class).add(Restrictions.eq("id", intermediateIdRawTableDDL));
                    Intermediate rawTableDDL = (Intermediate) rawTableDDLCriteria.list().get(0);

                    HiveTables rawTableHive = new HiveTables();
                    rawTableHive.setComments("for raw table");
                    rawTableHive.setLocationType("hdfs");
                    rawTableHive.setDbname("raw");
                    rawTableHive.setBatchIdPartitionCol("batchid");
                    rawTableHive.setTableName(rawTableName.getInterValue());
                    rawTableHive.setType("raw");
                    rawTableHive.setDdl(rawTableDDL.getInterValue());
                    session.save(rawTableHive);


                    IntermediateId intermediateIdBaseTableName = new IntermediateId();
                    intermediateIdBaseTableName.setUuid(intermediateInfo.getUuid());
                    intermediateIdBaseTableName.setInterKey("baseTableName_" + i);

                    Criteria baseTableNameCriteria = session.createCriteria(Intermediate.class).add(Restrictions.eq("id", intermediateIdBaseTableName));
                    Intermediate baseTableName = (Intermediate) baseTableNameCriteria.list().get(0);

                    IntermediateId intermediateIdBaseTableDDL = new IntermediateId();
                    intermediateIdBaseTableDDL.setUuid(intermediateInfo.getUuid());
                    intermediateIdBaseTableDDL.setInterKey("baseDDL_" + i);

                    Criteria baseTableDDLCriteria = session.createCriteria(Intermediate.class).add(Restrictions.eq("id", intermediateIdBaseTableDDL));
                    Intermediate baseTableDDL = (Intermediate) baseTableDDLCriteria.list().get(0);

                    IntermediateId intermediateIdBaseDB = new IntermediateId();
                    intermediateIdBaseDB.setUuid(intermediateInfo.getUuid());
                    intermediateIdBaseDB.setInterKey("hiveDB");

                    Criteria baseDBCriteria = session.createCriteria(Intermediate.class).add(Restrictions.eq("id", intermediateIdBaseDB));
                    Intermediate baseDB = (Intermediate) baseDBCriteria.list().get(0);

                    HiveTables baseTableHive = new HiveTables();
                    baseTableHive.setComments("for base table");
                    baseTableHive.setLocationType("hdfs");
                    baseTableHive.setDbname(baseDB.getInterValue());
                    baseTableHive.setBatchIdPartitionCol("batchid");
                    baseTableHive.setTableName(baseTableName.getInterValue());
                    baseTableHive.setType("base");
                    baseTableHive.setDdl(baseTableDDL.getInterValue());
                    session.save(baseTableHive);


                    IntermediateId intermediateIdViewName = new IntermediateId();
                    intermediateIdViewName.setUuid(intermediateInfo.getUuid());
                    intermediateIdViewName.setInterKey("rawViewName_" + i);

                    Criteria viewNameCriteria = session.createCriteria(Intermediate.class).add(Restrictions.eq("id", intermediateIdViewName));
                    Intermediate viewName = (Intermediate) viewNameCriteria.list().get(0);

                    IntermediateId intermediateIdViewDDL = new IntermediateId();
                    intermediateIdViewDDL.setUuid(intermediateInfo.getUuid());
                    intermediateIdViewDDL.setInterKey("rawViewDDL_" + i);

                    Criteria viewDDLCriteria = session.createCriteria(Intermediate.class).add(Restrictions.eq("id", intermediateIdViewDDL));
                    Intermediate viewDDL = (Intermediate) viewDDLCriteria.list().get(0);

                    HiveTables viewHive = new HiveTables();
                    viewHive.setComments("for view");
                    viewHive.setLocationType("hdfs");
                    viewHive.setDbname("raw");
                    viewHive.setBatchIdPartitionCol("batchid");
                    viewHive.setTableName(viewName.getInterValue());
                    viewHive.setType("view");
                    viewHive.setDdl(viewDDL.getInterValue());
                    session.save(viewHive);

                    EtlDriver rawETLDriver = new EtlDriver();
                    rawETLDriver.setEtlProcessId(file2Raw.getProcessId());
                    rawETLDriver.setProcess(file2Raw);
                    rawETLDriver.setHiveTablesByRawTableId(rawTableHive);
                    rawETLDriver.setHiveTablesByBaseTableId(baseTableHive);
                    rawETLDriver.setInsertType(Short.parseShort("1"));
                    rawETLDriver.setDropRaw(false);
                    rawETLDriver.setHiveTablesByRawViewId(viewHive);
                    session.save(rawETLDriver);


                    EtlDriver stageETLDriver = new EtlDriver();
                    stageETLDriver.setEtlProcessId(raw2Stage.getProcessId());
                    stageETLDriver.setProcess(raw2Stage);
                    stageETLDriver.setHiveTablesByRawTableId(rawTableHive);
                    stageETLDriver.setHiveTablesByBaseTableId(baseTableHive);
                    stageETLDriver.setInsertType(Short.parseShort("1"));
                    stageETLDriver.setDropRaw(false);
                    stageETLDriver.setHiveTablesByRawViewId(viewHive);
                    session.save(stageETLDriver);


                    EtlDriver baseETLDriver = new EtlDriver();
                    baseETLDriver.setEtlProcessId(stage2Base.getProcessId());
                    baseETLDriver.setProcess(stage2Base);
                    baseETLDriver.setHiveTablesByRawTableId(rawTableHive);
                    baseETLDriver.setHiveTablesByBaseTableId(baseTableHive);
                    baseETLDriver.setInsertType(Short.parseShort("1"));
                    baseETLDriver.setDropRaw(false);
                    baseETLDriver.setHiveTablesByRawViewId(viewHive);
                    session.save(baseETLDriver);
                }
            }
            if (flag == 1) {
                LOGGER.info("nextProcessForDataLoadParent is " + nextProcessForDataLoadParent);
                nextProcessForDataLoadParent = nextProcessForDataLoadParent.substring(0, nextProcessForDataLoadParent.length() - 1);
                nextProcessForF2R = nextProcessForF2R.substring(0, nextProcessForF2R.length() - 1);
                nextProcessForR2S = nextProcessForR2S.substring(0, nextProcessForR2S.length() - 1);
                dataLoadParent.setNextProcessId(nextProcessForDataLoadParent);
                session.update(dataLoadParent);


                Criteria fileToRawCriteria = session.createCriteria(Process.class).add(Restrictions.eq("processType", file2RawType))
                        .add(Restrictions.eq("process", dataLoadParent));
                for (Object fileToRawObject : fileToRawCriteria.list()) {
                    Process fileToRaw = (Process) fileToRawObject;
                    fileToRaw.setNextProcessId(nextProcessForF2R);
                    session.update(fileToRaw);
                }

                Criteria rawToStageCriteria = session.createCriteria(Process.class).add(Restrictions.eq("processType", raw2StageType))
                        .add(Restrictions.eq("process", dataLoadParent));
                for (Object rawToStageObject : rawToStageCriteria.list()) {
                    Process rawToStage = (Process) rawToStageObject;
                    rawToStage.setNextProcessId(nextProcessForR2S);
                    session.update(rawToStage);
                }
                if (parentProcessIdList.size() != 0) {
                    Criteria parentProcessCriteria = session.createCriteria(Process.class).add(Restrictions.in("processId", parentProcessIdList));
                    LOGGER.info("size of parent process list is" + parentProcessCriteria.list().size());
                    for (Object resultObject : parentProcessCriteria.list()) {
                        Process resultProcess = (Process) resultObject;
                        com.wipro.ats.bdre.md.beans.table.Process createdProcess = new com.wipro.ats.bdre.md.beans.table.Process();
                        createdProcess.setProcessId(resultProcess.getProcessId());
                        createdProcess.setDescription(resultProcess.getDescription());
                        createdProcess.setAddTS(resultProcess.getAddTs());
                        createdProcess.setProcessName(resultProcess.getProcessName());
                        createdProcess.setBusDomainId(resultProcess.getBusDomain().getBusDomainId());
                        createdProcess.setProcessTypeId(resultProcess.getProcessType().getProcessTypeId());
                        if (resultProcess.getProcess() != null) {
                            createdProcess.setParentProcessId(resultProcess.getProcess().getProcessId());
                        }
                        createdProcess.setCanRecover(resultProcess.getCanRecover());
                        createdProcess.setEnqProcessId(resultProcess.getEnqueuingProcessId());
                        if (resultProcess.getBatchCutPattern() != null) {
                            createdProcess.setBatchPattern(resultProcess.getBatchCutPattern());
                        }
                        createdProcess.setNextProcessIds(resultProcess.getNextProcessId());
                        if (resultProcess.getWorkflowType() != null) {
                            createdProcess.setWorkflowId(resultProcess.getWorkflowType().getWorkflowId());
                        }
                        createdProcess.setCounter(parentProcessCriteria.list().size());
                        createdProcesses.add(createdProcess);
                    }
                }


            } else {
                if (parentProcessIdList.size() != 0) {
                    Criteria parentProcessCriteria = session.createCriteria(Process.class).add(Restrictions.in("processId", parentProcessIdList));
                    LOGGER.info("size of parent process list is" + parentProcessCriteria.list().size());

                    for (Object resultObject : parentProcessCriteria.list()) {
                        Process resultProcess = (Process) resultObject;
                        com.wipro.ats.bdre.md.beans.table.Process createdProcess = new com.wipro.ats.bdre.md.beans.table.Process();
                        createdProcess.setProcessId(resultProcess.getProcessId());
                        createdProcess.setDescription(resultProcess.getDescription());
                        createdProcess.setAddTS(resultProcess.getAddTs());
                        createdProcess.setProcessName(resultProcess.getProcessName());
                        createdProcess.setBusDomainId(resultProcess.getBusDomain().getBusDomainId());
                        createdProcess.setProcessTypeId(resultProcess.getProcessType().getProcessTypeId());
                        if (resultProcess.getProcess() != null) {
                            createdProcess.setParentProcessId(resultProcess.getProcess().getProcessId());
                        }
                        createdProcess.setCanRecover(resultProcess.getCanRecover());
                        createdProcess.setEnqProcessId(resultProcess.getEnqueuingProcessId());
                        if (resultProcess.getBatchCutPattern() != null) {
                            createdProcess.setBatchPattern(resultProcess.getBatchCutPattern());
                        }
                        createdProcess.setNextProcessIds(resultProcess.getNextProcessId());
                        if (resultProcess.getWorkflowType() != null) {
                            createdProcess.setWorkflowId(resultProcess.getWorkflowType().getWorkflowId());
                        }
                        createdProcess.setCounter(parentProcessCriteria.list().size());
                        createdProcesses.add(createdProcess);
                    }
                }
            }
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return createdProcesses;
    }
}
