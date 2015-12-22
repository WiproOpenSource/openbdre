/*
 * Copyright (c) 2014 Wipro Limited
 * All Rights Reserved
 * This code is protected by copyright and distributed under licenses restricting copying, distribution and decompilation.
 */

package com.wipro.ats.bdre.filemon;

import com.wipro.ats.bdre.IMConfig;
import com.wipro.ats.bdre.im.etl.api.exception.ETLException;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.impl.DefaultFileMonitor;
import org.apache.log4j.Logger;
import java.util.List;


/**
 * Created by vishnu on 1/11/15.
 */
public class FileMonRunnable implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(FileMonRunnable.class);
    public static int runnableCount = 0;

    @Override
    public void run() {
        try {
            FileSystemManager fsManager = VFS.getManager();
            //Reading directory paths and adding to the DefaultFileMonitor
            List<String> dirPaths = IMConfig.getPropertyList("file-mon.dirs");

            DefaultFileMonitor fm = new DefaultFileMonitor(FileMonitor.getInstance());
            FileObject listendir = null;
            for (String dir : dirPaths) {
                LOGGER.debug("Monitoring directories "+dir);
                listendir = fsManager.resolveFile(dir);
                fm.setRecursive(true);
                fm.addFile(listendir);
            }
            fm.start();
        } catch (Exception err) {
            LOGGER.error(err.toString());
            throw new ETLException(err);
        }
    }


}