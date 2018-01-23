package com.wipro.ats.bdre.pdeploy;

import com.wipro.ats.bdre.md.api.JobTrigger;
import com.wipro.ats.bdre.md.dao.ProcessExecutionQueueDAO;
import com.wipro.ats.bdre.md.dao.jpa.Process;
import com.wipro.ats.bdre.md.dao.jpa.ProcessExecutionQueue;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.logging.Logger;

/**
 * Created by SH387936 on 01/17/2018.
 */

public class ProcessExecutionMain {
    @Autowired
    ProcessExecutionQueueDAO processExecutionQueueDAO;
    private static final Logger LOGGER=Logger.getLogger("ProcessExecutionMain.class");
    public static void main(String[] args){
        try {
            LOGGER.info("calling runOozieDownstream function in JobTrigger");
            new JobTrigger().runOozieDownStream();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
