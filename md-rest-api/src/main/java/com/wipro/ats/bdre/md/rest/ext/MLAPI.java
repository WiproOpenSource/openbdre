package com.wipro.ats.bdre.md.rest.ext;

import com.wipro.ats.bdre.md.dao.jpa.Properties;
import com.wipro.ats.bdre.md.dao.jpa.Users;
import com.wipro.ats.bdre.md.rest.RestWrapper;
import com.wipro.ats.bdre.md.rest.RestWrapperOptions;
import com.wipro.ats.bdre.md.rest.util.Dao2TableUtil;
import com.wipro.ats.bdre.md.rest.util.DateConverter;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.wipro.ats.bdre.md.dao.ProcessDAO;
import com.wipro.ats.bdre.md.dao.UserRolesDAO;
import com.wipro.ats.bdre.md.dao.jpa.*;
import com.wipro.ats.bdre.md.dao.jpa.Process;
import com.wipro.ats.bdre.md.rest.RestWrapper;
import com.wipro.ats.bdre.md.rest.util.Dao2TableUtil;
import com.wipro.ats.bdre.md.rest.util.DateConverter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.sql.*;
import java.util.*;
import java.io.Serializable;
import java.security.Principal;
import java.util.ArrayList;
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
    @Autowired
    private ProcessDAO processDAO;
    @Autowired
    UserRolesDAO userRolesDAO;
    private static final String EXPORTCONFIG = "ml";
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

    //Fetching data from a Hive table
    @RequestMapping(value = "/data/{srcEnv}/{srcDB}/{pid}", method = {RequestMethod.GET})
    @ResponseBody
    public RestWrapper getTableData(@PathVariable("srcEnv") String srcEnv, @PathVariable("srcDB") String srcDB, @PathVariable("pid") String pid) {
        RestWrapper restWrapper = null;
        LOGGER.info(srcEnv + "srcEnvi");
        LOGGER.info(srcDB + "srcDB");
        try {
            Class.forName(driverName);
            connection = DriverManager.getConnection("jdbc:hive2://" + srcEnv + "/" + srcDB.toLowerCase(), "", "");
            String tableName="ML_"+pid;
            ResultSet rs = connection.createStatement().executeQuery("select * from " + srcDB + "." + tableName);

            ResultSetMetaData metaData = rs.getMetaData();
            List<Map<String, Object>> tables = new ArrayList<Map<String, Object>>();
            while (rs.next()) {
                Map<String,Object> m=new LinkedHashMap<>();
                for(int j=1;j<=metaData.getColumnCount();j++){

                    String colName = metaData.getColumnLabel(j).replaceFirst(tableName.toLowerCase()+".", "");
                    if(!colName.equals("features") && !colName.equals("rawprediction") && !colName.equals("probability")){
                    Object colValue=rs.getObject(j);
                    m.put(colName,colValue);}
                }
                tables.add(m);
            }
            restWrapper = new RestWrapper(tables, RestWrapperOptions.OK);

        } catch (Exception e) {
            LOGGER.error("error occured " + e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
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
                String colName = metaData.getColumnLabel(i).replaceFirst(tableName.toLowerCase()+".", "");
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

    @RequestMapping(value = "/createjobs/", method = RequestMethod.POST)
    @ResponseBody
    public RestWrapper createJob(@RequestParam Map<String, String> map, Principal principal) {
        LOGGER.debug(" value of map is " + map.size());

        com.wipro.ats.bdre.md.dao.jpa.Properties jpaProperties = null;
        List<Properties> propertiesList = new ArrayList<Properties>();
        String processName = null;
        String processDesc = null;
        Integer busDomainID = null;
        RestWrapper restWrapper = null;

        com.wipro.ats.bdre.md.dao.jpa.Process parentProcess = null;
        Process childProcess = null;

        for (String s : map.keySet()) {
            if (map.get(s) == null || ("").equals(map.get(s))) {
                continue;
            }
            LOGGER.info("String is" + s);
            //(s.contains("modelBusDomain")) && !(s.contains("modelDescription")) && !(s.contains("modelName"))
            if (s.contains("modelBusDomain")){
                busDomainID=Integer.parseInt(map.get(s));
            }
            else if(s.contains("modelDescription")) {
                processDesc=map.get(s);
            }
            else if(s.contains("modelName")) {
                processName=map.get(s);
            }
            else {
                jpaProperties = Dao2TableUtil.buildJPAProperties(EXPORTCONFIG, s, map.get(s), "Properties of ML model");
                propertiesList.add(jpaProperties);
            }

        }


        parentProcess = Dao2TableUtil.buildJPAProcess(86,processName, processDesc ,2,busDomainID);
        Users users=new Users();
        users.setUsername(principal.getName());
        parentProcess.setUsers(users);
        parentProcess.setUserRoles(userRolesDAO.minUserRoleId(principal.getName()));
        childProcess = Dao2TableUtil.buildJPAProcess(87, "SubProcess of "+processName, processDesc, 0,busDomainID);
        List<Process> processList = processDAO.createOneChildJob(parentProcess,childProcess,null,propertiesList);

        List<com.wipro.ats.bdre.md.beans.table.Process>tableProcessList=Dao2TableUtil.jpaList2TableProcessList(processList);
        Integer counter=tableProcessList.size();
        for(com.wipro.ats.bdre.md.beans.table.Process process:tableProcessList){
            process.setCounter(counter);
            process.setTableAddTS(DateConverter.dateToString(process.getAddTS()));
            process.setTableEditTS(DateConverter.dateToString(process.getEditTS()));
        }
        restWrapper = new RestWrapper(tableProcessList, RestWrapper.OK);
        LOGGER.info("Process and Properties for data generation process inserted by" + principal.getName());
        return restWrapper;




    }
}

