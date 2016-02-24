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

package com.wipro.ats.bdre;

import com.wipro.ats.bdre.md.api.GetGeneralConfig;
import com.wipro.ats.bdre.md.beans.table.GeneralConfig;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arijit on 12/28/14.
 */
public class IMConfig {
    private static final Logger LOGGER=Logger.getLogger(IMConfig.class);
    private IMConfig(){
    }

    public static List<String> getPropertyList(String key) {

        LOGGER.debug("fetching for: " + key);
        GetGeneralConfig getGeneralConfig = new GetGeneralConfig();
        GeneralConfig generalConfig = getGeneralConfig.byConigGroupAndKey("imconfig", key);
        List<String> propetiesList = new ArrayList<String>();
        if (generalConfig.getDefaultVal().contains(",")){
            for (String s : generalConfig.getDefaultVal().split(",")){
                propetiesList.add(s);
            }
        }
        return propetiesList;
    }

    public static String getProperty(String key) {

        LOGGER.debug("fetching for: " + key);
        GetGeneralConfig getGeneralConfig = new GetGeneralConfig();
        GeneralConfig generalConfig = getGeneralConfig.byConigGroupAndKey("imconfig", key);

        return generalConfig.getDefaultVal();
    }
}
