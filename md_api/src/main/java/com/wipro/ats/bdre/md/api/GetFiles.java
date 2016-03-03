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
import com.wipro.ats.bdre.md.beans.FileInfo;
import com.wipro.ats.bdre.md.dao.FileDAO;
import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import java.util.List;

/**
 * Created by arijit on 12/8/14.
 */
public class GetFiles extends MetadataAPIBase {

    @Autowired
    FileDAO fileDAO;

    private static final Logger LOGGER = Logger.getLogger(GetFiles.class);
    private static final String[][] PARAMS_STRUCTURE = {
            {"minB", "min-batch-id", "minimum batch id"},
            {"maxB", "max-batch-id", "maximum batch id"}
    };

    public GetFiles() {
        AutowireCapableBeanFactory acbFactory = getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }
    /**
     * This method runs GetFile proc for some batch-id between mininmum and maximum batch id
     * and return corresponding file and their server specification.
     *
     * @param params String array containing minimum-batch-id,maximum-batch-id,
     *               env with their respective notation on command line.
     * @return This method return output of GetFile proc having information regarding
     * files and their server specifications.
     */
    public List<FileInfo> execute(String[] params) {

        try {
            FileInfo fileInfo = new FileInfo();
            CommandLine commandLine = getCommandLine(params, PARAMS_STRUCTURE);
            String minBId = commandLine.getOptionValue("min-batch-id");
            LOGGER.debug("minimum-batch-id is " + minBId);
            String maxBId = commandLine.getOptionValue("max-batch-id");
            LOGGER.debug("maximum-batch-id is " + maxBId);

            fileInfo.setMinBatch(Long.parseLong(minBId));
            fileInfo.setMaxBatch(Long.parseLong(maxBId));
            //calling GetFiles

            return fileDAO.getFiles(Long.parseLong(minBId), Long.parseLong(maxBId));
        } catch (Exception e) {
            LOGGER.error("Error occurred", e);
            throw new MetadataException(e);
        }
    }
}
