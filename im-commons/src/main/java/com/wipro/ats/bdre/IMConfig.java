/*
 * Copyright (c) 2014 Wipro Limited
 * All Rights Reserved
 *
 * This code is protected by copyright and distributed under
 * licenses restricting copying, distribution and decompilation.
 */

package com.wipro.ats.bdre;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

/**
 * Created by arijit on 12/28/14.
 */
public class IMConfig {
    private static Logger LOGGER=Logger.getLogger(IMConfig.class);
    private static XMLConfiguration config;
    private static String defaultEnv;
    private IMConfig() {
        try {
            config = new XMLConfiguration("im-config.xml");
            config.setExpressionEngine(new XPathExpressionEngine());
            defaultEnv= (String) config.getProperty("environments/default/@id");
            LOGGER.info("defaultEnv: " + defaultEnv);
        } catch (
                ConfigurationException cex) {
            LOGGER.error(cex);
        }
    }
    public static String getProperty(String key,String env){

        if(config==null){
            new IMConfig();
        }
        if(env==null || env.isEmpty())
        {
            Properties properties=new Properties();
            try {

                properties.load(IMConfig.class.getResourceAsStream("/ENVIRONMENT"));
                defaultEnv=properties.getProperty("environment");

            } catch (IOException e) {
                LOGGER.error("Please create ENVIRONMENT file in resources folder of im-commons project with content <environment=env1> in it");

            }

            env=defaultEnv;
            LOGGER.info("Environment set to "+env);
        }
        key = "environments/environment[@id=\""+env+"\"]/"+key.replaceAll("\\.","/");
        LOGGER.debug("fetching for: "+key);

        if(config.getProperty(key) instanceof List){
            String propVal="";
            for(String val: (List<String>)config.getProperty(key)) {
                propVal = propVal + "," +val;
            }
            return propVal;
        }
        return (String)config.getProperty(key);
    }

    //If the val is comma seperated the function will return it as a List<String>
    public static List<String> getPropertyList(String key,String env){

        if(config==null){
            new IMConfig();
        }
        if(env==null || env.isEmpty())
        {
            Properties properties=new Properties();
            try {
                properties.load(IMConfig.class.getResourceAsStream("ENVIRONMENT"));
                defaultEnv=properties.getProperty("environment");

            } catch (Exception e){
                LOGGER.error("Please create ENVIRONMENT file with content 'environment=<your environment>' in it");
            }
            env=defaultEnv;
            LOGGER.info("Environment set to "+env);
        }
        key = "environments/environment[@id=\""+env+"\"]/"+key.replaceAll("\\.","/");
        LOGGER.debug("fetching for: "+key);
        if(config.getProperty(key) instanceof List){
            return  (List<String>)config.getProperty(key);
        }
        else{
            List<String> props=new ArrayList<String>();
            props.add((String)config.getProperty(key));
            return props;
        }

    }
}
