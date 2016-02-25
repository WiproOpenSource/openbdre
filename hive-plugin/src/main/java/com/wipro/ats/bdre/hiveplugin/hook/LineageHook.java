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

package com.wipro.ats.bdre.hiveplugin.hook;

import com.wipro.ats.bdre.lineage.LineageConstants;
import com.wipro.ats.bdre.lineage.LineageProcessor;
import org.apache.hadoop.hive.ql.hooks.HookContext;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.Properties;

/**
 * Created by arijit on 6/5/15.
 */
public class LineageHook implements org.apache.hadoop.hive.ql.hooks.ExecuteWithHookContext {
	private static final Logger LOGGER = Logger.getLogger(LineageHook.class);
	private boolean successFlag;
	public boolean isSuccess() {
		return successFlag;
	}

	@Override
	public void run(HookContext hookContext) {

		LOGGER.info("Running LineageHook...");
		String wholeQuery = null;
		String processId = null;
		String instanceExecId = null;

	    try {
		    if (hookContext == null) {
			    LOGGER.warn("HookContext is null. Assuming default values");
			    wholeQuery = LineageConstants.query;
			    processId = "" + LineageConstants.processId;
			    instanceExecId = "" + LineageConstants.instanceId;

		    } else {
			    wholeQuery = hookContext.getQueryPlan().getQueryString();

			    Properties properties = hookContext.getConf().getAllProperties();
			    for (Map.Entry entry : properties.entrySet()) {
				    String key = (String)(entry.getKey());
				    if (LineageConstants.processIdString.equalsIgnoreCase(key)) {
					    processId = (String) (entry.getValue());
					    LOGGER.info("ProcessId found in Hive = " + processId);
				    } else if (LineageConstants.instanceExecIdString.equalsIgnoreCase(key)) {
					    instanceExecId = (String) (entry.getValue());
					    LOGGER.info("InstanceExecId found in Hive = " + instanceExecId);
				    }
			    }

			    if (processId == null || instanceExecId == null) {
				    LOGGER.info("Warning: ProcessId or InstanceExecId is null. Lineage not done. End of Lineage Hook.");
				    return;
			    }

		    }
		    LOGGER.info("BDRE Printed query = " + wholeQuery);

		    LineageProcessor lineageProcessor = new LineageProcessor();
		    lineageProcessor.execute(wholeQuery, processId, instanceExecId);
			successFlag = true;

	    } catch (Exception e) {
		    LOGGER.error("Error in executing LineageHook", e);
			e.printStackTrace(System.out);
		    successFlag = false;
	    }
    }
}
