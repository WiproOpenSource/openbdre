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

package com.wipro.ats.bdre.datagen;

import com.wipro.ats.bdre.BaseStructure;
import com.wipro.ats.bdre.datagen.mr.Driver;
import com.wipro.ats.bdre.exception.BDREException;
import com.wipro.ats.bdre.md.api.GetProperties;
import org.apache.commons.cli.CommandLine;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.ToolRunner;

/**
 * Created by arijit on 3/1/15.
 */
public class GeneratorMain extends BaseStructure {
    private static final String[][] PARAMS_STRUCTURE = {
            {"p", "sub-process-id", "Sub Process id of the step"},
    };
    public static void main(String[] args) throws Exception {
        CommandLine commandLine = new GeneratorMain().getCommandLine(args, PARAMS_STRUCTURE);
        String processId = commandLine.getOptionValue("sub-process-id");

        GetProperties getProperties=new GetProperties();
        java.util.Properties listForParams= getProperties.getProperties(processId,"table");
         String outputDir=listForParams.getProperty("outputPath");

        String[] params =new String[]{processId,outputDir};
        int res = ToolRunner.run(new Configuration(), new Driver(), params);
        if(res != 0)
            throw new BDREException("Hive Generator error");

    }
}
