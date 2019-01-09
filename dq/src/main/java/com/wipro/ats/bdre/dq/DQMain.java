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

package com.wipro.ats.bdre.dq;

import com.wipro.ats.bdre.BaseStructure;
import org.apache.commons.cli.CommandLine;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.ToolRunner;

/**
 * Created by arijit on 2/27/15.
 */
public class DQMain extends BaseStructure {
    private static final String[][] PARAMS_STRUCTURE = {
            {"p", "process-id", " Process id of the DQ job"},
            {"s", "source-file-path", "File path of the source file to be validated. It should be one single file or use list-of-files style input."},
            {"d", "destination-directory", "Directory directory"}

    };

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

            CommandLine commandLine = new DQMain().getCommandLine(args, PARAMS_STRUCTURE);
            String processId = commandLine.getOptionValue("process-id");
            String sPath = commandLine.getOptionValue("source-file-path");
            String destDir = commandLine.getOptionValue("destination-directory");

            int result = 0;
            if (sPath.indexOf(';') != -1 || sPath.indexOf(',') != -1) {
                String[] lof = sPath.split(";");
                String[] entries = lof[0].split(",");
                String[] params = {processId, entries[2], destDir};
                result = ToolRunner.run(new Configuration(), new DQDriver(), params);

            } else {
                String[] params = {processId, sPath, destDir};
                result = ToolRunner.run(new Configuration(), new DQDriver(), params);
            }
       if (result != 0)
           throw new DQValidationException(new DQStats());
    }
}
