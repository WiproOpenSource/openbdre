package com.wipro.ats.bdre.md.api;

import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.dao.ProcessLogDAO;
import com.wipro.ats.bdre.md.dao.ProcessTypeDAO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

/**
 * Created by cloudera on 6/2/16.
 */
public class ProcessType  extends MetadataAPIBase {

    private static final Logger LOGGER = Logger.getLogger(ProcessType.class);

    @Autowired
    ProcessTypeDAO processTypeDAO;


    public ProcessType() {
        AutowireCapableBeanFactory acbFactory = getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    public void insert(com.wipro.ats.bdre.md.beans.table.ProcessType processType){
        com.wipro.ats.bdre.md.dao.jpa.ProcessType processTypeJPA = new com.wipro.ats.bdre.md.dao.jpa.ProcessType();
        processTypeJPA.setProcessTypeId(processType.getProcessTypeId());
        processTypeJPA.setProcessTypeName(processType.getProcessTypeName());
        processTypeJPA.setParentProcessTypeId(processType.getParentProcessTypeId());
        processTypeDAO.insert(processTypeJPA);
    }


    public void update(com.wipro.ats.bdre.md.beans.table.ProcessType processType){
        com.wipro.ats.bdre.md.dao.jpa.ProcessType processTypeJPA = processTypeDAO.get(processType.getProcessTypeId());
        processTypeJPA.setProcessTypeName(processType.getProcessTypeName());
        processTypeJPA.setParentProcessTypeId(processType.getParentProcessTypeId());
        processTypeDAO.update(processTypeJPA);
    }

    public void delete(com.wipro.ats.bdre.md.beans.table.ProcessType processType){
        processTypeDAO.delete(processType.getProcessTypeId());
    }

    public Object execute(String[] args){
        return null;
    }
}
