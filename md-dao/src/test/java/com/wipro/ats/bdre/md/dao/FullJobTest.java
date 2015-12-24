package com.wipro.ats.bdre.md.dao;

import com.wipro.ats.bdre.md.beans.InitJobRowInfo;
import com.wipro.ats.bdre.md.beans.table.*;
import com.wipro.ats.bdre.md.dao.jpa.*;
import com.wipro.ats.bdre.md.dao.jpa.BusDomain;
import com.wipro.ats.bdre.md.dao.jpa.Process;
import com.wipro.ats.bdre.md.dao.jpa.ProcessType;
import com.wipro.ats.bdre.md.dao.jpa.WorkflowType;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import static org.junit.Assert.*;
import java.util.Date;
import java.util.List;

/**
 * Created by PR324290 on 12/17/2015.
 */
public class FullJobTest {
    private static final Logger LOGGER = Logger.getLogger(FullJobTest.class);

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    @Autowired
    ProcessDAO processDAO;
    @Autowired
    BusDomainDAO busDomainDAO;
    @Autowired
    ProcessTypeDAO processTypeDAO;
    @Autowired
    WorkflowTypeDAO workflowTypeDAO;
    @Autowired
    JobDAO jobDAO;
    @Autowired
    StepDAO stepDAO;


    @Test
     public void completeJobTest() throws Exception {


         BusDomain busDomain=new BusDomain();
         busDomain.setBusDomainName("testName");
         busDomain.setDescription("testDescription");
         busDomain.setBusDomainOwner("testOwner");

        //inserting new BusDomain
         int busDomainId = busDomainDAO.insert(busDomain);
        //updating inserted BusDomain
          busDomain.setDescription("updateDescription");
          busDomainDAO.update(busDomain);
       //fetching updated busDomain
          BusDomain updatedBusDomain=busDomainDAO.get(busDomainId);

          assertEquals("update failed","updateDescription",updatedBusDomain.getDescription());

   /*

         ProcessType processType = processTypeDAO.get(1);
         ProcessType childProcessType = processTypeDAO.get(12);
         WorkflowType workflowType=workflowTypeDAO.get(1);
          WorkflowType childWworkflowType=workflowTypeDAO.get(0);

         Process subProcess1 = new Process();
         subProcess1.setProcessName("Test");
         subProcess1.setDescription("Test Process");
         subProcess1.setBusDomain(busDomain);
         subProcess1.setProcessType(childProcessType);
         subProcess1.setAddTs(new Date());
         subProcess1.setCanRecover(true);
         subProcess1.setEnqueuingProcessId(0);
         subProcess1.setProcess(null);
         subProcess1.setNextProcessId("1");
         subProcess1.setDeleteFlag(false);
         subProcess1.setEditTs(new Date());
        subProcess1.setWorkflowType(childWworkflowType);
         Integer subProcessId1 = processDAO.insert(subProcess1);

         Process subProcess2 = new Process();
         subProcess2.setProcessName("Test");
         subProcess2.setDescription("Test Process");
         subProcess2.setBusDomain(busDomain);
         subProcess2.setProcessType(childProcessType);
         subProcess2.setAddTs(new Date());
         subProcess2.setCanRecover(true);
         subProcess2.setEnqueuingProcessId(0);
         subProcess2.setProcess(null);
         subProcess2.setNextProcessId("1");
         subProcess2.setDeleteFlag(false);
         subProcess2.setEditTs(new Date());
        subProcess2.setWorkflowType(childWworkflowType);
         Integer subProcessId2 = processDAO.insert(subProcess2);

        Process subProcess3 = new Process();
        subProcess3.setProcessName("Test");
        subProcess3.setDescription("Test Process");
        subProcess3.setBusDomain(busDomain);
        subProcess3.setProcessType(childProcessType);
        subProcess3.setAddTs(new Date());
        subProcess3.setCanRecover(true);
        subProcess3.setEnqueuingProcessId(0);
        subProcess3.setProcess(null);
        subProcess3.setNextProcessId("1");
        subProcess3.setDeleteFlag(false);
        subProcess3.setEditTs(new Date());
        subProcess3.setWorkflowType(childWworkflowType);
        Integer subProcessId3 = processDAO.insert(subProcess3);

         Process parentProcess1 = new Process();
         parentProcess1.setProcessName("Test");
         parentProcess1.setDescription("Test Process");
         parentProcess1.setBusDomain(busDomain);
         parentProcess1.setProcessType(processType);
         parentProcess1.setAddTs(new Date());
         parentProcess1.setCanRecover(true);
         parentProcess1.setEnqueuingProcessId(0);
         parentProcess1.setProcess(null);
         parentProcess1.setNextProcessId("1");
         parentProcess1.setDeleteFlag(false);
         parentProcess1.setEditTs(new Date());
        parentProcess1.setWorkflowType(workflowType);
         Integer parentProcessId1 = processDAO.insert(parentProcess1);

         parentProcess1=processDAO.get(parentProcessId1);
         parentProcess1.setNextProcessId(subProcessId1.toString()+","+subProcessId2.toString());
        processDAO.update(parentProcess1);

         subProcess1=processDAO.get(subProcessId1);
         subProcess1.setProcess(parentProcess1);
         subProcess1.setNextProcessId(parentProcessId1.toString());
         processDAO.update(subProcess1);

         subProcess2=processDAO.get(subProcessId2);
         subProcess2.setProcess(parentProcess1);
         subProcess2.setNextProcessId(subProcessId3.toString());
         processDAO.update(subProcess2);

        subProcess3=processDAO.get(subProcessId3);
        subProcess3.setProcess(parentProcess1);
        subProcess3.setNextProcessId(parentProcessId1.toString());
        processDAO.update(subProcess3);




        Process subProcess4 = new Process();
        subProcess4.setProcessName("Test");
        subProcess4.setDescription("Test Process");
        subProcess4.setBusDomain(busDomain);
        subProcess4.setProcessType(childProcessType);
        subProcess4.setAddTs(new Date());
        subProcess4.setCanRecover(true);
        subProcess4.setEnqueuingProcessId(0);
        subProcess4.setProcess(null);
        subProcess4.setNextProcessId("1");
        subProcess4.setDeleteFlag(false);
        subProcess4.setEditTs(new Date());
        subProcess4.setWorkflowType(childWworkflowType);
        Integer subProcessId4 = processDAO.insert(subProcess4);

        Process subProcess5 = new Process();
        subProcess5.setProcessName("Test");
        subProcess5.setDescription("Test Process");
        subProcess5.setBusDomain(busDomain);
        subProcess5.setProcessType(childProcessType);
        subProcess5.setAddTs(new Date());
        subProcess5.setCanRecover(true);
        subProcess5.setEnqueuingProcessId(0);
        subProcess5.setProcess(null);
        subProcess5.setNextProcessId("1");
        subProcess5.setDeleteFlag(false);
        subProcess5.setEditTs(new Date());
        subProcess5.setWorkflowType(childWworkflowType);
        Integer subProcessId5 = processDAO.insert(subProcess5);

        Process parentProcess2 = new Process();
        parentProcess2.setProcessName("Test");
        parentProcess2.setDescription("Test Process");
        parentProcess2.setBusDomain(busDomain);
        parentProcess2.setProcessType(processType);
        parentProcess2.setAddTs(new Date());
        parentProcess2.setCanRecover(true);
        parentProcess2.setEnqueuingProcessId(0);
        parentProcess2.setProcess(null);
        parentProcess2.setNextProcessId("1");
        parentProcess2.setDeleteFlag(false);
        parentProcess2.setEditTs(new Date());
        parentProcess2.setWorkflowType(workflowType);
        Integer parentProcessId2 = processDAO.insert(parentProcess1);

        parentProcess2=processDAO.get(parentProcessId2);
        parentProcess2.setNextProcessId(subProcessId4.toString()+","+subProcessId5.toString());
        processDAO.update(parentProcess2);

        subProcess4=processDAO.get(subProcessId4);
        subProcess4.setProcess(parentProcess2);
        subProcess4.setEnqueuingProcessId(parentProcessId1);
        subProcess4.setNextProcessId(parentProcessId2.toString());
        processDAO.update(subProcess4);

        subProcess5=processDAO.get(subProcessId5);
        subProcess5.setProcess(parentProcess2);
        subProcess5.setEnqueuingProcessId(parentProcessId1);
        subProcess5.setNextProcessId(parentProcessId2.toString());
        processDAO.update(subProcess5);





         List<InitJobRowInfo> initJobRowInfos1 = jobDAO.initJob(parentProcessId1, 1);
        Long batchId1=initJobRowInfos1.get(0).getSourceBatchId();

        Long sub_instance_exec_id1 = stepDAO.initStep(subProcessId1);
        Long sub_instance_exec_id2 = stepDAO.initStep(subProcessId2);

        Long sub_instance_exec_id3 = stepDAO.initStep(subProcessId3);
        LOGGER.info("1st is "+sub_instance_exec_id1+"1st is "+sub_instance_exec_id2+"1st is "+sub_instance_exec_id3);

        stepDAO.haltStep(subProcessId1);

        stepDAO.haltStep(subProcessId2);

        stepDAO.haltStep(subProcessId3);

        jobDAO.haltJob(parentProcessId1,"parentProcessFirst");


        List<InitJobRowInfo> initJobRowInfos2 = jobDAO.initJob(parentProcessId2, 1);
        Long sub_instance_exec_id4 = stepDAO.initStep(subProcessId4);

        Long sub_instance_exec_id5 = stepDAO.initStep(subProcessId5);
        LOGGER.info(sub_instance_exec_id4+"1st is "+sub_instance_exec_id5);
        stepDAO.termStep(subProcessId4);


        stepDAO.haltStep(subProcessId5);



        //Long sub_instance_exec_id6 = stepDAO.initStep(subProcessId4);

       // stepDAO.haltStep(subProcessId4);

        jobDAO.haltJob(parentProcessId2,"parentProcessSecond");

        jobDAO.initJob(parentProcessId2, 1);
        Long sub_instance_exec_id6=stepDAO.initStep(subProcessId4);
        stepDAO.haltStep(subProcessId4);
       LOGGER.info("1st is "+sub_instance_exec_id6);
        jobDAO.haltJob(parentProcessId2,"parentProcessSecond");

       processDAO.delete(subProcessId1);
        processDAO.delete(subProcessId2);
        processDAO.delete(subProcessId3);
        processDAO.delete(subProcessId4);
        processDAO.delete(subProcessId5);
        processDAO.delete(parentProcessId1);
        processDAO.delete(parentProcessId2);
        busDomainDAO.delete(busDomainId);
*/



    }
}
