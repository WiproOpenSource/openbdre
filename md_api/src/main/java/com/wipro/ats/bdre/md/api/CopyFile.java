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
import com.wipro.ats.bdre.md.beans.CopyFileInfo;
import com.wipro.ats.bdre.md.beans.FileInfo;
import com.wipro.ats.bdre.md.dao.CopyFileDAO;
import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;


/**
 * Created by arijit on 12/8/14.
 */
public class CopyFile extends MetadataAPIBase {

    @Autowired
    CopyFileDAO copyFileDAO;
    private static final Logger LOGGER = Logger.getLogger(CopyFile.class);
    private static final String[][] PARAMS_STRUCTURE = {
            {"dsid", "destination-server-id", "Destination server id"},
            {"sbid", "source-batch-id", "Source batch id"},
            {"dbid", "destination-batch-id", "Destination batch id"},
            {"prefix", "destination-path-prefix", "Destination path prefix"}
    };

    public CopyFile() {
        AutowireCapableBeanFactory acbFactory = getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    /**
     * This method runs CopyFile proc for a batchid and make an entry in file table.
     *
     * @param params String array containing destination-server-id,source-batch-id,destination-batch-id,
     *               destination-path-prefix,env with their respective notation on command line.
     * @return This method return output of CopyFile proc having information regarding
     * file and server specifications.
     */

    public FileInfo execute(String[] params) {
        try {
            CommandLine commandLine = getCommandLine(params, PARAMS_STRUCTURE);
            int dsid = Integer.parseInt(commandLine.getOptionValue("destination-server-id"));
            LOGGER.debug("destination-server-id is " + dsid);
            Long sbid = Long.parseLong(commandLine.getOptionValue("source-batch-id"));
            LOGGER.debug("source-batch-id is " + sbid);
            Long dbid = Long.parseLong(commandLine.getOptionValue("destination-batch-id"));
            LOGGER.debug("destination-batch-id is " + dbid);
            String prefix = commandLine.getOptionValue("destination-path-prefix");
            LOGGER.debug("destination-batch-id is " + prefix);

            //Polpulating copyfileInfo bean to pass as parameter
            CopyFileInfo copyFileInfo = new CopyFileInfo();
            copyFileInfo.setDestBatchId(dbid);
            copyFileInfo.setDestPrefix(prefix);
            copyFileInfo.setDestServerId(dsid);
            copyFileInfo.setSourceBatchId(sbid);
            //This takes CopyFileInfo as input and emits FileInfo bean as output

            return copyFileDAO.copyFile(copyFileInfo);
        } catch (Exception e) {
            LOGGER.error("Error occurred", e);
            throw new MetadataException(e);
        }
    }
}
