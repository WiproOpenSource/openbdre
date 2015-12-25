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
import com.wipro.ats.bdre.md.beans.table.IntermediateInfo;
import com.wipro.ats.bdre.md.dao.IntermediateDAO;
import com.wipro.ats.bdre.md.dao.jpa.IntermediateId;
import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

/**
 * Created by KA294215 on 03-04-2015.
 */
public class Intermediate extends MetadataAPIBase {
    public Intermediate() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    private static final Logger LOGGER = Logger.getLogger(Intermediate.class);
    private static final String[][] PARAMS_STRUCTURE = {
            {"uuid", "uuid", "unique identity for key value pair"},
            {"key", "key", "key"},
            {"value", "value", "value"},
    };
    @Autowired
    IntermediateDAO intermediateDAO;

    /**
     * This method runs InsertIntermediate proc and stores intermediate properties as key value pairs which is linked
     * with a unique uuid.
     *
     * @param params String array containing uuid.key,value and environment with their respective notation on command line.
     * @return This method returns list of key value pairs.
     */

    public IntermediateInfo execute(String[] params) {

        try {
            IntermediateInfo intermediateInfo = new IntermediateInfo();
            CommandLine commandLine = getCommandLine(params, PARAMS_STRUCTURE);
            String uuid = commandLine.getOptionValue("uuid");
            LOGGER.debug("UUID is " + uuid);
            String key = commandLine.getOptionValue("key");
            LOGGER.debug("key is " + key);
            String value = commandLine.getOptionValue("value");
            LOGGER.debug("value is " + value);


            intermediateInfo.setUuid(uuid);
            intermediateInfo.setKey(key);
            intermediateInfo.setValue(value);
            //intermediateInfo = s.selectOne("call_procedures.InsertIntermediate", intermediateInfo);

            com.wipro.ats.bdre.md.dao.jpa.Intermediate intermediate = new com.wipro.ats.bdre.md.dao.jpa.Intermediate();
            IntermediateId intermediateId = new IntermediateId();
            intermediateId.setInterKey(key);
            intermediateId.setUuid(uuid);

            intermediate.setId(intermediateId);
            intermediate.setInterValue(value);

            intermediateDAO.insert(intermediate);

            return intermediateInfo;
        } catch (Exception e) {
            LOGGER.error("Error occurred", e);
            throw new MetadataException(e);
        }
    }

    /**
     * This method runs InsertIntermediate proc and stores intermediate properties as key value pairs which is linked
     * with a unique uuid.
     *
     * @param intermediateInfos List of instance of IntermediateInfo.
     * @return nothing.
     */
    public void keyValue(List<IntermediateInfo> intermediateInfos) {

        try {
            for (IntermediateInfo intermediateInfo : intermediateInfos) {

                //    s.selectOne("call_procedures.InsertIntermediate", intermediateInfo);
                com.wipro.ats.bdre.md.dao.jpa.Intermediate intermediate = new com.wipro.ats.bdre.md.dao.jpa.Intermediate();
                IntermediateId intermediateId = new IntermediateId();
                intermediateId.setInterKey(intermediateInfo.getKey());
                intermediateId.setUuid(intermediateInfo.getUuid());

                intermediate.setId(intermediateId);
                intermediate.setInterValue(intermediateInfo.getValue());

                intermediateDAO.insert(intermediate);
            }
        } catch (Exception e) {
            LOGGER.error("Error occurred", e);
            throw new MetadataException(e);
        }
    }
}
