package com.wipro.ats.bdre.md.triggers;

import com.wipro.ats.bdre.exception.MetadataException;
import com.wipro.ats.bdre.md.dao.ProcessDAO;
import com.wipro.ats.bdre.md.dao.jpa.*;
import com.wipro.ats.bdre.md.dao.jpa.Process;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.Assert.*;

public class ProcessValidateInsertTest {
    private static final Logger LOGGER = Logger.getLogger(ProcessValidateInsertTest.class);
    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    @Test
    public void testProcessVal() throws Exception {

        try
        {
            Process process =new Process();
            process.setProcessId(10805);
            ProcessType processType=new ProcessType();
            processType.setProcessTypeId(1);
            Process parentProcess=new Process();
            parentProcess.setProcessId(10802);
            process.setProcessType(processType);
            process.setProcess(parentProcess);
            ProcessValidateInsert processValidateInsert=new ProcessValidateInsert();
            processValidateInsert.ProcessTypeValidator(process);
        }
        catch (MetadataException e)
        {
            LOGGER.info("error in testing " + e);
        }
    }
}