package com.wipro.ats.bdre.md.rest.ext;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by su324335 on 10/12/16.
 */
@Controller
@RequestMapping("/dataexport")
public class DataExportAPI {
    private static final Logger LOGGER = Logger.getLogger(DataExportAPI.class);
    private static final String EXPORTCONFIG = "exp-common";
    @Autowired
    private ProcessDAO processDAO;
    @Autowired
    UserRolesDAO userRolesDAO;

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

        for (String string : map.keySet()) {
            LOGGER.info("String is" + string);
            if (map.get(string) == null || ("").equals(map.get(string))) {
                continue;
            } else if (string.startsWith("dbDetails_dbDriver")) {
                jpaProperties = Dao2TableUtil.buildJPAProperties(EXPORTCONFIG, "driver", map.get(string), "Database Driver");
                propertiesList.add(jpaProperties);
            } else if (string.startsWith("dbDetails_dbUser")) {
                jpaProperties = Dao2TableUtil.buildJPAProperties(EXPORTCONFIG, "username", map.get(string), "Database Username");
                propertiesList.add(jpaProperties);
            } else if (string.startsWith("dbDetails_dbPassword")) {
                jpaProperties = Dao2TableUtil.buildJPAProperties(EXPORTCONFIG, "password", map.get(string), "Database Password");
                propertiesList.add(jpaProperties);
            } else if (string.startsWith("inputData_inputHDFSDir")) {
                jpaProperties = Dao2TableUtil.buildJPAProperties(EXPORTCONFIG, "export.dir", map.get(string), "Export Directory");
                propertiesList.add(jpaProperties);
            }
            else if (string.startsWith("inputData_mode")) {
                jpaProperties = Dao2TableUtil.buildJPAProperties(EXPORTCONFIG, "mode", map.get(string), "Mode(Insert/Update)");
                propertiesList.add(jpaProperties);
            }
            else if (string.startsWith("inputData_inputDataDelimiter")) {
                jpaProperties = Dao2TableUtil.buildJPAProperties(EXPORTCONFIG, "delimiter", map.get(string), "Input Data Delimiter");
                propertiesList.add(jpaProperties);
            }
            else if (string.startsWith("dbDetails_dbURL")) {
                jpaProperties = Dao2TableUtil.buildJPAProperties(EXPORTCONFIG, "db", map.get(string), "Database URL");
                propertiesList.add(jpaProperties);
            }
            else if (string.startsWith("dbDetails_dbSchema")) {
                jpaProperties = Dao2TableUtil.buildJPAProperties(EXPORTCONFIG, "schema", map.get(string), "Database Schema");
                propertiesList.add(jpaProperties);
            }
            else if (string.startsWith("dbDetails_table")) {
                jpaProperties = Dao2TableUtil.buildJPAProperties(EXPORTCONFIG, "table", map.get(string), "Table Name");
                propertiesList.add(jpaProperties);
            }
            else if (string.startsWith("dbDetails_columns")) {
                jpaProperties = Dao2TableUtil.buildJPAProperties(EXPORTCONFIG, "updateColumns", map.get(string), "Update Columns");
                propertiesList.add(jpaProperties);
            }
            else if (string.startsWith("processFields_processName")) {
                LOGGER.debug("srcEnv_processName" + map.get(string));
                processName = map.get(string);
            } else if (string.startsWith("processFields_processDescription")) {
                LOGGER.debug("srcEnv_processDescription" + map.get(string));
                processDesc = map.get(string);
            } else if (string.startsWith("processFields_busDomainId")) {
                LOGGER.debug("srcEnv_busDomainID" + map.get(string));
                busDomainID = new Integer(map.get(string));
            }
        }


            parentProcess = Dao2TableUtil.buildJPAProcess(3,processName, processDesc ,1,busDomainID);
            Users users=new Users();
            users.setUsername(principal.getName());
            parentProcess.setUsers(users);
            parentProcess.setUserRoles(userRolesDAO.minUserRoleId(principal.getName()));
            childProcess = Dao2TableUtil.buildJPAProcess(17, "SubProcess of "+processName, processDesc, 0,busDomainID);
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
