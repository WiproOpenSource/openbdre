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

package com.wipro.ats.bdre.im.etl.api.sftp;

import com.wipro.ats.bdre.BaseStructure;
import com.wipro.ats.bdre.IMConfig;
import com.wipro.ats.bdre.im.etl.api.exception.ETLException;
import com.wipro.ats.bdre.md.api.*;
import com.wipro.ats.bdre.md.beans.InitJobInfo;
import com.wipro.ats.bdre.md.beans.InitJobRowInfo;
import com.wipro.ats.bdre.md.beans.InitStepInfo;
import com.wipro.ats.bdre.md.beans.FileInfo;
import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Set;

/**
 * Created by arijit on 12/30/14.
 */
public class SFTP2HDFSMain extends BaseStructure {
    private static final Logger LOGGER = Logger.getLogger(SFTP2HDFSMain.class);
    private static final int MAX_NUM_BATCH = 1;
    private static final String[][] PARAMS_STRUCTURE = {
            {"pid", "process-id", "Parent process id of the file fetcher process"},
            {"spid", "sub-process-id", "Sub process id of the file fetcher single sub-process"},
            {"hsid", "hdfs-server-id", "Server id of the DFS file system"},
    };

    public static void main(String[] args) {
        //since there is no oozie API for this creating an instance of itself.
        CommandLine commandLine = new SFTP2HDFSMain().getCommandLine(args, PARAMS_STRUCTURE);
        String pid = commandLine.getOptionValue("process-id");
        String subPid = commandLine.getOptionValue("sub-process-id");
        String hdfsServerId = commandLine.getOptionValue("hdfs-server-id");


        String destPrefix = IMConfig.getProperty("etl.hdfs-raw-directory");

        InitJob initJob = new InitJob();
        String[] initJobParam = {"--max-batch", MAX_NUM_BATCH + "", "--process-id", pid};
        String[] termJobParam = {"--process-id", pid};

        InitJobInfo initJobInfo = null;
        try {
            LOGGER.info("InitJob Started for pid=" + pid);
            List<InitJobRowInfo> initJobRowInfo = initJob.execute(initJobParam);
            initJobInfo = InitJob.parseBean(initJobRowInfo);
            LOGGER.info("InitJob Complete for " + pid + "; Target batch = " + initJobInfo.getTargetBatchId() + "; min batch " + initJobInfo.getMinBatchIdMap() + "; max batch " + initJobInfo.getMaxBatchIdMap());
        } catch (Exception e) {
            LOGGER.error(e);
                LOGGER.info("TermJob started for " + pid);
            TermJob termJob = new TermJob();
            termJob.execute(termJobParam);
            LOGGER.info("TermJob completed for " + pid);
            throw new ETLException(e);
        }

        String[] initHaltStepsParam = new String[]{"--sub-process-id", subPid};

        try {
            LOGGER.info("InitStep started for " + subPid);
            InitStep initStep = new InitStep();
            InitStepInfo initStepInfo = initStep.execute(initHaltStepsParam);
            LOGGER.info("InitStep completed. Exec id " + initStepInfo.getSubInstanceExecId());
        } catch (Exception e) {
            LOGGER.error(e);
            LOGGER.info("TermJob started for " + pid);
            TermJob termJob = new TermJob();
            termJob.execute(termJobParam);
            LOGGER.info("TermJob completed for " + pid);
            throw new ETLException(e);

        }
        //maxB and minB are the same as it's one batch/file at a time
        String minB = initJobInfo.getMinBatchIdMap().get(subPid);
        String[] sftpParam = {"--maxB", minB, "--minB", minB};

        try {
            LOGGER.info("Starting file download/upload job for " + pid);
            SFTP2HDFS sftp2HDFS = new SFTP2HDFS();
            sftp2HDFS.execute(sftpParam);
            Long targetBatchId = initJobInfo.getTargetBatchId();

            CopyFile copyFile = new CopyFile();
            String[] param = new String[]{"-dsid", hdfsServerId, "-sbid", minB, "-dbid", targetBatchId.toString(), "-prefix", destPrefix};
            FileInfo fileInfo = copyFile.execute(param);
            LOGGER.info("Copied file updated in metadata " + fileInfo);

        } catch (Exception e) {
            LOGGER.error(e);
            TermStep termStep = new TermStep();
            termStep.execute(initHaltStepsParam);
            LOGGER.info("TermStep completed. for " + subPid);
            LOGGER.info("TermJob started for " + pid);
            TermJob termJob = new TermJob();
            termJob.execute(termJobParam);
            LOGGER.info("TermJob completed for " + pid);
            throw new ETLException(e);
        }

        try {
            HaltStep haltStep = new HaltStep();
            haltStep.execute(initHaltStepsParam);
        } catch (Exception e) {
            LOGGER.error(e);
            TermStep termStep = new TermStep();
            termStep.execute(initHaltStepsParam);
            LOGGER.info("TermStep completed. for " + subPid);
            LOGGER.info("TermJob started for " + pid);
            TermJob termJob = new TermJob();
            termJob.execute(termJobParam);
            LOGGER.info("TermJob completed for " + pid);
            throw new ETLException(e);
        }
        Set<String> targetBatchMarkingSet = initJobInfo.getTargetBatchMarkingSet();
        LOGGER.info("batch marking is set " + targetBatchMarkingSet);
        String targetMarking = "";
        if (targetBatchMarkingSet != null) {
            for (String marking : targetBatchMarkingSet) {
                targetMarking = targetMarking + marking + ",";
            }
        }
        String[] haltJobParam = {"--process-id", pid, "--batch-marking", targetMarking};

        try {
            HaltJob haltJob = new HaltJob();
            haltJob.execute(haltJobParam);
        } catch (Exception e) {
            LOGGER.error(e);
            LOGGER.info("TermJob started for " + pid);
            TermJob termJob = new TermJob();
            termJob.execute(termJobParam);
            LOGGER.info("TermJob completed for " + pid);
            throw new ETLException(e);
        }
    }

}
