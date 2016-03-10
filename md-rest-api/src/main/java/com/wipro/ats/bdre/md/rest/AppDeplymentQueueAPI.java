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

public class AppDeplymentQueueAPI {

    private static final Logger LOGGER = Logger.getLogger(AppDeplymentQueueAPI.class);
    @Autowired
    AppDeploymentQueueDAO appDeploymentQueueDAO;
    @Autowired
    private ProcessDAO processDAO;
    @Autowired
    private PropertiesDAO propertiesDAO;

    @RequestMapping(value = {"/", ""}, method = RequestMethod.POST)
    @ResponseBody
    public
    RestWrapper insert(@ModelAttribute("acq")
                       @Valid AppDeploymentQueue appDeploymentQueue, BindingResult bindingResult, Principal principal) {
        RestWrapper restWrapper = null;
        if (bindingResult.hasErrors()) {
            BindingResultError bindingResultError = new BindingResultError();
            return bindingResultError.errorMessage(bindingResult);
        }
            AppDeploymentQueue returnedAppDeploymentQueue=new AppDeploymentQueue();
        ProcessExport processExport = new ProcessExport();
        try{
            LOGGER.info("app domain = "+appDeploymentQueue.getAppDomain()+"app name = "+appDeploymentQueue.getAppName()+" processID = "+appDeploymentQueue.getProcessId());


            Process process = new Process();
            process.setProcessId(appDeploymentQueue.getProcessId());
            List<Process> processList = new ArrayList<Process>();
            List<com.wipro.ats.bdre.md.dao.jpa.Process> daoProcessList = processDAO.selectProcessList(appDeploymentQueue.getProcessId());
            for (com.wipro.ats.bdre.md.dao.jpa.Process daoProcess : daoProcessList) {
                Process tableProcess = new Process();
                tableProcess.setProcessId(daoProcess.getProcessId());
                tableProcess.setBusDomainId(daoProcess.getBusDomain().getBusDomainId());
                if (daoProcess.getWorkflowType() != null) {
                    tableProcess.setWorkflowId(daoProcess.getWorkflowType().getWorkflowId());
                }
                tableProcess.setDescription(daoProcess.getDescription());
                tableProcess.setProcessName(daoProcess.getProcessName());
                tableProcess.setProcessTypeId(daoProcess.getProcessType().getProcessTypeId());
                if (daoProcess.getProcess() != null) {
                    tableProcess.setParentProcessId(daoProcess.getProcess().getProcessId());
                }
                tableProcess.setCanRecover(daoProcess.getCanRecover());
                if (daoProcess.getProcessTemplate() != null) {
                    tableProcess.setProcessTemplateId(daoProcess.getProcessTemplate().getProcessTemplateId());
                }
                tableProcess.setEnqProcessId(daoProcess.getEnqueuingProcessId());
                tableProcess.setNextProcessIds(daoProcess.getNextProcessId());
                tableProcess.setBatchPattern(daoProcess.getBatchCutPattern());
                if (daoProcess.getBatchCutPattern() != null) {
                    tableProcess.setTableAddTS(DateConverter.dateToString(daoProcess.getAddTs()));
                }
                tableProcess.setTableEditTS(DateConverter.dateToString(daoProcess.getEditTs()));
                tableProcess.setDeleteFlag(daoProcess.getDeleteFlag());
                tableProcess.setProcessCode(daoProcess.getProcessCode());
                processList.add(tableProcess);
            }
            List<Properties> propertiesList = new ArrayList<Properties>();
            for (com.wipro.ats.bdre.md.dao.jpa.Process process1 : daoProcessList){
                List<com.wipro.ats.bdre.md.dao.jpa.Properties> daoPropertiesList = propertiesDAO.getByProcessId(process1);
                for (com.wipro.ats.bdre.md.dao.jpa.Properties daoProperties : daoPropertiesList) {
                    Properties tableProperties = new Properties();
                    tableProperties.setProcessId(daoProperties.getProcess().getProcessId());
                    tableProperties.setConfigGroup(daoProperties.getConfigGroup());
                    tableProperties.setKey(daoProperties.getId().getPropKey());
                    tableProperties.setValue(daoProperties.getPropValue());
                    tableProperties.setDescription(daoProperties.getDescription());
                    propertiesList.add(tableProperties);
                }}
            processExport.setProcessList(processList);
            processExport.setPropertiesList(propertiesList);
            LOGGER.info("export object is "+processExport);
            AddJson addJson=new AddJson();
            String status=addJson.addJsonToProcessId(appDeploymentQueue.getProcessId().toString(),processExport);

            LOGGER.info("status of process.json addition "+status);
            com.wipro.ats.bdre.md.dao.jpa.AppDeploymentQueue jpaAppDeploymentQueue=new com.wipro.ats.bdre.md.dao.jpa.AppDeploymentQueue();
            AppDeploymentQueueStatus appDeploymentQueueStatus=new AppDeploymentQueueStatus();
            appDeploymentQueueStatus.setAppDeployStatusId((short) 0);
            appDeploymentQueueStatus.setDescription("pull request created");
            jpaAppDeploymentQueue.setAppDeploymentQueueStatus(appDeploymentQueueStatus);
            com.wipro.ats.bdre.md.dao.jpa.Process process1=new com.wipro.ats.bdre.md.dao.jpa.Process();
            process1.setProcessId(appDeploymentQueue.getProcessId());
            jpaAppDeploymentQueue.setProcess(process1);
            jpaAppDeploymentQueue.setAppDomain(appDeploymentQueue.getAppDomain());
            jpaAppDeploymentQueue.setAppName(appDeploymentQueue.getAppName());
            Users users=new Users();
            users.setUsername(principal.getName());
            jpaAppDeploymentQueue.setUsers(users);
            Long adqId=appDeploymentQueueDAO.insert(jpaAppDeploymentQueue);
            LOGGER.info("app deployment queue Id is "+adqId);
             returnedAppDeploymentQueue.setProcessId(appDeploymentQueue.getProcessId());
             returnedAppDeploymentQueue.setAppName(appDeploymentQueue.getAppName());
             returnedAppDeploymentQueue.setAppDomain(appDeploymentQueue.getAppDomain());
             returnedAppDeploymentQueue.setUsername(principal.getName());
             returnedAppDeploymentQueue.setAppDeploymentQueueId(adqId);
             returnedAppDeploymentQueue.setAppDeploymentQueueStatus((short) 0);
             restWrapper = new RestWrapper(returnedAppDeploymentQueue, RestWrapper.OK);
        } catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }


    @RequestMapping(value = "/merge/{id}", method = RequestMethod.POST)
    @ResponseBody
    public
    RestWrapper merge(@PathVariable("id") Long queueId, Principal principal) {
        RestWrapper restWrapper = null;
        AppDeploymentQueue returnedAppDeploymentQueue=new AppDeploymentQueue();
        ProcessExport processExport = new ProcessExport();
        try{
            String temp;
            BufferedReader br = null;
            String jsonfile="";
            String homeDir = System.getProperty("user.home");
            LOGGER.info("home directory" + homeDir);
            br = new BufferedReader(new FileReader(homeDir+"/bdreappstore/store.json"));
            while ((temp=br.readLine()) != null) {
                jsonfile=jsonfile+temp;
                LOGGER.info(jsonfile);
            }
            LOGGER.info("final string is"+jsonfile);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            AppStore appStore = mapper.readValue(jsonfile, AppStore.class);
            restWrapper = new RestWrapper(returnedAppDeploymentQueue, RestWrapper.OK);
        } catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        } catch (IOException e) {
            e.printStackTrace();
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }



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
                appDeploymentQueue.setAppDeploymentQueueStatus(adq.getAppDeploymentQueueStatus().getAppDeployStatusId());
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
