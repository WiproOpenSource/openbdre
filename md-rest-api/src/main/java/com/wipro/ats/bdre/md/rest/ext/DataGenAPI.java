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
import com.wipro.ats.bdre.md.beans.table.GeneralConfig;
import com.wipro.ats.bdre.md.dao.ProcessDAO;
import com.wipro.ats.bdre.md.dao.UserRolesDAO;
import com.wipro.ats.bdre.md.dao.jpa.Process;
import com.wipro.ats.bdre.md.dao.jpa.Properties;
import com.wipro.ats.bdre.md.dao.jpa.Users;
import com.wipro.ats.bdre.md.rest.RestWrapper;
import com.wipro.ats.bdre.md.rest.util.BindingResultError;
import com.wipro.ats.bdre.md.rest.util.Dao2TableUtil;
import com.wipro.ats.bdre.md.rest.util.DateConverter;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Principal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by AS294216 on 13-10-2015.
 */
@Controller
@RequestMapping("/datagenproperties")
public class DataGenAPI extends MetadataAPIBase {
    private static final Logger LOGGER = Logger.getLogger(DataGenAPI.class);
    private static final String TABLE = "table";
    @Autowired
    private ProcessDAO processDAO;
    @Autowired
    UserRolesDAO userRolesDAO;
    @RequestMapping(value = {"/createjobs"}, method = RequestMethod.POST)


