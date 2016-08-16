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
import com.wipro.ats.bdre.md.api.InitJob;
import com.wipro.ats.bdre.md.beans.InitJobInfo;
import com.wipro.ats.bdre.md.beans.InitJobRowInfo;
import com.wipro.ats.bdre.util.OozieUtil;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.List;


/**
 * Created by arijit on 12/11/14.
 */
public class OozieInitJob {
    private static final Logger LOGGER = Logger.getLogger(OozieInitJob.class);

    /**
     * default constructor
     */
    private OozieInitJob() {

    }

    /**
     * This method calls execute method and parseBean method to refine output of execute method and
     * persist the output till runtime.
     *
     * @param args String array contains maximum-batch, environment and process-id with their
     *             command line notations.
     */
    public static void main(String[] args) {
        InitJob bp = new InitJob();
        List<InitJobRowInfo> initJobRowInfos = bp.execute(args);
        InitJobInfo initJobInfo = InitJob.parseBean(initJobRowInfos);
        OozieUtil oozieUtil = new OozieUtil();
        try {
            oozieUtil.persistBeanData(initJobInfo, false);
            try
            {
                String homeDir = System.getProperty("user.home");
                FileWriter fw = new FileWriter(homeDir+"/jobinfo.txt", true);
                BufferedWriter bw = new BufferedWriter(fw);

                bw.write("initJobInfo.getInstanceExecId():"+initJobInfo.getInstanceExecId().toString()+"\n");
                bw.write("initJobInfo.getLastRecoverableSpId():"+initJobInfo.getLastRecoverableSpId().toString()+"\n");
                bw.write("initJobInfo.getTargetBatchId():"+initJobInfo.getTargetBatchId().toString()+"\n");
                bw.write("initJobInfo.getMinBatchIdMap():"+initJobInfo.getMinBatchIdMap().toString().replace("=", ":")+"\n");
                bw.write("initJobInfo.getMaxBatchIdMap():"+initJobInfo.getMaxBatchIdMap().toString().replace("=",":")+"\n");
                bw.write("initJobInfo.getMinBatchMarkingMap():"+initJobInfo.getMinBatchMarkingMap().toString().replace("=", ":")+"\n");
                bw.write("initJobInfo.getMaxBatchMarkingMap():"+initJobInfo.getMaxBatchMarkingMap().toString().replace("=", ":")+"\n");
                bw.write("initJobInfo.getTargetBatchMarkingSet():"+initJobInfo.getTargetBatchMarkingSet().toString()+"\n");
                bw.write("initJobInfo.getMinSourceInstanceExecIdMap():"+initJobInfo.getMinSourceInstanceExecIdMap().toString().replace("=", ":")+"\n");
                bw.write("initJobInfo.getMaxSourceInstanceExecIdMap():"+initJobInfo.getMaxSourceInstanceExecIdMap().toString().replace("=", ":")+"\n");
                bw.write("initJobInfo.getFileListMap():"+initJobInfo.getFileListMap().toString().replace("=", ":")+"\n");
                bw.write("initJobInfo.getBatchListMap():"+initJobInfo.getBatchListMap().toString().replace("=",":")+"\n");

                bw.close();

            }catch(IOException i)
            {
                i.printStackTrace();
            }
        } catch (Exception e) {
            LOGGER.error(e);
            throw new MetadataException(e);
        }
    }


}
