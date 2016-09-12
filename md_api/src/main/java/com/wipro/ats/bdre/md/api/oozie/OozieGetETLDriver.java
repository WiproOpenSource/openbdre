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

package com.wipro.ats.bdre.md.api.oozie;

import com.wipro.ats.bdre.exception.MetadataException;
import com.wipro.ats.bdre.md.api.GetETLDriver;
import com.wipro.ats.bdre.md.beans.GetETLDriverInfo;
import com.wipro.ats.bdre.util.OozieUtil;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by leela on 12-12-2014.
 */
public class OozieGetETLDriver {
    private static final Logger LOGGER = Logger.getLogger(OozieInitJob.class);

    /**
     * default constructor
     */
    private OozieGetETLDriver() {

    }

    /**
     * This method calls execute method and persist the output till runtime.
     *
     * @param args String array having minimum-batch-id, maximum-batch-id, environment with their command line notations.
     */
    public static void main(String[] args) {
        GetETLDriver fc = new GetETLDriver();
        GetETLDriverInfo getETLDriverInfo = fc.execute(args);
        OozieUtil oozieUtil = new OozieUtil();
        try {
            oozieUtil.persistBeanData(getETLDriverInfo, false);

            try
            {
               // String processId = args[1];
                String homeDir = System.getProperty("user.home");
                LOGGER.info("home Directory is "+homeDir);
                Files.deleteIfExists(Paths.get(homeDir + "/bdre/airflow/etldriverInfo.txt"));
                FileWriter fw = new FileWriter(homeDir+"/bdre/airflow/etldriverInfo.txt", true);
                BufferedWriter bw = new BufferedWriter(fw);

                if(getETLDriverInfo.getMinBatch() != null)
                    bw.write("getETLDriverInfo.getMinBatch()::"+getETLDriverInfo.getMinBatch().toString()+"\n");
                else
                    bw.write("getETLDriverInfo.getMinBatch()::null\n");

                if(getETLDriverInfo.getMaxBatch() != null)
                    bw.write("getETLDriverInfo.getMaxBatch()::"+getETLDriverInfo.getMaxBatch().toString()+"\n");
                else
                    bw.write("getETLDriverInfo.getMaxBatch()::null\n");

                if(getETLDriverInfo.getFileList() != null)
                    bw.write("getETLDriverInfo.getFileList()::"+getETLDriverInfo.getFileList().toString()+"\n");
                else
                    bw.write("getETLDriverInfo.getFileList()::null\n");

                bw.close();

            }catch(IOException i) {
                i.printStackTrace();
            }
        } catch (Exception e) {
            LOGGER.error(e);
            throw new MetadataException(e);
        }
    }
}
