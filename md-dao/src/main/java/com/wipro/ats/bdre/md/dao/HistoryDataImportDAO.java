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

import javax.persistence.criteria.CriteriaBuilder;
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
            IntermediateId intermediateId = new IntermediateId();
            LOGGER.info("uuid is =" + intermediateInfo.getUuid());
            intermediateId.setUuid(intermediateInfo.getUuid());
            intermediateId.setInterKey("numberOfTables");
            Intermediate numberOfTables = (Intermediate) session.get(Intermediate.class,intermediateId);
            LOGGER.info("inter value is = " + numberOfTables.getInterValue() + " key is " + numberOfTables.getId().getInterKey());
            Integer numOfTable = Integer.parseInt(numberOfTables.getInterValue());
            boolean triggerCheck;
            int flag = 0;
            int numOfTableToIngest = 0;
            LOGGER.info("number of table is " + numOfTable);
            for (int i = 1; i <= numOfTable; i++) {
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
            // Getting master data from metadata
            IntermediateId getBusDomain = new IntermediateId();
            getBusDomain.setInterKey("busdomainid");
            getBusDomain.setUuid(intermediateInfo.getUuid());
            Criteria getBusDomainCriteria = session.createCriteria(Intermediate.class).add(Restrictions.eq("id", getBusDomain));
            Intermediate busDomainRow = (Intermediate) getBusDomainCriteria.list().get(0);
            Integer busDomainId = Integer.parseInt(busDomainRow.getInterValue());
            BusDomain busDomain = new BusDomain();
            busDomain.setBusDomainId(busDomainId);

            IntermediateId getProcessName = new IntermediateId();
            getProcessName.setInterKey("processName");
            getProcessName.setUuid(intermediateInfo.getUuid());
            Criteria getProcessNameCriteria = session.createCriteria(Intermediate.class).add(Restrictions.eq("id", getProcessName));
            Intermediate processNameRow = (Intermediate) getProcessNameCriteria.list().get(0);
            String processName = processNameRow.getInterValue();


            IntermediateId getProcessDescription = new IntermediateId();
            getProcessDescription.setInterKey("processDescription");
            getProcessDescription.setUuid(intermediateInfo.getUuid());
            Criteria getProcessDescriptionCriteria = session.createCriteria(Intermediate.class).add(Restrictions.eq("id", getProcessDescription));
            Intermediate processDescriptionRow = (Intermediate) getProcessDescriptionCriteria.list().get(0);
            String processDescription = processDescriptionRow.getInterValue();


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

            // adding data laod parent
            if (flag == 1) {
                dataLoadParent.setDescription(processDescription+"_Load");
                dataLoadParent.setAddTs(new Date());
                dataLoadParent.setEditTs(new Date());
                dataLoadParent.setProcessName(processName+"_Load");
                dataLoadParent.setBusDomain(busDomain);
                dataLoadParent.setProcessType(dataLoadProcessType);
                dataLoadParent.setNextProcessId("0");
                dataLoadParent.setCanRecover(false);
                dataLoadParent.setEnqueuingProcessId(0);
                dataLoadParent.setBatchCutPattern(null);
                dataLoadParent.setDeleteFlag(false);
                dataLoadParent.setWorkflowType(oozieType);
                session.save(dataLoadParent);

                LOGGER.info("the inserted data load parent is " + dataLoadParent.getProcessId());
                parentProcessIdList.add(dataLoadParent.getProcessId());
            }
            // looping for each table and creating subsequent data import and data load processes with required properties
            for (int i = 1; i <= numOfTable; i++) {
                //data import process
                Process dataImportProcess = new Process();
                dataImportProcess.setDescription(processDescription+"_Import" + i);
                dataImportProcess.setAddTs(new Date());
                dataImportProcess.setEditTs(new Date());
                dataImportProcess.setProcessName(processName+"_Import" + i);
                dataImportProcess.setBusDomain(busDomain);
                dataImportProcess.setProcessType(dataImportProcessType);
                dataImportProcess.setNextProcessId("0");
                dataImportProcess.setCanRecover(false);
                dataImportProcess.setEnqueuingProcessId(0);
                dataImportProcess.setBatchCutPattern(null);
                dataImportProcess.setWorkflowType(oozieType);
                dataImportProcess.setDeleteFlag(false);
                session.save(dataImportProcess);

                //
                LOGGER.info("the inserted data import parent is " + dataImportProcess.getProcessId());
                parentProcessIdList.add(dataImportProcess.getProcessId());

                // child of data import process
                Process childDataImportProcess = new Process();
                childDataImportProcess.setDescription(processDescription+"_Import");
                childDataImportProcess.setProcessName("SubProcess of "+processName+"_Import");
                childDataImportProcess.setAddTs(new Date());
                childDataImportProcess.setEditTs(new Date());
                childDataImportProcess.setProcess(dataImportProcess);
                childDataImportProcess.setBusDomain(busDomain);
                childDataImportProcess.setProcessType(childDataImportProcessType);
                childDataImportProcess.setEnqueuingProcessId(0);
                childDataImportProcess.setNextProcessId(dataImportProcess.getProcessId().toString());
                childDataImportProcess.setWorkflowType(actionType);
                childDataImportProcess.setDeleteFlag(false);
                childDataImportProcess.setCanRecover(false);

                Process parentProcessCheckDataImportChild = null;
                if (childDataImportProcess.getProcess() != null) {
                    parentProcessCheckDataImportChild = (Process) session.get(Process.class, childDataImportProcess.getProcess().getProcessId());
                }

                session.save(childDataImportProcess);

                // updating parent data import process
                session.update(dataImportProcess);

                LOGGER.info("the inserted data import is " + childDataImportProcess.getProcessId());
                dataImportProcess.setNextProcessId(childDataImportProcess.getProcessId().toString());
                session.update(dataImportProcess);

                LOGGER.info(dataImportProcess.getNextProcessId());

                // data import action related properties reading from intermediate table
                //database name
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

                // driver name
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

                //password
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

                //username
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

                //table name
                IntermediateId intermediateIdRawName = new IntermediateId();
                intermediateIdRawName.setUuid(intermediateInfo.getUuid());
                intermediateIdRawName.setInterKey("rawTableName_" + i);

                Criteria rawNameValueCriteria = session.createCriteria(Intermediate.class).add(Restrictions.eq("id", intermediateIdRawName));
                Intermediate rawNameValue = (Intermediate) rawNameValueCriteria.list().get(0);

                PropertiesId propertiesIdRawName = new PropertiesId();
                propertiesIdRawName.setProcessId(childDataImportProcess.getProcessId());
                propertiesIdRawName.setPropKey("table");
                Properties rawNameProperties = new Properties();
                rawNameProperties.setId(propertiesIdRawName);
                rawNameProperties.setConfigGroup("imp-common");
                rawNameProperties.setPropValue(rawNameValue.getInterValue());
                rawNameProperties.setDescription("properties for  data import");
                session.save(rawNameProperties);

                // file layout
                PropertiesId propertiesIdFileLayout = new PropertiesId();
                propertiesIdFileLayout.setProcessId(childDataImportProcess.getProcessId());
                propertiesIdFileLayout.setPropKey("file.layout");
                Properties fileLayoutProperties = new Properties();
                fileLayoutProperties.setId(propertiesIdFileLayout);
                fileLayoutProperties.setConfigGroup("imp-common");
                fileLayoutProperties.setPropValue("TextFile");
                fileLayoutProperties.setDescription("properties for  data import");
                session.save(fileLayoutProperties);


                PropertiesId propertiesIdImport = new PropertiesId();
                propertiesIdImport.setProcessId(childDataImportProcess.getProcessId());
                propertiesIdImport.setPropKey("import");
                Properties importProperties = new Properties();
                importProperties.setId(propertiesIdImport);
                importProperties.setConfigGroup("imp-common");
                importProperties.setPropValue("1");
                importProperties.setDescription("properties for  data import");
                session.save(importProperties);

                // number of mapper
                PropertiesId propertiesIdMappers = new PropertiesId();
                propertiesIdMappers.setProcessId(childDataImportProcess.getProcessId());
                propertiesIdMappers.setPropKey("mappers");
                Properties mapeersProperties = new Properties();
                mapeersProperties.setId(propertiesIdMappers);
                mapeersProperties.setConfigGroup("imp-common");
                mapeersProperties.setPropValue("1");
                mapeersProperties.setDescription("properties for  data import");
                session.save(mapeersProperties);

                //column list
                IntermediateId intermediateIdColumnList = new IntermediateId();
                intermediateIdColumnList.setUuid(intermediateInfo.getUuid());
                intermediateIdColumnList.setInterKey("columnList_" + i);

                Criteria columnListValueCriteria = session.createCriteria(Intermediate.class).add(Restrictions.eq("id", intermediateIdColumnList));
                Intermediate columnListValue = (Intermediate) columnListValueCriteria.list().get(0);

                PropertiesId propertiesIdColumnList = new PropertiesId();
                propertiesIdColumnList.setProcessId(childDataImportProcess.getProcessId());
                propertiesIdColumnList.setPropKey("columns");
                Properties columnListProperties = new Properties();
                columnListProperties.setId(propertiesIdColumnList);
                columnListProperties.setConfigGroup("imp-common");
                columnListProperties.setPropValue(columnListValue.getInterValue());
                columnListProperties.setDescription("properties for  data import");
                session.save(columnListProperties);

                // increment type
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

                //primary key column
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

                // checking whether process to load data into  hive is reuired or not
                String ingestOnlyCount = "ingestOnly_" + i;
                IntermediateId intermediateIdLoadOrNot = new IntermediateId();
                intermediateIdLoadOrNot.setUuid(intermediateInfo.getUuid());
                intermediateIdLoadOrNot.setInterKey(ingestOnlyCount);

                Criteria loadOrNotCriteria = session.createCriteria(Intermediate.class).add(Restrictions.eq("id", intermediateIdLoadOrNot));
                Intermediate loadOrNotValue = (Intermediate) loadOrNotCriteria.list().get(0);
                LOGGER.debug("loadOrNotValue " + loadOrNotValue.getInterValue());
                if ("false".equals(loadOrNotValue.getInterValue())) {
                    // file2Raw process
                    Process file2Raw = new Process();
                    file2Raw.setDescription(processDescription+"_'File2Raw'");
                    file2Raw.setAddTs(new Date());
                    file2Raw.setEditTs(new Date());
                    file2Raw.setProcessName(processName+"_Data Load-F2R");
                    file2Raw.setBusDomain(busDomain);
                    file2Raw.setProcessType(file2RawType);
                    file2Raw.setNextProcessId("0");
                    file2Raw.setProcess(dataLoadParent);
                    file2Raw.setCanRecover(false);
                    file2Raw.setEnqueuingProcessId(dataImportProcess.getProcessId());
                    file2Raw.setBatchCutPattern(null);
                    file2Raw.setDeleteFlag(false);
                    file2Raw.setWorkflowType(actionType);
                    session.save(file2Raw);

                    nextProcessForDataLoadParent += file2Raw.getProcessId().toString() + ",";
                    //raw2stage process
                    Process raw2Stage = new Process();
                    raw2Stage.setDescription(processDescription+"_''Raw2Stage''");
                    raw2Stage.setAddTs(new Date());
                    raw2Stage.setEditTs(new Date());
                    raw2Stage.setProcessName(processName+"_Data Load-R2S");
                    raw2Stage.setBusDomain(busDomain);
                    raw2Stage.setProcessType(raw2StageType);
                    raw2Stage.setProcess(dataLoadParent);
                    raw2Stage.setCanRecover(false);
                    raw2Stage.setNextProcessId("0");
                    raw2Stage.setEnqueuingProcessId(dataImportProcess.getProcessId());
                    raw2Stage.setBatchCutPattern(null);
                    raw2Stage.setDeleteFlag(false);
                    raw2Stage.setWorkflowType(actionType);
                    session.save(raw2Stage);

                    nextProcessForF2R += raw2Stage.getProcessId().toString() + ",";
                    //stage2base process
                    Process stage2Base = new Process();
                    stage2Base.setDescription(processDescription+"_'''Stage2Base'''");
                    stage2Base.setAddTs(new Date());
                    stage2Base.setEditTs(new Date());
                    stage2Base.setProcessName(processName+"_Data Load-S2B");
                    stage2Base.setBusDomain(busDomain);
                    stage2Base.setProcessType(stage2BaseType);
                    stage2Base.setProcess(dataLoadParent);
                    stage2Base.setCanRecover(false);
                    stage2Base.setEnqueuingProcessId(0);
                    stage2Base.setBatchCutPattern(null);
                    stage2Base.setDeleteFlag(false);
                    stage2Base.setNextProcessId("0");
                    stage2Base.setWorkflowType(actionType);
                    stage2Base.setNextProcessId(dataLoadParent.getProcessId().toString());
                    Process stage2BaseParent = null;
                    session.save(stage2Base);


                    nextProcessForR2S += stage2Base.getProcessId() + ",";

                    //field delim
                    PropertiesId propertiesFieldDelimId = new PropertiesId();
                    propertiesFieldDelimId.setProcessId(file2Raw.getProcessId());
                    propertiesFieldDelimId.setPropKey("field.delim");
                    Properties propertiesFieldDelim = new Properties();
                    propertiesFieldDelim.setId(propertiesFieldDelimId);
                    propertiesFieldDelim.setConfigGroup("raw-serde-props");
                    propertiesFieldDelim.setPropValue(",");
                    propertiesFieldDelim.setDescription("properties for File delimiter");
                    session.save(propertiesFieldDelim);

                    // file type
                    PropertiesId propertiesFileFormatId = new PropertiesId();
                    propertiesFileFormatId.setProcessId(file2Raw.getProcessId());
                    propertiesFileFormatId.setPropKey("file_type");
                    Properties propertiesFileFormat = new Properties();
                    propertiesFileFormat.setId(propertiesFileFormatId);
                    propertiesFileFormat.setConfigGroup("raw-table");
                    propertiesFileFormat.setPropValue("delimited");
                    propertiesFileFormat.setDescription("properties for File Format");
                    session.save(propertiesFileFormat);
                    //input class
                    PropertiesId propertiesInputFormatId = new PropertiesId();
                    propertiesInputFormatId.setProcessId(file2Raw.getProcessId());
                    propertiesInputFormatId.setPropKey("input.format");
                    Properties propertiesInputFormat = new Properties();
                    propertiesInputFormat.setId(propertiesInputFormatId);
                    propertiesInputFormat.setConfigGroup("raw-table");
                    propertiesInputFormat.setPropValue("org.apache.hadoop.mapred.TextInputFormat");
                    propertiesInputFormat.setDescription("properties for input Format");
                    session.save(propertiesInputFormat);
                    //output class
                    PropertiesId propertiesOutputFormatId = new PropertiesId();
                    propertiesOutputFormatId.setProcessId(file2Raw.getProcessId());
                    propertiesOutputFormatId.setPropKey("output.format");
                    Properties propertiesOutputFormat = new Properties();
                    propertiesOutputFormat.setId(propertiesOutputFormatId);
                    propertiesOutputFormat.setConfigGroup("raw-table");
                    propertiesOutputFormat.setPropValue("org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat");
                    propertiesOutputFormat.setDescription("properties for output Format");
                    session.save(propertiesOutputFormat);
                    //serde class
                    PropertiesId propertiesSerdeClassId = new PropertiesId();
                    propertiesSerdeClassId.setProcessId(file2Raw.getProcessId());
                    propertiesSerdeClassId.setPropKey("serde.class");
                    Properties propertiesSerdeClass = new Properties();
                    propertiesSerdeClass.setId(propertiesSerdeClassId);
                    propertiesSerdeClass.setConfigGroup("raw-table");
                    propertiesSerdeClass.setPropValue("org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe");
                    propertiesSerdeClass.setDescription("properties for Serde Class");
                    session.save(propertiesSerdeClass);

                    //raw table name
                    IntermediateId intermediateIdRawTableName = new IntermediateId();
                    intermediateIdRawTableName.setUuid(intermediateInfo.getUuid());
                    intermediateIdRawTableName.setInterKey("rawTableName_" + i);

                    Criteria rawTableNameCriteria = session.createCriteria(Intermediate.class).add(Restrictions.eq("id", intermediateIdRawTableName));
                    Intermediate rawTableName = (Intermediate) rawTableNameCriteria.list().get(0);

                    PropertiesId rawTableNamePropertiesId = new PropertiesId();
                    rawTableNamePropertiesId.setProcessId(file2Raw.getProcessId());
                    rawTableNamePropertiesId.setPropKey("table_name");
                    Properties rawTableNameProperties = new Properties();
                    rawTableNameProperties.setId(rawTableNamePropertiesId);
                    rawTableNameProperties.setConfigGroup("raw-table");
                    rawTableNameProperties.setPropValue("raw_" + rawTableName.getInterValue());
                    rawTableNameProperties.setDescription("Raw Table Name");
                    session.save(rawTableNameProperties);

                    PropertiesId rawTableNamePropertiesIdForStage = new PropertiesId();
                    rawTableNamePropertiesIdForStage.setProcessId(raw2Stage.getProcessId());
                    rawTableNamePropertiesIdForStage.setPropKey("table_name_raw");
                    Properties rawTableNamePropertiesForStage = new Properties();
                    rawTableNamePropertiesForStage.setId(rawTableNamePropertiesIdForStage);
                    rawTableNamePropertiesForStage.setConfigGroup("raw-table");
                    rawTableNamePropertiesForStage.setPropValue("raw_" + rawTableName.getInterValue());
                    rawTableNamePropertiesForStage.setDescription("Raw Table Name");
                    session.save(rawTableNamePropertiesForStage);

                    // raw db
                    IntermediateId intermediateIdRawTableDB = new IntermediateId();
                    intermediateIdRawTableDB.setUuid(intermediateInfo.getUuid());
                    intermediateIdRawTableDB.setInterKey("rawHiveDB");

                    Criteria rawTableDBCriteria = session.createCriteria(Intermediate.class).add(Restrictions.eq("id", intermediateIdRawTableDB));
                    Intermediate rawTableDB = (Intermediate) rawTableDBCriteria.list().get(0);

                    PropertiesId rawTableDBPropertiesId = new PropertiesId();
                    rawTableDBPropertiesId.setProcessId(file2Raw.getProcessId());
                    rawTableDBPropertiesId.setPropKey("table_db");
                    Properties rawTableDBProperties = new Properties();
                    rawTableDBProperties.setId(rawTableDBPropertiesId);
                    rawTableDBProperties.setConfigGroup("raw-table");
                    rawTableDBProperties.setPropValue(rawTableDB.getInterValue());
                    rawTableDBProperties.setDescription("Raw Table Name");
                    session.save(rawTableDBProperties);

                    PropertiesId rawTableDBPropertiesIdForStage = new PropertiesId();
                    rawTableDBPropertiesIdForStage.setProcessId(raw2Stage.getProcessId());
                    rawTableDBPropertiesIdForStage.setPropKey("table_db_raw");
                    Properties rawTableDBPropertiesForStage = new Properties();
                    rawTableDBPropertiesForStage.setId(rawTableDBPropertiesIdForStage);
                    rawTableDBPropertiesForStage.setConfigGroup("raw-table");
                    rawTableDBPropertiesForStage.setPropValue(rawTableDB.getInterValue());
                    rawTableDBPropertiesForStage.setDescription("Raw Table Name");
                    session.save(rawTableDBPropertiesForStage);

                    //cloumns and datyptes
                    IntermediateId intermediateIdRawTableColumns = new IntermediateId();
                    intermediateIdRawTableColumns.setUuid(intermediateInfo.getUuid());
                    intermediateIdRawTableColumns.setInterKey("rawColumnsAndDataTypes_" +  i);
                    LOGGER.info("key is : " + i);

                    Criteria rawTableColumnsCriteria = session.createCriteria(Intermediate.class).add(Restrictions.eq("id", intermediateIdRawTableColumns));
                    Intermediate rawTableColumns = (Intermediate) rawTableColumnsCriteria.list().get(0);
                    LOGGER.info("intermediate column : uuid " + rawTableColumns.getId().getUuid() + " key " + rawTableColumns.getId().getInterKey() + " value " + rawTableColumns.getInterValue());
                    String[] rawTableColumn = rawTableColumns.getInterValue().split(",");
                    LOGGER.info("inter value after splitting " + rawTableColumn.toString());
                    for ( int columnCounter = 1;columnCounter <= rawTableColumn.length; columnCounter ++){
                        PropertiesId rawTableColumnPropertiesId = new PropertiesId();
                        rawTableColumnPropertiesId.setProcessId(file2Raw.getProcessId());
                        rawTableColumnPropertiesId.setPropKey("raw_column_name." + columnCounter);
                        Properties rawTableColumnProperties = new Properties();
                        rawTableColumnProperties.setId(rawTableColumnPropertiesId);
                        rawTableColumnProperties.setConfigGroup("raw-cols");
                        rawTableColumnProperties.setPropValue(rawTableColumn[columnCounter-1].split(" ")[0]);
                        rawTableColumnProperties.setDescription("Raw Table Columns");
                        session.save(rawTableColumnProperties);

                        PropertiesId rawTableDataTypesPropertiesId = new PropertiesId();
                        rawTableDataTypesPropertiesId.setProcessId(file2Raw.getProcessId());
                        rawTableDataTypesPropertiesId.setPropKey("raw_column_datatype." + columnCounter);
                        Properties rawTableDataTypesProperties = new Properties();
                        rawTableDataTypesProperties.setId(rawTableDataTypesPropertiesId);
                        rawTableDataTypesProperties.setConfigGroup("raw-data-types");
                        rawTableDataTypesProperties.setPropValue(rawTableColumn[columnCounter-1].split(" ")[1]);
                        rawTableDataTypesProperties.setDescription("Raw Table Data Types");
                        session.save(rawTableDataTypesProperties);

                        PropertiesId baseTableColumnPropertiesId = new PropertiesId();
                        baseTableColumnPropertiesId.setProcessId(raw2Stage.getProcessId());
                        baseTableColumnPropertiesId.setPropKey("transform_" + rawTableColumn[columnCounter-1].split(" ")[0]);
                        Properties baseTableColumnProperties = new Properties();
                        baseTableColumnProperties.setId(baseTableColumnPropertiesId);
                        baseTableColumnProperties.setConfigGroup("base-columns");
                        baseTableColumnProperties.setPropValue(rawTableColumn[columnCounter-1].split(" ")[0]);
                        baseTableColumnProperties.setDescription("Base Table Columns");
                        session.save(baseTableColumnProperties);

                        PropertiesId baseTableDataTypePropertiesId = new PropertiesId();
                        baseTableDataTypePropertiesId.setProcessId(raw2Stage.getProcessId());
                        baseTableDataTypePropertiesId.setPropKey(rawTableColumn[columnCounter-1].split(" ")[0]);
                        Properties baseTableDataTypeProperties = new Properties();
                        baseTableDataTypeProperties.setId(baseTableDataTypePropertiesId);
                        baseTableDataTypeProperties.setConfigGroup("base-data-types");
                        baseTableDataTypeProperties.setPropValue(rawTableColumn[columnCounter-1].split(" ")[1]);
                        baseTableDataTypeProperties.setDescription("Base Table data types");
                        session.save(baseTableDataTypeProperties);

                        PropertiesId lastStageId = new PropertiesId();
                        lastStageId.setProcessId(stage2Base.getProcessId());
                        lastStageId.setPropKey(rawTableColumn[columnCounter-1].split(" ")[0]);
                        Properties lastStage = new Properties();
                        lastStage.setId(lastStageId);
                        lastStage.setConfigGroup("base-columns-and-types");
                        lastStage.setPropValue(rawTableColumn[columnCounter-1].split(" ")[1]);
                        lastStage.setDescription("Base Table columns and data types");
                        session.save(lastStage);

                    }
                    //base table name
                    IntermediateId intermediateIdBaseTableName = new IntermediateId();
                    intermediateIdBaseTableName.setUuid(intermediateInfo.getUuid());
                    intermediateIdBaseTableName.setInterKey("baseTableName_" + i);

                    Criteria baseTableNameCriteria = session.createCriteria(Intermediate.class).add(Restrictions.eq("id", intermediateIdBaseTableName));
                    Intermediate baseTableName = (Intermediate) baseTableNameCriteria.list().get(0);

                    PropertiesId baseTableNamePropertiesId = new PropertiesId();
                    baseTableNamePropertiesId.setProcessId(raw2Stage.getProcessId());
                    baseTableNamePropertiesId.setPropKey("table_name");
                    Properties baseTableNameProperties = new Properties();
                    baseTableNameProperties.setId(baseTableNamePropertiesId);
                    baseTableNameProperties.setConfigGroup("base-table");
                    baseTableNameProperties.setPropValue(baseTableName.getInterValue());
                    baseTableNameProperties.setDescription("Base Table Name");
                    session.save(baseTableNameProperties);

                    baseTableNamePropertiesId = new PropertiesId();
                    baseTableNamePropertiesId.setProcessId(stage2Base.getProcessId());
                    baseTableNamePropertiesId.setPropKey("table_name");
                    baseTableNameProperties = new Properties();
                    baseTableNameProperties.setId(baseTableNamePropertiesId);
                    baseTableNameProperties.setConfigGroup("base-table");
                    baseTableNameProperties.setPropValue(baseTableName.getInterValue());
                    baseTableNameProperties.setDescription("Base Table Name");
                    session.save(baseTableNameProperties);

                    // base database
                    IntermediateId intermediateIdBaseTableDB = new IntermediateId();
                    intermediateIdBaseTableDB.setUuid(intermediateInfo.getUuid());
                    intermediateIdBaseTableDB.setInterKey("baseHiveDB");

                    Criteria baseTableDBCriteria = session.createCriteria(Intermediate.class).add(Restrictions.eq("id", intermediateIdBaseTableDB));
                    Intermediate baseTableDB = (Intermediate) baseTableDBCriteria.list().get(0);

                    PropertiesId baseTableDBPropertiesId = new PropertiesId();
                    baseTableDBPropertiesId.setProcessId(raw2Stage.getProcessId());
                    baseTableDBPropertiesId.setPropKey("table_db");
                    Properties baseTableDBProperties = new Properties();
                    baseTableDBProperties.setId(baseTableDBPropertiesId);
                    baseTableDBProperties.setConfigGroup("base-table");
                    baseTableDBProperties.setPropValue(baseTableDB.getInterValue());
                    baseTableDBProperties.setDescription("Base Table DB");
                    session.save(baseTableDBProperties);

                    baseTableDBPropertiesId = new PropertiesId();
                    baseTableDBPropertiesId.setProcessId(stage2Base.getProcessId());
                    baseTableDBPropertiesId.setPropKey("table_db");
                    baseTableDBProperties = new Properties();
                    baseTableDBProperties.setId(baseTableDBPropertiesId);
                    baseTableDBProperties.setConfigGroup("base-table");
                    baseTableDBProperties.setPropValue(baseTableDB.getInterValue());
                    baseTableDBProperties.setDescription("Base Table DB");
                    session.save(baseTableDBProperties);

                }
            }
            if (flag == 1) {
                //updating next process for every process
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
                        createdProcess.setEditTS(resultProcess.getEditTs());
                        createdProcess.setProcessName(resultProcess.getProcessName());
                        createdProcess.setBusDomainId(resultProcess.getBusDomain().getBusDomainId());
                        createdProcess.setProcessTypeId(resultProcess.getProcessType().getProcessTypeId());
                        if (resultProcess.getProcess() != null) {
                            createdProcess.setParentProcessId(resultProcess.getProcess().getProcessId());
                        }
                        createdProcess.setCanRecover(resultProcess.getCanRecover());
                        if(resultProcess.getCanRecover() == null){
                            createdProcess.setCanRecover(false);
                        }

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
                        createdProcess.setEditTS(resultProcess.getEditTs());
                        createdProcess.setProcessName(resultProcess.getProcessName());
                        createdProcess.setBusDomainId(resultProcess.getBusDomain().getBusDomainId());
                        createdProcess.setProcessTypeId(resultProcess.getProcessType().getProcessTypeId());
                        if (resultProcess.getProcess() != null) {
                            createdProcess.setParentProcessId(resultProcess.getProcess().getProcessId());
                        }
                        createdProcess.setCanRecover(resultProcess.getCanRecover());
                        if(resultProcess.getCanRecover() == null){
                            createdProcess.setCanRecover(false);
                        }
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
