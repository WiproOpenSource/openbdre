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
import com.wipro.ats.bdre.md.beans.ETLJobInfo;
import com.wipro.ats.bdre.md.dao.jpa.*;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by MR299389 on 10/15/2015.
 */

@Transactional
@Service
public class ETLStepDAO {
    private static final Logger LOGGER = Logger.getLogger(ETLStepDAO.class);
    @Autowired
    SessionFactory sessionFactory;
    Process parentTrigger = new Process();
    Process dummyProcess = new Process();


    public List<Etlstep> list(Integer pageNum, Integer numResults) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(Etlstep.class);
        criteria.setFirstResult(pageNum);
        criteria.setMaxResults(numResults);
        List<Etlstep> etlsteps = criteria.list();
        session.getTransaction().commit();
        session.close();
        return etlsteps;
    }

    public Long totalRecordCount() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        long size = session.createCriteria(Etlstep.class).list().size();
        session.getTransaction().commit();
        session.close();
        return size;
    }

    public List<ETLJobInfo> get(ETLJobInfo etlJobInfo) {
        Session session = sessionFactory.openSession();
        List<ETLJobInfo> etlJobInfoList = new ArrayList<ETLJobInfo>();

        try {
            session.beginTransaction();
            EtlstepId etlstepId = new EtlstepId();
            etlstepId.setUuid(etlJobInfo.getUuid());
            if (etlJobInfo.getSerialNumber() != null) {
                etlstepId.setSerialNumber(new Date().getTime());
                Etlstep etlstep = (Etlstep) session.get(Etlstep.class, etlstepId);
                etlJobInfo.setBusDomainId(etlstep.getBusDomainId());
                etlJobInfo.setProcessName(etlstep.getProcessName());
                etlJobInfo.setDescription(etlstep.getDescription());
                etlJobInfo.setSerialNumber(new Date().getTime());
                if (etlstep.getBaseTableName() != null) {
                    etlJobInfo.setBaseTableName(etlstep.getBaseTableName());
                }
                if (etlstep.getRawTableName() != null) {
                    etlJobInfo.setRawTableName(etlstep.getRawTableName());
                }
                if (etlstep.getRawViewName() != null) {
                    etlJobInfo.setRawViewName(etlstep.getRawViewName());
                }
                if (etlstep.getBaseDbName() != null) {
                    etlJobInfo.setBaseDBName(etlstep.getBaseDbName());
                }
                if (etlstep.getBaseDbName() != null) {
                    etlJobInfo.setRawDBName(etlstep.getBaseDbName());
                }
                if (etlstep.getBaseTableDdl() != null) {
                    etlJobInfo.setBaseTableDDL(etlstep.getBaseTableDdl());
                }
                if (etlstep.getRawViewDdl() != null) {
                    etlJobInfo.setRawViewDDL(etlstep.getRawViewDdl());
                }
                if (etlstep.getRawTableDdl() != null) {
                    etlJobInfo.setRawTableDDL(etlstep.getRawTableDdl());
                }
                if (etlstep.getColumnInfo() != null) {
                    etlJobInfo.setColumnInfo(etlstep.getColumnInfo());
                }
                if (etlstep.getSerdeProperties() != null) {
                    etlJobInfo.setSerdeProperties(etlstep.getSerdeProperties());
                }
                if (etlstep.getTableProperties() != null) {
                    etlJobInfo.setTableProperties(etlstep.getTableProperties());
                }
                if (etlstep.getRawPartitionCol() != null) {
                    etlJobInfo.setRawPartitionCol(etlstep.getRawPartitionCol());
                }

                etlJobInfo.setDropRaw(etlstep.getDropRaw());

                if (etlstep.getEnqId() != null) {
                    etlJobInfo.setEnqId(etlstep.getEnqId());
                }
                if (etlstep.getInputFormat() != null) {
                    etlJobInfo.setInputFormat(Integer.parseInt(etlstep.getInputFormat()));
                }
                etlJobInfo.setCounter(1);
                etlJobInfoList.add(etlJobInfo);
            } else {
                Criteria etlstepCriteria = session.createCriteria(Etlstep.class).add(Restrictions.eq("id.uuid", etlJobInfo.getUuid()));
                List<Etlstep> etlstepList = etlstepCriteria.list();
                for (Etlstep etlstep : etlstepList) {
                    ETLJobInfo etlJobInfo1 = new ETLJobInfo();
                    etlJobInfo1.setBusDomainId(etlstep.getBusDomainId());
                    etlJobInfo1.setProcessName(etlstep.getProcessName());
                    etlJobInfo1.setDescription(etlstep.getDescription());
                    etlJobInfo1.setSerialNumber(etlstep.getId().getSerialNumber());
                    if (etlstep.getBaseTableName() != null) {
                        etlJobInfo1.setBaseTableName(etlstep.getBaseTableName());
                    }
                    if (etlstep.getRawTableName() != null) {
                        etlJobInfo1.setRawTableName(etlstep.getRawTableName());
                    }
                    if (etlstep.getRawViewName() != null) {
                        etlJobInfo1.setRawViewName(etlstep.getRawViewName());
                    }
                    if (etlstep.getBaseDbName() != null) {
                        etlJobInfo1.setBaseDBName(etlstep.getBaseDbName());
                    }
                    if (etlstep.getBaseDbName() != null) {
                        etlJobInfo1.setRawDBName(etlstep.getBaseDbName());
                    }
                    if (etlstep.getBaseTableDdl() != null) {
                        etlJobInfo1.setBaseTableDDL(etlstep.getBaseTableDdl());
                    }
                    if (etlstep.getRawViewDdl() != null) {
                        etlJobInfo1.setRawViewDDL(etlstep.getRawViewDdl());
                    }
                    if (etlstep.getRawTableDdl() != null) {
                        etlJobInfo1.setRawTableDDL(etlstep.getRawTableDdl());
                    }
                    if (etlstep.getColumnInfo() != null) {
                        etlJobInfo1.setColumnInfo(etlstep.getColumnInfo());
                    }
                    if (etlstep.getSerdeProperties() != null) {
                        etlJobInfo1.setSerdeProperties(etlstep.getSerdeProperties());
                    }
                    if (etlstep.getTableProperties() != null) {
                        etlJobInfo1.setTableProperties(etlstep.getTableProperties());
                    }
                    if (etlstep.getRawPartitionCol() != null) {
                        etlJobInfo1.setRawPartitionCol(etlstep.getRawPartitionCol());
                    }
                    etlJobInfo1.isDropRaw();
                    if (etlstep.getEnqId() != null) {
                        etlJobInfo1.setEnqId(etlstep.getEnqId());
                    }
                    if (etlstep.getInputFormat() != null) {
                        etlJobInfo1.setInputFormat(Integer.parseInt(etlstep.getInputFormat()));
                    }
                    etlJobInfo1.setCounter(etlstepList.size());
                    etlJobInfo1.setSerialNumber(etlstep.getId().getSerialNumber());
                    etlJobInfo1.setUuid(etlstep.getId().getUuid());
                    etlJobInfoList.add(etlJobInfo1);
                }

            }

            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return etlJobInfoList;
    }

    public ETLJobInfo insert(ETLJobInfo etlJobInfo) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            Etlstep etlstep = new Etlstep();
            EtlstepId etlstepId = new EtlstepId();
            etlstepId.setUuid(etlJobInfo.getUuid());
            etlstepId.setSerialNumber(etlJobInfo.getSerialNumber());
            etlstep.setId(etlstepId);
            etlstep.setDescription(etlJobInfo.getDescription());
            etlstep.setProcessName(etlJobInfo.getProcessName());
            etlstep.setBusDomainId(etlJobInfo.getBusDomainId());
            etlstep.setBaseTableName(etlJobInfo.getBaseTableName());
            etlstep.setRawTableName(etlJobInfo.getRawTableName());
            etlstep.setRawViewName(etlJobInfo.getRawViewName());
            etlstep.setBaseDbName(etlJobInfo.getBaseDBName());
            etlstep.setRawDbName(etlJobInfo.getRawDBName());
            etlstep.setColumnInfo(etlJobInfo.getColumnInfo());
            etlstep.setSerdeProperties(etlJobInfo.getSerdeProperties());
            etlstep.setBaseTableDdl(etlJobInfo.getBaseTableDDL());
            etlstep.setRawTableDdl(etlJobInfo.getRawTableDDL());
            etlstep.setRawViewDdl(etlJobInfo.getRawViewDDL());
            etlstep.setRawPartitionCol(etlJobInfo.getRawPartitionCol());
            etlstep.setDropRaw(etlJobInfo.isDropRaw());
            etlstep.setEnqId(etlJobInfo.getEnqId());
            if (etlJobInfo.getInputFormat() != null) {
                etlstep.setInputFormat(etlJobInfo.getInputFormat().toString());
            }
            session.save(etlstep);
            session.getTransaction().commit();
            etlJobInfo.setSerialNumber(etlstep.getId().getSerialNumber());
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return etlJobInfo;

    }

    public ETLJobInfo update(ETLJobInfo etlJobInfo) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            EtlstepId etlstepId = new EtlstepId();
            etlstepId.setUuid(etlJobInfo.getUuid());
            etlstepId.setSerialNumber(etlJobInfo.getSerialNumber());
            Etlstep etlstep = (Etlstep) session.get(Etlstep.class, etlstepId);
            etlstep.setBaseTableName(etlJobInfo.getBaseTableName());
            etlstep.setRawTableName(etlJobInfo.getRawTableName());
            etlstep.setRawViewName(etlJobInfo.getRawViewName());
            etlstep.setBaseDbName(etlJobInfo.getBaseDBName());
            etlstep.setRawDbName(etlJobInfo.getRawDBName());
            etlstep.setColumnInfo(etlJobInfo.getColumnInfo());
            etlstep.setSerdeProperties(etlJobInfo.getSerdeProperties());
            etlstep.setTableProperties(etlJobInfo.getTableProperties());
            etlstep.setBaseTableDdl(etlJobInfo.getBaseTableDDL());
            etlstep.setRawTableDdl(etlJobInfo.getRawTableDDL());
            etlstep.setRawViewDdl(etlJobInfo.getRawViewDDL());
            etlstep.setRawPartitionCol(etlJobInfo.getRawPartitionCol());
            etlstep.setDropRaw(etlJobInfo.isDropRaw());
            etlstep.setEnqId(etlJobInfo.getEnqId());
            etlstep.setInputFormat(etlJobInfo.getInputFormat().toString());
            session.update(etlstep);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return etlJobInfo;
    }

    public void delete(ETLJobInfo etlJobInfo) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            EtlstepId etlstepId = new EtlstepId();
            etlstepId.setSerialNumber(etlJobInfo.getSerialNumber());
            etlstepId.setUuid(etlJobInfo.getUuid());
            Etlstep etlstep = (Etlstep) session.get(Etlstep.class, etlstepId);
            session.delete(etlstep);
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
    }

    public ETLJobInfo etlJob(ETLJobInfo etlJobInfo) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            // fetching data for given UUID from etlstep table
            Criteria etlstepCriteria = session.createCriteria(Etlstep.class).add(Restrictions.eq("id.uuid", etlJobInfo.getUuid()));
            List<Etlstep> etlstepList = etlstepCriteria.list();
            Etlstep etlstep = etlstepList.get(0);

            // Inserting Data Load Parent Process
            com.wipro.ats.bdre.md.dao.jpa.Process dataLoadParent = new Process();

            dataLoadParent.setDescription(etlstep.getDescription());
            dataLoadParent.setProcessName(etlstep.getProcessName());
            BusDomain busDomain = new BusDomain();
            busDomain.setBusDomainId(etlstep.getBusDomainId());
            dataLoadParent.setBusDomain(busDomain);
            ProcessType dataLoadProcessType = new ProcessType();
            dataLoadProcessType.setProcessTypeId(5);
            dataLoadParent.setProcessType(dataLoadProcessType);
            WorkflowType oozieWorkflowType = new WorkflowType();
            oozieWorkflowType.setWorkflowId(1);
            dataLoadParent.setWorkflowType(oozieWorkflowType);
            dataLoadParent.setEnqueuingProcessId(0);
            dataLoadParent.setNextProcessId("");
            dataLoadParent.setCanRecover(true);
            dataLoadParent.setDeleteFlag(false);
            Integer parentProcessId = (Integer) session.save(dataLoadParent);

            String nextProcessForParent = "";
            String nextProcessForF2R = "";
            String nextProcessForR2S = "";

            ProcessType f2RProcessType = new ProcessType();
            f2RProcessType.setProcessTypeId(6);
            ProcessType r2SProcessType = new ProcessType();
            r2SProcessType.setProcessTypeId(7);
            ProcessType s2bProcessType = new ProcessType();
            s2bProcessType.setProcessTypeId(8);


            for (Etlstep etlstep1 : etlstepList) {
                // Inserting File2Row action
                Process file2Row = new Process();
                file2Row.setDescription(etlstep1.getDescription() + " - File2Raw");
                file2Row.setProcessName("f2r");
                file2Row.setProcess(dataLoadParent);
                file2Row.setBusDomain(busDomain);
                file2Row.setProcessType(f2RProcessType);
                if (etlstep1.getEnqId() == null) {
                    file2Row.setEnqueuingProcessId(0);
                } else {
                    file2Row.setEnqueuingProcessId(etlstep1.getEnqId());
                }

                WorkflowType actionType = new WorkflowType();
                actionType.setWorkflowId(0);
                file2Row.setWorkflowType(actionType);
                file2Row.setNextProcessId("");
                file2Row.setDeleteFlag(false);
                file2Row.setCanRecover(true);
                session.save(file2Row);

                nextProcessForParent += file2Row.getProcessId() + ",";

                // Inserting Raw2Stage action
                Process raw2Stage = new Process();
                raw2Stage.setDescription(etlstep1.getDescription() + " - Raw2Stage");
                raw2Stage.setProcessName("r2s");
                raw2Stage.setProcess(dataLoadParent);
                raw2Stage.setBusDomain(busDomain);
                raw2Stage.setProcessType(r2SProcessType);
                if (etlstep1.getEnqId() == null) {
                    raw2Stage.setEnqueuingProcessId(0);
                } else {
                    raw2Stage.setEnqueuingProcessId(etlstep1.getEnqId());
                }
                raw2Stage.setWorkflowType(actionType);
                raw2Stage.setNextProcessId("");
                raw2Stage.setCanRecover(true);
                raw2Stage.setDeleteFlag(false);
                session.save(raw2Stage);

                nextProcessForF2R += raw2Stage.getProcessId() + ",";

                // Inserting Stage2Base action
                Process stage2Base = new Process();
                stage2Base.setDescription(etlstep1.getDescription() + " - Stage2Base");
                stage2Base.setProcessName("s2b");
                stage2Base.setProcess(dataLoadParent);
                stage2Base.setBusDomain(busDomain);
                stage2Base.setProcessType(s2bProcessType);
                if (etlstep1.getEnqId() == null) {
                    stage2Base.setEnqueuingProcessId(0);
                } else {
                    stage2Base.setEnqueuingProcessId(etlstep1.getEnqId());
                }
                stage2Base.setWorkflowType(actionType);
                stage2Base.setNextProcessId(parentProcessId.toString());
                stage2Base.setDeleteFlag(false);
                stage2Base.setCanRecover(true);
                session.save(stage2Base);

                nextProcessForR2S += stage2Base.getProcessId() + ",";

                // Inserting DDLS into hive tables
                //raw table DDL
                HiveTables rawTable = new HiveTables();
                rawTable.setComments("for raw table");
                rawTable.setLocationType("hdfs");
                rawTable.setDbname(etlstep1.getRawDbName());
                rawTable.setBatchIdPartitionCol(etlstep1.getRawPartitionCol());
                rawTable.setTableName(etlstep1.getRawTableName());
                rawTable.setType("raw");
                rawTable.setDdl(etlstep1.getRawTableDdl());
                session.save(rawTable);

                //base table DDL
                HiveTables baseTable = new HiveTables();
                baseTable.setComments("for base table");
                baseTable.setLocationType("hdfs");
                baseTable.setDbname(etlstep1.getBaseDbName());
                baseTable.setBatchIdPartitionCol(etlstep1.getRawPartitionCol());
                baseTable.setTableName(etlstep1.getBaseTableName());
                baseTable.setType("base");
                baseTable.setDdl(etlstep1.getBaseTableDdl());
                session.save(baseTable);

                //view DDL
                HiveTables view = new HiveTables();
                view.setComments("for view");
                view.setLocationType("hdfs");
                view.setDbname(etlstep1.getRawDbName());
                view.setBatchIdPartitionCol(etlstep1.getRawPartitionCol());
                view.setTableName(etlstep1.getRawViewName());
                view.setType("view");
                view.setDdl(etlstep1.getRawViewDdl());
                session.save(view);

                // inserting into ETL_Driver table for all ddls
                EtlDriver f2rETLDriver = new EtlDriver();
                f2rETLDriver.setProcess(file2Row);
                f2rETLDriver.setEtlProcessId(file2Row.getProcessId());
                f2rETLDriver.setHiveTablesByRawTableId(rawTable);
                f2rETLDriver.setHiveTablesByBaseTableId(baseTable);
                f2rETLDriver.setInsertType(Short.parseShort("1"));
                if (etlstep1.getDropRaw() == null)
                    f2rETLDriver.setDropRaw(false);
                else
                    f2rETLDriver.setDropRaw(etlstep1.getDropRaw());
                f2rETLDriver.setHiveTablesByRawViewId(view);
                session.save(f2rETLDriver);

                // inserting into ETL_Driver table for all ddls
                EtlDriver r2sETLDriver = new EtlDriver();
                r2sETLDriver.setProcess(raw2Stage);
                r2sETLDriver.setEtlProcessId(raw2Stage.getProcessId());
                r2sETLDriver.setHiveTablesByRawTableId(rawTable);
                r2sETLDriver.setHiveTablesByBaseTableId(baseTable);
                r2sETLDriver.setInsertType(Short.parseShort("1"));
                if (etlstep1.getDropRaw() == null)
                    r2sETLDriver.setDropRaw(false);
                else
                    r2sETLDriver.setDropRaw(etlstep1.getDropRaw());
                r2sETLDriver.setHiveTablesByRawViewId(view);
                session.save(r2sETLDriver);

                // inserting into ETL_Driver table for all ddls
                EtlDriver s2bETLDriver = new EtlDriver();
                s2bETLDriver.setProcess(stage2Base);
                s2bETLDriver.setEtlProcessId(stage2Base.getProcessId());
                s2bETLDriver.setHiveTablesByRawTableId(rawTable);
                s2bETLDriver.setHiveTablesByBaseTableId(baseTable);
                s2bETLDriver.setInsertType(Short.parseShort("1"));
                if (etlstep1.getDropRaw() == null)
                    s2bETLDriver.setDropRaw(false);
                else
                    s2bETLDriver.setDropRaw(etlstep1.getDropRaw());

                s2bETLDriver.setHiveTablesByRawViewId(view);
                session.save(s2bETLDriver);

                LOGGER.debug(file2Row.getProcessId() + " file2raw");

            }
            List<com.wipro.ats.bdre.md.beans.table.Process> createdProcesses = new ArrayList<com.wipro.ats.bdre.md.beans.table.Process>();
            LOGGER.info("nextProcessForDataLoadParent is " + nextProcessForParent);
            nextProcessForParent = nextProcessForParent.substring(0, nextProcessForParent.length() - 1);
            nextProcessForF2R = nextProcessForF2R.substring(0, nextProcessForF2R.length() - 1);
            nextProcessForR2S = nextProcessForR2S.substring(0, nextProcessForR2S.length() - 1);
            dataLoadParent.setNextProcessId(nextProcessForParent);
            dataLoadParent.setEditTs(new Date());
            session.update(dataLoadParent);


            Criteria fileToRawCriteria = session.createCriteria(Process.class).add(Restrictions.eq("processType", f2RProcessType))
                    .add(Restrictions.eq("process", dataLoadParent));
            for (Object fileToRawObject : fileToRawCriteria.list()) {
                Process fileToRaw = (Process) fileToRawObject;
                fileToRaw.setNextProcessId(nextProcessForF2R);
                session.update(fileToRaw);
            }

            Criteria rawToStageCriteria = session.createCriteria(Process.class).add(Restrictions.eq("processType", r2SProcessType))
                    .add(Restrictions.eq("process", dataLoadParent));
            for (Object rawToStageObject : rawToStageCriteria.list()) {
                Process rawToStage = (Process) rawToStageObject;
                rawToStage.setNextProcessId(nextProcessForR2S);
                session.update(rawToStage);
            }

            session.getTransaction().commit();
            etlJobInfo.setProcessId(dataLoadParent.getProcessId());
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return etlJobInfo;
    }

    public List<ETLJobInfo> updateETLJob(ETLJobInfo etlJobInfo) {
        Session session = sessionFactory.openSession();
        List<ETLJobInfo> etlJobInfoList = new ArrayList<ETLJobInfo>();

        try {
            session.beginTransaction();
            // fetching data for given UUID from etlstep table
            Criteria etlstepCriteria = session.createCriteria(Etlstep.class).add(Restrictions.eq("id.uuid", etlJobInfo.getUuid()));
            List<Etlstep> etlstepList = etlstepCriteria.list();
            for (Etlstep etlstep : etlstepList) {
                etlstep.setBusDomainId(etlJobInfo.getBusDomainId());
                etlstep.setProcessName(etlJobInfo.getProcessName());
                etlstep.setDescription(etlJobInfo.getDescription());
                etlstep.setDropRaw(etlJobInfo.isDropRaw());
                session.update(etlstep);

                ETLJobInfo etlJobInfo1 = new ETLJobInfo();
                etlJobInfo1.setUuid(etlstep.getId().getUuid());
                etlJobInfo1.setBusDomainId(etlstep.getBusDomainId());
                etlJobInfo1.setProcessName(etlstep.getProcessName());
                etlJobInfo1.setDescription(etlstep.getDescription());
                etlJobInfo1.setCounter(etlstepList.size());
                etlJobInfoList.add(etlJobInfo1);
            }
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return etlJobInfoList;
    }

    public ETLJobInfo insertETLJob(ETLJobInfo etlJobInfo) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            // inserting data for given UUID in etlstep table
            Etlstep etlstep = new Etlstep();

            EtlstepId etlstepId = new EtlstepId();
            etlstepId.setUuid(etlJobInfo.getUuid());
            etlstepId.setSerialNumber(new Date().getTime());
            etlstep.setId(etlstepId);

            etlstep.setBusDomainId(etlJobInfo.getBusDomainId());
            etlstep.setProcessName(etlJobInfo.getProcessName());
            etlstep.setDescription(etlJobInfo.getDescription());
            etlstep.setDropRaw(false);
            session.save(etlstep);
            etlJobInfo.setSerialNumber(etlstep.getId().getSerialNumber());

            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return etlJobInfo;
    }

    public void deleteETLJob(ETLJobInfo etlJobInfo) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            // deleting data for given UUID in etlstep table
            // fetching data for given UUID from etlstep table
            Criteria etlstepCriteria = session.createCriteria(Etlstep.class).add(Restrictions.eq("id.uuid", etlJobInfo.getUuid()));
            List<Etlstep> etlstepList = etlstepCriteria.list();
            for (Etlstep etlstep : etlstepList) {
                session.delete(etlstep);
            }
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
    }

    public List<ETLJobInfo> getETLJob(ETLJobInfo etlJobInfo) {
        Session session = sessionFactory.openSession();
        List<ETLJobInfo> etlJobInfoList = new ArrayList<ETLJobInfo>();

        try {
            session.beginTransaction();
            //fetching data from etlstep table
            Criteria listETLStep = session.createCriteria(Etlstep.class).setProjection(Projections.distinct(Projections.property("id.uuid")));
            Integer counter = listETLStep.list().size();
            listETLStep.setMaxResults(etlJobInfo.getPageSize());
            listETLStep.setFirstResult(etlJobInfo.getPage());
            List<String> etlstepList = listETLStep.list();
            for (String uuid : etlstepList) {
                ETLJobInfo etlJobInfo1 = new ETLJobInfo();
                Etlstep etlstep = (Etlstep) session.createCriteria(Etlstep.class).add(Restrictions.eq("id.uuid", uuid)).setMaxResults(1).list().get(0);
                etlJobInfo1.setUuid(etlstep.getId().getUuid());
                etlJobInfo1.setBusDomainId(etlstep.getBusDomainId());
                etlJobInfo1.setProcessName(etlstep.getProcessName());
                etlJobInfo1.setDescription(etlstep.getDescription());
                etlJobInfo1.setCounter(counter);
                etlJobInfoList.add(etlJobInfo1);
            }
            session.getTransaction().commit();
        } catch (MetadataException e) {
            session.getTransaction().rollback();
            LOGGER.error(e);
        } finally {
            session.close();
        }
        return etlJobInfoList;
    }
}
