package com.wipro.ats.bdre.md.rest.ext;

import com.wipro.ats.bdre.md.dao.ProcessDAO;
import com.wipro.ats.bdre.md.dao.jpa.*;
import com.wipro.ats.bdre.md.dao.jpa.Process;
import com.wipro.ats.bdre.md.dao.jpa.Properties;
import com.wipro.ats.bdre.md.rest.RestWrapper;
import com.wipro.ats.bdre.md.rest.util.Dao2TableUtil;
import com.wipro.ats.bdre.md.rest.util.DateConverter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.*;

/**
 * Created by SU324335 on 4/13/2016.
 */
@Controller
@RequestMapping("/analyticsapp")
public class AnalyticsAppAPI {
    private static final Logger LOGGER = Logger.getLogger(AnalyticsAppAPI.class);
    @Autowired
    private ProcessDAO processDAO;

    @RequestMapping(value = {"/createjobs"}, method = RequestMethod.POST)

    @ResponseBody
    public RestWrapper createJob(HttpServletRequest request,@RequestParam Map<String, String> map, Principal principal) {
        LOGGER.debug(" value of map is " + map.size());
        String appImagePathRequest = request.getParameter("appimage");
        LOGGER.info("Path from request "+appImagePathRequest);

        String actualPath = request.getRealPath(appImagePathRequest);
        LOGGER.info("actual Path "+actualPath);
        RestWrapper restWrapper = null;

        String processName = null;
        String processDescription = null;
        Integer busDomainId = null;

        Map<Process,List<Properties>> processPropertiesMap = new HashMap<Process, List<Properties>>();

        List<com.wipro.ats.bdre.md.dao.jpa.Properties> propertiesList = new ArrayList<Properties>();

        for (String string : map.keySet()) {
            com.wipro.ats.bdre.md.dao.jpa.Properties jpaProperties=new Properties();
            LOGGER.info("String is" + string);
            if (map.get(string) == null || ("").equals(map.get(string))) {
                continue;
            }
            else if (string.startsWith("appproperties_processName")) {
                LOGGER.debug("process_processName" + map.get(string));
                processName = map.get(string);
            }else if (string.startsWith("appproperties_processDescription")) {
                LOGGER.debug("process_processDescription" + map.get(string));
                processDescription = map.get(string);
            }else if (string.startsWith("appproperties_busDomainId")) {
                LOGGER.debug("appproperties_busDomainId" + map.get(string));
                busDomainId = new Integer(map.get(string));
            }
            else if (string.startsWith("appproperties_industry")) {
                jpaProperties = Dao2TableUtil.buildJPAProperties("analytics-app", "industry", map.get(string), "Industry Name");
                propertiesList.add(jpaProperties);
                LOGGER.debug("appproperties__industry" + map.get(string));

            }
            else if (string.startsWith("appproperties_category")) {
                jpaProperties = Dao2TableUtil.buildJPAProperties("analytics-app", "category", map.get(string), "category Name");
                propertiesList.add(jpaProperties);
                LOGGER.debug("appproperties_category" + map.get(string));

            }
            else if (string.startsWith("appproperties_appname")) {
                jpaProperties = Dao2TableUtil.buildJPAProperties("analytics-app", "app-name", map.get(string), "Appname");
                propertiesList.add(jpaProperties);
                LOGGER.debug("appproperties_appname" + map.get(string));
            }
            else if (string.startsWith("appproperties_appdesc")) {
                jpaProperties = Dao2TableUtil.buildJPAProperties("analytics-app", "app-desc", map.get(string), "appdesc");
                propertiesList.add(jpaProperties);
                LOGGER.debug("appproperties_appdesc" + map.get(string));
            }
            else if (string.startsWith("appproperties_questionsjson")) {
                jpaProperties = Dao2TableUtil.buildJPAProperties("analytics-app", "questions-json", map.get(string), "questions json");
                propertiesList.add(jpaProperties);
                LOGGER.debug("appproperties_questionsjson" + map.get(string));
            }
            else if (string.startsWith("appproperties_dashboardurl")) {
                jpaProperties = Dao2TableUtil.buildJPAProperties("analytics-app", "dashboard-url", map.get(string), "dashboard url");
                propertiesList.add(jpaProperties);
                LOGGER.debug("appproperties_dashboardurl" + map.get(string));
            }
            else if (string.startsWith("appproperties_ddpurl")) {
                jpaProperties = Dao2TableUtil.buildJPAProperties("analytics-app", "ddp-url", map.get(string), "DDP url");
                propertiesList.add(jpaProperties);
                LOGGER.debug("appproperties_ddpurl" + map.get(string));
            }
            else if (string.startsWith("appImage")) {
                jpaProperties = Dao2TableUtil.buildJPAProperties("analytics-app", "app-image", map.get(string), "appimage");
                propertiesList.add(jpaProperties);
                LOGGER.debug("appproperties_appimage" + map.get(string));
            }



        }


        com.wipro.ats.bdre.md.dao.jpa.Process parentProcess = Dao2TableUtil.buildJPAProcess(37, processName, processDescription, 1,busDomainId);
        com.wipro.ats.bdre.md.dao.jpa.Process childProcess1 = Dao2TableUtil.buildJPAProcess(38, "subprocess1 of "+processName, "subprocess1 of "+processDescription, 1,busDomainId);
    //    com.wipro.ats.bdre.md.dao.jpa.Process childProcess2 = Dao2TableUtil.buildJPAProcess(38, "subprocess2 of "+processName, "subprocess1 of "+processDescription, 1,busDomainId);

        List<Process> childProcesses = new ArrayList<Process>();
        childProcesses.add(childProcess1);
   //     childProcesses.add(childProcess2);


        List<com.wipro.ats.bdre.md.dao.jpa.Process> processList = processDAO.createAnalyticsAppJob(parentProcess, childProcesses, propertiesList);
        List<com.wipro.ats.bdre.md.beans.table.Process> tableProcessList = Dao2TableUtil.jpaList2TableProcessList(processList);
        Integer counter = tableProcessList.size();
        for (com.wipro.ats.bdre.md.beans.table.Process process:tableProcessList) {
            process.setCounter(counter);
            process.setTableAddTS(DateConverter.dateToString(process.getAddTS()));
            process.setTableEditTS(DateConverter.dateToString(process.getEditTS()));
        }
        restWrapper = new RestWrapper(tableProcessList, RestWrapper.OK);
        LOGGER.info("Process and Properties for Analytics App process inserted by" + principal.getName());


        return restWrapper;
    }


}
