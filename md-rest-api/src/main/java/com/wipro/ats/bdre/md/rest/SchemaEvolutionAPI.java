package com.wipro.ats.bdre.md.rest;

import com.wipro.ats.bdre.md.api.GetProperties;
import com.wipro.ats.bdre.md.dao.ProcessDAO;
import com.wipro.ats.bdre.md.dao.PropertiesDAO;
import com.wipro.ats.bdre.md.dao.jpa.Process;
import com.wipro.ats.bdre.md.dao.jpa.Properties;
import com.wipro.ats.bdre.md.dao.jpa.PropertiesId;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.*;
import java.util.*;

/**
 * Created by Shubham on 03/13/18.
 */

@Controller
@RequestMapping("/schema")

public class SchemaEvolutionAPI {
    private final Logger LOGGER=Logger.getLogger(SchemaEvolutionAPI.class);
    @Autowired
    private PropertiesDAO propertiesDAO;
    @Autowired
    private ProcessDAO processDAO;
    private static String driverName = "com.mysql.jdbc.Driver";
    private static List<String> columnsList;
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public RestWrapper getSchemaDetails(@PathVariable("id") String processId ){
        RestWrapper restWrapper=null;
        try {
            Integer subProcessId=Integer.parseInt(processId)+1;
            String subPid=""+subProcessId;
            LOGGER.info("sub process id is : "+ subPid);
            LOGGER.info("inside getSchemaDetails");
            String dbName = null, tableName = null, columns = null, username = null, password = null;
            List<String> schemaList = new ArrayList<>();
            GetProperties getProperties = new GetProperties();
            java.util.Properties importProperties = getProperties.getProperties(subPid, "imp-common");
            Enumeration e = importProperties.propertyNames();
            List<String> importPropertyList= Collections.list(e);
            if(!importProperties.isEmpty()) {
                for (String key : importPropertyList) {
                    //String key = (String) e.nextElement();
                    if (key.equalsIgnoreCase("db")) {
                        dbName = importProperties.getProperty(key);
                        LOGGER.info("dbName is " + dbName);
                    }
                    if (key.equalsIgnoreCase("table")) {
                        tableName = importProperties.getProperty(key);
                        LOGGER.info("tableName is " + tableName);
                    }
                    if (key.equalsIgnoreCase("columns")) {
                        columns = importProperties.getProperty(key);
                        LOGGER.info("columns are " + columns);
                    }
                    if (key.equalsIgnoreCase("password")) {
                        password = importProperties.getProperty(key);
                        LOGGER.info("password is " + password);
                    }
                    if (key.equalsIgnoreCase("username")) {
                        username = importProperties.getProperty(key);
                        LOGGER.info("username is " + username);
                    }
                }
            }
            else{
                LOGGER.warn("import properties empty");
            }
            String[] columnNames=columns.split(",");
            columnsList= Arrays.asList(columnNames);
            Class.forName(driverName);
            Connection con = DriverManager.getConnection(
                    dbName, username, password);
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select * from " + tableName + "  limit 1");
            ResultSetMetaData metaData = rs.getMetaData();
            Map<String,String> sourceTableColumnsAndDataTypes=new LinkedHashMap<>();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                String columnName = metaData.getColumnLabel(i);
                String datatype = metaData.getColumnTypeName(i);
                sourceTableColumnsAndDataTypes.put(columnName,datatype);
            }
            for(String c : columnsList){
                if(sourceTableColumnsAndDataTypes.containsKey(c)){
                    LOGGER.info(c + " is already present in import");
                    schemaList.add(c+":" + sourceTableColumnsAndDataTypes.get(c) + ":" + getHiveDataType(sourceTableColumnsAndDataTypes.get(c)) + ".1");
                }
                else{
                    LOGGER.info(c + " is deleted from source table");
                    schemaList.add(c+":NA:NA.3");
                }
            }
            for(String columnName : sourceTableColumnsAndDataTypes.keySet()){
                if(!columnsList.contains(columnName)){
                    schemaList.add(columnName+":" + sourceTableColumnsAndDataTypes.get(columnName) + ":" + getHiveDataType(sourceTableColumnsAndDataTypes.get(columnName)) + ".2");
                    LOGGER.info(columnName + " is currently not present in import");
                }
            }
            restWrapper=new RestWrapper(schemaList,RestWrapper.OK);
        }

