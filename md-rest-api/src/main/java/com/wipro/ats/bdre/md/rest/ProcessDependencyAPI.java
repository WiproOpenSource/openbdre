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

import com.wipro.ats.bdre.exception.MetadataException;
import com.wipro.ats.bdre.md.api.GetProcessDependency;
import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.beans.ProcessDependencyInfo;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.List;

/**
 * Created by arijit on 1/9/15.
 */
@Controller
@RequestMapping("/prodep")


public class ProcessDependencyAPI extends MetadataAPIBase {
    private static final Logger LOGGER = Logger.getLogger(ProcessDependencyAPI.class);


    /**
     * This method is used to show the dependency of Processes. It is used to graphically show the upstream and downstream
     * processes of a given processId using dot string.
     *
     * @param processId
     * @return restWrapper It contains an instance of LineageInfo.
     */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    public
    @ResponseBody
    RestWrapper get(
            @RequestParam(value = "pid", defaultValue = "0") String processId, Principal principal
    ) {
        RestWrapper restWrapper = null;
        try {
            StringBuffer dot = new StringBuffer();
            String[] args = {"-p", processId};
            GetProcessDependency bs = new GetProcessDependency();
            List<ProcessDependencyInfo> processDependencyInfoList = bs.execute(args);
            LineageInfo lineageInfo = new LineageInfo();
            String borderColor = "black";
        /*
        1	sftp
        2	Semantic
        3	Export
        4	Import
        5	f2c
        6	f2s
        7	s2p
        8	p2c
         */
            String tooltip = "";
            boolean hasDownStream = false;
            boolean hasUpStream = false;

            Integer pid = new Integer(processId);
            for (ProcessDependencyInfo processDependencyInfo : processDependencyInfoList) {
                switch (processDependencyInfo.getProcessTypeId()) {
                    case 1:
                        borderColor = "green";
                        break;
                    case 2:
                        borderColor = "brown";
                        break;
                    case 3:
                        borderColor = "aqua";
                        break;
                    case 4:
                        borderColor = "pink";
                        break;
                    case 5:
                        borderColor = "purple";
                        break;
                    case 6:
                        borderColor = "gray";
                        break;
                    case 7:
                        borderColor = "yellow";
                        break;
                }
                tooltip = "Description: " + processDependencyInfo.getDescription() + " Added on:" + processDependencyInfo.getAddTS();
                //String label = "\"" + processDependencyInfo.getProcessName()+ "\n(" +processDependencyInfo.getProcessId() +")" + "\"";
                String label = "<<TABLE CELLSPACING=\"4\" CELLPADDING=\"0\" BORDER=\"0\" WIDTH=\"100%\"><TR><TD COLSPAN=\"3\">" + processDependencyInfo.getProcessName().replace("&", "&amp;") +
                        "</TD></TR><TR><TD COLSPAN=\"3\">" + processDependencyInfo.getProcessId() + "</TD></TR>" +
                        "<TR>" +
                        "<TD COLOR=\"blue\"  href=\"javascript:popModal(" + processDependencyInfo.getProcessId() + ")\"><FONT COLOR=\"blue\" POINT-SIZE=\"8\">Diagram </FONT></TD>" +
                        "<TD COLOR=\"blue\"  href=\"javascript:popModalXml(" + processDependencyInfo.getProcessId() + ")\"><FONT COLOR=\"blue\" POINT-SIZE=\"8\"> XML </FONT></TD>" +
                        "<TD COLOR=\"blue\"  href=\"javascript:GotoProcess(" + processDependencyInfo.getProcessId() + ")\"><FONT COLOR=\"blue\" POINT-SIZE=\"8\"> Details</FONT></TD></TR></TABLE>>";

                //String label = "<" + processDependencyInfo.getProcessName()+ "\n(" +processDependencyInfo.getProcessId() +")" + ">";

                dot.append(processDependencyInfo.getProcessId() + " [label=" + label + " tooltip=\"" + tooltip + "\"shape=rectangle,style=filled,fontcolor=black,color=" + borderColor + ",style=\"rounded\",penwidth=2,fontsize=10,URL=\"javascript:getPid(" + processDependencyInfo.getProcessId() + ")\"];\n");
                //Checking if the process is an upstream or downstream
                if ("D".equals(processDependencyInfo.getRowType())) {
                    dot.append(pid + " -> " + processDependencyInfo.getProcessId() + ";\n");
                    hasDownStream = true;
                } else if ("U".equals(processDependencyInfo.getRowType())) {
                    dot.append(processDependencyInfo.getProcessId() + " -> " + pid + ";\n");
                    hasUpStream = true;
                }

            }
            if (!hasDownStream) {
                tooltip = "\"No more downstream for " + pid + " in the pipeline\"";
                dot.append("end" + pid + " [margin=\".11,.05\",label=ND,tooltip=" + tooltip + ",shape=circle,style=filled,penwidth=2,fontsize=10];\n");
                dot.append(pid + " -> end" + pid + "[size=1,arrowhead=none,color=gray,penwidth=.5] ;\n");
            }
            if (!hasUpStream) {
                tooltip = "\"Process " + pid + " does not have any upstream in the pipeline\"";
                dot.append("start" + pid + " [margin=\".11,.05\",label=NU,tooltip=" + tooltip + ",shape=circle,style=filled,penwidth=2,fontsize=10];\n");
                dot.append("\"start" + pid + "\" -> " + pid + "[size=1,arrowhead=none,color=gray,penwidth=.5];\n");
            }
            dot.append("");
            lineageInfo.setDot(dot.toString());
            lineageInfo.setPid(processId);
            restWrapper = new RestWrapper(lineageInfo, RestWrapper.OK);
            LOGGER.info("Record with ID:" + processId + " selected from LineageInfo by User:" + principal.getName());

        } catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }


    @Override
    public Object execute(String[] params) {
        return null;
    }

    /**
     *
     */
    private class LineageInfo {
        public String getPid() {
            return pid;
        }

        public void setPid(String pid) {
            this.pid = pid;
        }

        public String getDot() {
            return dot;
        }

        public void setDot(String dot) {
            this.dot = dot;
        }

        private String pid;
        private String dot;

    }

}





