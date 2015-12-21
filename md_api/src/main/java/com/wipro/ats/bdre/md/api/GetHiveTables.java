/*
 * Copyright 2015 Wipro Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import com.wipro.ats.bdre.md.beans.GetHiveTablesInfo;
import com.wipro.ats.bdre.md.dao.ETLDriverDAO;
import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

/**
 * This class calls GetETLDriverTables proc and fetches information regarding tables present
 * in hive metastore in a particular order.
 */
public class GetHiveTables extends MetadataAPIBase {
    public GetHiveTables() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    private static final Logger LOGGER = Logger.getLogger(GetHiveTables.class);

    private static final String[][] PARAMS_STRUCTURE = {
            {"p", "process-id", " Process id of ETLDriver"},
    };

    /**
     * This method calls GetETLDriverTables proc and fetches information regarding tables present
     * in hive metastore in a particular order.
     *
     * @param params String array having environment and process-id with their command line notations.
     * @return The return of this method is instance of class GetHiveTablesInfo having information regarding tables present
     * in hive metastore in a particular order from hive_tables.
     */
    @Autowired
    private ETLDriverDAO etlDriverDAO;

    public List<GetHiveTablesInfo> execute(String[] params) {
        List<GetHiveTablesInfo> hivePropertyList;
        try {
            GetHiveTablesInfo getHiveTablesInfo = new GetHiveTablesInfo();
            CommandLine commandLine = getCommandLine(params, PARAMS_STRUCTURE);
            String pid = commandLine.getOptionValue("process-id");
            LOGGER.debug("process-id  is " + pid);


            //Calling proc GetETLDriverTables
//            hivePropertyList = s.selectList("call_procedures.GetHiveTablesETL", getHiveTablesInfo);
            hivePropertyList = etlDriverDAO.getETLDriverTables(Integer.parseInt(pid));
            LOGGER.debug(hivePropertyList);
            for (GetHiveTablesInfo info : hivePropertyList) {
                if (info == null) {
                    LOGGER.warn("No items retrieved from call_procedures.GetHiveTablesETL for pid=" + pid);
                    break;
                }
                LOGGER.debug("Got props from HiveTable: " + info.getTableId() + "," + info.getComment() + "," + info.getLocationType() + "," + info.getDbName() + "," + info.getBatchIdPartitionCol() + "," + info.getTableName() + "," + info.getType() + info.getDdl());
            }

            return hivePropertyList;
        } catch (Exception e) {
            LOGGER.error("Error occurred", e);
            throw new MetadataException(e);
        }
    }

}

