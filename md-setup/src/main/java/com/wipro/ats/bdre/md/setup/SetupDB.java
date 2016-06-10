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

package com.wipro.ats.bdre.md.setup;


import com.wipro.ats.bdre.exception.MetadataException;
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
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by AR288503 on 12/15/2015.
 */
public class SetupDB {
    private static final Logger LOGGER = Logger.getLogger(SetupDB.class);
    @Autowired
    private SessionFactory sessionFactory;
    private Session session;
    private static java.util.Properties map;
    private static Pattern pattern = Pattern.compile("\\$\\{(?<key>[^}]*)\\}");
    String inFile="In File: ";
    String badLine="; Bad Line: ";
    String lineNumber="Line #";

    public static void main(String[] args) throws Exception {
        if (args == null || args.length != 2) {
            LOGGER.info("Usage SetupDB <CSV file base dir> <profile>");
        }
        String projectRoot = args[0] + "/";
        String profile = args[1];

        SetupDB setupDB = new SetupDB();
        setupDB.init();
        try {
            map =new java.util.Properties();
            map.load(new FileInputStream(projectRoot + "databases/setup/profile."+profile+".properties"));
            setupDB.populateExecStatus(projectRoot + "databases/setup/ExecStatus.csv");
            setupDB.populateBatchStatus(projectRoot + "databases/setup/BatchStatus.csv");
            setupDB.populateDeployStatus(projectRoot + "databases/setup/DeployStatus.csv");
            setupDB.populateProcessType(projectRoot + "databases/setup/ProcessType.csv");
            setupDB.populatePermissionType(projectRoot + "databases/setup/PermissionType.csv");
            setupDB.populateWorkflowType(projectRoot + "databases/setup/WorkflowType.csv");
            setupDB.populateBusDomain(projectRoot + "databases/setup/BusDomain.csv");
            setupDB.populateBatch(projectRoot + "databases/setup/Batch.csv");
            setupDB.populateServers(projectRoot + "databases/setup/Servers.csv");
            setupDB.populateLineageNodeType(projectRoot + "databases/setup/LineageNodeType.csv");
            setupDB.populateLineageQueryType(projectRoot + "databases/setup/LineageQueryType.csv");
            setupDB.populateProcessTemplate(projectRoot + "databases/setup/ProcessTemplate.csv");
            setupDB.populateGeneralConfig(projectRoot + "databases/setup/GeneralConfig.csv");
            setupDB.populateProcess(projectRoot + "databases/setup/Process.csv");
            setupDB.populateProperties(projectRoot + "databases/setup/Properties.csv");
            setupDB.populateUsers(projectRoot + "databases/setup/Users.csv");
            setupDB.populateUserRoles(projectRoot + "databases/setup/UserRoles.csv");
            setupDB.populateADQStatus(projectRoot + "databases/setup/ADQStatus.csv");
            setupDB.populateInstalledPlugins(projectRoot + "databases/setup/InstalledPlugins.csv");

            setupDB.halt();
        } catch (MetadataException e) {
            LOGGER.error("Error Occurred", e);
            setupDB.term();
            throw e;
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
        session.close();
    }

    public void term() {
        session.getTransaction().rollback();
        session.close();
    }


    public static String replaceVars(String line) throws MetadataException {
        StringBuffer sb = new StringBuffer();
        Matcher m = pattern.matcher(line);
        while (m.find()) {
            String key = m.group("key");
            if (map.containsKey(key)) {//replace if founded key exists in map
                m.appendReplacement(sb, map.get(key).toString());
            }
            else{
                throw new MetadataException("There is no variable defined for ${"+key+"} for line: "+line);
            }
        }
        m.appendTail(sb);

        return sb.toString();
    }
    private String[] getColumns(String line) throws MetadataException {
        if (line.trim().isEmpty() || line.trim().startsWith("--") || line.trim().startsWith("#")) {
            LOGGER.info("Ignoring comment:" + line);
            return null;
        }
        //replace the variables
        line=replaceVars(line);
        //This will split the line by , but ignore if ' is within single quote(e.g. in the data)
        String[] cols = line.split(",(?=([^']*'[^']*')*[^']*$)");
        for (int i = 0; i < cols.length; i++) {
            cols[i] = cols[i].trim();
            cols[i] = cols[i].replaceAll("(^'|'$)", "");
        }
        return cols;
    }

    private void populateBatch(String dataFile) throws MetadataException, IOException {
        String line = null;
        int lineNum = 0;

        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            while ((line = br.readLine()) != null) {
                lineNum++;
                LOGGER.debug(lineNumber + lineNum + ": " + line);
                String[] cols = getColumns(line);
                if (cols == null)
                    continue;
                Batch batch = new Batch();
                batch.setBatchId(new Long(cols[0]));
                LOGGER.info(batch.getBatchId().toString());
                if (!("null".equals(cols[1]))) {
                    batch.setSourceInstanceExecId(new Long(cols[1]));

                }

                batch.setBatchType(cols[2]);
                Object existing = session.get(batch.getClass(), batch.getBatchId());
                if (existing == null) {
                    session.save(batch);
                }

            }
        } catch (MetadataException e) {
            LOGGER.error(inFile + dataFile + badLine + line);
            LOGGER.error(e.getMessage());
            throw new MetadataException(e);
        }
        catch (IOException e) {
            LOGGER.error(inFile + dataFile + badLine + line);
            LOGGER.error(e.getMessage());
            throw new IOException(e);
        }
    }

