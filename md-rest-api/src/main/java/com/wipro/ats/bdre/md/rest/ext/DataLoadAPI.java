package com.wipro.ats.bdre.md.rest.ext;

import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.beans.table.Batch;
import com.wipro.ats.bdre.md.beans.table.GeneralConfig;
import com.wipro.ats.bdre.md.dao.ProcessDAO;
import com.wipro.ats.bdre.md.dao.PropertiesDAO;
import com.wipro.ats.bdre.md.dao.jpa.Process;
import com.wipro.ats.bdre.md.dao.jpa.Properties;
import com.wipro.ats.bdre.md.rest.RestWrapper;
import com.wipro.ats.bdre.md.rest.util.Dao2TableUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cloudera on 1/8/16.
 */

@Controller
@RequestMapping("/dataload")
public class DataLoadAPI extends MetadataAPIBase {
    private static final Logger LOGGER = Logger.getLogger(DataLoadAPI.class);
    @Autowired
    private ProcessDAO processDAO;
    @Autowired
    private PropertiesDAO propertiesDAO;


    @RequestMapping(value = {"/", ""}, method = RequestMethod.PUT)
    public
    @ResponseBody
    RestWrapper insert(Principal principal) {
        LOGGER.debug("Updating jtable for new advanced config");


        RestWrapper restWrapper = null;
        try {

            restWrapper = new RestWrapper(null, RestWrapper.OK);
            LOGGER.info(" Jtable created by User:" + principal.getName() );
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }


    @RequestMapping(value = {"/", ""}, method = RequestMethod.GET)
    public
    @ResponseBody
    RestWrapper list(Principal principal) {
        LOGGER.debug("Updating jtable for new advanced config");


        RestWrapper restWrapper = null;


        try {
            restWrapper = new RestWrapper(null, RestWrapper.OK);
            LOGGER.info("All records listed from Batch by User:" + principal.getName());
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    @RequestMapping(value = {"/createjobs"}, method = RequestMethod.POST)

    public
    @ResponseBody
    RestWrapper createJob(@RequestParam Map<String, String> map, Principal principal) {
        LOGGER.debug(" value of map is " + map.size());
        RestWrapper restWrapper = null;

        String processName = null;
        String processDescription = null;
        Integer busDomainId = null;

        List<com.wipro.ats.bdre.md.dao.jpa.Process> childProcesses=new ArrayList<com.wipro.ats.bdre.md.dao.jpa.Process>();
        List<com.wipro.ats.bdre.md.dao.jpa.Properties> file2RawProperties=new ArrayList<Properties>();
        List<com.wipro.ats.bdre.md.dao.jpa.Properties> raw2StageProperties=new ArrayList<Properties>();
        List<com.wipro.ats.bdre.md.dao.jpa.Properties> stage2BaseProperties=new ArrayList<Properties>();
        Map<Process,List<Properties>> processPropertiesMap = new HashMap<Process, List<Properties>>();
        int rawColumnCounter = 1;

        com.wipro.ats.bdre.md.dao.jpa.Properties jpaProperties=null;
        for (String string : map.keySet()) {
            LOGGER.debug("String is" + string);
            if (map.get(string) == null || ("").equals(map.get(string))) {
                continue;
            }
            if (string.startsWith("rawtablecolumn_")) {
                jpaProperties = Dao2TableUtil.buildJPAProperties("raw-columns", "raw_column_name." + rawColumnCounter, string.replaceAll("rawtablecolumn_", ""), "Column name for raw table");
                file2RawProperties.add(jpaProperties);
                jpaProperties = Dao2TableUtil.buildJPAProperties("raw-data-types", "raw_column_datatype." + rawColumnCounter, map.get(string), "Data Type for raw table");
                rawColumnCounter++;
                file2RawProperties.add(jpaProperties);
            }else if (string.startsWith("fileformat_")) {
                if("fileformat".equals(string.replaceAll("fileformat_", ""))){
                    continue;
                }else if("rawDBName".equals(string.replaceAll("fileformat_", ""))){
                    if("".equals(map.get(string))){
                        jpaProperties = Dao2TableUtil.buildJPAProperties("raw-table", "table_db", "raw", "RAW DB Name");
                    }else{
                        jpaProperties = Dao2TableUtil.buildJPAProperties("raw-table", "table_db", map.get(string), "RAW DB Name");
                    }
                    file2RawProperties.add(jpaProperties);
                }
            }
            else if (string.startsWith("fileformatdetails_")) {
                if ("inputFormat".equals(string.replaceAll("fileformatdetails_", ""))) {
                    jpaProperties = Dao2TableUtil.buildJPAProperties("raw-table", "input.format", map.get(string), "Input Format");
                    file2RawProperties.add(jpaProperties);
                } else  if ("outputFormat".equals(string.replaceAll("fileformatdetails_", ""))) {
                    jpaProperties = Dao2TableUtil.buildJPAProperties("raw-table", "output.format", map.get(string), "Output Format");
                    file2RawProperties.add(jpaProperties);
                }  else if ("serdeClass".equals(string.replaceAll("fileformatdetails_", ""))) {
                    jpaProperties = Dao2TableUtil.buildJPAProperties("raw-table", "serde.class", map.get(string), "Serde Class to be used");
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
            }else if (string.startsWith("basetable_")) {
                if("baseTableName".equals(string.replaceAll("basetable_",""))){
                    jpaProperties = Dao2TableUtil.buildJPAProperties("base-table", "table_name" , map.get(string) , "Base Table Name");
                    raw2StageProperties.add(jpaProperties);
                    jpaProperties = Dao2TableUtil.buildJPAProperties("raw-table", "table_name" , "raw"+map.get(string) , "RAW Table Name");
                    file2RawProperties.add(jpaProperties);
                    jpaProperties = Dao2TableUtil.buildJPAProperties("base-table", "table_name" , map.get(string) , "Base Table Name");
                    stage2BaseProperties.add(jpaProperties);
                }else if("baseDBName".equals(string.replaceAll("basetable_",""))){
                    jpaProperties = Dao2TableUtil.buildJPAProperties("base-table", "table_db" , map.get(string) , "Base Table Name");
                    stage2BaseProperties.add(jpaProperties);
                    raw2StageProperties.add(jpaProperties);
                }


            }else if (string.startsWith("transform_")) {
                jpaProperties = Dao2TableUtil.buildJPAProperties("base-columns", string.replaceAll("transform_","") , map.get(string) , "Transformation on column");
                raw2StageProperties.add(jpaProperties);
            }else if (string.startsWith("stagedatatype_")) {
                jpaProperties = Dao2TableUtil.buildJPAProperties("base-data-types", string.replaceAll("stagedatatype_","") , map.get(string) , "data type of column");
                raw2StageProperties.add(jpaProperties);
            }else if (string.startsWith("baseaction_")) {
                jpaProperties = Dao2TableUtil.buildJPAProperties("base-columns-and-types", string.replaceAll("baseaction_","") , map.get(string) , "Transformation on column");
                raw2StageProperties.add(jpaProperties);
            }
            else if (string.startsWith("process_processName")) {
                LOGGER.debug("process_processName" + map.get(string));
                processName = map.get(string);
            }else if (string.startsWith("process_processDescription")) {
                LOGGER.debug("process_processDescription" + map.get(string));
                processDescription = map.get(string);
            }else if (string.startsWith("process_busDomainId")) {
                LOGGER.debug("process_busDomainId" + map.get(string));
                busDomainId = new Integer(map.get(string));
            }
        }

        jpaProperties = Dao2TableUtil.buildJPAProperties("raw-table", "raw.table.db", "raw", "Input Format");
        file2RawProperties.add(jpaProperties);

        com.wipro.ats.bdre.md.dao.jpa.Process parentProcess = Dao2TableUtil.buildJPAProcess(5, processName, processDescription, 1,busDomainId);
        com.wipro.ats.bdre.md.dao.jpa.Process file2Raw = Dao2TableUtil.buildJPAProcess(6, "File2Raw of "+processName , processDescription, 0,busDomainId);
        com.wipro.ats.bdre.md.dao.jpa.Process raw2Stage = Dao2TableUtil.buildJPAProcess(7, "Raw2Stage of "+processName , processDescription, 0,busDomainId);
        com.wipro.ats.bdre.md.dao.jpa.Process stage2Base = Dao2TableUtil.buildJPAProcess(8, "stage2Base of "+processName , processDescription, 0,busDomainId);
        childProcesses.add(file2Raw);
        childProcesses.add(raw2Stage);
        childProcesses.add(stage2Base);
        processPropertiesMap.put(file2Raw,file2RawProperties);
        processPropertiesMap.put(raw2Stage,raw2StageProperties);
        processPropertiesMap.put(stage2Base,stage2BaseProperties);
        processDAO.createDataloadJob(parentProcess,childProcesses,null,processPropertiesMap);





        return restWrapper;
    }






    @Override
    public Object execute(String[] params) {
        return null;
    }

}
