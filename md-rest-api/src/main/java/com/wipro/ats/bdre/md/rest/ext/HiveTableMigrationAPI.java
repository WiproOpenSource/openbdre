package com.wipro.ats.bdre.md.rest.ext;

import com.wipro.ats.bdre.exception.MetadataException;
import com.wipro.ats.bdre.md.api.GetGeneralConfig;
import com.wipro.ats.bdre.md.beans.ClusterInfo;
import com.wipro.ats.bdre.md.beans.table.GeneralConfig;
import com.wipro.ats.bdre.md.dao.GeneralConfigDAO;
import com.wipro.ats.bdre.md.dao.ProcessDAO;
import com.wipro.ats.bdre.md.dao.jpa.GeneralConfigId;
import com.wipro.ats.bdre.md.dao.jpa.Properties;
import com.wipro.ats.bdre.md.rest.RestWrapper;
import com.wipro.ats.bdre.md.rest.RestWrapperOptions;
import com.wipro.ats.bdre.md.rest.util.BindingResultError;
import com.wipro.ats.bdre.md.rest.util.Dao2TableUtil;
import com.wipro.ats.bdre.md.rest.util.DateConverter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by SU324335 on 3/29/2016.
 */
@Controller
@RequestMapping("/hivemigration")
public class HiveTableMigrationAPI {


    private static final Logger LOGGER = Logger.getLogger(HiveTableMigrationAPI.class);
    private static final String HIVEMIGRATION = "hive-migration";
    private static Connection connection;
    private static String driverName = "org.apache.hive.jdbc.HiveDriver";


    @Autowired
    private ProcessDAO processDAO;

    @Autowired
    GeneralConfigDAO generalConfigDAO;

