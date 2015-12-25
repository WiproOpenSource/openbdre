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
import com.wipro.ats.bdre.md.beans.InitJobInfo;
import com.wipro.ats.bdre.md.beans.InitJobRowInfo;
import com.wipro.ats.bdre.md.dao.JobDAO;
import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.*;

/**
 * Created by arijit on 12/8/14.
 */


public class InitJob extends MetadataAPIBase {
    public InitJob() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    private static final Logger LOGGER = Logger.getLogger(InitJob.class);
    private static final String[][] PARAMS_STRUCTURE = {
            {"p", "process-id", "Process Id of the process to begin"},
            {"bmax", "max-batch", "Maximum no.of batches to be taken"},
    };

    /**
     * This method uses output from InitJob proc and extracts more data out of it and
     * retained by the VM at run time, so they may be read reflectively.
     *
     * @param initJobRowInfos This variable contains output of InitJob proc.
     * @return This method gives output of InitJob proc and retained by the VM at run time,
     * so they may be read reflectively.
     */

    public static InitJobInfo parseBean(List<InitJobRowInfo> initJobRowInfos) {
        //These variables will have same values for all rows
        Long instanceExecId = null;
        Long targetBatchId = null;


        //batchMap is map of sub-pid and list of batchids(Long) for that sub process.
        Map<Integer, List<Long>> batchMap = new HashMap<Integer, List<Long>>();
        //markingMap is map of sub-pid and list of batch markings(String) for that sub process.
        Map<Integer, List<String>> markingMap = new HashMap<Integer, List<String>>();
        //sourceInstanceExecIdMap is map of sub-pid and list of source-instance-exec-id(Long) of all batches for that sub process.
        Map<Integer, List<Long>> sourceInstanceExecIdMap = new HashMap<Integer, List<Long>>();
        //fileList is string of  list of file attached of all batches for that sub process.
        Map<Integer, List<String>> fileListMap = new HashMap<Integer, List<String>>();
        //fileList is string of  list of file attached of all batches for that sub process.

        InitJobInfo initJobInfo = new InitJobInfo();
        for (InitJobRowInfo initJobRowInfo : initJobRowInfos) {
            //instanceExecId,targetBatchId variables will have same values for all rows
            if (instanceExecId == null) {
                instanceExecId = initJobRowInfo.getInstanceExecId();
            }
            if (targetBatchId == null) {
                targetBatchId = initJobRowInfo.getTargetBatchId();
            }
            //For most cases(when the previous run is successful) LastRecoverableSpId is going to be null
            initJobInfo.setLastRecoverableSpId(initJobRowInfo.getLastRecoverableSpId());
            //calculate min max
            //Initially batchMap , markingMap are going to be empty
            // as there would be no sub-process id and corresponding batch or marking list populated

            List batchListForSubProcess = batchMap.get(initJobRowInfo.getProcessId());
            List markingListForSubProcess = markingMap.get(initJobRowInfo.getProcessId());
            List sourceInstanceExecIdListForSubProcess = sourceInstanceExecIdMap.get(initJobRowInfo.getProcessId());
            List fileListForSubProcess = sourceInstanceExecIdMap.get(initJobRowInfo.getProcessId());
            //If the subprocess does not yet have an entry we create an empty list
            if (batchListForSubProcess == null) {
                batchListForSubProcess = new ArrayList<Long>();
            }
            if (markingListForSubProcess == null) {
                markingListForSubProcess = new ArrayList<Long>();
            }
            if (sourceInstanceExecIdListForSubProcess == null) {
                sourceInstanceExecIdListForSubProcess = new ArrayList<Long>();
            }
            if (fileListForSubProcess == null) {
                fileListForSubProcess = new ArrayList<String>();
            }

            //Populate the map as we loop through each row
            batchMap.put(initJobRowInfo.getProcessId(), batchListForSubProcess);
            markingMap.put(initJobRowInfo.getProcessId(), markingListForSubProcess);
            sourceInstanceExecIdMap.put(initJobRowInfo.getProcessId(), sourceInstanceExecIdListForSubProcess);
            fileListMap.put(initJobRowInfo.getProcessId(), fileListForSubProcess);

            batchListForSubProcess.add(initJobRowInfo.getSourceBatchId());
            markingListForSubProcess.add(initJobRowInfo.getBatchMarking());
            if (initJobRowInfo.getSourceInstanceExecId() != null) {
                sourceInstanceExecIdListForSubProcess.add(initJobRowInfo.getSourceInstanceExecId());
            }
            if (initJobRowInfo.getFileList() != null || "".equals(initJobRowInfo.getFileList())) {
                fileListForSubProcess.add(initJobRowInfo.getFileList());
            }
        }
        //set these values that we captured earlier inside the loop
        initJobInfo.setTargetBatchId(targetBatchId);
        initJobInfo.setInstanceExecId(instanceExecId);
        //Now that we have a map that represents list of batchids group by subprocesses
        // Calculate min max batch ids by sorting the lists for each sub-process
        for (Integer subProcessId : batchMap.keySet()) {
            List<Long> batchList = batchMap.get(subProcessId);
            LOGGER.debug("batchList = " + batchList);
            Collections.sort(batchList);
            initJobInfo.getMinBatchIdMap().put(subProcessId.toString(), batchList.get(0).toString());
            initJobInfo.getMaxBatchIdMap().put(subProcessId.toString(), batchList.get(batchList.size() - 1).toString());
        }
        //Similar logic as min/max batchid
        //except the batch markings can be multiple comma separated markers.
        //uniqueMarkingSet is to return a batch marking for output(target) batch
        Set<String> uniqueMarkingSet = new HashSet<String>();
        for (Integer subProcessId : markingMap.keySet()) {
            List<String> markingList = markingMap.get(subProcessId);
            //This will store individual markers after extraction from the comma separated list.
            List<String> individualMarkingList = new ArrayList<String>();
            uniqueMarkingSet.addAll(markingList);
            LOGGER.debug("markingList = " + markingList);
            //To avoid null pointer exception
            if (markingList == null || markingList.isEmpty() || markingList.get(0) == null) {
                markingList = new ArrayList<String>();
                markingList.add(" ");
            }
            for (String marker : markingList) {
                String[] tokens = marker.split(",");
                for (String token : tokens) {
                    if (token != null) {
                        //Remove everything before @ using regex
                        token = token.replaceAll("^(.+@)", "");
                        individualMarkingList.add(token.trim());
                    }
                }
            }
            LOGGER.debug("For subprocess id=" + subProcessId + "; individualMarkingList=" + individualMarkingList);
            Collections.sort(individualMarkingList);
            initJobInfo.getMinBatchMarkingMap().put(subProcessId.toString(), individualMarkingList.get(0).toString());
            initJobInfo.getMaxBatchMarkingMap().put(subProcessId.toString(), individualMarkingList.get(individualMarkingList.size() - 1).toString());
            initJobInfo.setTargetBatchMarkingSet(uniqueMarkingSet);
        }
        //Now that we have a map that represents list of source_process_run_ids group by subprocesses
        // Calculate min max source-process-run-ids by sorting the lists for each sub-process

        for (Integer subProcessId : sourceInstanceExecIdMap.keySet()) {
            List<Long> instanceExecIdList = sourceInstanceExecIdMap.get(subProcessId);
            LOGGER.debug("instanceExecIdList = " + instanceExecIdList);
            //do not proceed if the src process run id is null for a given sub-process
            if (instanceExecIdList.isEmpty() || instanceExecIdList.get(0) == null || instanceExecIdList.get(instanceExecIdList.size() - 1) == null) {
                continue;
            }
            Collections.sort(instanceExecIdList);
            initJobInfo.getMinSourceInstanceExecIdMap().put(subProcessId.toString(), instanceExecIdList.get(0).toString());
            initJobInfo.getMaxSourceInstanceExecIdMap().put(subProcessId.toString(), instanceExecIdList.get(instanceExecIdList.size() - 1).toString());
        }

        for (Integer subProcessId : fileListMap.keySet()) {
            List<String> fileList = fileListMap.get(subProcessId);
            LOGGER.debug("fileList = " + fileList);
            if (fileList.isEmpty() || fileList.get(0) == null || fileList.get(fileList.size() - 1) == null) {
                continue;
            }
            initJobInfo.getFileListMap().put("FileList." + subProcessId.toString(), fileList.toString().replace("[", "").replace("]", "").replace(", ", ","));
        }

        return initJobInfo;
    }

