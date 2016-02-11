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
package com.wipro.ats.bdre.pdeploy;

import com.wipro.ats.bdre.MDConfig;
import com.wipro.ats.bdre.exception.MetadataException;
import com.wipro.ats.bdre.md.api.HaltDeploy;
import com.wipro.ats.bdre.md.api.InitDeploy;
import com.wipro.ats.bdre.md.api.TermDeploy;
import com.wipro.ats.bdre.md.beans.table.ProcessDeploymentQueue;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.log4j.Logger;

/**
 * Created by MI294210 on 9/2/2015.
 */

/**
 * ProcessDeploy class accepts the ProcessDeploymentQueue bean and deploys it.
 */
public class ProcessDeploy implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(ProcessDeploy.class);
    ProcessDeploymentQueue pdq;


    /**
     * Constructor to initialise the class variables
     *
     * @param pdq
     */
    public ProcessDeploy(ProcessDeploymentQueue pdq) {
        this.pdq = pdq;
    }

    /**
     * This method overrides the run() method of Thread, the script path is fetched and then InitDeploy is called. If the script is executed
     * successfully, the HaltDeploy is called else TermDeploy is called.
     *
     * @param
     * @return
     */
    @Override
    public void run() {
        //obtain the script path from the bean, if null then fetch  the default script (busdomainId/processTypeId/fetch the script name from config file)
        LOGGER.debug("PDQ in run:" + pdq.getDeploymentId());
        if (pdq.getDeployScriptLocation() == null) {
            pdq.setDeployScriptLocation(MDConfig.getProperty("deploy.script-path") + "/job-deployer.sh");
            LOGGER.debug(pdq.getDeployScriptLocation());
        }
        String[] params = {"--deployment-id", pdq.getDeploymentId().toString()};
        //using Apache Commons Exec library
        int iExitValue;
        String sCommandString;
        String command = "sh " + pdq.getDeployScriptLocation() + " " + pdq.getBusDomainId() + " " + pdq.getProcessTypeId() + " " + pdq.getProcessId();
        sCommandString = command;
        CommandLine oCmdLine = CommandLine.parse(sCommandString);
        LOGGER.debug("executing command with deploymentId=" + pdq.getDeploymentId());
        LOGGER.debug("executing command :" + command);

        DefaultExecutor oDefaultExecutor = new DefaultExecutor();
        oDefaultExecutor.setExitValue(0);

        //In try block calling InitDeploy java api and then running the script
        try {

            InitDeploy initDeploy = new InitDeploy();
            initDeploy.execute(params);

            iExitValue = oDefaultExecutor.execute(oCmdLine);

            //If the script is successful then call HaltDeploy
            HaltDeploy haltDeploy = new HaltDeploy();
            haltDeploy.execute(params);

        } catch (Exception e) {
            //If error occurs in catch block calling the TermDeploy
            TermDeploy termDeploy = new TermDeploy();
            termDeploy.execute(params);
            LOGGER.error("Error occurred", e);
            throw new MetadataException(e);
        }

    }
}