    //Fetching all the databases from hive of source cluster
    @RequestMapping(value = "/databases/{srcEnv:.+}", method = {RequestMethod.GET})
    @ResponseBody
    public RestWrapperOptions getDBList(@PathVariable("srcEnv") String sourceEnv) {

        RestWrapperOptions restWrapperOptions = null;
        LOGGER.info(sourceEnv + "srcEnv");
        try {
            Class.forName(driverName);
            connection = DriverManager.getConnection("jdbc:hive2://" + sourceEnv + "/default", "", "");
            ResultSet rs = connection.createStatement().executeQuery("SHOW DATABASES");

            List<String> databases = new ArrayList<String>();
            while (rs.next()) {
                String dbName = rs.getString(1);
                databases.add(dbName.toUpperCase());
            }
            List<RestWrapperOptions.Option> options = new ArrayList<RestWrapperOptions.Option>();
            for (String database : databases) {
                RestWrapperOptions.Option option = new RestWrapperOptions.Option(database, database);
                options.add(option);
            }
            restWrapperOptions = new RestWrapperOptions(options, RestWrapperOptions.OK);
        } catch (Exception e) {
            LOGGER.error("error occured:" + e);
            restWrapperOptions = new RestWrapperOptions(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapperOptions;
    }


    //Fetching all the databases from hive of dest cluster
    @RequestMapping(value = "/destdatabases/{destEnv:.+}", method = {RequestMethod.GET})
    @ResponseBody
    public RestWrapperOptions getdestDBList(@PathVariable("destEnv") String destEnv) {
        LOGGER.info(destEnv + "destENV");
        RestWrapperOptions restWrapperOptions = null;
        try {
            Class.forName(driverName);
            connection = DriverManager.getConnection("jdbc:hive2://" + destEnv + "/default", "", "");
            ResultSet rs = connection.createStatement().executeQuery("SHOW DATABASES");

            List<String> databases = new ArrayList<String>();
            while (rs.next()) {
                String dbName = rs.getString(1);
                databases.add(dbName.toUpperCase());
            }
            List<RestWrapperOptions.Option> options = new ArrayList<RestWrapperOptions.Option>();
            for (String database : databases) {
                RestWrapperOptions.Option option = new RestWrapperOptions.Option(database, database);
                options.add(option);
            }
            restWrapperOptions = new RestWrapperOptions(options, RestWrapperOptions.OK);
        } catch (Exception e) {
            LOGGER.error("error occured :" + e);
            restWrapperOptions = new RestWrapperOptions(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapperOptions;
    }


    //Fetching all the databases from hive of dest cluster
    @RequestMapping(value = "/tables/{srcEnv}/{srcDB}", method = {RequestMethod.GET})
    @ResponseBody
    public RestWrapperOptions getTablesList(@PathVariable("srcEnv") String srcEnv, @PathVariable("srcDB") String srcDB) {
        RestWrapperOptions restWrapperOptions = null;
        LOGGER.info(srcEnv + "srcEnvi");
        LOGGER.info(srcDB + "srcDB");
        try {
            Class.forName(driverName);
            connection = DriverManager.getConnection("jdbc:hive2://" + srcEnv + "/" + srcDB.toLowerCase(), "", "");
            ResultSet rs = connection.createStatement().executeQuery("SHOW TABLES");

            List<String> databases = new ArrayList<String>();
            while (rs.next()) {
                String dbName = rs.getString(1);
                databases.add(dbName.toUpperCase());
            }
            List<RestWrapperOptions.Option> options = new ArrayList<RestWrapperOptions.Option>();
            for (String database : databases) {
                RestWrapperOptions.Option option = new RestWrapperOptions.Option(database, database);
                options.add(option);
            }
            restWrapperOptions = new RestWrapperOptions(options, RestWrapperOptions.OK);
        } catch (Exception e) {
            LOGGER.error("error occured " + e);
            restWrapperOptions = new RestWrapperOptions(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapperOptions;
    }

    @RequestMapping(value = "/createjobs/{checked}", method = RequestMethod.POST)
    @ResponseBody
    public RestWrapper createJob(@RequestParam Map<String, String> map, Principal principal, @PathVariable("checked") String[] checkedTables) {
        LOGGER.debug(" value of map is " + map.size());
        RestWrapper restWrapper = null;

        String processName = null;
        String processDesc = null;
        Integer busDomainID = null;
        String nameNodeIp = map.get("scrNameNode");
        String jobTrackerIp = map.get("srcJobTracker");
        String destnameNodeIp = map.get("destNameNode");
        String destjobTrackerIp = map.get("destjobTracker");
        com.wipro.ats.bdre.md.dao.jpa.Properties jpaProperties = null;
        for (int i = 1; i <= checkedTables.length; i++)
            LOGGER.info("table is " + checkedTables[i - 1]);
        List<com.wipro.ats.bdre.md.beans.table.Process> allTableProcessList = new ArrayList<com.wipro.ats.bdre.md.beans.table.Process>();

        for (int i = 1; i <= checkedTables.length; i++) {
            List<Properties> propertiesList = new ArrayList<Properties>();
            LOGGER.info("table name is " + checkedTables[i - 1]);
            jpaProperties = Dao2TableUtil.buildJPAProperties(HIVEMIGRATION, "src-nn", nameNodeIp, "SourceNameNodeAddress");
            propertiesList.add(jpaProperties);
            jpaProperties = Dao2TableUtil.buildJPAProperties(HIVEMIGRATION, "src-jt", jobTrackerIp, "SourceJobTrackerAddress");
            propertiesList.add(jpaProperties);

            jpaProperties = Dao2TableUtil.buildJPAProperties(HIVEMIGRATION, "dest-nn", destnameNodeIp, "DestNameNodeAddress");
            propertiesList.add(jpaProperties);
            jpaProperties = Dao2TableUtil.buildJPAProperties(HIVEMIGRATION, "dest-jt", destjobTrackerIp, "DestJobTrackerAddress");
            propertiesList.add(jpaProperties);

            jpaProperties = Dao2TableUtil.buildJPAProperties(HIVEMIGRATION, "src-table", checkedTables[i - 1], "source Table");
            propertiesList.add(jpaProperties);
            for (String string : map.keySet()) {
                LOGGER.info("String is" + string);
                if (map.get(string) == null || ("").equals(map.get(string))) {
                    continue;
                }
                if (string.startsWith("srcEnv_srcEnv")) {
                    String str = map.get(string);
                    int pos = str.indexOf(",\"-%%-\",");
                    LOGGER.info("pos is " + pos);
                    String srcHiveAddr = str.substring(0, pos);
                    LOGGER.info("SrcHIve " + srcHiveAddr);
                    jpaProperties = Dao2TableUtil.buildJPAProperties(HIVEMIGRATION, "src-hive", "jdbc:hive2://" + srcHiveAddr, "source Hive address");
                    propertiesList.add(jpaProperties);
                } else if (string.startsWith("srcEnv_processName")) {
                    LOGGER.debug("srcEnv_processName" + map.get(string));
                    processName = map.get(string);
                } else if (string.startsWith("srcEnv_processDesc")) {
                    LOGGER.debug("srcEnv_processDescription" + map.get(string));
                    processDesc = map.get(string);
                } else if (string.startsWith("srcEnv_busDomainId")) {
                    LOGGER.debug("srcEnv_busDomainID" + map.get(string));
                    busDomainID = new Integer(map.get(string));
                } else if (string.startsWith("srcDB_")) {
                    jpaProperties = Dao2TableUtil.buildJPAProperties(HIVEMIGRATION, "src-db", map.get(string), "source database");
                    propertiesList.add(jpaProperties);
                } else if (string.startsWith("destEnv_instexecId")) {
                    jpaProperties = Dao2TableUtil.buildJPAProperties(HIVEMIGRATION, "bdre-tech-pt", map.get(string) + " bigint", "technical partition");
                    propertiesList.add(jpaProperties);
                } else if (string.startsWith("destEnv_destEnv")) {
                    String str = map.get(string);
                    int pos = str.indexOf(",\"-%%-\",");
                    String destHiveAddr = str.substring(0, pos);
                    LOGGER.info("dest pos " + pos);
                    LOGGER.info("DestHive " + destHiveAddr);
                    jpaProperties = Dao2TableUtil.buildJPAProperties(HIVEMIGRATION, "dest-hive", "jdbc:hive2://" + destHiveAddr, "destination Hive address");
                    propertiesList.add(jpaProperties);
                } else if (string.startsWith("destDB_")) {
                    jpaProperties = Dao2TableUtil.buildJPAProperties(HIVEMIGRATION, "dest-db", map.get(string), "destination database");
                    propertiesList.add(jpaProperties);
                }

            }

            List<com.wipro.ats.bdre.md.dao.jpa.Process> childProcesses = new ArrayList<com.wipro.ats.bdre.md.dao.jpa.Process>();
            com.wipro.ats.bdre.md.dao.jpa.Process parentProcess = Dao2TableUtil.buildJPAProcess(31, processName + "-" + i, "table:" + i + "-" + processDesc, 1, busDomainID);

            com.wipro.ats.bdre.md.dao.jpa.Process preprocessingProcess = new com.wipro.ats.bdre.md.dao.jpa.Process();
            com.wipro.ats.bdre.md.dao.jpa.Process sourcestageloadProcess = new com.wipro.ats.bdre.md.dao.jpa.Process();
            com.wipro.ats.bdre.md.dao.jpa.Process sourcetodeststagecopyProcess = new com.wipro.ats.bdre.md.dao.jpa.Process();
            com.wipro.ats.bdre.md.dao.jpa.Process desttableloadProcess = new com.wipro.ats.bdre.md.dao.jpa.Process();
            com.wipro.ats.bdre.md.dao.jpa.Process registerpartitionProcess = new com.wipro.ats.bdre.md.dao.jpa.Process();


            preprocessingProcess = Dao2TableUtil.buildJPAProcess(32, "PreProcessing of table-" + i, "preprocessing:table-" + i, 1, busDomainID);
            sourcestageloadProcess = Dao2TableUtil.buildJPAProcess(33, "source stage load of table-" + i, "sourcestageload:table-" + i, 1, busDomainID);
            sourcetodeststagecopyProcess = Dao2TableUtil.buildJPAProcess(34, "src-dest stagecopy of table-" + i, "sourcetodeststagecopy:table-" + i, 1, busDomainID);
            desttableloadProcess = Dao2TableUtil.buildJPAProcess(35, "dest table load of table-" + i, "desttableload:table-" + i, 1, busDomainID);
            registerpartitionProcess = Dao2TableUtil.buildJPAProcess(36, "register partition of table-" + i, "registerpartition:table-" + i, 1, busDomainID);
            childProcesses.add(preprocessingProcess);
            childProcesses.add(sourcestageloadProcess);
            childProcesses.add(sourcetodeststagecopyProcess);
            childProcesses.add(desttableloadProcess);
            childProcesses.add(registerpartitionProcess);

            LOGGER.info("childprocess size" + childProcesses.size());
            LOGGER.info("Properties size" + propertiesList.size());
            List<com.wipro.ats.bdre.md.dao.jpa.Process> processList = processDAO.createHiveMigrationJob(parentProcess, childProcesses, propertiesList);
            LOGGER.info("after method size" + processList.size());
            List<com.wipro.ats.bdre.md.beans.table.Process> tableProcessList = new ArrayList<com.wipro.ats.bdre.md.beans.table.Process>();
            tableProcessList = Dao2TableUtil.jpaList2TableProcessList(processList);
            Integer counter = tableProcessList.size();
            LOGGER.info(counter + "counter");
            for (com.wipro.ats.bdre.md.beans.table.Process process : tableProcessList) {
                process.setCounter(counter);
                process.setTableAddTS(DateConverter.dateToString(process.getAddTS()));
                process.setTableEditTS(DateConverter.dateToString(process.getEditTS()));
                allTableProcessList.add(process);
            }

        }
        restWrapper = new RestWrapper(allTableProcessList, RestWrapper.OK);
        LOGGER.info("Process and Properties for data load process inserted by" + principal.getName());

        return restWrapper;

    }


    @RequestMapping(value = {"/cluster/{description}"}, method = RequestMethod.GET)
    @ResponseBody
    public RestWrapper list(@PathVariable("description") String description, Principal principal) {

        RestWrapper restWrapper = null;
        try {

            GetGeneralConfig generalConfigs = new GetGeneralConfig();
            List<GeneralConfig> generalConfigList = generalConfigs.byLikeConfigGroup(description, 1);
            if (!generalConfigList.isEmpty()) {
                if (generalConfigList.get(0).getRequired() == 2) {
                    restWrapper = new RestWrapper("Listing of Records Failed", RestWrapper.ERROR);
                } else {
                    restWrapper = new RestWrapper(generalConfigList, RestWrapper.OK);
                    LOGGER.info("All records listed with config group :" + "cluster" + "from General  Config by User:" + principal.getName());
                }
            } else {
                restWrapper = new RestWrapper(generalConfigList, RestWrapper.OK);

                LOGGER.info("All records listed with config group :" + "cluster" + "from General  Config by User:" + principal.getName());
            }

        } catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper("Exception while fetching details of cluster", RestWrapper.ERROR);
        }
        return restWrapper;
    }

    @RequestMapping(value = {"/insertcluster", "insertcluster"}, method = RequestMethod.PUT)

    @ResponseBody
    public RestWrapper insertCluster(@ModelAttribute("clusterInfo")
                                     @Valid ClusterInfo cluster, BindingResult bindingResult, Principal principal) {

        RestWrapper restWrapper = null;
        if (bindingResult.hasErrors()) {
            BindingResultError bindingResultError = new BindingResultError();
            return bindingResultError.errorMessage(bindingResult);
        }
        try {
            LOGGER.info("name_node_host " + cluster.getNameNodeHostName() + ":" + cluster.getNameNodePort());
            generalConfigDAO.insertCluster(cluster);


            restWrapper = new RestWrapper(cluster, RestWrapper.OK);
            LOGGER.info("Record inserted in General Config by User:" + principal.getName());

        } catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    @RequestMapping(value = {"/updatecluster", "updatecluster"}, method = RequestMethod.POST)

    @ResponseBody
    public RestWrapper updateCluster(@RequestParam Map<String, String> map, Principal principal) {

        RestWrapper restWrapper = null;
        String description;
        String defaultVal;
        try {
            String cgKey = map.get("key");
            defaultVal = map.get("defaultVal");
            description = map.get("description");


            GeneralConfig generalConfigUpdate = new GeneralConfig();
            GeneralConfig generalConfig = new GeneralConfig();

            CharSequence nn = "Namenode";
            CharSequence jt = "Job Tracker";
            CharSequence hive = "Hive Server2";

            if(cgKey.contains(nn))
                generalConfig.setConfigGroup("cluster.nn-address");
            if(cgKey.contains(jt))
                generalConfig.setConfigGroup("cluster.jt-address");
            if(cgKey.contains(hive))
                generalConfig.setConfigGroup("cluster.hive-address");

            generalConfig.setKey(cgKey);
            generalConfig.setDefaultVal(defaultVal);
            //initialising values to generalConfigId of dao
            GeneralConfigId jpaGeneralConfigId = new GeneralConfigId();
            jpaGeneralConfigId.setConfigGroup(generalConfig.getConfigGroup());
            jpaGeneralConfigId.setGcKey(generalConfig.getKey());
            //initialising values to generalConfig of dao
            com.wipro.ats.bdre.md.dao.jpa.GeneralConfig jpaGeneralConfig = generalConfigDAO.get(jpaGeneralConfigId);
            jpaGeneralConfig.setDefaultVal(generalConfig.getDefaultVal());
            //Calling Update method of generalConfigDAO
            generalConfigDAO.update(jpaGeneralConfig);
            generalConfigUpdate = generalConfig;

            restWrapper = new RestWrapper(generalConfigUpdate, RestWrapper.OK);
            LOGGER.info(" Record with key:" + generalConfigUpdate.getKey() + " and config group:" + generalConfigUpdate.getConfigGroup() + " updated in general_config by User:" + principal.getName());

        } catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }





}