    private void populateBatchStatus(String dataFile) throws MetadataException, IOException {
        String line = null;
        int lineNum = 0;

        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            while ((line = br.readLine()) != null) {
                lineNum++;
                LOGGER.debug(lineNumber + lineNum + ": " + line);
                String[] cols = getColumns(line);
                if (cols == null)
                    continue;
                BatchStatus batchStatus = new BatchStatus();
                batchStatus.setBatchStateId(new Integer(cols[0]));
                batchStatus.setDescription(cols[1]);
                Object existing = session.get(batchStatus.getClass(), batchStatus.getBatchStateId());
                if (existing == null) {
                    session.save(batchStatus);
                }
            }
        } catch (MetadataException e) {
            LOGGER.error(inFile + dataFile + badLine + line);
            LOGGER.error(e.getMessage());
            throw new MetadataException(e);
        }
        catch (IOException e) {
            LOGGER.error(inFile + dataFile + badLine + line);
            LOGGER.error(e.getMessage());
            throw new IOException(e);
        }
    }

    private void populateBusDomain(String dataFile) throws MetadataException, IOException {
        String line = null;
        int lineNum = 0;

        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            while ((line = br.readLine()) != null) {
                lineNum++;
                LOGGER.debug(lineNumber + lineNum + ": " + line);
                String[] cols = getColumns(line);
                if (cols == null)
                    continue;
                BusDomain busDomain = new BusDomain();
                busDomain.setBusDomainId(new Integer(cols[0]));
                busDomain.setBusDomainName(cols[1]);
                busDomain.setDescription(cols[2]);
                busDomain.setBusDomainOwner(cols[3]);
                Object existing = session.get(busDomain.getClass(), busDomain.getBusDomainId());
                if (existing == null) {
                    session.save(busDomain);
                }
            }
        } catch (MetadataException e) {
            LOGGER.error(inFile + dataFile + badLine + line);
            LOGGER.error(e.getMessage());
            throw new MetadataException(e);
        }
        catch (IOException e) {
            LOGGER.error(inFile + dataFile + badLine + line);
            LOGGER.error(e.getMessage());
            throw new IOException(e);
        }
    }

    private void populateDeployStatus(String dataFile) throws MetadataException, IOException {
        String line = null;
        int lineNum = 0;

        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            while ((line = br.readLine()) != null) {
                lineNum++;
                LOGGER.debug(lineNumber + lineNum + ": " + line);
                String[] cols = getColumns(line);
                if (cols == null)
                    continue;
                DeployStatus deployStatus = new DeployStatus();
                deployStatus.setDeployStatusId(new Short(cols[0]));
                deployStatus.setDescription(cols[1]);
                Object existing = session.get(deployStatus.getClass(), deployStatus.getDeployStatusId());
                if (existing == null) {
                    session.save(deployStatus);
                }
            }
        } catch (MetadataException e) {
            LOGGER.error(inFile + dataFile + badLine + line);
            LOGGER.error(e.getMessage());
            throw new MetadataException(e);
        }
        catch (IOException e) {
            LOGGER.error(inFile + dataFile + badLine + line);
            LOGGER.error(e.getMessage());
            throw new IOException(e);
        }
    }

    private void populateExecStatus(String dataFile) throws MetadataException, IOException {
        String line = null;
        int lineNum = 0;

        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            while ((line = br.readLine()) != null) {
                lineNum++;
                LOGGER.debug(lineNumber + lineNum + ": " + line);
                String[] cols = getColumns(line);
                if (cols == null)
                    continue;
                ExecStatus execStatus = new ExecStatus();
                execStatus.setExecStateId(new Integer(cols[0]));
                execStatus.setDescription(cols[1]);
                Object existing = session.get(execStatus.getClass(), execStatus.getExecStateId());
                if (existing == null) {
                    session.save(execStatus);
                }
            }
        } catch (MetadataException e) {
            LOGGER.error(inFile + dataFile + badLine + line);
            LOGGER.error(e.getMessage());
            throw new MetadataException(e);
        }
        catch (IOException e) {
            LOGGER.error(inFile + dataFile + badLine + line);
            LOGGER.error(e.getMessage());
            throw new IOException(e);
        }
    }

    private void populateLineageNodeType(String dataFile) throws MetadataException, IOException {
        String line = null;
        int lineNum = 0;

        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            while ((line = br.readLine()) != null) {
                lineNum++;
                LOGGER.debug(lineNumber + lineNum + ": " + line);
                String[] cols = getColumns(line);
                if (cols == null)
                    continue;
                LineageNodeType lineageNodeType = new LineageNodeType();
                lineageNodeType.setNodeTypeId(new Integer(cols[0]));
                lineageNodeType.setNodeTypeName(cols[1]);
                Object existing = session.get(lineageNodeType.getClass(), lineageNodeType.getNodeTypeId());
                if (existing == null) {
                    session.save(lineageNodeType);
                }
            }
        } catch (MetadataException e) {
            LOGGER.error(inFile + dataFile + badLine + line);
            LOGGER.error(e.getMessage());
            throw new MetadataException(e);
        }
        catch (IOException e) {
            LOGGER.error(inFile + dataFile + badLine + line);
            LOGGER.error(e.getMessage());
            throw new IOException(e);
        }
    }

    private void populateLineageQueryType(String dataFile) throws MetadataException, IOException {
        String line = null;
        int lineNum = 0;

        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            while ((line = br.readLine()) != null) {
                lineNum++;
                LOGGER.debug(lineNumber + lineNum + ": " + line);
                String[] cols = getColumns(line);
                if (cols == null)
                    continue;
                LineageQueryType lineageQueryType = new LineageQueryType();
                lineageQueryType.setQueryTypeId(new Integer(cols[0]));
                lineageQueryType.setQueryTypeName(cols[1]);
                Object existing = session.get(lineageQueryType.getClass(), lineageQueryType.getQueryTypeId());
                if (existing == null) {
                    session.save(lineageQueryType);
                }
            }
        } catch (MetadataException e) {
            LOGGER.error(inFile + dataFile + badLine + line);
            LOGGER.error(e.getMessage());
            throw new MetadataException(e);
        }
        catch (IOException e) {
            LOGGER.error(inFile + dataFile + badLine + line);
            LOGGER.error(e.getMessage());
            throw new IOException(e);
        }

    }

    private void populateProcessType(String dataFile) throws MetadataException, IOException {
        String line = null;
        int lineNum = 0;

        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            while ((line = br.readLine()) != null) {
                lineNum++;
                LOGGER.debug(lineNumber + lineNum + ": " + line);
                String[] cols = getColumns(line);
                if (cols == null)
                    continue;
                ProcessType pType = new ProcessType();
                pType.setProcessTypeId(new Integer(cols[0]));
                pType.setProcessTypeName(cols[1]);
                if (!("null".equals(cols[2])))
                    pType.setParentProcessTypeId(new Integer(cols[2]));
                Object existing = session.get(pType.getClass(), pType.getProcessTypeId());
                if (existing == null) {
                    session.save(pType);
                }
            }
        }catch (MetadataException e) {
            LOGGER.error(inFile + dataFile + badLine + line);
            LOGGER.error(e.getMessage());
            throw new MetadataException(e);
        }
        catch (IOException e) {
            LOGGER.error(inFile + dataFile + badLine + line);
            LOGGER.error(e.getMessage());
            throw new IOException(e);
        }
    }


   private void populatePermissionType(String dataFile) throws MetadataException, IOException {
        String line = null;
        int lineNum = 0;

        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            while ((line = br.readLine()) != null) {
                lineNum++;
                LOGGER.debug(lineNumber + lineNum + ": " + line);
                String[] cols = getColumns(line);
                if (cols == null)
                    continue;
                PermissionType pType=new PermissionType();
                pType.setPermissionTypeId(new Integer(cols[0]));
                pType.setPermissionTypeName(cols[1]);
                Object existing = session.get(pType.getClass(), pType.getPermissionTypeId());
                if (existing == null) {
                    session.save(pType);
                }
            }
        }catch (MetadataException e) {
            LOGGER.error(inFile + dataFile + badLine + line);
            LOGGER.error(e.getMessage());
            throw new MetadataException(e);
        }
        catch (IOException e) {
            LOGGER.error(inFile + dataFile + badLine + line);
            LOGGER.error(e.getMessage());
            throw new IOException(e);
        }
    }

    private void populateProcessTemplate(String dataFile) throws MetadataException, IOException {
        String line = null;
        int lineNum = 0;

        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            while ((line = br.readLine()) != null) {
                lineNum++;
                LOGGER.debug(lineNumber + lineNum + ": " + line);
                String[] cols = getColumns(line);
                if (cols == null)
                    continue;
                ProcessTemplate processTemplate = new ProcessTemplate();
                processTemplate.setProcessTemplateId(new Integer(cols[0]));
                processTemplate.setDescription(cols[1]);
                processTemplate.setAddTs(new Date());
                processTemplate.setProcessName(cols[3]);

                processTemplate.setBusDomainId(new Integer(cols[4]));

                processTemplate.setProcessTypeId(new Integer(cols[5]));
                if (!("null".equals(cols[6]))) {

                    processTemplate.setProcessTemplateId(new Integer(cols[6]));
                }
                if ("1".equals(cols[7]))
                    cols[7] = "true";
                processTemplate.setCanRecover(new Boolean(cols[7]));

                processTemplate.setBatchCutPattern(cols[8]);
                processTemplate.setNextProcessTemplateId(cols[9]);
                if ("1".equals(cols[10]))
                    cols[10] = "true";
                processTemplate.setDeleteFlag(new Boolean(cols[10]));

                processTemplate.setWorkflowId(new Integer(cols[11]));
                Object existing = session.get(processTemplate.getClass(), processTemplate.getProcessTemplateId());
                if (existing == null) {
                    session.save(processTemplate);
                }
            }
        } catch (MetadataException e) {
            LOGGER.error(inFile + dataFile + badLine + line);
            LOGGER.error(e.getMessage());
            throw new MetadataException(e);
        }
        catch (IOException e) {
            LOGGER.error(inFile + dataFile + badLine + line);
            LOGGER.error(e.getMessage());
            throw new IOException(e);
        }
    }

    private void populateProcess(String dataFile) throws MetadataException, IOException {
        String line = null;
        int lineNum = 0;

        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            while ((line = br.readLine()) != null) {
                lineNum++;
                LOGGER.debug(lineNumber + lineNum + ": " + line);
                String[] cols = getColumns(line);
                if (cols == null)
                    continue;
                Process process = new Process();
                process.setProcessId(new Integer(cols[0]));
                process.setDescription(cols[1]);
                process.setAddTs(new Date());
                process.setProcessName(cols[3]);
                LOGGER.info(new Integer(cols[4]));

                process.setBusDomainId(new Integer(cols[4]));

                process.setProcessTypeId(new Integer(cols[5]));
                if (!("null".equals(cols[6]))) {
                    process.setParentProcessId(new Integer(cols[6]));
                }
                if ("1".equals(cols[7]))
                    cols[7] = "true";
                process.setCanRecover(new Boolean(cols[7]));

                process.setEnqueuingProcessId(new Integer(cols[8]));
                process.setBatchCutPattern(cols[9]);
                process.setNextProcessId(cols[10]);
                if ("1".equals(cols[11]))
                    cols[11] = "true";
                process.setDeleteFlag(new Boolean(cols[11]));

                process.setWorkflowId(new Integer(cols[12]));
                if (!("null".equals(cols[13]))) {
                    process.setProcessTemplateId(new Integer(cols[13]));
                }
                process.setEditTs(new Date());
                Object existing = session.get(process.getClass(), process.getProcessId());
                if (existing == null) {
                    session.save(process);
                }
            }
        } catch (MetadataException e) {
            LOGGER.error(inFile + dataFile + badLine + line);
            LOGGER.error(e.getMessage());
            throw new MetadataException(e);
        }
        catch (IOException e) {
            LOGGER.error(inFile + dataFile + badLine + line);
            LOGGER.error(e.getMessage());
            throw new IOException(e);
        }
    }

    private void populateProperties(String dataFile) throws MetadataException, IOException {
        String line = null;
        int lineNum = 0;

        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            while ((line = br.readLine()) != null) {
                lineNum++;
                LOGGER.debug(lineNumber + lineNum + ": " + line);
                String[] cols = getColumns(line);
                if (cols == null)
                    continue;
                PropertiesId propertiesId = new PropertiesId();
                propertiesId.setProcessId(new Integer(cols[0]));
                propertiesId.setPropKey(cols[2]);
                Properties properties = new Properties();
                properties.setId(propertiesId);
                properties.setConfigGroup(cols[1]);
                properties.setPropValue(cols[3]);
                properties.setDescription(cols[4]);
                Object existing = session.get(properties.getClass(), properties.getId());
                if (existing == null) {
                    session.save(properties);
                }
            }
        } catch (MetadataException e) {
            LOGGER.error(inFile + dataFile + badLine + line);
            LOGGER.error(e.getMessage());
            throw new MetadataException(e);
        }
        catch (IOException e) {
            LOGGER.error(inFile + dataFile + badLine + line);
            LOGGER.error(e.getMessage());
            throw new IOException(e);
        }
    }

    private void populateServers(String dataFile) throws MetadataException, IOException {
        String line = null;
        int lineNum = 0;

        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            while ((line = br.readLine()) != null) {
                lineNum++;
                LOGGER.debug(lineNumber + lineNum + ": " + line);
                String[] cols = getColumns(line);
                if (cols == null)
                    continue;
                Servers servers = new Servers();
                servers.setServerId(new Integer(cols[0]));
                servers.setServerType(cols[1]);
                servers.setServerName(cols[2]);
                servers.setServerMetainfo(cols[3]);
                servers.setLoginUser(cols[4]);
                servers.setLoginPassword(cols[5]);
                servers.setSshPrivateKey(cols[6]);
                servers.setServerIp(cols[7]);
                Object existing = session.get(servers.getClass(), servers.getServerId());
                if (existing == null) {
                    session.save(servers);
                }
            }
        } catch (MetadataException e) {
            LOGGER.error(inFile + dataFile + badLine + line);
            LOGGER.error(e.getMessage());
            throw new MetadataException(e);
        }
        catch (IOException e) {
            LOGGER.error(inFile + dataFile + badLine + line);
            LOGGER.error(e.getMessage());
            throw new IOException(e);
        }
    }

    private void populateUserRoles(String dataFile) throws MetadataException, IOException {
        String line = null;
        int lineNum = 0;

        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            while ((line = br.readLine()) != null) {
                lineNum++;
                LOGGER.debug(lineNumber + lineNum + ": " + line);
                String[] cols = getColumns(line);
                if (cols == null)
                    continue;
                UserRoles userRoles = new UserRoles();
                userRoles.setUserRoleId(new Integer(cols[0]));
                userRoles.setUsername(cols[1]);
                userRoles.setRole(cols[2]);
                Object existing = session.get(userRoles.getClass(), userRoles.getUserRoleId());
                if (existing == null) {
                    session.save(userRoles);
                }
            }
        } catch (MetadataException e) {
            LOGGER.error(inFile + dataFile + badLine + line);
            LOGGER.error(e.getMessage());
            throw new MetadataException(e);
        }
        catch (IOException e) {
            LOGGER.error(inFile + dataFile + badLine + line);
            LOGGER.error(e.getMessage());
            throw new IOException(e);
        }
    }

    private void populateUsers(String dataFile) throws MetadataException , IOException {
        String line = null;
        int lineNum = 0;

        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            while ((line = br.readLine()) != null) {
                lineNum++;
                LOGGER.debug(lineNumber + lineNum + ": " + line);
                String[] cols = getColumns(line);
                if (cols == null)
                    continue;
                Users users = new Users();
                users.setUsername(cols[0]);
                users.setPassword(cols[1]);
                if ("1".equals(cols[2]))
                    cols[2] = "true";
                users.setEnabled(new Boolean(cols[2]));
                Object existing = session.get(users.getClass(), users.getUsername());
                if (existing == null) {
                    session.save(users);
                }
            }
        } catch (MetadataException e) {
            LOGGER.error(inFile + dataFile + badLine + line);
            LOGGER.error(e.getMessage());
            throw new MetadataException(e);
        }
        catch (IOException e) {
            LOGGER.error(inFile + dataFile + badLine + line);
            LOGGER.error(e.getMessage());
            throw new IOException(e);
        }
    }

    private void populateWorkflowType(String dataFile) throws MetadataException, IOException {
        String line = null;
        int lineNum = 0;

        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            while ((line = br.readLine()) != null) {
                lineNum++;
                LOGGER.debug(lineNumber + lineNum + ": " + line);
                String[] cols = getColumns(line);
                if (cols == null)
                    continue;
                WorkflowType workflowType = new WorkflowType();
                workflowType.setWorkflowId(new Integer(cols[0]));
                workflowType.setWorkflowTypeName(cols[1]);
                Object existing = session.get(workflowType.getClass(), workflowType.getWorkflowId());
                if (existing == null) {
                    session.save(workflowType);
                }
            }
        } catch (MetadataException e) {
            LOGGER.error(inFile + dataFile + badLine + line);
            LOGGER.error(e.getMessage());
            throw new MetadataException(e);
        }
        catch (IOException e) {
            LOGGER.error(inFile + dataFile + badLine + line);
            LOGGER.error(e.getMessage());
            throw new IOException(e);
        }
    }

    private void populateGeneralConfig(String dataFile) throws MetadataException, IOException {
        String line = null;
        int lineNum = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            while ((line = br.readLine()) != null) {
                lineNum++;
                LOGGER.debug(lineNumber + lineNum + ": " + line);

                String[] cols = getColumns(line);
                if (cols == null)
                    continue;

                GeneralConfigId generalConfigId = new GeneralConfigId();
                generalConfigId.setConfigGroup(cols[0]);
                generalConfigId.setGcKey(cols[1]);
                GeneralConfig generalConfig = new GeneralConfig();
                generalConfig.setId(generalConfigId);
                if (!"null".equals(cols[2]))
                    generalConfig.setGcValue(cols[2]);
                if (!"null".equals(cols[3]))
                    generalConfig.setDescription(cols[3]);
                if ("1".equals(cols[4]))
                    cols[4] = "true";
                generalConfig.setRequired(new Boolean(cols[4]));
                if (!"null".equals(cols[5]))
                    generalConfig.setDefaultVal(cols[5]);
                generalConfig.setType(cols[6]);
                if ("1".equals(cols[7]))
                    cols[7] = "true";
                generalConfig.setEnabled(new Boolean(cols[7]));
                Object existing = session.get(generalConfig.getClass(), generalConfig.getId());
                if (existing == null) {
                    session.save(generalConfig);
                }
            }
        } catch (MetadataException e) {
            LOGGER.error(inFile + dataFile + badLine + line);
            LOGGER.error(e.getMessage());
            throw new MetadataException(e);
        }
        catch (IOException e) {
            LOGGER.error(inFile + dataFile + badLine + line);
            LOGGER.error(e.getMessage());
            throw new IOException(e);
        }
    }

    private void populateADQStatus(String dataFile) throws MetadataException, IOException {
        String line = null;
        int lineNum = 0;

        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            while ((line = br.readLine()) != null) {
                lineNum++;
                LOGGER.debug(lineNumber + lineNum + ": " + line);
                String[] cols = getColumns(line);
                if (cols == null)
                    continue;
                AppDeploymentQueueStatus adqStatus = new AppDeploymentQueueStatus();
                adqStatus.setAppDeploymentStatusId(new Integer(cols[0]));
                adqStatus.setDescription(cols[1]);
                Object existing = session.get(adqStatus.getClass(), adqStatus.getAppDeploymentStatusId());
                if (existing == null) {
                    session.save(adqStatus);
                }
            }
        } catch (MetadataException e) {
            LOGGER.error(inFile + dataFile + badLine + line);
            LOGGER.error(e.getMessage());
            throw new MetadataException(e);
        }
        catch (IOException e) {
            LOGGER.error(inFile + dataFile + badLine + line);
            LOGGER.error(e.getMessage());
            throw new IOException(e);
        }
    }



    private void populateInstalledPlugins(String dataFile) throws MetadataException, IOException {
        String line = null;
        int lineNum = 0;

        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            while ((line = br.readLine()) != null) {
                lineNum++;
                LOGGER.debug(lineNumber + lineNum + ": " + line);
                String[] cols = getColumns(line);
                if (cols == null)
                    continue;
                InstalledPlugins installedPlugins=new InstalledPlugins();
                installedPlugins.setPluginUniqueId(cols[0]);
                installedPlugins.setPluginId(cols[1]);
                installedPlugins.setName(cols[2]);
                installedPlugins.setDescription(cols[3]);
                installedPlugins.setAuthor(cols[4]);
                installedPlugins.setPluginVersion(cols[5]);
                installedPlugins.setAddTs(new Date());
                installedPlugins.setPlugin("bdre-min");
                installedPlugins.setUninstallable(false);
                Object existing = session.get(installedPlugins.getClass(), installedPlugins.getPluginUniqueId());
                if (existing == null) {
                    session.save(installedPlugins);
                }
            }
        } catch (MetadataException e) {
            LOGGER.error(inFile + dataFile + badLine + line);
            LOGGER.error(e.getMessage());
            throw new MetadataException(e);
        }
        catch (IOException e) {
            LOGGER.error(inFile + dataFile + badLine + line);
            LOGGER.error(e.getMessage());
            throw new IOException(e);
        }
    }

}
