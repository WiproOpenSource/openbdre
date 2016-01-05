package com.wipro.ats.bdre;

import org.apache.log4j.Logger;

/**
 * Created by AS294216 on 1/5/2016.
 */
public class resolvePath {
    private static Logger LOGGER=Logger.getLogger(resolvePath.class);

    public static String getResolvedPath (String path) {
        LOGGER.debug("path before resolving: "+path);

        LOGGER.debug("path after resolving: "+path);
        return path;
    }
}
