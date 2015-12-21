/*
 * Copyright (c) 2014 Wipro Limited
 * All Rights Reserved
 *
 * This code is protected by copyright and distributed under
 * licenses restricting copying, distribution and decompilation.
 */

package com.wipro.ats.bdre.datagen.util;

import com.wipro.ats.bdre.md.api.GetProperties;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by arijit on 2/22/15.
 */
public class Config {
    public static final String NUM_SPLITS_KEY = "datagen.split.num";;
    public static final  String SEPARATOR_KEY ="mapreduce.textoutputformat.separator" ;
    public static final String NUM_ROWS_KEY = "datagen.row.num";
    public static final String PID_KEY = "datagen.pid";

    private static Properties dataProperties=null;
    private static Properties tableProperties=null;



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
