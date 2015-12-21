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

package com.wipro.ats.bdre.md.rest.ext;

import com.wipro.ats.bdre.md.api.ArrangePositions;
import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.rest.RestWrapper;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.Map;

/**
 * Created by RI294200 on 10/5/2015.
 */
@Controller
@RequestMapping("/arrangepositions")


public class ArrangePositionsAPI extends MetadataAPIBase {
    private static final Logger LOGGER = Logger.getLogger(ArrangePositionsAPI.class);

    /**
     * This method calls proc GetSubProcesses and returns a record corresponding to the processid passed.
     *
     * @param
     * @return restWrapper It contains an instance of SubProcess corresponding to processid passed.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)

    public
    @ResponseBody
    RestWrapper get(
            @PathVariable("id") Integer processId, Principal principal
    ) {

        ArrangePositions arrangePositions = new ArrangePositions();
        Map<String, ArrangePositions.PositionsInfo> positionsInfoList = arrangePositions.getListPositionInfo(processId);
        LOGGER.info("Record with ID:" + processId + " arranged by User:" + principal.getName());
        RestWrapper restWrapper = new RestWrapper(positionsInfoList, RestWrapper.OK);
        return restWrapper;

    }

    @Override
    public Object execute(String[] params) {
        return null;
    }
}