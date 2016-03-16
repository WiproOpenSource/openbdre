package com.wipro.ats.bdre.md.rest;

import com.wipro.ats.bdre.exception.MetadataException;
import com.wipro.ats.bdre.md.api.util.AddJson;
import com.wipro.ats.bdre.md.app.AppStore;
import com.wipro.ats.bdre.md.beans.table.AppDeploymentQueue;
import com.wipro.ats.bdre.md.beans.table.Process;
import com.wipro.ats.bdre.md.beans.table.Properties;
import com.wipro.ats.bdre.md.dao.AppDeploymentQueueDAO;
import com.wipro.ats.bdre.md.dao.ProcessDAO;
import com.wipro.ats.bdre.md.dao.PropertiesDAO;
import com.wipro.ats.bdre.md.dao.jpa.AppDeploymentQueueStatus;
import com.wipro.ats.bdre.md.dao.jpa.Users;
import com.wipro.ats.bdre.md.rest.beans.ProcessExport;
import com.wipro.ats.bdre.md.rest.util.BindingResultError;
import com.wipro.ats.bdre.md.rest.util.DateConverter;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cloudera on 3/8/16.
 */
@Controller
@RequestMapping("/adq")

public class AppDeploymentQueueAPI {

    private static final Logger LOGGER = Logger.getLogger(AppDeploymentQueueAPI.class);
    @Autowired
    AppDeploymentQueueDAO appDeploymentQueueDAO;
    @Autowired
    private ProcessDAO processDAO;
    @Autowired
    private PropertiesDAO propertiesDAO;


   /**
     * This method fetches a list records from
     * AppDeploymentQueues table.
     *
     * @param
     * @return restWrapper returns a list of instances of AppDeploymentQueue object.
     */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    @ResponseBody public
    RestWrapper list(@RequestParam(value = "page", defaultValue = "0") int startPage,
                     @RequestParam(value = "size", defaultValue = "10") int pageSize, Principal principal) {

        RestWrapper restWrapper = null;
        try {
            Integer counter=appDeploymentQueueDAO.totalRecordCount();
            List<com.wipro.ats.bdre.md.dao.jpa.AppDeploymentQueue> jpaAdqList = appDeploymentQueueDAO.list(startPage, pageSize);
            List<AppDeploymentQueue> appDeploymentQueues = new ArrayList<AppDeploymentQueue>();
            for (com.wipro.ats.bdre.md.dao.jpa.AppDeploymentQueue adq : jpaAdqList) {
                AppDeploymentQueue appDeploymentQueue = new AppDeploymentQueue();
                appDeploymentQueue.setUsername(adq.getUsers().getUsername());
                appDeploymentQueue.setAppDomain(adq.getAppDomain());
                appDeploymentQueue.setAppName(adq.getAppName());
                appDeploymentQueue.setProcessId(adq.getProcess().getProcessId());
                appDeploymentQueue.setAppDeploymentQueueId(adq.getAppDeploymentQueueId());
                appDeploymentQueue.setAppDeploymentStatusId(adq.getAppDeploymentQueueStatus().getAppDeploymentStatusId());
                appDeploymentQueue.setCounter(counter);
                appDeploymentQueues.add(appDeploymentQueue);
            }


            restWrapper = new RestWrapper(appDeploymentQueues, RestWrapper.OK);
            LOGGER.info("All records listed from AppDeploymentQueue by User:" + principal.getName());
        } catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

}
