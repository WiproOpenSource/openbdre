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

import java.util.List;

/**
 * Created by MI294210 on 9/2/2015.
 */

/**
 * This class calls FetchDeploy proc and fetches the rows to be deployed. It updates the deploy_status_id of the fetched
 * rows to picked from not started.
 */
public class FetchDeploy extends MetadataAPIBase {

    @Autowired
    FetchDeployDAO fetchDeployDAO;

    private static final Logger LOGGER = Logger.getLogger(FetchDeploy.class);
    private static final String[][] PARAMS_STRUCTURE = {
            {"num", "fetch-num", "Fetch limit"}
    };

    public FetchDeploy() {
        AutowireCapableBeanFactory acbFactory = getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    /**
     * @param params Sting array of Command line arguments.
     * @return
     */
    @Override
    public List<ProcessDeploymentQueue> execute(String[] params) {
        try {
            CommandLine commandLine = getCommandLine(params, PARAMS_STRUCTURE);
            String fetchNum = commandLine.getOptionValue("fetch-num");
            LOGGER.info("fetch-num is " + fetchNum);

            return fetchDeployDAO.fetchDeploy(Integer.parseInt(fetchNum));
        } catch (Exception e) {
            LOGGER.error("Error occurred", e);
            throw new MetadataException(e);
        }
    }

}
