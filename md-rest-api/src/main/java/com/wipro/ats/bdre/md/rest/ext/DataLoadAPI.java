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

package com.wipro.ats.bdre.md.rest.ext;

import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.dao.ProcessDAO;
import com.wipro.ats.bdre.md.dao.UserRolesDAO;
import com.wipro.ats.bdre.md.dao.jpa.Process;
import com.wipro.ats.bdre.md.dao.jpa.Properties;
import com.wipro.ats.bdre.md.dao.jpa.Users;
import com.wipro.ats.bdre.md.rest.RestWrapper;
import com.wipro.ats.bdre.md.rest.util.Dao2TableUtil;
import com.wipro.ats.bdre.md.rest.util.DateConverter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.*;

/**
 * Created by cloudera on 1/8/16.
 */

@Controller
@RequestMapping("/dataload")
public class DataLoadAPI extends MetadataAPIBase {
    private static final Logger LOGGER = Logger.getLogger(DataLoadAPI.class);
    private static final String FILEFORMAT = "fileformat_";
    private static final String RAWTABLE = "raw-table";
    private static final String TABLEDB = "table_db";
    private static final String FILEFORMATDETAILS = "fileformatdetails_";
    private static final String BASETABLEPREFIX = "basetable_";
    private static final String TABLENAME = "table_name";
    private static final String BASETABLE = "base-table";
    private static final String BASETABLENAME = "Base Table Name";
    private static final String TRANSFORM = "transform_";
    private static final String PARTITION = "partition_";
    private static final String TRANSFORMCOMMENT = "Transformation on column";
    private static final String STAGEDATATYPE = "stagedatatype_";
    private static final String BASEACTION = "baseaction_";
    private static final String BUSDOMAIN = "process_busDomainId";
    @Autowired
    private ProcessDAO processDAO;
    @Autowired
    UserRolesDAO userRolesDAO;