    /**
     * This method starts InitJob proc in mysql.
     *
     * @param params String array contains maximum-batch, environment and process-id with their
     * command line notations.
     * @return This method return ouptut of InitJob proc as an instance of class InitJobRowInfo.
     */
    @Autowired
    private JobDAO jobDAO;

    public List<InitJobRowInfo> execute(String[] params) {
        List<InitJobRowInfo> initJobRowInfos = new ArrayList<InitJobRowInfo>();
//        SqlSession s = null;
        try {
            InitJobRowInfo initJobInfo = new InitJobRowInfo();
            CommandLine commandLine = getCommandLine(params, PARAMS_STRUCTURE);
            String pid = commandLine.getOptionValue("process-id");
            LOGGER.debug("processId is " + pid);
            String maxBId = commandLine.getOptionValue("max-batch");
            LOGGER.debug("maxBatch is " + maxBId);

            initJobInfo.setProcessId(Integer.parseInt(pid));
            initJobInfo.setMaxBatch(Integer.parseInt(maxBId));
//            initJobRowInfos = s.selectList("call_procedures.InitJob", initJobInfo);
            initJobRowInfos = jobDAO.initJob(initJobInfo.getProcessId(), initJobInfo.getMaxBatch());
            return initJobRowInfos;
        } catch (Exception e) {
            LOGGER.error("Error occurred", e);
            throw new MetadataException(e);
        }
    }
}
