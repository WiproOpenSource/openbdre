/*
 * Copyright (c) 2014 Wipro Limited
 * All Rights Reserved
 *
 * This code is protected by copyright and distributed under
 * licenses restricting copying, distribution and decompilation.
 */

package com.wipro.ats.bdre.im.etl.api.sftp;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by arijit on 12/30/14.
 */
public class FTPUtil {
    public static final String KNOWN_HOSTS_FILE = "known_hosts";
    private static final Logger LOGGER = Logger.getLogger(FTPUtil.class);

    public static List<String> getKnownFootPrints() {


        List<String> knownFootPrints = new ArrayList<String>();
        try {

            File file = new File(KNOWN_HOSTS_FILE);
            //Create file if not exists
            if (!file.exists()) {
                file.createNewFile();
                LOGGER.info("Creating new file");
                return knownFootPrints;

            }

            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String s;
            while ((s = bufferedReader.readLine()) != null) {
                if (s.trim().isEmpty()) continue;
                knownFootPrints.add(s);
            }
            bufferedReader.close();
            fileReader.close();
        } catch (FileNotFoundException e) {

        } catch (IOException e) {
            LOGGER.error(e);
        }
        return knownFootPrints;
    }

    public static void addToKnownFootPrints(String footPrint) {
        if (footPrint == null || footPrint.isEmpty()) {
            return;
        }
        try {
            FileWriter fileWriter = new FileWriter(KNOWN_HOSTS_FILE, true);
            fileWriter.append(footPrint + "\n");
            fileWriter.close();
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    public static String extractFootPrint(String message) {
        String findText = "RSA key fingerprint is ";
        if (!message.contains(findText)) return "";
        //Meaasge is like below

        //The authenticity of host '192.168.56.101' can't be established.
        //RSA key fingerprint is 2b:e0:2b:60:e7:63:9d:8d:06:da:53:f8:b8:27:b0:5f.
        //Are you sure you want to continue connecting?

        message = message.substring(message.indexOf(findText) + findText.length(), message.length());
        //message is now
        //2b:e0:2b:60:e7:63:9d:8d:06:da:53:f8:b8:27:b0:5f.
        //Are you sure you want to continue connecting?

        message = message.substring(0, message.indexOf("\nAre you sure you want to continue connecting?"));
        //message is now 2b:e0:2b:60:e7:63:9d:8d:06:da:53:f8:b8:27:b0:5f.
        return message.trim();
    }
}
