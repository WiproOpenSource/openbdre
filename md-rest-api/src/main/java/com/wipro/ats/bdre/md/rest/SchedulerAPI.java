package com.wipro.ats.bdre.md.rest;

import com.wipro.ats.bdre.MDConfig;
import com.wipro.ats.bdre.md.beans.ExecutionInfo;
import com.wipro.ats.bdre.md.dao.ProcessDAO;
import com.wipro.ats.bdre.md.dao.PropertiesDAO;
import com.wipro.ats.bdre.md.dao.jpa.Properties;
import com.wipro.ats.bdre.md.dao.jpa.PropertiesId;
import com.wipro.ats.bdre.md.rest.util.Dao2TableUtil;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.security.Principal;
import java.util.*;

/**
 * Created by su324335 on 8/2/16.
 */

@Controller
@RequestMapping("/scheduler")
public class SchedulerAPI {
    private static final Logger LOGGER = Logger.getLogger(SchedulerAPI.class);

    @Autowired
    ProcessDAO processDAO;
    @Autowired
    PropertiesDAO propertiesDAO;

    @RequestMapping(value = {"/schedulejob/{processId}","/schedulejob/{processId}/"}, method = RequestMethod.POST)
    @ResponseBody
    public RestWrapper addScheduleProperties(@PathVariable("processId") Integer processId, @RequestParam Map<String, String> map, Principal principal) {
        LOGGER.debug(" value of map is " + map.size());
        LOGGER.info("process id is "+processId);
        RestWrapper restWrapper = null;
        String frequency = null;
        String startTime = null;
        String endTime = null;
        String timeZone = null;

        com.wipro.ats.bdre.md.dao.jpa.Properties jpaProperties = null;
        List<Properties> propertiesList = new ArrayList<Properties>();


        com.wipro.ats.bdre.md.dao.jpa.Process process = processDAO.get(processId);
        LOGGER.info("process is "+process.getProcessName());
        boolean firstTime = false;

        PropertiesId propertiesId = new PropertiesId(processId,"schedule-frequency");
        if(propertiesDAO.get(propertiesId) == null){
            firstTime = true;
        }

        for (String string : map.keySet()) {
            LOGGER.info("String is " + string + " And value is " + map.get(string));

            if (map.get(string) == null || ("").equals(map.get(string))) {
                continue;
            }

            if (string.startsWith("scheduleProperties_frequency")) {
                LOGGER.info(string + " : frequency");
                jpaProperties = Dao2TableUtil.buildJPAProperties("schedule", "schedule-frequency", map.get(string), "Frequency of scheduling");
                propertiesList.add(jpaProperties);
                frequency =  map.get(string);
            }

            else if (string.startsWith("scheduleProperties_startTime")) {
                LOGGER.info(string + " : starttime");
                String utcTime=null;
                if(map.get(string).contains(" "))
                    utcTime = map.get(string).replace(" ","T")+"Z";
                else
                    utcTime=map.get(string);
                jpaProperties = Dao2TableUtil.buildJPAProperties("schedule", "schedule-start-time", utcTime, "Start Time of scheduling");
                propertiesList.add(jpaProperties);
                startTime =  utcTime;
            }
            else if (string.startsWith("scheduleProperties_endTime")) {
                LOGGER.info(string + " : endtime");
                String utcTime=null;
                if(map.get(string).contains(" "))
                    utcTime = map.get(string).replace(" ","T")+"Z";
                else
                    utcTime=map.get(string);
                jpaProperties = Dao2TableUtil.buildJPAProperties("schedule", "schedule-end-time", utcTime, "End Time of scheduling");
                propertiesList.add(jpaProperties);
                endTime =  utcTime;
            }
            else if (string.startsWith("scheduleProperties_timeZone")) {
                LOGGER.info(string + " : timezone");
                jpaProperties = Dao2TableUtil.buildJPAProperties("schedule", "schedule-time-zone", map.get(string), "Time Zone of start time and end time");
                propertiesList.add(jpaProperties);
                timeZone =  map.get(string);
            }
        }

        //String jobId = propertiesDAO.getPropertiesValueForConfigAndKey(processId,"schedule", "schedule-job-id");
        String jobId = "null";

        LOGGER.info("jpa properties list "+propertiesList);
        for(Properties properties: propertiesList){
            properties.setProcess(process);
            properties.getId().setProcessId(processId);
            if(firstTime) {
                propertiesDAO.insert(properties);
            }
            else{
                propertiesDAO.update(properties);
            }
            LOGGER.info(properties + " inserted in "+processId);
        }


        try {

            LOGGER.info("process is "+process);
            LOGGER.info("process id is "+process.getProcessId().toString());
            /*String command = "sh " + MDConfig.getProperty("execute.script-path") + "/scheduler.sh" + " "+process.getBusDomain().getBusDomainId().toString()+" "+process.getProcessType().getProcessTypeId().toString()+" "+process.getProcessId().toString()+" \""+frequency+"\" "+startTime+" "+endTime+" "+timeZone+" "+jobId;
            CommandLine oCmdLine = CommandLine.parse(command);
            LOGGER.info("executing command :" + command);*/
            String[] command=new String[10];

            command[0]=MDConfig.getProperty("execute.script-path") + "/scheduler.sh";
            command[1]=process.getBusDomain().getBusDomainId().toString();
            command[2]=Integer.toString(process.getProcessType().getProcessTypeId());
            command[3]=process.getProcessId().toString();
            command[4]=frequency;
            command[5]=startTime;
            command[6]=endTime;
            command[7]=timeZone;
            command[8]=jobId;
            command[9]=principal.getName();

            LOGGER.info(Arrays.toString(command));

            /*ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            DefaultExecutor oDefaultExecutor = new DefaultExecutor();
            PumpStreamHandler pumpStreamHandler = new PumpStreamHandler(outputStream);
            //oDefaultExecutor.setExitValue(0);
            oDefaultExecutor.setStreamHandler(pumpStreamHandler);
            oDefaultExecutor.execute(oCmdLine);
            LOGGER.info(outputStream.toString());*/

            ExecutionInfo executionInfo = new ExecutionInfo();
            executionInfo.setProcessId(process.getProcessId());

            ProcessBuilder processBuilder = new ProcessBuilder(command[0], command[1], command[2], command[3], command[4],command[5],command[6],command[7],command[8],command[9]);
            processBuilder.redirectOutput(new File(MDConfig.getProperty("execute.log-path") + process.getProcessId().toString()));
            LOGGER.info("The output is redirected to " + MDConfig.getProperty("execute.log-path") + process.getProcessId().toString());
            processBuilder.redirectErrorStream(true);

            java.lang.Process osProcess = processBuilder.start();
            //BufferedReader reader = new BufferedReader(new InputStreamReader(osProcess.getInputStream()));
            //String line = reader.readLine();

            /*try {
                Class<?> cProcessImpl = osProcess.getClass();
                Field fPid = cProcessImpl.getDeclaredField("pid");
                if (!fPid.isAccessible()) {
                    fPid.setAccessible(true);
                }
                executionInfo.setOSProcessId(fPid.getInt(osProcess));
                LOGGER.debug(" OS process Id : " + executionInfo.getOSProcessId() + "executed by " + principal.getName());
            } catch (Exception e) {
                executionInfo.setOSProcessId(-1);
                LOGGER.error(e + " Setting OS Process ID failed " + executionInfo.getOSProcessId());
            }
*/


/*            Properties properties = new Properties();

                properties = Dao2TableUtil.buildJPAProperties("schedule", "schedule-job-id", line.replace("job: ", ""), "Oozie job Id of coordinator workflow");
                properties.setProcess(process);
                properties.getId().setProcessId(processId);
            if(firstTime) {
                propertiesDAO.insert(properties);
            }
            else{
                propertiesDAO.update(properties);
            }*/


        }catch (Exception e){
            LOGGER.error(e);
            e.printStackTrace();
        }

        com.wipro.ats.bdre.md.beans.table.Process tableProcess = Dao2TableUtil.jpa2TableProcess(process);
        restWrapper = new RestWrapper(tableProcess, RestWrapper.OK);
        LOGGER.info("Restwrapper result "+restWrapper.getResult());
        LOGGER.info("Properties for Scheduling process inserted by" + principal.getName());
        return restWrapper;


    }

    @RequestMapping(value = {"/"}, method = RequestMethod.GET)
    @ResponseBody
    public RestWrapper getTimeZones(Principal principal) {
        RestWrapper restWrapper = null;
        String[] ids = TimeZone.getAvailableIDs();
        List<String> timeZones = new LinkedList<String>();
        for(String id:ids){
            timeZones.add(id);
        }
        restWrapper = new RestWrapper(timeZones, RestWrapper.OK);
        return restWrapper;
    }


}
