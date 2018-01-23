package com.wipro.ats.bdre.md.api;
import com.wipro.ats.bdre.MDConfig;
import com.wipro.ats.bdre.exception.MetadataException;
import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.beans.ExecutionInfo;
import com.wipro.ats.bdre.md.beans.ProcessInfo;
import com.wipro.ats.bdre.md.dao.JobTriggerDAO;
import com.wipro.ats.bdre.md.dao.ProcessDAO;
import com.wipro.ats.bdre.md.dao.ProcessExecutionQueueDAO;
import com.wipro.ats.bdre.md.dao.ProcessPipelineDAO;
import com.wipro.ats.bdre.md.dao.jpa.Process;
import com.wipro.ats.bdre.md.dao.jpa.ProcessExecutionQueue;
import com.wipro.ats.bdre.md.dao.jpa.ProcessType;
import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SH387936 on 01-03-2018.
 */

public class JobTrigger extends MetadataAPIBase {
    private static final Logger LOGGER = Logger.getLogger(JobTrigger.class);
    @Autowired
     ProcessDAO processDAO;
    @Autowired
    JobTriggerDAO jobTriggerDAO;
    @Autowired
    ProcessExecutionQueueDAO processExecutionQueueDAO;
    public JobTrigger() {
        AutowireCapableBeanFactory acbFactory = getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }
    public  void runDownStream(Integer processId){
        try {
            LOGGER.info("inside runDownStream");
            int flag = jobTriggerDAO.checkDownStream(processId);
            LOGGER.info("The value of flag is : " + flag);
            //new ProcessPipelineDAO().createPipeline();
            if (flag == 0) {
                Process pProcess = jobTriggerDAO.getDownStreamProcess(processId);
                execute(pProcess, "admin");
            }
        }
        catch (Exception e){
            LOGGER.error(e);
        }
    }

    public void runOozieDownStream(){
        List<ProcessExecutionQueue> processExecutionQueueList=new ArrayList<>();
        try{
            LOGGER.info("inside runOozieDownstream");
            processExecutionQueueList=processExecutionQueueDAO.get();
            for(ProcessExecutionQueue peq:processExecutionQueueList){
                Process process=peq.getProcess();
                LOGGER.info("process with process id= " + process.getProcessId() + " picked for execution");
                execute(process,"admin");
                processExecutionQueueDAO.updateStatusToStarted(peq.getExecutionId());
            }
        }
        catch (Exception e){
            LOGGER.error(e);
        }
    }



    public void execute(Process pProcess,String userName){
        LOGGER.info("Execution of process started");
        ExecutionInfo executionInfo = new ExecutionInfo();
        com.wipro.ats.bdre.md.beans.table.Process process=new com.wipro.ats.bdre.md.beans.table.Process();
        process.setProcessId(pProcess.getProcessId());
        Process p=processDAO.get(pProcess.getProcessId());
        ProcessType processType=p.getProcessType();
        process.setBusDomainId(1);
        process.setProcessTypeId(processType.getProcessTypeId());
        process.setWorkflowId(1);
        LOGGER.info("process id is : " + process.getProcessId());
        try {
            //processDAO.securityCheck(process.getProcessId(),userName,"execute");
            String[] command=new String[5];
            LOGGER.info("workflow typeid  is "+process.getWorkflowId());
            if (process.getWorkflowId()==3)
                command[0]= MDConfig.getProperty("execute.script-path") + "/job-executor-airflow.sh";
            else {
                command[0]=MDConfig.getProperty("execute.script-path") + "/job-executor.sh";

            }
            command[1]=process.getBusDomainId().toString();
            command[2]=process.getProcessTypeId().toString();
            command[3]=process.getProcessId().toString();
            command[4]=userName;
            LOGGER.info("Running the command : -- " + command[0] + " " + command[1] + " " + command[2] + " " + command[3]+" "+command[4]);
            ProcessBuilder processBuilder = new ProcessBuilder(command[0],command[1],command[2],command[3],command[4]);
            processBuilder.redirectOutput(new File(MDConfig.getProperty("execute.log-path") + process.getProcessId().toString()));
            LOGGER.info("The output is redirected to " + MDConfig.getProperty("execute.log-path") + process.getProcessId().toString());
            processBuilder.redirectErrorStream(true);
            java.lang.Process osProcess = processBuilder.start();
            try {
                Class<?> cProcessImpl = osProcess.getClass();
                Field fPid = cProcessImpl.getDeclaredField("pid");
                if (!fPid.isAccessible()) {
                    fPid.setAccessible(true);
                }
                executionInfo.setOSProcessId(fPid.getInt(osProcess));
                LOGGER.debug(" OS process Id : " + executionInfo.getOSProcessId() + "executed by " + userName);
            } catch (Exception e) {
                executionInfo.setOSProcessId(-1);
                LOGGER.error(e + " Setting OS Process ID failed " + executionInfo.getOSProcessId());
            }

        } catch (MetadataException e) {
            LOGGER.error(e + " Executing workflow failed " + e.getCause());

        }catch (SecurityException e) {
            LOGGER.error(e + " security check failed " + e.getCause());

        }catch (IOException e) {
            LOGGER.error(e + " Executing workflow failed " + e.getCause());

        }
        catch (Exception e){
            LOGGER.error(e);
        }
    }
    @Override
    public Object execute(String[] params) {
        return null;
    }
}

