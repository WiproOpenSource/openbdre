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
import com.wipro.ats.bdre.md.beans.GetLineageByBatchInfo;
import com.wipro.ats.bdre.md.dao.LineageByBatchDAO;
import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import java.util.List;

/**
 * This class gets list of Upstream and Downstream processes of a particular process.
 */
public class GetLineageByBatch extends MetadataAPIBase {

    private static final Logger LOGGER = Logger.getLogger(GetLineageByBatch.class);

    private static final String[][] PARAMS_STRUCTURE = {
            {"bid", "batch-id", " Target batch id whose lineage to be determined"},
    };

    @Autowired
    private LineageByBatchDAO lineageByBatchDAO;

    public GetLineageByBatch() {
        AutowireCapableBeanFactory acbFactory = getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    /**
     * This method gets list of all processes present in batch_consump_queue and archive_consump_queue
     * linked to a particular batch id.
     *
     * @param params String array having batch-id and  environment with their
     * command line notations.
     * @return This method returns list of all processes present in batch_consump_queue and archive_consump_queue
     * linked to a particular batch id.
     */

    @Override
    public List<GetLineageByBatchInfo> execute(String[] params) {
        List<GetLineageByBatchInfo> lineageByBatchInfos;

        try {

            CommandLine commandLine = getCommandLine(params, PARAMS_STRUCTURE);
            String bid = commandLine.getOptionValue("batch-id");
            LOGGER.debug("Batch id  is " + bid);

            GetLineageByBatchInfo getLineageByBatchInfo = new GetLineageByBatchInfo();
            getLineageByBatchInfo.setTargetBatchId(Long.parseLong(bid));
            //calling LineageByBatch

            lineageByBatchInfos = lineageByBatchDAO.lineageByBatch(getLineageByBatchInfo);
            LOGGER.info("Details of batch is " + lineageByBatchInfos);
            return lineageByBatchInfos;
        } catch (Exception e) {
            LOGGER.error("Error occurred", e);
            throw new MetadataException(e);
        }
    }

}

