package com.wipro.ats.bdre.md.rest.ext;

import com.wipro.ats.bdre.md.beans.table.Process;
import com.wipro.ats.bdre.md.dao.ProcessDAO;
import com.wipro.ats.bdre.md.dao.UserRolesDAO;
import com.wipro.ats.bdre.md.dao.jpa.Properties;
import com.wipro.ats.bdre.md.dao.jpa.Users;
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

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by cloudera on 12/14/16.
 */

@Controller
@RequestMapping("/kafkaproducer")
public class KafkaProducerAPI {
    private static final String KAFKAPRODUCER = "kafkaproducer";
    private static final Logger LOGGER = Logger.getLogger(KafkaProducerAPI.class);

    @Autowired
    private ProcessDAO processDAO;
    @Autowired
    UserRolesDAO userRolesDAO;

    @RequestMapping(value = {"/createjobs"}, method = RequestMethod.POST)
    @ResponseBody
    public RestWrapper createJob(@RequestParam Map<String, String> map, Principal principal) {
        LOGGER.debug(" value of map is " + map.size());
        RestWrapper restWrapper = null;

        String processName = null;
        String processDescription = null;
        Integer busDomainId = null;


        List<Properties> childProps=new ArrayList<Properties>();
        com.wipro.ats.bdre.md.dao.jpa.Properties jpaProperties=null;

        for (String string : map.keySet()) {
            LOGGER.debug("String is" + string);
            if (map.get(string) == null || ("").equals(map.get(string))) {
                continue;
            }
            Integer splitIndex = string.lastIndexOf("_");
            String key = string.substring(splitIndex + 1, string.length());
            LOGGER.debug("key is " + key);
            if (string.startsWith("properties_topicName")) {
                jpaProperties = Dao2TableUtil.buildJPAProperties(KAFKAPRODUCER, key, map.get(string), "Topic Name");
                childProps.add(jpaProperties);
            } else if (string.startsWith("properties_zkConnectionString")) {
                jpaProperties = Dao2TableUtil.buildJPAProperties(KAFKAPRODUCER,  key, map.get(string), "ZooKeeper Connection String");
                childProps.add(jpaProperties);
            } else if (string.startsWith("properties_brokersList")) {
                jpaProperties = Dao2TableUtil.buildJPAProperties(KAFKAPRODUCER,  key, map.get(string), "Brokers List");
                childProps.add(jpaProperties);
            }else if (string.startsWith("properties_processName")) {
                LOGGER.debug("process_processName" + map.get(string));
                processName = map.get(string);
            }else if (string.startsWith("properties_processDesc")) {
                LOGGER.debug("process_processDescription" + map.get(string));
                processDescription = map.get(string);
            }else if (string.startsWith("properties_busDomainId")) {
                LOGGER.debug("process_busDomainId" + map.get(string));
                busDomainId = new Integer(map.get(string));
            }
        }

        com.wipro.ats.bdre.md.dao.jpa.Process parentProcess = Dao2TableUtil.buildJPAProcess(41, processName, processDescription, 2,busDomainId);
        parentProcess.setUserRoles(userRolesDAO.minUserRoleId(principal.getName()));
        Users users=new Users();
        users.setUsername(principal.getName());
        parentProcess.setUsers(users);
        com.wipro.ats.bdre.md.dao.jpa.Process childProcess = Dao2TableUtil.buildJPAProcess(42, "SubProcess of "+processName , processDescription, 0,busDomainId);

        List<com.wipro.ats.bdre.md.dao.jpa.Process> processList = processDAO.createOneChildJob(parentProcess,childProcess,null,childProps);
        List<Process> tableProcessList = Dao2TableUtil.jpaList2TableProcessList(processList);
        Integer counter = tableProcessList.size();
        for (Process process:tableProcessList) {
            process.setCounter(counter);
            process.setTableAddTS(DateConverter.dateToString(process.getAddTS()));
            process.setTableEditTS(DateConverter.dateToString(process.getEditTS()));
        }
        restWrapper = new RestWrapper(tableProcessList, RestWrapper.OK);
        LOGGER.info("Process and properties inserted for kafka producer Process by " + principal.getName());
        return restWrapper;
    }
}
