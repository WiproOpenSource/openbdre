/*
 * Copyright (c) 2014 Wipro Limited
 * All Rights Reserved
 * This code is protected by copyright and distributed under licenses restricting copying, distribution and decompilation.
 */

package com.wipro.ats.bdre.filemon;

import com.wipro.ats.bdre.IMConfig;
import com.wipro.ats.bdre.im.etl.api.base.ETLBase;
import com.wipro.ats.bdre.im.etl.api.exception.ETLException;
import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;

/**
 * Created by vishnu on 1/11/15.
 */
public class FileMonRunnableMain extends ETLBase {
    private static final Logger LOGGER = Logger.getLogger(FileMonRunnableMain.class);

    //Created a dummy PARAMS_STRUCTURE variable to get the environment value.
    private static final String[][] PARAMS_STRUCTURE = {
    };

    public static void main(String[] args) {
        FileMonRunnableMain f2SFileMonitorMain = new FileMonRunnableMain();
        f2SFileMonitorMain.execute(args);
    }

    private void execute(String[] params) {
        try {
            CommandLine commandLine = getCommandLine(params, PARAMS_STRUCTURE);
            String env = commandLine.getOptionValue("environment-id");

            FileMonRunnable fileMonRunnable = new FileMonRunnable();
            fileMonRunnable.setEnv(env);
            //mrt.setEnv(env);
            Thread t = new Thread(fileMonRunnable);
            t.start();
            long sleepTime = Long.parseLong(IMConfig.getProperty("file-mon.thread-wait", env));
            while (FileMonRunnable.runnableCount <= 10) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException iex) {
                    LOGGER.error(iex.getMessage());
                    throw new ETLException(iex);
                }
            }
        } catch (Exception err) {
            LOGGER.error(err.getMessage());
            throw new ETLException(err);
        }
    }
}
