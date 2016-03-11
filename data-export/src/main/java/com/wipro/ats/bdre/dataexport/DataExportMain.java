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

package com.wipro.ats.bdre.dataexport;

import com.wipro.ats.bdre.BaseStructure;
import com.wipro.ats.bdre.im.etl.api.exception.ETLException;
import com.wipro.ats.bdre.md.api.GetProperties;
import org.apache.commons.cli.CommandLine;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;

import java.util.Properties;

/**
 * Created by MI294210 on 05-02-2015.
 */
public class DataExportMain extends BaseStructure {

    private static final Logger LOGGER = Logger.getLogger(DataExportMain.class);

    //Created PARAMS_STRUCTURE variable to get the environment value.
    private static final String[][] PARAMS_STRUCTURE = {
            {"p", "process-id", " Process id of the import process which requires properties"},
            {"bid", "batch-id", " Target batch id of the process"},
            {"cg", "config-group", "Configuration group of the process to run "},
            {"eid", "instance-exec-id", "Instance execution id of the running process "}
    };

    public static void main(String[] args) {
        DataExportMain dataImportMain = new DataExportMain();
        dataImportMain.execute(args);

    }

    private void execute(String[] params) {
        try {
            CommandLine commandLine = getCommandLine(params, PARAMS_STRUCTURE);
            String processId = commandLine.getOptionValue("process-id");
            String configGroup = commandLine.getOptionValue("config-group");
            String batchId = commandLine.getOptionValue("batch-id");
            String instanceExecId = commandLine.getOptionValue("instance-exec-id");
            //fetching common export properties
            GetProperties getProperties = new GetProperties();
            Properties commonProperties = getProperties.getProperties(processId, configGroup);

            String[] param = {processId, batchId,instanceExecId};
            ToolRunner.run(new Configuration(), new HDFSExport(commonProperties), param);


        } catch (Exception e) {
            LOGGER.error(e);
            throw new ETLException(e);
        }
    }


}