    @ResponseBody public
    RestWrapper createJobs(@RequestParam Map<String, String> map, Principal principal) {
        LOGGER.debug(" value of map is " + map.size());
        RestWrapper restWrapper = null;
        Process parentProcess = null;
        Process childProcess = null;
        Properties jpaProperties=null;


        String processName = null;
        String processDescription = null;
        Integer busDomainId = null;
        Integer workflowTypeId=null;

        StringBuilder tableSchema = new StringBuilder("");
        //to handle argument id's in sequence if rows are deleted and added in UI
        int fieldArgCounter = 1;
        int fieldTypeCounter = 0;
        int fieldCounter = 1;
        String[] dateContent;
        StringBuilder unifiedDate = new StringBuilder("");
        Date date = null, date2 = null;

        List<Properties> childProps=new ArrayList<>();
        Map<String,String> orderedMap = new TreeMap<>(map);
        //inserting in properties table
        for (String string : orderedMap.keySet()) {
            if (map.get(string) == null || ("").equals(map.get(string))) {
                continue;
            }
            Integer splitIndex = string.lastIndexOf("_");
            String key = string.substring(splitIndex + 1, string.length());
            LOGGER.info("key is " + key);

            if (string.startsWith("type_genArg") && map.get(string).split(",").length == 3) {
                fieldTypeCounter = Integer.parseInt(string.substring(string.lastIndexOf(".") + 1, string.length()));
                LOGGER.info("genArg key Index" + fieldTypeCounter);

                dateContent = map.get(string).split(",");
                DateFormat dF = new SimpleDateFormat(dateContent[2]);
                try {
                    date = dF.parse(dateContent[0].trim());
                    date2 = dF.parse(dateContent[1].trim());
                } catch (ParseException e) {
                    LOGGER.info(e);
                    LOGGER.info("error in Date entry");
                }
                unifiedDate.append(date.getTime() + "," + date2.getTime() + "," + dateContent[2]);
                jpaProperties = Dao2TableUtil.buildJPAProperties("data", "args." + fieldArgCounter, unifiedDate.toString(), "Generated Argument");
                childProps.add(jpaProperties);
                jpaProperties = Dao2TableUtil.buildJPAProperties("data", "data-gen-id." + fieldArgCounter, map.get("type_generatedType." + fieldTypeCounter), "Generated Type");
                childProps.add(jpaProperties);
                fieldArgCounter++;
            }
            else if(string.startsWith("type_genArg")){
                fieldTypeCounter = Integer.parseInt(string.substring(string.lastIndexOf(".") + 1, string.length()));
                LOGGER.debug("genArg key Index" + fieldTypeCounter);
                jpaProperties =Dao2TableUtil.buildJPAProperties("data", "args." + fieldArgCounter, map.get(string), "Generated Argument");
                childProps.add(jpaProperties);
                jpaProperties =Dao2TableUtil.buildJPAProperties("data", "data-gen-id." + fieldArgCounter, map.get("type_generatedType." + fieldTypeCounter), "Generated Type");
                childProps.add(jpaProperties);
                fieldArgCounter++;
            }

            else if (string.startsWith("type_fieldName")) {
                Map<String,String> temp=new HashMap();
                temp.put("randomRegexPattern","string");
                temp.put("randomNumber","int");
                temp.put("randomDate","date");
                String dtype=temp.get(map.get("type_generatedType."+fieldCounter));
                tableSchema.append(map.get(string) + ":"+dtype+":" + fieldCounter + ",");
                LOGGER.info("type_fieldName" + tableSchema);
                LOGGER.info("data type is " +dtype );
                fieldCounter++;
            } else if (string.startsWith("other_numRows")) {
                LOGGER.debug("other_numRows" + map.get(string));
                jpaProperties =Dao2TableUtil.buildJPAProperties("data", key, map.get(string), "number of rows");
                childProps.add(jpaProperties );
            } else if (string.startsWith("other_numSplits")) {
                LOGGER.debug("other_numSplits" + map.get(string));
                jpaProperties =Dao2TableUtil.buildJPAProperties("data", key, map.get(string), "number of splits");
                childProps.add(jpaProperties );
            } else if (string.startsWith("other_tableName")) {
                LOGGER.debug("other_tableName" + map.get(string));
                jpaProperties =Dao2TableUtil.buildJPAProperties(TABLE, key, map.get(string), "Table Name");
                childProps.add(jpaProperties );
            } else if (string.startsWith("other_separator")) {
                LOGGER.debug("other_separator" + map.get(string));
                jpaProperties =Dao2TableUtil.buildJPAProperties(TABLE, key, map.get(string), "Separator");
                childProps.add(jpaProperties );
            }else if (string.startsWith("process_processName")) {
                LOGGER.debug("process_processName" + map.get(string));
                processName = map.get(string);
            }else if (string.startsWith("process_outputPath")) {
                LOGGER.debug("process_outputPath" + map.get(string));
                jpaProperties =Dao2TableUtil.buildJPAProperties(TABLE, key, map.get(string), "Output path");
                childProps.add(jpaProperties );
            }else if (string.startsWith("process_processDescription")) {
                LOGGER.debug("process_processDescription" + map.get(string));
                processDescription = map.get(string);
            }else if (string.startsWith("process_busDomainId")) {
                LOGGER.debug("process_busDomainId" + map.get(string));
                busDomainId = new Integer(map.get(string));
            }else if (string.startsWith("process_workflowTypeId")) {
                LOGGER.info("process_workflowTypeId" + map.get(string));
                workflowTypeId = new Integer(map.get(string));
            }

        }
        parentProcess = Dao2TableUtil.buildJPAProcess(18,processName, processDescription, workflowTypeId,busDomainId);
        Users users=new Users();
        users.setUsername(principal.getName());
        parentProcess.setUsers(users);
        parentProcess.setUserRoles(userRolesDAO.minUserRoleId(principal.getName()));
        childProcess = Dao2TableUtil.buildJPAProcess(14, "SubProcess of "+processName, processDescription, 0,busDomainId);


        //remove last : in tableSchema String
        tableSchema.deleteCharAt(tableSchema.length() - 1);
        jpaProperties =Dao2TableUtil.buildJPAProperties(TABLE, "tableSchema", tableSchema.toString(), "Table Schema");
        childProps.add(jpaProperties );
        //creating parent and child processes and inserting properties
        List<Process> processList = processDAO.createOneChildJob(parentProcess,childProcess,null,childProps);
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


    @RequestMapping(value = {"/", ""}, method = RequestMethod.PUT)
    @ResponseBody public
    RestWrapper insert(@ModelAttribute("generalconfig")
                       @Valid GeneralConfig generalConfig, BindingResult bindingResult, Principal principal) {
        LOGGER.debug("Updating jtable for new advanced config");


        RestWrapper restWrapper = null;
        if (bindingResult.hasErrors()) {
            BindingResultError bindingResultError = new BindingResultError();
            return bindingResultError.errorMessage(bindingResult);
        }

        try {

            restWrapper = new RestWrapper(generalConfig, RestWrapper.OK);
            LOGGER.info("Record with configGroup:" + generalConfig.getConfigGroup() + " inserted in Jtable by User:" + principal.getName() + generalConfig);
        } catch (Exception e) {
            LOGGER.error("error occured " + e.getMessage());
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
            throw e;
        }
        return restWrapper;
    }


    @RequestMapping(value = {"/import", "/import/"}, method = RequestMethod.POST)
    @ResponseBody
    public RestWrapper importData(@ModelAttribute("fileString")
                                  @Valid String uploadedFileName, BindingResult bindingResult, Principal principal) {
        RestWrapper restWrapper = null;
        if (bindingResult.hasErrors()) {
            BindingResultError bindingResultError = new BindingResultError();
            return bindingResultError.errorMessage(bindingResult);
        }
        LOGGER.info("uploaded exel file name is "+uploadedFileName);
        String homeDir = System.getProperty("user.home");
        LOGGER.info("home directory" + homeDir);
        String excelFileLocation = homeDir + "/bdre-wfd/exel/" + uploadedFileName;
        String FilePath = excelFileLocation;
        FileInputStream fs = null;
        try {
            fs = new FileInputStream(FilePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
            return restWrapper;
        }
        try {
            List<String[]> dataTypeList=new ArrayList<>();

            XSSFWorkbook workbook = new XSSFWorkbook(fs);
            XSSFSheet sheet = workbook.getSheetAt(0);

            Iterator<Row> rowIterator = sheet.iterator();
            while (rowIterator.hasNext())
            {
                String[] rowString=new String[3];
                Row row = rowIterator.next();
                //For each row, iterate through all the columns
                Iterator<Cell> cellIterator = row.cellIterator();
                int i=0;
                while (cellIterator.hasNext())
                {
                    Cell cell = cellIterator.next();
                    //Check the cell type and format accordingly

                    switch (cell.getCellType())
                    {
                        case Cell.CELL_TYPE_NUMERIC:
                            System.out.print(cell.getNumericCellValue() + " ");
                            rowString[i++]= String.valueOf(cell.getNumericCellValue());
                            break;
                        case Cell.CELL_TYPE_STRING:
                            System.out.print(cell.getStringCellValue() + " ");
                            rowString[i++]=cell.getStringCellValue();
                            break;
                    }
                }
              dataTypeList.add(rowString);
            }
            fs.close();
            restWrapper = new RestWrapper(dataTypeList, RestWrapper.OK);
        } catch (IOException e) {
            e.printStackTrace();
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    @Override
    public Object execute(String[] params) {
        return null;
    }
}