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

package com.wipro.ats.bdre.md.util;

import com.wipro.ats.bdre.md.dao.jpa.GeneralConfig;
import com.wipro.ats.bdre.md.dao.jpa.GeneralConfigId;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Created by AR288503 on 12/15/2015.
 */
public class SetupDB {
    private static final Logger LOGGER = Logger.getLogger(SetupDB.class);
    @Autowired
    private SessionFactory sessionFactory;
    private Session session;

    public static void main(String[] args) {
        SetupDB setupDB = new SetupDB();
        setupDB.init();
        try {
            setupDB.populateBatch("databases/setup/Batch.csv");
            setupDB.populateBatchStatus("databases/setup/BatchStatus.csv");
            setupDB.populateBusDomain("databases/setup/BusDomain.csv");
            setupDB.populateDeployStatus("databases/setup/DeployStatus.csv");
            setupDB.populateExecStatus("databases/setup/ExecStatus.csv");
            setupDB.populateGeneralConfig("databases/setup/GeneralConfig.csv");
            setupDB.populateLineageNodeType("databases/setup/LineageNodeType.csv");
            setupDB.populateLineageQueryType("databases/setup/LineageQueryType.csv");
            setupDB.populateProcess("databases/setup/Process.csv");
            setupDB.populateProcessTemplate("databases/setup/ProcessTemplate.csv");
            setupDB.populateProcessType("databases/setup/ProcessType.csv");
            setupDB.populateProperties("databases/setup/Properties.csv");
            setupDB.populateServers("databases/setup/Servers.csv");
            setupDB.populateUserRoles("databases/setup/UserRoles.csv");
            setupDB.populateUsers("databases/setup/Users.csv");
            setupDB.populateWorkflowType("databases/setup/WorkflowType.csv");

            setupDB.halt();
        } catch (Exception e) {
            LOGGER.error("Error Occurred",e);
            setupDB.term();
        }

    }


    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
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
        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            while ((line = br.readLine()) != null) {
                String[] cols = getColumns(line);
                if (cols == null) continue;
                
                ////session.saveOrUpdate(generalConfig);
            }
            session.flush();
        } catch (Exception e) {
            LOGGER.error("In File: "+dataFile+"; Bad Line: " + line);
            LOGGER.error(e.getMessage());
            throw new Exception(e);
        }
    }

    private void populateBatchStatus(String dataFile) throws Exception {
        String line = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            while ((line = br.readLine()) != null) {
                String[] cols = getColumns(line);
                if (cols == null) continue;

                //session.saveOrUpdate(generalConfig);
            }
            session.flush();
        } catch (Exception e) {
            LOGGER.error("In File: "+dataFile+"; Bad Line: " + line);
            LOGGER.error(e.getMessage());
            throw new Exception(e);
        }
    }

    private void populateBusDomain(String dataFile) throws Exception {
        String line = null;

        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            while ((line = br.readLine()) != null) {

                String[] cols = getColumns(line);
                if (cols == null) continue;

                //session.saveOrUpdate(generalConfig);
            }
            session.flush();
        } catch (Exception e) {
            LOGGER.error("In File: "+dataFile+"; Bad Line: " + line);
            LOGGER.error(e.getMessage());
            throw new Exception(e);
        }
    }

    private void populateDeployStatus(String dataFile) throws Exception {
        String line = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            while ((line = br.readLine()) != null) {
                String[] cols = getColumns(line);
                if (cols == null) continue;

                //session.saveOrUpdate(generalConfig);
            }
            session.flush();
        } catch (Exception e) {
            LOGGER.error("In File: "+dataFile+"; Bad Line: " + line);
            LOGGER.error(e.getMessage());
            throw new Exception(e);
        }
    }

    private void populateExecStatus(String dataFile) throws Exception {
        String line = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            while ((line = br.readLine()) != null) {
                String[] cols = getColumns(line);
                if (cols == null) continue;

                //session.saveOrUpdate(generalConfig);
            }
            session.flush();
        } catch (Exception e) {
            LOGGER.error("In File: "+dataFile+"; Bad Line: " + line);
            LOGGER.error(e.getMessage());
            throw new Exception(e);
        }
    }

    private void populateLineageNodeType(String dataFile) throws Exception {
        String line = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            while ((line = br.readLine()) != null) {
                String[] cols = getColumns(line);
                if (cols == null) continue;

                //session.saveOrUpdate(generalConfig);
            }
            session.flush();
        } catch (Exception e) {
            LOGGER.error("In File: "+dataFile+"; Bad Line: " + line);
            LOGGER.error(e.getMessage());
            throw new Exception(e);
        }
    }

    private void populateLineageQueryType(String dataFile) throws Exception {
        String line = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            while ((line = br.readLine()) != null) {
                String[] cols = getColumns(line);
                if (cols == null) continue;

                //session.saveOrUpdate(generalConfig);
            }
            session.flush();
        } catch (Exception e) {
            LOGGER.error("In File: "+dataFile+"; Bad Line: " + line);
            LOGGER.error(e.getMessage());
            throw new Exception(e);
        }
    }

    private void populateProcess(String dataFile) throws Exception {
        String line = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            while ((line = br.readLine()) != null) {
                String[] cols = getColumns(line);
                if (cols == null) continue;

                //session.saveOrUpdate(generalConfig);
            }
            session.flush();
        } catch (Exception e) {
            LOGGER.error("In File: "+dataFile+"; Bad Line: " + line);
            LOGGER.error(e.getMessage());
            throw new Exception(e);
        }
    }

    private void populateProcessTemplate(String dataFile) throws Exception {
        String line = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            while ((line = br.readLine()) != null) {
                String[] cols = getColumns(line);
                if (cols == null) continue;

                //session.saveOrUpdate(generalConfig);
            }
            session.flush();
        } catch (Exception e) {
            LOGGER.error("In File: "+dataFile+"; Bad Line: " + line);
            LOGGER.error(e.getMessage());
            throw new Exception(e);
        }
    }

    private void populateProcessType(String dataFile) throws Exception {
        String line = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            while ((line = br.readLine()) != null) {
                String[] cols = getColumns(line);
                if (cols == null) continue;

                //session.saveOrUpdate(generalConfig);
            }
            session.flush();
        } catch (Exception e) {
            LOGGER.error("In File: "+dataFile+"; Bad Line: " + line);
            LOGGER.error(e.getMessage());
            throw new Exception(e);
        }
    }

    private void populateProperties(String dataFile) throws Exception {
        String line = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            while ((line = br.readLine()) != null) {
                String[] cols = getColumns(line);
                if (cols == null) continue;

                //session.saveOrUpdate(generalConfig);
            }
            session.flush();
        } catch (Exception e) {
            LOGGER.error("In File: "+dataFile+"; Bad Line: " + line);
            LOGGER.error(e.getMessage());
            throw new Exception(e);
        }
    }

    private void populateServers(String dataFile) throws Exception {
        String line = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            while ((line = br.readLine()) != null) {
                String[] cols = getColumns(line);
                if (cols == null) continue;

                //session.saveOrUpdate(generalConfig);
            }
            session.flush();
        } catch (Exception e) {
            LOGGER.error("In File: "+dataFile+"; Bad Line: " + line);
            LOGGER.error(e.getMessage());
            throw new Exception(e);
        }
    }

    private void populateUserRoles(String dataFile) throws Exception {
        String line = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            while ((line = br.readLine()) != null) {
                String[] cols = getColumns(line);
                if (cols == null) continue;

                //session.saveOrUpdate(generalConfig);
            }
            session.flush();
        } catch (Exception e) {
            LOGGER.error("In File: "+dataFile+"; Bad Line: " + line);
            LOGGER.error(e.getMessage());
            throw new Exception(e);
        }
    }

    private void populateUsers(String dataFile) throws Exception {
        String line = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            while ((line = br.readLine()) != null) {
                String[] cols = getColumns(line);
                if (cols == null) continue;

                //session.saveOrUpdate(generalConfig);
            }
            session.flush();
        } catch (Exception e) {
            LOGGER.error("In File: "+dataFile+"; Bad Line: " + line);
            LOGGER.error(e.getMessage());
            throw new Exception(e);
        }
    }

    private void populateWorkflowType(String dataFile) throws Exception {
        String line = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            while ((line = br.readLine()) != null) {
                String[] cols = getColumns(line);
                if (cols == null) continue;

                //session.saveOrUpdate(generalConfig);
            }
            session.flush();
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
                generalConfig.setRequired(new Boolean(cols[4]));
                generalConfig.setDefaultVal(cols[5]);
                generalConfig.setType(cols[6]);
                generalConfig.setEnabled(new Boolean(cols[7]));
                session.saveOrUpdate(generalConfig);
            }
            session.flush();
        } catch (Exception e) {
            LOGGER.error("In File: "+dataFile+"; Bad Line: " + line);
            throw new Exception(e);
        }
    }


}
