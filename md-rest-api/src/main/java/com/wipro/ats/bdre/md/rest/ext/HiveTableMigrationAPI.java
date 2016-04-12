package com.wipro.ats.bdre.md.rest.ext;

import com.wipro.ats.bdre.IMConfig;
import com.wipro.ats.bdre.im.IMConstant;
import com.wipro.ats.bdre.im.etl.api.exception.ETLException;
import com.wipro.ats.bdre.lineage.LineageConstants;
import com.wipro.ats.bdre.md.dao.ProcessDAO;
import com.wipro.ats.bdre.md.dao.jpa.*;
import com.wipro.ats.bdre.md.dao.jpa.Process;
import com.wipro.ats.bdre.md.rest.RestWrapper;
import com.wipro.ats.bdre.md.rest.RestWrapperOptions;
import com.wipro.ats.bdre.md.rest.util.Dao2TableUtil;
import com.wipro.ats.bdre.md.rest.util.DateConverter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by SU324335 on 3/29/2016.
 */
@Controller
@RequestMapping("/hivemigration")
public class HiveTableMigrationAPI {


    private static final Logger LOGGER = Logger.getLogger(HiveTableMigrationAPI.class);

    private static Connection connection;
    private static String driverName = "org.apache.hive.jdbc.HiveDriver";


