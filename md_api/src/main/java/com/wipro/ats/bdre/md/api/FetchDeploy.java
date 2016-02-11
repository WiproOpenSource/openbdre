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
package com.wipro.ats.bdre.md.api;

import com.wipro.ats.bdre.exception.MetadataException;
import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.beans.table.ProcessDeploymentQueue;
import com.wipro.ats.bdre.md.dao.FetchDeployDAO;
import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

/**
 * Created by MI294210 on 9/2/2015.
 */

/**
 * This class calls FetchDeploy proc and fetches the rows to be deployed. It updates the deploy_status_id of the fetched
 * rows to picked from not started.
 */
public class FetchDeploy extends MetadataAPIBase {
    public FetchDeploy() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    @Autowired
    FetchDeployDAO fetchDeployDAO;

    private static final Logger LOGGER = Logger.getLogger(FetchDeploy.class);
    private static final String[][] PARAMS_STRUCTURE = {
            {"num", "fetch-num", "Fetch limit"}
    };

    /**
     * @param params Sting array of Command line arguments.
     * @return
     */
    public List<ProcessDeploymentQueue> execute(String[] params) {
        try {
            ProcessDeploymentQueue processDeploymentQueue = new ProcessDeploymentQueue();
            CommandLine commandLine = getCommandLine(params, PARAMS_STRUCTURE);
            String fetchNum = commandLine.getOptionValue("fetch-num");
            LOGGER.debug("fetch-num is " + fetchNum);

            // List<ProcessDeploymentQueue> processDeploymentQueues = s.selectList("call_procedures.FetchDeploy", processDeploymentQueue);

            List<ProcessDeploymentQueue> processDeploymentQueues = fetchDeployDAO.fetchDeploy(Integer.parseInt(fetchNum));
            return processDeploymentQueues;
        } catch (Exception e) {
            LOGGER.error("Error occurred", e);
            throw new MetadataException(e);
        }
    }

}
