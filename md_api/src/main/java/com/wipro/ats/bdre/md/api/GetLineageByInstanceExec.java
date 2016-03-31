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
import com.wipro.ats.bdre.md.beans.GetLineageByInstanceExecInfo;
import com.wipro.ats.bdre.md.dao.LineageByInstanceExecDAO;
import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import java.util.List;

/**
 * Created by IshitaParekh on 28-01-2015.
 */
public class GetLineageByInstanceExec extends MetadataAPIBase {

    private static final Logger LOGGER = Logger.getLogger(GetLineageByInstanceExec.class);

    private static final String[][] PARAMS_STRUCTURE = {
            {"eid", "instance-exec-id", " Instance exec id whose lineage to be determined"},
    };

    @Autowired
    private LineageByInstanceExecDAO lineageByInstanceExecDAO;

    public GetLineageByInstanceExec() {
        AutowireCapableBeanFactory acbFactory = getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    /**
     * This method gets list of all processes present in batch_consump_queue and archive_consump_queue
     * linked to a particular instance exec id.
     *
     * @param params String array having batch-id and  environment with their
     * command line notations.
     * @return This method returns list of all processes present in batch_consump_queue and archive_consump_queue
     * linked to a particular instance exec id.
     */

    @Override
    public List<GetLineageByInstanceExecInfo> execute(String[] params) {
        List<GetLineageByInstanceExecInfo> lineageByInstanceExecInfos;
        try {

            CommandLine commandLine = getCommandLine(params, PARAMS_STRUCTURE);
            String eid = commandLine.getOptionValue("instance-exec-id");
            LOGGER.debug("Instance exec id  is " + eid);


            GetLineageByInstanceExecInfo getLineageByInstanceExecInfo = new GetLineageByInstanceExecInfo();
            getLineageByInstanceExecInfo.setInstanceExecId(Long.parseLong(eid));
            //calling proc LineageByInstanceExec
            lineageByInstanceExecInfos = lineageByInstanceExecDAO.LineageByInstanceExec(getLineageByInstanceExecInfo);
            LOGGER.debug("Details of batch is " + lineageByInstanceExecInfos);
            return lineageByInstanceExecInfos;
        } catch (Exception e) {
            LOGGER.error("Error occurred", e);
            throw new MetadataException(e);
        }
    }


}

