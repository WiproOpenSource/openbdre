/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wipro.ats.bdre.md.triggers;

import com.wipro.ats.bdre.exception.MetadataException;
import com.wipro.ats.bdre.md.dao.jpa.Process;
import com.wipro.ats.bdre.md.dao.jpa.ProcessType;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ProcessValidateInsertTest {
    private static final Logger LOGGER = Logger.getLogger(ProcessValidateInsertTest.class);

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    @Test
    @Ignore
    public void testProcessVal() throws Exception {

        try {
            Process process = new Process();
            process.setProcessId(10805);
            ProcessType processType = new ProcessType();
            processType.setProcessTypeId(14);
            Process parentProcess = new Process();
            parentProcess.setProcessId(10802);
            process.setProcessType(processType);
            process.setProcess(parentProcess);

            Process parentProcessCheck = new Process();
            parentProcessCheck.setProcessId(10802);
            ProcessType parentProcessType = new ProcessType();
            parentProcessType.setProcessTypeId(18);
            parentProcessCheck.setProcessType(parentProcessType);
            parentProcessCheck.setProcess(null);
            ProcessValidateInsert processValidateInsert = new ProcessValidateInsert();
            processValidateInsert.ProcessTypeValidator(process, parentProcessCheck);
        } catch (MetadataException e) {
            LOGGER.info("error in testing " + e);
        }
    }
}