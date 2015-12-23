/*
 * Copyright (c) 2014 Wipro Limited
 * All Rights Reserved
 * This code is protected by copyright and distributed under licenses restricting copying, distribution and decompilation.
 */

package com.wipro.ats.bdre.filemon;

import com.wipro.ats.bdre.im.etl.api.exception.ETLException;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.impl.DefaultFileMonitor;
import org.apache.log4j.Logger;


/**
 * Created by vishnu on 1/11/15.
 */
public class QueueConsumerRunnable implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(QueueConsumerRunnable.class);

    @Override
    public void run() {
        try {
           while(true){
               QueuedFileUploader.executeCopyProcess();
               //TODO: Use what u are capturing from the UI
               Thread.sleep(1000);
           }

        } catch (Exception err) {
            LOGGER.error(err.toString());
            throw new ETLException(err);
        }
    }


}