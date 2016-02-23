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
//    private static XMLConfiguration config;
//    private static String defaultEnv;
//    private IMConfig() {
//        try {
//            config = new XMLConfiguration("im-config.xml");
//            config.setExpressionEngine(new XPathExpressionEngine());
//            defaultEnv= (String) config.getProperty("environments/default/@id");
//            LOGGER.info("defaultEnv: " + defaultEnv);
//        } catch (
//                ConfigurationException cex) {
//            LOGGER.error(cex);
//        }
//    }
//    public static String getProperty(String key,String env){
//
//        if(config==null){
//            new IMConfig();
//        }
//        if(env==null || env.isEmpty())
//        {
//            Properties properties=new Properties();
//            try {
//
//                properties.load(IMConfig.class.getResourceAsStream("/ENVIRONMENT"));
//                defaultEnv=properties.getProperty("environment");
//
//            } catch (IOException e) {
//                LOGGER.error("Please create ENVIRONMENT file in resources folder of im-commons project with content <environment=env1> in it");
//
//            }
//
//            env=defaultEnv;
//            LOGGER.info("Environment set to "+env);
//        }
//        key = "environments/environment[@id=\""+env+"\"]/"+key.replaceAll("\\.","/");
//        LOGGER.debug("fetching for: "+key);
//
//        if(config.getProperty(key) instanceof List){
//            String propVal="";
//            for(String val: (List<String>)config.getProperty(key)) {
//                propVal = propVal + "," +val;
//            }
//            return propVal;
//        }
//        return (String)config.getProperty(key);
//    }

    //If the val is comma seperated the function will return it as a List<String>
//    public static List<String> getPropertyList(String key,String env){
//
//        if(config==null){
//            new IMConfig();
//        }
//        if(env==null || env.isEmpty())
//        {
//            Properties properties=new Properties();
//            try {
//                properties.load(IMConfig.class.getResourceAsStream("ENVIRONMENT"));
//                defaultEnv=properties.getProperty("environment");
//
//            } catch (Exception e){
//                LOGGER.error("Please create ENVIRONMENT file with content 'environment=<your environment>' in it");
//            }
//            env=defaultEnv;
//            LOGGER.info("Environment set to "+env);
//        }
//        key = "environments/environment[@id=\""+env+"\"]/"+key.replaceAll("\\.","/");
//        LOGGER.debug("fetching for: "+key);
//        if(config.getProperty(key) instanceof List){
//            return  (List<String>)config.getProperty(key);
//        }
//        else{
//            List<String> props=new ArrayList<String>();
//            props.add((String)config.getProperty(key));
//            return props;
//        }
//
//    }

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