    @RequestMapping(value = {"/", ""}, method = RequestMethod.PUT)
    @ResponseBody public
    RestWrapper insert(Principal principal) {
        LOGGER.debug("Updating jtable for new advanced config");


        RestWrapper restWrapper = null;
        try {

            restWrapper = new RestWrapper(null, RestWrapper.OK);
            LOGGER.info(" Jtable created by User:" + principal.getName() );
        } catch (Exception e) {
            LOGGER.error("error occured : " + e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }


    @RequestMapping(value = {"/", ""}, method = RequestMethod.GET)
    @ResponseBody public
    RestWrapper list(Principal principal) {
        LOGGER.debug("Updating jtable for new advanced config");


        RestWrapper restWrapper = null;


        try {
            restWrapper = new RestWrapper(null, RestWrapper.OK);
            LOGGER.info("All records listed from Batch by User:" + principal.getName());
        } catch (Exception e) {
            LOGGER.error("error occured : " + e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    @RequestMapping(value = {"/createjobs"}, method = RequestMethod.POST)

    @ResponseBody public
    RestWrapper createJob( HttpServletRequest request, Principal principal) {
        // Read from request
        String query="";
        String tmp1="";
        StringBuilder buffer = null;
        try {
             buffer = new StringBuilder();
            BufferedReader reader = request.getReader();
            while ((tmp1 = reader.readLine()) != null) {
                buffer.append(tmp1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            query = java.net.URLDecoder.decode(new String(buffer), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String[] linkedList=query.split("&");
        LinkedHashMap<String, String> map=new LinkedHashMap<>();
        for (int i=0;i<linkedList.length;i++)
        {
            String[] tmp=linkedList[i].split("=");
            if (tmp.length==2)
                map.put(tmp[0],tmp[1]);
            else
                map.put(tmp[0],"");
        }
        LOGGER.debug(" value of map is " + map.size());
        RestWrapper restWrapper = null;

        String processName = null;
        String processDescription = null;
        Integer busDomainId = null;
        Integer workflowTypeId=null;
        Integer enqId = null;
        String filePath = null;
        Map<String,String> partitionCols = new TreeMap<String, String>();
        Map<String,String> partitionDataTypes = new TreeMap<String, String>();

        List<com.wipro.ats.bdre.md.dao.jpa.Process> childProcesses=new ArrayList<com.wipro.ats.bdre.md.dao.jpa.Process>();
        List<com.wipro.ats.bdre.md.dao.jpa.Properties> file2RawProperties=new ArrayList<Properties>();
        List<com.wipro.ats.bdre.md.dao.jpa.Properties> raw2StageProperties=new ArrayList<Properties>();
        List<com.wipro.ats.bdre.md.dao.jpa.Properties> stage2BaseProperties=new ArrayList<Properties>();
        Map<Process,List<Properties>> processPropertiesMap = new HashMap<Process, List<Properties>>();
        int rawColumnCounter = 1;

        com.wipro.ats.bdre.md.dao.jpa.Properties jpaProperties=null;
        for (String string : map.keySet()) {
            LOGGER.info("String is" + string);
            if (map.get(string) == null || ("").equals(map.get(string))) {
                continue;
            }
            if (string.startsWith("rawtablecolumn_")) {
                jpaProperties = Dao2TableUtil.buildJPAProperties("raw-cols", "raw_column_name." + rawColumnCounter, string.replaceAll("rawtablecolumn_", ""), "Column name for raw table");
                file2RawProperties.add(jpaProperties);
                jpaProperties = Dao2TableUtil.buildJPAProperties("raw-data-types", "raw_column_datatype." + rawColumnCounter, map.get(string), "Data Type for raw table");
                rawColumnCounter++;
                file2RawProperties.add(jpaProperties);
            }else if (string.startsWith(FILEFORMAT)) {
                if("fileformat".equals(string.replaceAll(FILEFORMAT, ""))){
                    jpaProperties = Dao2TableUtil.buildJPAProperties(RAWTABLE, "file_type", map.get(string), "file type");
                    file2RawProperties.add(jpaProperties);
                }else if("rawDBName".equals(string.replaceAll(FILEFORMAT, ""))){
                    jpaProperties = Dao2TableUtil.buildJPAProperties(RAWTABLE, "table_db", map.get(string), "RAW DB Name");
                    file2RawProperties.add(jpaProperties);
                    jpaProperties = Dao2TableUtil.buildJPAProperties(RAWTABLE, "table_db_raw", map.get(string), "RAW DB Name");
                    raw2StageProperties.add(jpaProperties);
                }
            }
            else if (string.startsWith(FILEFORMATDETAILS)) {
                if ("inputFormat".equals(string.replaceAll(FILEFORMATDETAILS, ""))) {
                    jpaProperties = Dao2TableUtil.buildJPAProperties(RAWTABLE, "input.format", map.get(string), "Input Format");
                    file2RawProperties.add(jpaProperties);
                } else  if ("outputFormat".equals(string.replaceAll(FILEFORMATDETAILS, ""))) {
                    jpaProperties = Dao2TableUtil.buildJPAProperties(RAWTABLE, "output.format", map.get(string), "Output Format");
                    file2RawProperties.add(jpaProperties);
                }  else if ("serdeClass".equals(string.replaceAll(FILEFORMATDETAILS, ""))) {
                    jpaProperties = Dao2TableUtil.buildJPAProperties(RAWTABLE, "serde.class", map.get(string), "Serde Class to be used");
                    file2RawProperties.add(jpaProperties);
                }
            } else if (string.startsWith("serdeproperties_")) {
                if(string.replaceAll("serdeproperties_", "").contains("serdePropKey")) {
                    jpaProperties = Dao2TableUtil.buildJPAProperties("raw-serde-props", map.get(string) , map.get(string.replaceAll("Key","Value")) , "Serde Properties");
                    file2RawProperties.add(jpaProperties);
                }else{
                    continue;
                }
            }else if (string.startsWith("tableproperties_")) {
                if(string.replaceAll("tableproperties_", "").contains("tablePropKey")) {
                    jpaProperties = Dao2TableUtil.buildJPAProperties("raw-table-props", map.get(string) , map.get(string.replaceAll("Key","Value")) , "Table Properties");
                    file2RawProperties.add(jpaProperties);
                }else{
                    continue;
                }
            }else if (string.startsWith(BASETABLEPREFIX)) {
                if("baseTableName".equals(string.replaceAll(BASETABLEPREFIX,""))){
                    jpaProperties = Dao2TableUtil.buildJPAProperties(BASETABLE, TABLENAME , map.get(string) , BASETABLENAME);
                    raw2StageProperties.add(jpaProperties);
                    jpaProperties = Dao2TableUtil.buildJPAProperties(RAWTABLE, TABLENAME , "raw_"+map.get(string) , "RAW Table Name");
                    file2RawProperties.add(jpaProperties);
                    jpaProperties = Dao2TableUtil.buildJPAProperties(RAWTABLE, "table_name_raw" , "raw_"+map.get(string) , "RAW Table Name");
                    raw2StageProperties.add(jpaProperties);
                    jpaProperties = Dao2TableUtil.buildJPAProperties(BASETABLE, TABLENAME , map.get(string) , BASETABLENAME);
                    stage2BaseProperties.add(jpaProperties);
                }else if("baseDBName".equals(string.replaceAll(BASETABLEPREFIX,""))){
                    jpaProperties = Dao2TableUtil.buildJPAProperties(BASETABLE, TABLEDB, map.get(string) , BASETABLENAME);
                    stage2BaseProperties.add(jpaProperties);
                    jpaProperties = Dao2TableUtil.buildJPAProperties(BASETABLE, TABLEDB, map.get(string) , BASETABLENAME);
                    raw2StageProperties.add(jpaProperties);
                }
            }else if (string.startsWith(TRANSFORM)) {
                String column_name=string.replaceAll(TRANSFORM,"");
                if(map.get(string).equals("no transformation")||map.get(string).equals("no+transformation"))
                    map.put(string,column_name);
                else
                 map.put(string,map.get(string)+"("+column_name+")");
                LOGGER.info("key is "+string +" updated value is "+map.get(string)+" column name is "+column_name);
                if("".equals(map.get(string.replaceAll(TRANSFORM,PARTITION))) || map.get(string.replaceAll(TRANSFORM,PARTITION)) == null) {
                    jpaProperties = Dao2TableUtil.buildJPAProperties("base-columns", string, map.get(string), TRANSFORMCOMMENT);
                    raw2StageProperties.add(jpaProperties);
                }else{
                    partitionCols.put(map.get(string.replaceAll(TRANSFORM,PARTITION)),string.replaceAll(TRANSFORM,""));
                }
            }else if (string.startsWith(STAGEDATATYPE)) {
                if("".equals(map.get(string.replaceAll(STAGEDATATYPE,PARTITION))) || map.get(string.replaceAll(STAGEDATATYPE,PARTITION)) == null) {
                    jpaProperties = Dao2TableUtil.buildJPAProperties("base-data-types", string.replaceAll(STAGEDATATYPE,"") , map.get(string) , "data type of column");
                    raw2StageProperties.add(jpaProperties);
                }else{
                    partitionDataTypes.put(map.get(string.replaceAll(STAGEDATATYPE,PARTITION)),map.get(string));
                }

            }else if (string.startsWith(BASEACTION)) {
                if("".equals(map.get(string.replaceAll(BASEACTION,PARTITION))) || map.get(string.replaceAll(TRANSFORM,PARTITION)) == null) {
                    jpaProperties = Dao2TableUtil.buildJPAProperties("base-columns-and-types", string.replaceAll(BASEACTION, ""), map.get(string), TRANSFORMCOMMENT);
                    stage2BaseProperties.add(jpaProperties);
                }
            }
            else if (string.startsWith("process_processName")) {
                LOGGER.info("process_processName" + map.get(string));
                processName = map.get(string);
            }else if (string.startsWith("process_processDescription")) {
                LOGGER.info("process_processDescription" + map.get(string));
                processDescription = map.get(string);
            }else if (string.startsWith(BUSDOMAIN)) {
                LOGGER.info(BUSDOMAIN + map.get(string));
                busDomainId = new Integer(map.get(string));
            }else if (string.startsWith("process_enqueueId")) {
                LOGGER.info("EnqueueId " + map.get(string));
                if(! map.get(string).equals("null")) {
                    enqId = new Integer(map.get(string));
                    LOGGER.info("enqId is "+ enqId);
                }
                else {
                    enqId = 0;
                    LOGGER.info("enqId is "+ enqId);
                }
            }
            else if (string.startsWith("process_filePath")) {
                LOGGER.info("filepath" + map.get(string));
                filePath = map.get(string);

                    jpaProperties = Dao2TableUtil.buildJPAProperties("raw-table", "filepath",filePath, "File Path");
                    file2RawProperties.add(jpaProperties);

            }
            else if (string.startsWith("process_workflowTypeId")) {
                LOGGER.info("process_workflowTypeId" + map.get(string));
                workflowTypeId = new Integer(map.get(string));
            }
        }



        StringBuilder partitionColListBuilder = new StringBuilder();
        for (String order : partitionCols.keySet()){
            partitionColListBuilder.append(partitionCols.get(order));
            partitionColListBuilder.append(" ");
            partitionColListBuilder.append(partitionDataTypes.get(order));
            partitionColListBuilder.append(",");
        }
        if (partitionColListBuilder.length() > 0) {
            String  partitionColList = partitionColListBuilder.toString();
            jpaProperties = Dao2TableUtil.buildJPAProperties("partition", "partition_columns" , partitionColList , TRANSFORMCOMMENT);
            stage2BaseProperties.add(jpaProperties);
            jpaProperties = Dao2TableUtil.buildJPAProperties("partition", "partition_columns" , partitionColList , TRANSFORMCOMMENT);
            raw2StageProperties.add(jpaProperties);
        }

        com.wipro.ats.bdre.md.dao.jpa.Process parentProcess = Dao2TableUtil.buildJPAProcess(5, processName, processDescription, workflowTypeId,busDomainId);
        Users users=new Users();
        users.setUsername(principal.getName());
        parentProcess.setUsers(users);
        parentProcess.setUserRoles(userRolesDAO.minUserRoleId(principal.getName()));
        com.wipro.ats.bdre.md.dao.jpa.Process file2Raw = Dao2TableUtil.buildJPAProcess(6, "File2Raw of "+processName , processDescription, 0,busDomainId);
        com.wipro.ats.bdre.md.dao.jpa.Process raw2Stage = Dao2TableUtil.buildJPAProcess(7, "Raw2Stage of "+processName , processDescription, 0,busDomainId);
        com.wipro.ats.bdre.md.dao.jpa.Process stage2Base = Dao2TableUtil.buildJPAProcess(8, "stage2Base of "+processName , processDescription, 0,busDomainId);
        file2Raw.setEnqueuingProcessId(enqId);
        raw2Stage.setEnqueuingProcessId(enqId);
        childProcesses.add(file2Raw);
        childProcesses.add(raw2Stage);
        childProcesses.add(stage2Base);
        processPropertiesMap.put(file2Raw,file2RawProperties);
        processPropertiesMap.put(raw2Stage,raw2StageProperties);
        processPropertiesMap.put(stage2Base,stage2BaseProperties);
        List<com.wipro.ats.bdre.md.dao.jpa.Process> processList = processDAO.createDataloadJob(parentProcess,childProcesses,null,processPropertiesMap);

        List<com.wipro.ats.bdre.md.beans.table.Process> tableProcessList = Dao2TableUtil.jpaList2TableProcessList(processList);
        Integer counter = tableProcessList.size();
        for (com.wipro.ats.bdre.md.beans.table.Process process:tableProcessList) {
            process.setCounter(counter);
            process.setTableAddTS(DateConverter.dateToString(process.getAddTS()));
            process.setTableEditTS(DateConverter.dateToString(process.getEditTS()));
        }
        restWrapper = new RestWrapper(tableProcessList, RestWrapper.OK);
        LOGGER.info("Process and Properties for data load process inserted by" + principal.getName());


        return restWrapper;
    }






    @Override
    public Object execute(String[] params) {
        return null;
    }

}
