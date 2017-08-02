package com.wipro.ats.bdre.md.api;

import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.dao.ExecStatusDAO;
import com.wipro.ats.bdre.md.dao.InstanceExecDAO;
import com.wipro.ats.bdre.md.dao.MessagesDAO;
import com.wipro.ats.bdre.md.dao.ProcessDAO;
import com.wipro.ats.bdre.md.dao.jpa.ExecStatus;
import com.wipro.ats.bdre.md.dao.jpa.InstanceExec;
import com.wipro.ats.bdre.md.dao.jpa.Process;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

/**
 * Created by cloudera on 6/18/17.
 */
public class InstanceExecAPI extends MetadataAPIBase {
    private static final Logger LOGGER = Logger.getLogger(StreamingMessagesAPI.class);
    @Autowired
    InstanceExecDAO instanceExecDAO;
    @Autowired
    ProcessDAO processDAO;
    @Autowired
    ExecStatusDAO execStatusDAO;

    public InstanceExecAPI() {
        AutowireCapableBeanFactory acbFactory = getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    public void insertInstanceExec(Integer processId, String applicationId){
        InstanceExec instanceExec = new InstanceExec();
        Process process = processDAO.get(processId);
        instanceExec.setProcess(process);
        ExecStatus execStatus = execStatusDAO.get(2);
        instanceExec.setExecStatus(execStatus);
        instanceExec.setApplicationId(applicationId);

        instanceExecDAO.insert(instanceExec);
    }

    public void updateInstanceExec(Integer processId){
        InstanceExec instanceExec = instanceExecDAO.getLatestExecofProcess(processId);
        ExecStatus execStatus = execStatusDAO.get(8);
        instanceExec.setExecStatus(execStatus);
        if(instanceExec.getExecStatus().getExecStateId() != 7) {
            instanceExecDAO.update(instanceExec);
        }
    }


    @Override
    public Object execute(String[] params) {
        return null;
    }
}
