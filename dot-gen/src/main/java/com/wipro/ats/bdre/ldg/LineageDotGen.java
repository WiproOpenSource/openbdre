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
package com.wipro.ats.bdre.ldg;

import com.wipro.ats.bdre.BaseStructure;
import com.wipro.ats.bdre.GetLineageQueryByProcessId;
import com.wipro.ats.bdre.lineage.LineageConstants;
import com.wipro.ats.bdre.lineage.LineageMain;
import com.wipro.ats.bdre.md.api.GetProcess;
import com.wipro.ats.bdre.md.beans.ProcessInfo;
import com.wipro.ats.bdre.md.dao.jpa.LineageQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

/**
 * Created by AshutoshRai on 1/18/16.
 */
public class LineageDotGen extends BaseStructure {

    private static final Logger LOGGER = LoggerFactory.getLogger(LineageDotGen.class);
    private static String defaultHiveDbName = LineageConstants.defaultHiveDbName;
    private static String subProcessId = "";

    public static void main(String[] args) throws Exception {
        if(args.length==0){
            LOGGER.info("No process ID provided. Running the LineageDotGen for all queries");
            getDot("EMPTY");
        } else {
            GetProcess getProcess = new GetProcess();
            List<ProcessInfo> subProcessList = getProcess.getSubProcesses(args);
            for (ProcessInfo processInfo : subProcessList) {
                subProcessId = processInfo.getProcessId().toString();
                LOGGER.info("subProcessId=" + subProcessId);
                getDot(subProcessId);
            }
        }
    }

    private static void getDot(String processId) {
        //get query from DB using process id and the latest instanceExecId
        GetLineageQueryByProcessId getLineageQueryByProcessId = new GetLineageQueryByProcessId();
        String[] args = {"-pid", processId};
        List<LineageQuery> lineageQueryList = getLineageQueryByProcessId.execute(args);

        for (LineageQuery lineageQuery:lineageQueryList) {
            LOGGER.debug("Query extracted from LineageQuery Table: " + lineageQuery.getQueryString());
        }

        for (LineageQuery lineageQuery:lineageQueryList) {
            String query = lineageQuery.getQueryString();
            LOGGER.info("The Query provided is: " + query);
            String instanceid= lineageQuery.getInstanceExecId().toString();

            //to select which HiveDB to use by default
            if (query.toLowerCase().startsWith("use")) {
                // ignore 'use db' line for lineage
                // use last split to determine the db
                String[] splits = query.split(" ");
                defaultHiveDbName = splits[splits.length - 1].toUpperCase();
                LOGGER.debug("DefaulHiveDbName is set to " + defaultHiveDbName);
            }

            try {
                LineageMain.lineageMain(lineageQuery, defaultHiveDbName, processId, instanceid);
            } catch (Exception e) {
                LOGGER.info("Error while calling LineageMain's main()" + e);
                LOGGER.error("context", e);
            }
        }
    }

}
