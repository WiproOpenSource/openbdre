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

package com.wipro.ats.bdre.md.rest;

import com.wipro.ats.bdre.md.api.GetLineageByBatch;
import com.wipro.ats.bdre.md.api.GetLineageByInstanceExec;
import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.beans.GetLineageByBatchInfo;
import com.wipro.ats.bdre.md.beans.GetLineageByInstanceExecInfo;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by arijit on 1/9/15.
 */
@Controller
@RequestMapping("/lineage")


public class LineageAPI extends MetadataAPIBase {
    private static final Logger LOGGER = Logger.getLogger(LineageAPI.class);

    /**
     * This method returns the relationship between the processes linked to a batchId. It also generates the
     * dot string for visualisation.
     *
     * @param batchId
     * @return restWrapper It contains an instance of LineageInfo.
     */
    @RequestMapping(value = "/bybatch/{batchId}", method = RequestMethod.GET)
    public
    @ResponseBody
    RestWrapper getByBatch(@PathVariable("batchId") String batchId, Principal principal) {
        RestWrapper restWrapper = null;
        try {
            GetLineageByBatch getLineageByBatch = new GetLineageByBatch();
            String[] args = {"-bid", batchId};
            List<GetLineageByBatchInfo> lineageByBatchInfos = getLineageByBatch.execute(args);
            StringBuffer dot = new StringBuffer();
            Long sourceBatchId = null;
            Long instanceExecId = null;
            String processName = null;
            String processDesc = null;
            Long processId = null;
            Timestamp startTime = null;
            Timestamp endTime = null;
            boolean hasBatch = false;
            Integer instanceExecState = null;
            String tooltip = "";
            String inst_tooltip = "";


            for (GetLineageByBatchInfo lineageByBatchInfo : lineageByBatchInfos) {
                instanceExecId = lineageByBatchInfo.getInstanceExecId();
                processName = lineageByBatchInfo.getProcessName();
                processDesc = lineageByBatchInfo.getProcessDesc();
                processId = lineageByBatchInfo.getProcessId();
                startTime = lineageByBatchInfo.getStartTime();
                endTime = lineageByBatchInfo.getEndTime();
                sourceBatchId = lineageByBatchInfo.getSourceBatchId();
                instanceExecState = lineageByBatchInfo.getExecState();
                String label = "\"Batch# " + lineageByBatchInfo.getSourceBatchId() + "\"";
                if (sourceBatchId != null) {
                    hasBatch = true;
                    dot.append("\"" + lineageByBatchInfo.getSourceBatchId() + "b\"" + " [shape=point,fixedsize=true, height =0.2,width =0.2,style=filled,color=grey,tooltip = " + label + ",style=\"rounded\",penwidth=2,fontsize=8,URL=\"javascript:getBid(" + lineageByBatchInfo.getSourceBatchId() + ")\"];\n");
                    dot.append("\"" + lineageByBatchInfo.getSourceBatchId() + "b\"" + " -> \"" + instanceExecId + "e\"" + " [color=grey86,penwidth=2];\n");

                }
            }
            String color = "";
            String status = "";
            if (instanceExecState == 2) {
                color = "gold";
                status = "RUNNING";
            } else if (instanceExecState == 3) {
                color = "green";
                status = "SUCCESS";
            } else if (instanceExecState == 6) {
                color = "red";
                status = "FAILED";
            } else {
                color = "blue";
                status = "(NOT RUNNING)";
            }

            if (hasBatch) {
                SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

                StringBuffer InstanceExecLabel = new StringBuffer("<<TABLE ALIGN=\"LEFT\" CELLSPACING=\"4\" CELLPADDING=\"0\" BORDER=\"0\" WIDTH=\"100%\">" +
                        "<TR><TD><FONT POINT-SIZE=\"9\">Process Exec#" + "</FONT></TD></TR>" +
                        "<TR><TD><FONT POINT-SIZE=\"12\">" + instanceExecId + "</FONT></TD></TR>" +
                        "<TR><TD><FONT POINT-SIZE=\"9\">Process Name:" + "</FONT></TD></TR>" +
                        "<TR><TD><FONT POINT-SIZE=\"9\">" + processName.substring(0, Math.min(processName.length(), 25)) + "...</FONT></TD></TR>" +
                        "<TR><TD><FONT POINT-SIZE=\"9\"> Status:" + "</FONT></TD></TR>" +
                        "<TR><TD><FONT POINT-SIZE=\"12\">" + status + "...        </FONT></TD></TR>" +
                        "<TR><TD><FONT POINT-SIZE=\"9\">Process Id:" + "</FONT></TD></TR>" +
                        "<TR><TD><FONT POINT-SIZE=\"12\">" + processId + "</FONT></TD></TR>" +
                        "<TR><TD>Start Time:</TD></TR>");

                if (startTime == null) {
                    InstanceExecLabel.append("<TR><TD> NA </TD></TR>");
                } else {
                    InstanceExecLabel.append("<TR><TD>" + format.format(startTime) + "</TD></TR>");
                }

                InstanceExecLabel.append("<TR><TD>End Time:</TD></TR>");
                if (endTime == null) {
                    InstanceExecLabel.append("<TR><TD> NA </TD></TR>");
                } else {
                    InstanceExecLabel.append("<TR><TD>" + format.format(endTime) + "</TD></TR>");
                }
                InstanceExecLabel.append("<TR><TD COLOR=\"blue\"  href=\"javascript:popDetails(" + processId + "," + instanceExecId + ")\"><FONT COLOR=\"blue\" POINT-SIZE=\"8\">Details</FONT></TD></TR>" +
                        "</TABLE>>");
                inst_tooltip = "Process Description :" + processDesc;
                //Building dot
                dot.append("\"" + instanceExecId + "e\"" + " [label=" + InstanceExecLabel + ",shape=box,height=2.5,color=" + color + ",style=filled,fontcolor=black,style=\"rounded\",penwidth=2,fontsize=8,tooltip=\"" + inst_tooltip + "\"];\n");

                String batchLabel = "\"Batch# " + batchId + "\"";
                dot.append("\"" + batchId + "b\"" + " [shape=point,fixedsize=true, height =0.2,width =0.2,style=filled,color=grey,tooltip = " + batchLabel + ",style=\"rounded\",penwidth=2,fontsize=8,URL=\"javascript:getBid(" + batchId + ")\"];\n");
                dot.append("\"" + instanceExecId + "e\"" + " -> \"" + batchId + "b\"" + " [color=grey86,penwidth=2];");
            } else {
       /*     String batchLabel = "\"Batch# " + batchId + "\"";
              dot.append(batchId + " [label=" + batchLabel + ",shape=note,style=filled,fontcolor=black,style=\"rounded\",penwidth=2,fontsize=8,URL=\"javascript:getBid(" + batchId + ")\"];\n");
        */
                tooltip = "\"Batch " + batchId + " does not have any source batches\"";
                dot.append("NBCircle" + batchId + " [margin=\".11,.05\",label=NB,tooltip=" + tooltip + ",shape=circle,style=filled,penwidth=2,fontsize=10];\n");
                dot.append("NBCircle" + batchId + " -> " + "\"" + batchId + "b\"" + " [color=grey86,penwidth=2];");


            }
            LineageInfo lineageInfo = new LineageInfo();
            lineageInfo.setDot(dot.toString());
            lineageInfo.setBatchId(batchId);
            restWrapper = new RestWrapper(lineageInfo, RestWrapper.OK);
            LOGGER.info("Record with ID:" + batchId + " selected(getbyBatch) from Lineage by User:" + principal.getName());
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method is used to see the relationship between processes linked to a particular instanceExecId.
     * It also generates the dot string for visualisation of the links.
     *
     * @param instanceExecId
     * @return restWrapper It contains an instance of LineageInfo.
     */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)

    public
    @ResponseBody
    RestWrapper getByInstanceExec(@RequestParam(value = "ied", defaultValue = "0") String instanceExecId, Principal principal) {
        RestWrapper restWrapper = null;
        try {
            GetLineageByInstanceExec getLineageByInstanceExec = new GetLineageByInstanceExec();
            String[] args = {"-eid", instanceExecId};
            List<GetLineageByInstanceExecInfo> lineageByInstanceExecInfos = getLineageByInstanceExec.execute(args);
            LOGGER.info("size of list is :" + lineageByInstanceExecInfos.size());
            StringBuffer dot = new StringBuffer();
            Long targetBatchId = null;
            Date startTime = null;
            Date endTime = null;
            String processName = null;
            String processDesc = null;
            Long processId = null;
            Integer instanceExecState = null;
            String inst_tooltip = "";
            for (GetLineageByInstanceExecInfo lineageByInstanceExecInfo : lineageByInstanceExecInfos) {
                targetBatchId = lineageByInstanceExecInfo.getTargetBatchId();
                processId = lineageByInstanceExecInfo.getProcessId();
                processName = lineageByInstanceExecInfo.getProcessName();
                processDesc = lineageByInstanceExecInfo.getProcessDesc();
                startTime = lineageByInstanceExecInfo.getStartTime();
                endTime = lineageByInstanceExecInfo.getEndTime();
                instanceExecState = lineageByInstanceExecInfo.getExecState();
                String label = "\"Batch:" + lineageByInstanceExecInfo.getSourceBatchId() + "\"";
                dot.append("\"" + lineageByInstanceExecInfo.getSourceBatchId() + "b\"" + " [shape=point,fixedsize=true, height =0.2,width =0.2,style=filled,tooltip = \"+batchLabel+\",color=grey,style=\"rounded\",penwidth=2,fontsize=8,URL=\"javascript:getBid(" + lineageByInstanceExecInfo.getSourceBatchId() + ")\"];\n");
                dot.append("\"" + lineageByInstanceExecInfo.getSourceBatchId() + "b\"" + " -> " + "\"" + instanceExecId + "e\"" + " [color=grey86,penwidth=2];\n");
            }
            String color = "";
            String status = "";
            if (instanceExecState == 2) {
                color = "gold";
                status = "RUNNING";
            } else if (instanceExecState == 3) {
                color = "green";
                status = "SUCCESS";
            } else if (instanceExecState == 6) {
                color = "red";
                status = "FAILED";
            } else {
                color = "blue";
                status = "(NOT RUNNING)";
            }

            SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

            StringBuffer InstanceExecLabel = new StringBuffer("<<TABLE ALIGN=\"LEFT\" CELLSPACING=\"4\" CELLPADDING=\"0\" BORDER=\"0\" WIDTH=\"100%\">" +
                    "<TR><TD><FONT POINT-SIZE=\"9\">Instance Exec#" + "</FONT></TD></TR>" +
                    "<TR><TD><FONT POINT-SIZE=\"12\">" + instanceExecId + "</FONT></TD></TR>" +
                    "<TR><TD><FONT POINT-SIZE=\"9\">Process Name:" + "</FONT></TD></TR>" +
                    "<TR><TD><FONT POINT-SIZE=\"9\">" + processName.substring(0, Math.min(processName.length(), 20)) + "...</FONT></TD></TR>" +
                    "<TR><TD><FONT POINT-SIZE=\"9\"> Status:" + "</FONT></TD></TR>" +
                    "<TR><TD><FONT POINT-SIZE=\"12\">" + status + "...</FONT></TD></TR>" +
                    "<TR><TD><FONT POINT-SIZE=\"9\">Process Id:" + "</FONT></TD></TR>" +
                    "<TR><TD><FONT POINT-SIZE=\"12\">" + processId + "</FONT></TD></TR>" +
                    "<TR><TD>Start Time:</TD></TR>");

            if (startTime == null) {
                InstanceExecLabel.append("<TR><TD> NA </TD></TR>");
            } else {
                InstanceExecLabel.append("<TR><TD>" + format.format(startTime) + "</TD></TR>");
            }

            InstanceExecLabel.append("<TR><TD>End Time:</TD></TR>");
            if (endTime == null) {
                InstanceExecLabel.append("<TR><TD> NA </TD></TR>");
            } else {
                InstanceExecLabel.append("<TR><TD>" + format.format(endTime) + "</TD></TR>");
            }
            InstanceExecLabel.append("<TR><TD COLOR=\"blue\"  href=\"javascript:popDetails(" + processId + "," + instanceExecId + ")\"><FONT COLOR=\"blue\" POINT-SIZE=\"8\">Details</FONT></TD></TR>" +
                    "</TABLE>>");
            //Building dot

            inst_tooltip = "Process Description :" + processDesc;
            dot.append("\"" + instanceExecId + "e\"" + " [label=" + InstanceExecLabel + ",shape=box,height=2.5,color=" + color + ",style=filled,fontcolor=black,style=\"rounded\",penwidth=2,fontsize=8,tooltip=\"" + inst_tooltip + "\"];\n");

            String batchLabel = "\"Batch id:" + targetBatchId + "\"";
            dot.append("\"" + targetBatchId + "b\"" + " [shape=point,fixedsize=true, height =0.2,width =0.2,style=filled,color=grey,style=\"rounded\",penwidth=2,tooltip=" + batchLabel + ", fontsize=8,URL=\"javascript:getBid(" + targetBatchId + ")\"];\n");
            dot.append("\"" + instanceExecId + "e\"" + " -> " + "\"" + targetBatchId + "b\"" + " [color=grey86,penwidth=1];");
            LineageInfo lineageInfo = new LineageInfo();
            LOGGER.info(dot);
            lineageInfo.setDot(dot.toString());
            lineageInfo.setInstanceExecId(instanceExecId);
            restWrapper = new RestWrapper(lineageInfo, RestWrapper.OK);
            LOGGER.info("Record with ID:" + instanceExecId + " selected(getbyInstanceExec) from Lineage by User:" + principal.getName());

        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    @Override
    public Object execute(String[] params) {
        return null;
    }

    /**
     * This class is used to access the variables of LineageAPI.
     */
    private class LineageInfo {
        public String getBatchId() {
            return batchId;
        }

        public void setBatchId(String batchId) {
            this.batchId = batchId;
        }

        public String getInstanceExecId() {
            return instanceExecId;
        }

        public void setInstanceExecId(String instanceExecId) {
            this.instanceExecId = instanceExecId;
        }

        public String getDot() {
            return dot;
        }

        public void setDot(String dot) {
            this.dot = dot;
        }

        private String batchId;
        private String instanceExecId;
        private String dot;

    }

}
