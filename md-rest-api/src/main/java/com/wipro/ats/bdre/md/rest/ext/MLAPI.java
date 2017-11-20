package com.wipro.ats.bdre.md.rest.ext;

import com.wipro.ats.bdre.md.rest.RestWrapper;
import com.wipro.ats.bdre.md.rest.RestWrapperOptions;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by su324335 on 11/17/17.
 */

class TableColumns implements Serializable{
    private String columnName;
    private String dataType;
    public void setColumnName(String c){
        this.columnName=c;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
}

@Controller
@RequestMapping("/ml")
public class MLAPI {
    private static final Logger LOGGER = Logger.getLogger(MLAPI.class);

    private static Connection connection;
    private static String driverName = "org.apache.hive.jdbc.HiveDriver";

    //Fetching all the databases from hive cluster
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

    //Fetching all the tables from hive databases
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

            List<String> tables = new ArrayList<String>();
            while (rs.next()) {
                String tableName = rs.getString(1);
                tables.add(tableName.toUpperCase());
            }
            List<RestWrapperOptions.Option> options = new ArrayList<RestWrapperOptions.Option>();
            for (String table : tables) {
                RestWrapperOptions.Option option = new RestWrapperOptions.Option(table, table);
                options.add(option);
            }
            restWrapperOptions = new RestWrapperOptions(options, RestWrapperOptions.OK);
        } catch (Exception e) {
            LOGGER.error("error occured " + e);
            restWrapperOptions = new RestWrapperOptions(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapperOptions;
    }


    //Fetching all columns from hive tables
    @RequestMapping(value = "columns/{srcEnv}/{srcDB}/{table}", method = {RequestMethod.GET})
    @ResponseBody
    public RestWrapper getColumsnList(@PathVariable("srcEnv") String srcEnv, @PathVariable("srcDB") String srcDB, @PathVariable("table") String tableName) {
        RestWrapper restWrapper = null;
        LOGGER.info(srcEnv + "srcEnvi");
        LOGGER.info(srcDB + "srcDB");
        LOGGER.info(tableName + "tableName");
        try {
            Class.forName(driverName);
            connection = DriverManager.getConnection("jdbc:hive2://" + srcEnv + "/" + srcDB.toLowerCase(), "", "");
            ResultSet rs = connection.createStatement().executeQuery("select * from " + srcDB + "." + tableName +"  limit 1");
            ResultSetMetaData metaData = rs.getMetaData();
            Map<String, String> databases = new HashMap<String, String>();
            List<Map<String,String>> columnList=new ArrayList<>();

            TableColumns[] tableColumns=new TableColumns[1000];
            for(int i=1; i<=metaData.getColumnCount();i++){
                String colName = metaData.getColumnLabel(i).replaceFirst(tableName+".", "");
                String datatype = metaData.getColumnTypeName(i);
                TableColumns t=new TableColumns();
                Map<String,String> m1=new HashMap<>();

                t.setColumnName(colName);
                t.setDataType(datatype);
                m1.put("columnName", colName);
                m1.put("dataType", datatype);
                columnList.add(m1);

                databases.put(colName,datatype);
                tableColumns[i-1]=t;
            }
            //System.out.println("databases = " + databases);
            restWrapper = new RestWrapper(columnList, RestWrapperOptions.OK);
        } catch (Exception e) {
            LOGGER.error("error occured " + e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }
}