        catch (Exception e){
            restWrapper = new RestWrapper(null,RestWrapper.ERROR);
            e.printStackTrace();
        }
        return restWrapper;
    }
    @RequestMapping(value = "/{finalColumns}/{id}", method = RequestMethod.POST)
    @ResponseBody
    public RestWrapper saveSchemaDetails(@PathVariable("finalColumns") String finalColumns,@PathVariable("id") String processId ) {
        RestWrapper restWrapper = null;
        try {
            LOGGER.info("inside saveSchemaDetails");
            Map<String,String> finalColumnAndDataTypes=new LinkedHashMap<>();
            Integer subProcessId=Integer.parseInt(processId)+1;
            String subPid=""+subProcessId;
            String importColumns="";
            for(String c : finalColumns.split(",")){
                importColumns+=c.split(":")[0] + ",";
                LOGGER.info("column selected for import is : " + c.split(":")[0]);
                finalColumnAndDataTypes.put(c.split(":")[0],c.split(":")[1]);
            }
            importColumns=importColumns.substring(0,importColumns.length()-1);
            PropertiesId importPropertiesId=new PropertiesId();
            importPropertiesId.setProcessId(Integer.parseInt(subPid));
            importPropertiesId.setPropKey("columns");
            Properties importProperties=new Properties();
            importProperties.setProcess(processDAO.get(Integer.parseInt(subPid)));
            importProperties.setPropValue(importColumns);
            importProperties.setId(importPropertiesId);
            importProperties.setDescription("properties for data import");
            importProperties.setConfigGroup("imp-common");
            propertiesDAO.update(importProperties);


            List<Process> downStreamProcessList=processDAO.getDownStreamProcess(Integer.parseInt(processId));
            LOGGER.info("number of downstream processes are " + downStreamProcessList.size());
            for(Process p : downStreamProcessList){
                LOGGER.info("down stream process is " + p.getProcessId());
                String des=p.getDescription();
                Integer pid=p.getProcessId();
                if(des.contains("File2Raw")){
                    LOGGER.info("process id of raw load is " + p.getProcessId());
                    updateRawProperties(finalColumnAndDataTypes,p.getProcessId());
                    Integer basePid=pid+2;
                    updateBaseProperties(finalColumnAndDataTypes,basePid);
                }
                else if(des.contains("Raw2Stage")){
                    LOGGER.info("process id of stage load is " + p.getProcessId());
                    updateStageProperties(finalColumnAndDataTypes,p.getProcessId());
                }
            }

            restWrapper=new RestWrapper("properties updated successfully",RestWrapper.OK);
        } catch (Exception e) {
            restWrapper=new RestWrapper(null,RestWrapper.ERROR);
            e.printStackTrace();
        }
        return restWrapper;
    }
    private String getHiveDataType(String dtype){
        Map<String,String> mysql_To_Hive_Data_Type_Map=new LinkedHashMap<>();
        mysql_To_Hive_Data_Type_Map.put("VARCHAR","String");
        mysql_To_Hive_Data_Type_Map.put("INT","Int");
        mysql_To_Hive_Data_Type_Map.put("BIGINT","BigInt");
        mysql_To_Hive_Data_Type_Map.put("TIMESTAMP","Timestamp");
        mysql_To_Hive_Data_Type_Map.put("BIT","Boolean");
        mysql_To_Hive_Data_Type_Map.put("SMALLINT","SmallInt");
        mysql_To_Hive_Data_Type_Map.put("INTEGER","Int");
        mysql_To_Hive_Data_Type_Map.put("TINYINT","tinyInt");
        mysql_To_Hive_Data_Type_Map.put("VARCHAR2","String");
        mysql_To_Hive_Data_Type_Map.put("NUMBER","Int");
        return mysql_To_Hive_Data_Type_Map.get(dtype);
    }

    private void updateRawProperties(Map<String,String> propertiesMap,Integer processId){
        try {
            LOGGER.info("inside update raw properties");
            GetProperties getProperties=new GetProperties();
            java.util.Properties rawDataTypes=getProperties.getProperties(processId.toString(),"raw-data-types");
            java.util.Properties rawColumns=getProperties.getProperties(processId.toString(),"raw-cols");
            Enumeration e1=rawDataTypes.propertyNames();
            Enumeration e2=rawColumns.propertyNames();
            List<String> rawDataTypesList=Collections.list(e1);
            List<String> rawColumnsList=Collections.list(e2);
            for(int i=1;i<=rawColumnsList.size();i++){
                PropertiesId id1=new PropertiesId();
                id1.setProcessId(processId);
                id1.setPropKey("raw_column_datatype." + i);
                PropertiesId id2=new PropertiesId();
                id2.setProcessId(processId);
                id2.setPropKey("raw_column_name." + i);
                propertiesDAO.delete(id1);
                propertiesDAO.delete(id2);
            }
            int i=1;
            for(String column : propertiesMap.keySet()){
                Properties columnProperty=new Properties();
                PropertiesId id1=new PropertiesId();
                id1.setProcessId(processId);
                id1.setPropKey("raw_column_name." + i);
                columnProperty.setConfigGroup("raw-cols");
                columnProperty.setDescription("");
                columnProperty.setId(id1);
                columnProperty.setPropValue(column);
                columnProperty.setProcess(processDAO.get(processId));
                propertiesDAO.insert(columnProperty);
                LOGGER.info("adding column " + column + " to raw properties ");
                Properties dataTypeProperty=new Properties();
                PropertiesId id2=new PropertiesId();
                id2.setProcessId(processId);
                id2.setPropKey("raw_column_datatype." + i);
                dataTypeProperty.setConfigGroup("raw-data-types");
                dataTypeProperty.setDescription("");
                dataTypeProperty.setId(id2);
                dataTypeProperty.setPropValue(propertiesMap.get(column));
                dataTypeProperty.setProcess(processDAO.get(processId));
                propertiesDAO.insert(dataTypeProperty);
                LOGGER.info("adding datatype " + propertiesMap.get(column) + " to raw properties ");
                i++;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void updateStageProperties(Map<String,String> propertiesMap, Integer processId){
        try {
            LOGGER.info("inside update stage properties");
            GetProperties getProperties=new GetProperties();
            java.util.Properties stageDataTypes=getProperties.getProperties(processId.toString(),"base-data-types");
            java.util.Properties stageColumns=getProperties.getProperties(processId.toString(),"base-columns");
            Enumeration e1=stageDataTypes.propertyNames();
            Enumeration e2=stageColumns.propertyNames();
            List<String> stageDataTypesList=Collections.list(e1);
            List<String> stageColumnsList=Collections.list(e2);
            Map<String,String> existingColumns=new LinkedHashMap<>();
            for(String column : stageColumnsList){
                String columnName=column.split("\\.")[0].replaceAll("transform_","");
                LOGGER.info("deleting " + columnName);
                PropertiesId id1=new PropertiesId();
                id1.setPropKey(columnName);
                id1.setProcessId(processId);
                propertiesDAO.delete(id1);

                PropertiesId id2=new PropertiesId();
                id2.setProcessId(processId);
                id2.setPropKey(column);
                if(propertiesMap.containsKey(columnName)){
                    existingColumns.put(columnName,stageColumns.getProperty(column));
                }
                propertiesDAO.delete(id2);

            }
            int i=1;
            for(String column : propertiesMap.keySet()){
                LOGGER.info("adding " + column);
                PropertiesId id1=new PropertiesId();
                id1.setPropKey(column);
                id1.setProcessId(processId);
                Properties dataTypeProperty=new Properties();
                dataTypeProperty.setConfigGroup("base-data-types");
                dataTypeProperty.setDescription("");
                dataTypeProperty.setId(id1);
                dataTypeProperty.setPropValue(propertiesMap.get(column));
                dataTypeProperty.setProcess(processDAO.get(processId));
                propertiesDAO.insert(dataTypeProperty);

                PropertiesId id2=new PropertiesId();
                id2.setPropKey("transform_" + column + "." + i);
                id2.setProcessId(processId);
                Properties columnProperty=new Properties();
                columnProperty.setConfigGroup("base-columns");
                columnProperty.setDescription("");
                columnProperty.setId(id2);
                if(existingColumns.containsKey(column)){
                    columnProperty.setPropValue(existingColumns.get(column));
                }
                else {
                    columnProperty.setPropValue(column);
                }
                columnProperty.setProcess(processDAO.get(processId));
                propertiesDAO.insert(columnProperty);
                i++;
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void updateBaseProperties(Map<String,String> propertiesMap,Integer processId){
        try {
            LOGGER.info("inside update base properties");
            GetProperties getProperties=new GetProperties();
            java.util.Properties baseColumnAndDataTypes=getProperties.getProperties(processId.toString(),"base-columns-and-types");
            Enumeration e=baseColumnAndDataTypes.propertyNames();
            List<String> baseColumnList=Collections.list(e);
            for(String column : baseColumnList){
                if(!propertiesMap.containsKey(column)){
                    LOGGER.info(column + " is to be deleted from base table");
                    PropertiesId id=new PropertiesId();
                    id.setProcessId(processId);
                    id.setPropKey(column);
                    Properties deleteProperty=new Properties();
                    deleteProperty.setProcess(processDAO.get(processId));
                    deleteProperty.setPropValue(baseColumnAndDataTypes.getProperty(column));
                    deleteProperty.setId(id);
                    deleteProperty.setDescription("");
                    deleteProperty.setConfigGroup("deleted-columns");
                    propertiesDAO.update(deleteProperty);
                }
            }
            for(String column : propertiesMap.keySet()){
                if(!baseColumnList.contains(column)){
                    LOGGER.info(column + " is to be added to base table");
                    PropertiesId id=new PropertiesId();
                    id.setProcessId(processId);
                    id.setPropKey(column);
                    Properties appendProperty=new Properties();
                    appendProperty.setProcess(processDAO.get(processId));
                    appendProperty.setPropValue(propertiesMap.get(column));
                    appendProperty.setId(id);
                    appendProperty.setDescription("");
                    appendProperty.setConfigGroup("appended-columns");
                    propertiesDAO.insert(appendProperty);
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
