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

import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.beans.LineageNodeInfo;
import com.wipro.ats.bdre.md.dao.GetDotStringDAO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SR294224 on 6/8/2015.
 */
@Controller
@RequestMapping("/columnlineage")
public class ColumnLineageAPI extends MetadataAPIBase {
    private static final Logger LOGGER = Logger.getLogger(ColumnLineageAPI.class);
    @Autowired
    GetDotStringDAO getDotStringDAO;

    /**
     * This method calls proc GetDotString and fetches the dot string to show the column level lineage
     * graphically by querying the Data lineage tables corresponding to processId passed.
     *
     * @param processId
     * @return restWrapper It contains an instance of LineageNodeInfo which has the Dot String
     * corresponding to processId passed.
     */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    @ResponseBody public
    RestWrapper lineageByProcessId(@RequestParam(value = "pid", defaultValue = "0") Integer processId, Principal principal) {

        RestWrapper restWrapper = null;
        try {

            List<LineageNodeInfo> lineageNodeInfo = new ArrayList<LineageNodeInfo>();
            List<String> dotStringList = getDotStringDAO.getDotString(processId);
            for (String dotString : dotStringList) {
                LineageNodeInfo returnLineageNodeInfo = new LineageNodeInfo();
                returnLineageNodeInfo.setDotString(dotString);
                lineageNodeInfo.add(returnLineageNodeInfo);
            }
            String finalDot = "";
            for (LineageNodeInfo rowLevelDot : lineageNodeInfo) {
                finalDot += rowLevelDot.getDotString().substring(0, rowLevelDot.getDotString().length() - 1);
            }
            LineageNodeInfo lineageNodeInfo1 = new LineageNodeInfo();
            lineageNodeInfo1.setDotString(finalDot);


            restWrapper = new RestWrapper(lineageNodeInfo1, RestWrapper.OK);
            LOGGER.info("lineageByProcessId for processId:" + processId + "from ColumnLineage by User:" + principal.getName());
        } catch (Exception e) {
            LOGGER.error( e);
            return new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    @Override
    public Object execute(String[] params) {
        return null;
    }
}
