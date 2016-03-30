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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
    //Fetching all the databases from hive of choosen cluster
    @RequestMapping(value = "/databases", method = {RequestMethod.GET})
    @ResponseBody
    public RestWrapperOptions getDBList() {
        RestWrapperOptions restWrapperOptions = null;
        try {
            Class.forName(driverName);
            connection=  DriverManager.getConnection("jdbc:hive2://192.168.56.102:10000/default", "", "");
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


    protected static Connection getHiveJDBCConnection(String dbName) throws SQLException {
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
            restWrapperOptions = new RestWrapperOptions(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapperOptions;
    }


    @RequestMapping(value = "/createjob", method = RequestMethod.POST)
    @ResponseBody public
    RestWrapper createJob(@RequestParam Map<String, String> map, Principal principal) {
        LOGGER.debug(" value of map is " + map.size());
        RestWrapper restWrapper = null;

        String processName = null;
        Integer tablesSize = 0;

        com.wipro.ats.bdre.md.dao.jpa.Properties jpaProperties = null;
        for (String string : map.keySet()) {

            if (string.startsWith("tables_")) {
                tablesSize++;
            }
            LOGGER.info("table is "+string);
        }
        LOGGER.info("table size "+tablesSize);
        for (String string : map.keySet()) {
            LOGGER.info("String is" + string);
            if (map.get(string) == null || ("").equals(map.get(string))) {
                continue;
            }
            else if (string.startsWith("srcEnv_processName")) {
                LOGGER.debug("srcEnv_processName" + map.get(string));
                processName = map.get(string);
            }
        }

        List<com.wipro.ats.bdre.md.dao.jpa.Process> childProcesses=new ArrayList<com.wipro.ats.bdre.md.dao.jpa.Process>();
        com.wipro.ats.bdre.md.dao.jpa.Process parentProcess = Dao2TableUtil.buildJPAProcess(31, processName, "parent description of c2c", 1,1);

        com.wipro.ats.bdre.md.dao.jpa.Process preprocessingProcess = new com.wipro.ats.bdre.md.dao.jpa.Process();
        com.wipro.ats.bdre.md.dao.jpa.Process sourcestageloadProcess = new com.wipro.ats.bdre.md.dao.jpa.Process();
        com.wipro.ats.bdre.md.dao.jpa.Process sourcetodeststagecopyProcess = new com.wipro.ats.bdre.md.dao.jpa.Process();
        com.wipro.ats.bdre.md.dao.jpa.Process desttableloadProcess = new com.wipro.ats.bdre.md.dao.jpa.Process();
        com.wipro.ats.bdre.md.dao.jpa.Process registerpartitionProcess = new com.wipro.ats.bdre.md.dao.jpa.Process();


        for(int i=1; i<=tablesSize; i++)
        {
            preprocessingProcess = Dao2TableUtil.buildJPAProcess(32,"preprocessing for "+processName+":table"+i,"preprocessing:table"+i,1,1);
            sourcestageloadProcess = Dao2TableUtil.buildJPAProcess(33,"sourcestageload for "+processName+":table"+i,"sourcestageload:table"+i,1,1);
            sourcetodeststagecopyProcess = Dao2TableUtil.buildJPAProcess(34,"sourcetodeststagecopy for "+processName+":table"+i,"sourcetodeststagecopy:table"+i,1,1);
            desttableloadProcess = Dao2TableUtil.buildJPAProcess(35,"desttableload for "+processName+":table"+i,"desttableload:table"+i,1,1);
            registerpartitionProcess = Dao2TableUtil.buildJPAProcess(36,"registerpartition for "+processName+":table"+i,"registerpartition:table"+i,1,1);
            childProcesses.add(preprocessingProcess);
            childProcesses.add(sourcestageloadProcess);
            childProcesses.add(sourcetodeststagecopyProcess);
            childProcesses.add(desttableloadProcess);
            childProcesses.add(registerpartitionProcess);
        }
        LOGGER.info("childprocess size"+childProcesses.size());
        List<com.wipro.ats.bdre.md.dao.jpa.Process> processList = processDAO.createHiveMigrationJob(parentProcess,childProcesses);
        LOGGER.info("after method size"+processList.size());
        List<com.wipro.ats.bdre.md.beans.table.Process> tableProcessList = Dao2TableUtil.jpaList2TableProcessList(processList);
        Integer counter = tableProcessList.size();
        LOGGER.info(counter+"counter");
        for (com.wipro.ats.bdre.md.beans.table.Process process:tableProcessList) {
            process.setCounter(counter);
            process.setTableAddTS(DateConverter.dateToString(process.getAddTS()));
            process.setTableEditTS(DateConverter.dateToString(process.getEditTS()));
        }
        restWrapper = new RestWrapper(tableProcessList, RestWrapper.OK);
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
