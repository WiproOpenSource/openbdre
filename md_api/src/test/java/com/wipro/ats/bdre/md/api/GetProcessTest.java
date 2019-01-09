package com.wipro.ats.bdre.md.api;

import com.wipro.ats.bdre.md.beans.ProcessInfo;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by su324335 on 9/18/16.
 */
public class GetProcessTest {
    private static final Logger LOGGER = Logger.getLogger(GetPropertiesTest.class);

    @Ignore
    @Test
    public void testGetParentProcess() throws Exception {
        GetProcess getProcess = new GetProcess();
        ProcessInfo processInfo = getProcess.getParentProcess(335);
        LOGGER.info(processInfo.toString());
    }
}
