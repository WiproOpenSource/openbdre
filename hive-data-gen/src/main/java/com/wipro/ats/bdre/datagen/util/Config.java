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

package com.wipro.ats.bdre.datagen.util;

import com.wipro.ats.bdre.md.api.GetProperties;

import java.util.Properties;

/**
 * Created by arijit on 2/22/15.
 */
public class Config {


    public static final String NUM_SPLITS_KEY = "datagen.split.num";
    public static final  String SEPARATOR_KEY ="mapreduce.textoutputformat.separator" ;
    public static final String NUM_ROWS_KEY = "datagen.row.num";
    public static final String PID_KEY = "datagen.pid";

    private static Properties dataProperties=null;
    private static Properties tableProperties=null;

    private Config(){
    }

    public  static Properties getDataProperties(String pid) {
        if(dataProperties==null){
            GetProperties getProp=new GetProperties();
            dataProperties=getProp.getProperties(pid, "data");
        }
        return dataProperties;
    }

    public  static Properties getTableProperties(String pid) {
        if(tableProperties==null){
            GetProperties getProp=new GetProperties();
            tableProperties=getProp.getProperties(pid,"table");
        }
        return tableProperties;
    }

}