    @Autowired
    private ProcessDAO processDAO;
    //Fetching all the databases from hive of source cluster
    @RequestMapping(value = "/databases/{srcEnv:.+}", method = {RequestMethod.GET})
    @ResponseBody
    public RestWrapperOptions getDBList(@PathVariable("srcEnv") String sourceEnv) {

        RestWrapperOptions restWrapperOptions = null;
        LOGGER.info(sourceEnv + "srcEnv");
        try {
            Class.forName(driverName);
            connection=  DriverManager.getConnection("jdbc:hive2://"+sourceEnv+"/default", "", "");
            ResultSet rs = connection.createStatement().executeQuery("SHOW DATABASES");

            List<String> databases = new ArrayList<String>();
            while (rs.next()) {
                String dbName = rs.getString(1);
                databases.add(dbName.toUpperCase());
            }
            List<RestWrapperOptions.Option> options = new ArrayList<RestWrapperOptions.Option>();
            for(String database : databases)
            {
                RestWrapperOptions.Option option = new RestWrapperOptions.Option(database,database);
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
    @RequestMapping(value = "/destdatabases/{destEnv:.+}", method = {RequestMethod.GET})
    @ResponseBody
    public RestWrapperOptions getdestDBList(@PathVariable("destEnv") String destEnv) {
        LOGGER.info(destEnv+"destENV");
        RestWrapperOptions restWrapperOptions = null;
        try {
            Class.forName(driverName);
            connection=  DriverManager.getConnection("jdbc:hive2://"+destEnv+"/default", "", "");
            ResultSet rs = connection.createStatement().executeQuery("SHOW DATABASES");

            List<String> databases = new ArrayList<String>();
            while (rs.next()) {
                String dbName = rs.getString(1);
                databases.add(dbName.toUpperCase());
            }
            List<RestWrapperOptions.Option> options = new ArrayList<RestWrapperOptions.Option>();
            for(String database : databases)
            {
                RestWrapperOptions.Option option = new RestWrapperOptions.Option(database,database);
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
    public RestWrapperOptions getTablesList(@PathVariable("srcEnv") String srcEnv,@PathVariable("srcDB") String srcDB) {
        RestWrapperOptions restWrapperOptions = null;
        LOGGER.info(srcEnv + "srcEnvi");
        LOGGER.info(srcDB+"srcDB");
        try {
            Class.forName(driverName);
            connection=  DriverManager.getConnection("jdbc:hive2://"+srcEnv+"/"+srcDB.toLowerCase(), "", "");
            ResultSet rs = connection.createStatement().executeQuery("SHOW TABLES");

            List<String> databases = new ArrayList<String>();
            while (rs.next()) {
                String dbName = rs.getString(1);
                databases.add(dbName.toUpperCase());
            }
            List<RestWrapperOptions.Option> options = new ArrayList<RestWrapperOptions.Option>();
            for(String database : databases)
            {
                RestWrapperOptions.Option option = new RestWrapperOptions.Option(database,database);
                options.add(option);
            }
            restWrapperOptions = new RestWrapperOptions(options, RestWrapperOptions.OK);
        } catch (Exception e) {
            LOGGER.error("error occured :" + e);
            restWrapperOptions = new RestWrapperOptions(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapperOptions;
    }


   /* protected static Connection getHiveJDBCConnection(String dbName) throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName(IMConstant.HIVE_DRIVER_NAME);
//				String hiveConnectionString = MDConfig.getProperty("hive.hive-connection", null);
                String hiveConnectionString = IMConfig.getProperty("etl.hive-connection");
                LOGGER.info("hiveConn is " +hiveConnectionString);
                connection = DriverManager.getConnection("jdbc:hive2://192.168.56.102:10000" +"/"+ dbName, "", "");

            } catch (Exception e) {
                LOGGER.error(e);
                throw new ETLException(e);
            }
            LOGGER.info("Connection successful");
        }
        return connection;
    }

    public static void closeResultset(ResultSet resultSet) throws SQLException {
        if (resultSet != null)
            resultSet.close();
    }

    public static void closeConnection() throws SQLException {
        if (connection != null)
            connection.close();
    }

    //Fetching all the tables from connected database
    @RequestMapping(value = "/tables", method = {RequestMethod.GET})
    @ResponseBody
    public RestWrapperOptions getTableList(@RequestParam Map<String, String> map
    ) {
        String dbName = null;
        for (String string : map.keySet()) {
            LOGGER.info("String is" + string);
            if (map.get(string) == null || ("").equals(map.get(string))) {
                continue;
            }
            if (string.startsWith("srcDB_")) {
                dbName = map.get(string);
            }
        }
        LOGGER.info("Database is"+dbName);
        RestWrapperOptions restWrapperOptions = null;
        try {
            DatabaseMetaData metaData = getHiveJDBCConnection(dbName).getMetaData();

            ResultSet rs = metaData.getTables(null, null, null, null);
            List<String> tables = new ArrayList<String>();
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                tables.add(tableName.toUpperCase());
            }

            try {
                closeResultset(rs);
                closeConnection();
            } catch (SQLException ex) {
                LOGGER.error("Error in close");
            } finally {
                connection = null;
            }
            List<RestWrapperOptions.Option> options = new ArrayList<RestWrapperOptions.Option>();
            for(String table : tables)
            {
                RestWrapperOptions.Option option = new RestWrapperOptions.Option(table,table);
                options.add(option);
            }
            restWrapperOptions = new RestWrapperOptions(options, RestWrapperOptions.OK);
        } catch (Exception e) {
            LOGGER.error("error occured :" + e);
            restWrapperOptions = new RestWrapperOptions(e.getMessage(), RestWrapperOptions.ERROR);
        }
        return restWrapperOptions;
    }

*/
    @RequestMapping(value = "/createjobs/{checked}", method = RequestMethod.POST)
    @ResponseBody public
    RestWrapper createJob(@RequestParam Map<String, String> map,Principal principal,@PathVariable("checked") String[] checkedTables ) {
        LOGGER.debug(" value of map is " + map.size());
        RestWrapper restWrapper = null;

        String processName = null;
        String processDesc = null;
        Integer busDomainID = null;
        Integer tablesSize = 0;
        String nameNodeIp = map.get("scrNameNode");
        String jobTrackerIp =  map.get("srcJobTracker");
        String destnameNodeIp =  map.get("destNameNode");
        String destjobTrackerIp = map.get("destjobTracker");
        com.wipro.ats.bdre.md.dao.jpa.Properties jpaProperties = null;
        for(int i = 1; i <= checkedTables.length; i++)
                LOGGER.info("table is "+checkedTables[i-1]);
        List<com.wipro.ats.bdre.md.beans.table.Process> allTableProcessList = new ArrayList<com.wipro.ats.bdre.md.beans.table.Process>();

        for (int i = 1; i <= checkedTables.length; i++){
            List<Properties> propertiesList = new ArrayList<Properties>();
            LOGGER.info("table name is "+checkedTables[i-1]);
            jpaProperties = Dao2TableUtil.buildJPAProperties("hive-migration", "src-nn",nameNodeIp , "SourceNameNodeAddress");
            propertiesList.add(jpaProperties);
            jpaProperties = Dao2TableUtil.buildJPAProperties("hive-migration", "src-jt",jobTrackerIp , "SourceJobTrackerAddress");
            propertiesList.add(jpaProperties);

            jpaProperties = Dao2TableUtil.buildJPAProperties("hive-migration", "dest-nn",destnameNodeIp , "DestNameNodeAddress");
            propertiesList.add(jpaProperties);
            jpaProperties = Dao2TableUtil.buildJPAProperties("hive-migration", "dest-jt",destjobTrackerIp , "DestJobTrackerAddress");
            propertiesList.add(jpaProperties);

            jpaProperties = Dao2TableUtil.buildJPAProperties("hive-migration", "src-table",checkedTables[i-1] , "source Table");
            propertiesList.add(jpaProperties);
            for (String string : map.keySet()) {
            LOGGER.info("String is" + string);
            if (map.get(string) == null || ("").equals(map.get(string))) {
                continue;
            }
            if (string.startsWith("srcEnv_srcEnv")) {
                String str =  map.get(string);
                int pos = str.indexOf(",\"-%%-\",");
                LOGGER.info("pos is "+pos);
                String srcHiveAddr = str.substring(0,pos);
                LOGGER.info("SrcHIve "+srcHiveAddr);
                jpaProperties = Dao2TableUtil.buildJPAProperties("hive-migration", "src-hive",srcHiveAddr, "source Hive address");
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
                jpaProperties = Dao2TableUtil.buildJPAProperties("hive-migration", "src-db", map.get(string), "source database");
                propertiesList.add(jpaProperties);

            }
            else if (string.startsWith("destEnv_instexecId")) {
                jpaProperties = Dao2TableUtil.buildJPAProperties("hive-migration", "bdre-tech-pt", map.get(string), "technical partition");
                propertiesList.add(jpaProperties);
            }
            else if (string.startsWith("destEnv_destEnv")) {
                String str =  map.get(string);
                int pos = str.indexOf(",\"-%%-\",");
                String destHiveAddr = str.substring(0,pos);
                LOGGER.info("dest pos "+pos);
                LOGGER.info("DestHive "+destHiveAddr);
                jpaProperties = Dao2TableUtil.buildJPAProperties("hive-migration", "dest-hive" ,destHiveAddr, "destination Hive address");
                propertiesList.add(jpaProperties);
            } else if (string.startsWith("destDB_")) {
                jpaProperties = Dao2TableUtil.buildJPAProperties("hive-migration", "dest-db", map.get(string), "destination database");
                propertiesList.add(jpaProperties);
            }

        }


            List<com.wipro.ats.bdre.md.dao.jpa.Process> childProcesses = new ArrayList<com.wipro.ats.bdre.md.dao.jpa.Process>();
        com.wipro.ats.bdre.md.dao.jpa.Process parentProcess = Dao2TableUtil.buildJPAProcess(31, processName+"-"+i,"table:"+i+"-"+ processDesc, 1, busDomainID);

        com.wipro.ats.bdre.md.dao.jpa.Process preprocessingProcess = new com.wipro.ats.bdre.md.dao.jpa.Process();
        com.wipro.ats.bdre.md.dao.jpa.Process sourcestageloadProcess = new com.wipro.ats.bdre.md.dao.jpa.Process();
        com.wipro.ats.bdre.md.dao.jpa.Process sourcetodeststagecopyProcess = new com.wipro.ats.bdre.md.dao.jpa.Process();
        com.wipro.ats.bdre.md.dao.jpa.Process desttableloadProcess = new com.wipro.ats.bdre.md.dao.jpa.Process();
        com.wipro.ats.bdre.md.dao.jpa.Process registerpartitionProcess = new com.wipro.ats.bdre.md.dao.jpa.Process();


        preprocessingProcess = Dao2TableUtil.buildJPAProcess(32, "PreProcessing of table-"+i, "preprocessing:table-"+i, 1, busDomainID);
        sourcestageloadProcess = Dao2TableUtil.buildJPAProcess(33, "source stage load of table-"+i, "sourcestageload:table-"+i, 1, busDomainID);
        sourcetodeststagecopyProcess = Dao2TableUtil.buildJPAProcess(34, "src-dest stagecopy of table-"+i, "sourcetodeststagecopy:table-"+i, 1, busDomainID);
        desttableloadProcess = Dao2TableUtil.buildJPAProcess(35, "dest table load of table-"+i, "desttableload:table-"+i, 1, busDomainID);
        registerpartitionProcess = Dao2TableUtil.buildJPAProcess(36, "register partition of table-"+i, "registerpartition:table-"+i, 1, busDomainID);
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

  /*  public static Map<Integer, String> getAllColumnsMap(String dbName, String tableName) throws ETLException {
        Map<Integer, String> columnsMap = new TreeMap<Integer, String>();

            try {

                DatabaseMetaData metaData = getHiveJDBCConnection(dbName).getMetaData();

                ResultSet rs = metaData.getColumns(null, null, tableName, null);

                while (rs.next()) {
                    Integer ordinalPosition = Integer.valueOf(rs.getString("ORDINAL_POSITION"));
                    String columnName = rs.getString("COLUMN_NAME");
                    columnsMap.put(ordinalPosition, columnName.toUpperCase());
                }

                try {
                    closeResultset(rs);
                    closeConnection();
                } catch (SQLException ex) {
                    LOGGER.error("Error in close");
                } finally {
                    connection = null;
                }

            } catch (SQLException ex) {
                LOGGER.error("Error in Hive DB operation", ex);
                throw new ETLException(ex);
            }

        if (columnsMap.size() == 0) {
            LOGGER.warn("Table " + tableName + " does not exist in Hive db; inferring it to be a Temp table");
        }

        System.out.println("List of columns: ");
        for (Map.Entry<Integer, String> entry : columnsMap.entrySet())
            System.out.println(entry.getKey() + " ::: " + entry.getValue());
        return columnsMap;
    }*/

}
