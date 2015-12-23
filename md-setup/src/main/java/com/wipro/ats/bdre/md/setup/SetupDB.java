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

package com.wipro.ats.bdre.md.setup;


import com.wipro.ats.bdre.md.setup.beans.*;
import com.wipro.ats.bdre.md.setup.beans.Process;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Date;

/**
 * Created by AR288503 on 12/15/2015.
 */
public class SetupDB {
    private static final Logger LOGGER = Logger.getLogger(SetupDB.class);
    @Autowired
    private SessionFactory sessionFactory;
    private Session session;

    public static void main(String[] args) {
        System.out.println("args = " + args[0]);
        String projectRoot="";
        if(args!=null && args.length != 0 && args[0]!=null && !args[0].isEmpty()){
            projectRoot=args[0]+"/";
        }
        SetupDB setupDB = new SetupDB();
        setupDB.init();
        try {

            setupDB.populateExecStatus(projectRoot+"databases/setup/ExecStatus.csv");
            setupDB.populateBatchStatus(projectRoot+"databases/setup/BatchStatus.csv");
            setupDB.populateDeployStatus(projectRoot+"databases/setup/DeployStatus.csv");
            setupDB.populateProcessType(projectRoot+"databases/setup/ProcessType.csv");
            setupDB.populateWorkflowType(projectRoot+"databases/setup/WorkflowType.csv");
            setupDB.populateBusDomain(projectRoot+"databases/setup/BusDomain.csv");
            setupDB.populateBatch(projectRoot+"databases/setup/Batch.csv");
            setupDB.populateServers(projectRoot+"databases/setup/Servers.csv");
            setupDB.populateLineageNodeType(projectRoot+"databases/setup/LineageNodeType.csv");
            setupDB.populateLineageQueryType(projectRoot+"databases/setup/LineageQueryType.csv");
            setupDB.populateProcessTemplate(projectRoot+"databases/setup/ProcessTemplate.csv");
            setupDB.populateGeneralConfig(projectRoot+"databases/setup/GeneralConfig.csv");
            setupDB.populateProcess(projectRoot+"databases/setup/Process.csv");
            setupDB.populateProperties(projectRoot+"databases/setup/Properties.csv");
            setupDB.populateUsers(projectRoot+"databases/setup/Users.csv");
            setupDB.populateUserRoles(projectRoot+"databases/setup/UserRoles.csv");


            setupDB.halt();
        } catch (Exception e) {
            LOGGER.error("Error Occurred",e);
            setupDB.term();
        }

    }


    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("setup-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
        session = sessionFactory.openSession();
        session.beginTransaction();
    }

    public void halt() {
        session.getTransaction().commit();
    }

    public void term() {
        session.getTransaction().rollback();
    }

    private String[] getColumns(String line) {
        if (line.trim().isEmpty() || line.trim().startsWith("--") || line.trim().startsWith("#")) {
            LOGGER.info("Ignoring comment:" + line);
            return null;
        }
        //This will split the line by , but ignore if ' is within single quote(e.g. in the data)
        String[] cols = line.split(",(?=([^']*'[^']*')*[^']*$)");
        for (int i = 0; i < cols.length; i++) {
            cols[i] = cols[i].trim();
            cols[i] = cols[i].replaceAll("(^'|'$)", "");
        }
        return cols;
    }

    private void populateBatch(String dataFile) throws Exception {
        String line = null;
        int lineNum =0;

        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            while ((line = br.readLine()) != null) {
                lineNum++;
                LOGGER.debug("Line #" + lineNum + ": "+line);
                String[] cols = getColumns(line);
                if (cols == null) continue;
                Batch batch=new Batch();
                batch.setBatchId(new Long(cols[0]));
                LOGGER.info(batch.getBatchId().toString());
                if(!("null".equals(cols[1]))) {
                    batch.setSourceInstanceExecId(new Long(cols[1]));

                }

                batch.setBatchType(cols[2]);
                session.saveOrUpdate(batch);

            }

            //session.flush();;
        } catch (Exception e) {
            LOGGER.error("In File: "+dataFile+"; Bad Line: " + line);
            LOGGER.error(e.getMessage());
            throw new Exception(e);
        }
    }

    private void populateBatchStatus(String dataFile) throws Exception {
        String line = null;
        int lineNum =0;

        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            while ((line = br.readLine()) != null) {
                lineNum++;
                LOGGER.debug("Line #" + lineNum + ": "+line);
                String[] cols = getColumns(line);
                if (cols == null) continue;
                BatchStatus batchStatus=new BatchStatus();
                batchStatus.setBatchStateId(new Integer(cols[0]));
                batchStatus.setDescription(cols[1]);
                session.saveOrUpdate(batchStatus);
            }
            //session.flush();;
        } catch (Exception e) {
            LOGGER.error("In File: "+dataFile+"; Bad Line: " + line);
            LOGGER.error(e.getMessage());
            throw new Exception(e);
        }
    }

    private void populateBusDomain(String dataFile) throws Exception {
        String line = null;
        int lineNum =0;

        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            while ((line = br.readLine()) != null) {
                lineNum++;
                LOGGER.debug("Line #" + lineNum + ": "+line);
                String[] cols = getColumns(line);
                if (cols == null) continue;
                BusDomain busDomain=new BusDomain();
                busDomain.setBusDomainId(new Integer(cols[0]));
                busDomain.setBusDomainName(cols[1]);
                busDomain.setDescription(cols[2]);
                busDomain.setBusDomainOwner(cols[3]);
                session.saveOrUpdate(busDomain);
            }
            ////session.flush();;
        } catch (Exception e) {
            LOGGER.error("In File: "+dataFile+"; Bad Line: " + line);
            LOGGER.error(e.getMessage());
            throw new Exception(e);
        }
    }

    private void populateDeployStatus(String dataFile) throws Exception {
        String line = null;
        int lineNum =0;

        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            while ((line = br.readLine()) != null) {
                lineNum++;
                LOGGER.debug("Line #" + lineNum + ": "+line);
                String[] cols = getColumns(line);
                if (cols == null) continue;
                DeployStatus deployStatus=new DeployStatus();
                deployStatus.setDeployStatusId(new Short(cols[0]));
                deployStatus.setDescription(cols[1]);
                session.saveOrUpdate(deployStatus);
            }
            //session.flush();;
        } catch (Exception e) {
            LOGGER.error("In File: "+dataFile+"; Bad Line: " + line);
            LOGGER.error(e.getMessage());
            throw new Exception(e);
        }
    }

    private void populateExecStatus(String dataFile) throws Exception {
        String line = null;
        int lineNum =0;

        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            while ((line = br.readLine()) != null) {
                lineNum++;
                LOGGER.debug("Line #" + lineNum + ": "+line);
                String[] cols = getColumns(line);
                if (cols == null) continue;
                ExecStatus execStatus=new ExecStatus();
                execStatus.setExecStateId(new Integer(cols[0]));
                execStatus.setDescription(cols[1]);
                session.saveOrUpdate(execStatus);
            }

            //session.flush();;
        } catch (Exception e) {
            LOGGER.error("In File: "+dataFile+"; Bad Line: " + line);
            LOGGER.error(e.getMessage());
            throw new Exception(e);
        }
    }

    private void populateLineageNodeType(String dataFile) throws Exception {
        String line = null;
        int lineNum =0;

        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            while ((line = br.readLine()) != null) {
                lineNum++;
                LOGGER.debug("Line #" + lineNum + ": "+line);
                String[] cols = getColumns(line);
                if (cols == null) continue;
                LineageNodeType lineageNodeType=new LineageNodeType();
                lineageNodeType.setNodeTypeId(new Integer(cols[0]));
                lineageNodeType.setNodeTypeName(cols[1]);
                session.saveOrUpdate(lineageNodeType);
            }
            //session.flush();;
        } catch (Exception e) {
            LOGGER.error("In File: "+dataFile+"; Bad Line: " + line);
            LOGGER.error(e.getMessage());
            throw new Exception(e);
        }
    }

    private void populateLineageQueryType(String dataFile) throws Exception {
        String line = null;
        int lineNum =0;

        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            while ((line = br.readLine()) != null) {
                lineNum++;
                LOGGER.debug("Line #" + lineNum + ": "+line);
                String[] cols = getColumns(line);
                if (cols == null) continue;
                LineageQueryType lineageQueryType=new LineageQueryType();
                lineageQueryType.setQueryTypeId(new Integer(cols[0]));
                lineageQueryType.setQueryTypeName(cols[1]);
                session.saveOrUpdate(lineageQueryType);
            }
            //session.flush();;
        } catch (Exception e) {
            LOGGER.error("In File: "+dataFile+"; Bad Line: " + line);
            LOGGER.error(e.getMessage());
            throw new Exception(e);
        }
    }

    private void populateProcessType(String dataFile) throws Exception {
        String line = null;
        int lineNum =0;

        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            while ((line = br.readLine()) != null) {
                lineNum++;
                LOGGER.debug("Line #" + lineNum + ": "+line);
                String[] cols = getColumns(line);
                if (cols == null) continue;
                ProcessType pType=new ProcessType();
                pType.setProcessTypeId(new Integer(cols[0]));
                pType.setProcessTypeName(cols[1]);
                if(!("null".equals(cols[2])))
                    pType.setParentProcessTypeId(new Integer(cols[2]));
                session.saveOrUpdate(pType);
            }
            ////session.flush();;
        } catch (Exception e) {
            LOGGER.error("In File: "+dataFile+"; Bad Line: " + line);
            LOGGER.error(e.getMessage());
            throw new Exception(e);
        }
    }

    private void populateProcessTemplate(String dataFile) throws Exception {
        String line = null;
        int lineNum =0;

        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            while ((line = br.readLine()) != null) {
                lineNum++;
                LOGGER.debug("Line #" + lineNum + ": "+line);
                String[] cols = getColumns(line);
                if (cols == null) continue;
                ProcessTemplate processTemplate=new ProcessTemplate();
                processTemplate.setProcessTemplateId(new Integer(cols[0]));
                processTemplate.setDescription(cols[1]);
                processTemplate.setAddTs(new Date());
                processTemplate.setProcessName(cols[3]);

                processTemplate.setBusDomainId(new Integer(cols[4]));

                processTemplate.setProcessTypeId(new Integer(cols[5]));
                if(!("null".equals(cols[6]))) {

                    processTemplate.setProcessTemplateId(new Integer(cols[6]));
                }
                if("1".equals(cols[7])) cols[7]="true";
                processTemplate.setCanRecover(new Boolean(cols[7]));

                processTemplate.setBatchCutPattern(cols[8]);
                processTemplate.setNextProcessTemplateId(cols[9]);
                if("1".equals(cols[10])) cols[10]="true";
                processTemplate.setDeleteFlag(new Boolean(cols[10]));

                processTemplate.setWorkflowId(new Integer(cols[11]));
                session.saveOrUpdate(processTemplate);
            }
            //session.flush();;
        } catch (Exception e) {
            LOGGER.error("In File: "+dataFile+"; Bad Line: " + line);
            LOGGER.error(e.getMessage());
            throw new Exception(e);
        }
    }
    private void populateProcess(String dataFile) throws Exception {
        String line = null;
        int lineNum =0;

        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            while ((line = br.readLine()) != null) {
                lineNum++;
                LOGGER.debug("Line #" + lineNum + ": "+line);
                String[] cols = getColumns(line);
                if (cols == null) continue;
                Process process=new Process();
                process.setProcessId(new Integer(cols[0]));
                process.setDescription(cols[1]);
                process.setAddTs(new Date());
                process.setProcessName(cols[3]);
                LOGGER.info(new Integer(cols[4]));

                process.setBusDomainId(new Integer(cols[4]));

                process.setProcessTypeId(new Integer(cols[5]));
                if(!("null".equals(cols[6]))) {
                    process.setParentProcessId(new Integer(cols[6]));
                }
                if("1".equals(cols[7])) cols[7]="true";
                process.setCanRecover(new Boolean(cols[7]));

                process.setEnqueuingProcessId(new Integer(cols[8]));
                process.setBatchCutPattern(cols[9]);
                process.setNextProcessId(cols[10]);
                if("1".equals(cols[11])) cols[11]="true";
                process.setDeleteFlag(new Boolean(cols[11]));


                process.setWorkflowId(new Integer(cols[12]));
                if(!("null".equals(cols[13]))) {
                    process.setProcessTemplateId(new Integer(cols[13]));
                }
                process.setEditTs(new Date());
                session.saveOrUpdate(process);
            }
            //session.flush();;
        } catch (Exception e) {
            LOGGER.error("In File: "+dataFile+"; Bad Line: " + line);
            LOGGER.error(e.getMessage());
            throw new Exception(e);
        }
    }

    private void populateProperties(String dataFile) throws Exception {
        String line = null;
        int lineNum =0;

        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            while ((line = br.readLine()) != null) {
                lineNum++;
                LOGGER.debug("Line #" + lineNum + ": "+line);
                String[] cols = getColumns(line);
                if (cols == null) continue;
                PropertiesId propertiesId=new PropertiesId();
                propertiesId.setProcessId(new Integer(cols[0]));
                propertiesId.setPropKey(cols[2]);
                Properties properties = new Properties();
                properties.setId(propertiesId);
                properties.setConfigGroup(cols[1]);
                properties.setPropValue(cols[3]);
                properties.setDescription(cols[4]);
                session.saveOrUpdate(properties);
            }
            //session.flush();;
        } catch (Exception e) {
            LOGGER.error("In File: "+dataFile+"; Bad Line: " + line);
            LOGGER.error(e.getMessage());
            throw new Exception(e);
        }
    }

    private void populateServers(String dataFile) throws Exception {
        String line = null;
        int lineNum =0;

        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            while ((line = br.readLine()) != null) {
                lineNum++;
                LOGGER.debug("Line #" + lineNum + ": "+line);
                String[] cols = getColumns(line);
                if (cols == null) continue;
                Servers servers=new Servers();
                servers.setServerId(new Integer(cols[0]));
                servers.setServerType(cols[1]);
                servers.setServerName(cols[2]);
                servers.setServerMetainfo(cols[3]);
                servers.setLoginUser(cols[4]);
                servers.setLoginPassword(cols[5]);
                servers.setSshPrivateKey(cols[6]);
                servers.setServerIp(cols[7]);
                session.saveOrUpdate(servers);
            }
            //session.flush();;
        } catch (Exception e) {
            LOGGER.error("In File: "+dataFile+"; Bad Line: " + line);
            LOGGER.error(e.getMessage());
            throw new Exception(e);
        }
    }

    private void populateUserRoles(String dataFile) throws Exception {
        String line = null;
        int lineNum =0;

        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            while ((line = br.readLine()) != null) {
                lineNum++;
                LOGGER.debug("Line #" + lineNum + ": "+line);
                String[] cols = getColumns(line);
                if (cols == null) continue;
                UserRoles userRoles=new UserRoles();
                userRoles.setUserRoleId(new Integer(cols[0]));
                userRoles.setUsername(cols[1]);
                userRoles.setRole(cols[2]);
                session.saveOrUpdate(userRoles);
            }
            //session.flush();;
        } catch (Exception e) {
            LOGGER.error("In File: "+dataFile+"; Bad Line: " + line);
            LOGGER.error(e.getMessage());
            throw new Exception(e);
        }
    }

    private void populateUsers(String dataFile) throws Exception {
        String line = null;
        int lineNum =0;

        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            while ((line = br.readLine()) != null) {
                lineNum++;
                LOGGER.debug("Line #" + lineNum + ": "+line);
                String[] cols = getColumns(line);
                if (cols == null) continue;
                Users users=new Users();
                users.setUsername(cols[0]);
                users.setPassword(cols[1]);
                if("1".equals(cols[2])) cols[2]="true";
                users.setEnabled(new Boolean(cols[2]));
                session.saveOrUpdate(users);
            }
            //session.flush();;
        } catch (Exception e) {
            LOGGER.error("In File: "+dataFile+"; Bad Line: " + line);
            LOGGER.error(e.getMessage());
            throw new Exception(e);
        }
    }

    private void populateWorkflowType(String dataFile) throws Exception {
        String line = null;
        int lineNum =0;

        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            while ((line = br.readLine()) != null) {
                lineNum++;
                LOGGER.debug("Line #" + lineNum + ": "+line);
                String[] cols = getColumns(line);
                if (cols == null) continue;
                WorkflowType workflowType=new WorkflowType();
                workflowType.setWorkflowId(new Integer(cols[0]));
                workflowType.setWorkflowTypeName(cols[1]);
                session.saveOrUpdate(workflowType);
            }
            //session.flush();;
        } catch (Exception e) {
            LOGGER.error("In File: "+dataFile+"; Bad Line: " + line);
            LOGGER.error(e.getMessage());
            throw new Exception(e);
        }
    }

    private void populateGeneralConfig(String dataFile) throws Exception {
        String line = null;
        int lineNum =0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            while ((line = br.readLine()) != null) {
                lineNum++;
                LOGGER.debug("Line #" + lineNum + ": "+line);
                String[] cols = getColumns(line);
                if (cols == null) continue;
                GeneralConfigId generalConfigId = new GeneralConfigId();
                generalConfigId.setConfigGroup(cols[0]);
                generalConfigId.setGcKey(cols[1]);
                GeneralConfig generalConfig = new GeneralConfig();
                generalConfig.setId(generalConfigId);
                generalConfig.setGcValue(cols[2]);
                generalConfig.setDescription(cols[3]);
                if("1".equals(cols[4]))cols[4]="true";
                generalConfig.setRequired(new Boolean(cols[4]));
                generalConfig.setDefaultVal(cols[5]);
                generalConfig.setType(cols[6]);
                if("1".equals(cols[7]))cols[7]="true";
                generalConfig.setEnabled(new Boolean(cols[7]));
                session.saveOrUpdate(generalConfig);
            }
            //session.flush();;
        } catch (Exception e) {
            LOGGER.error("In File: "+dataFile+"; Bad Line: " + line);
            throw new Exception(e);
        }
    }

}
