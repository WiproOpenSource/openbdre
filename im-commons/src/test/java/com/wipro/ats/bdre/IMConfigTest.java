/*
 * Copyright (c) 2014 Wipro Limited
 * All Rights Reserved
 *
 * This code is protected by copyright and distributed under
 * licenses restricting copying, distribution and decompilation.
 */

package com.wipro.ats.bdre;

import org.apache.log4j.Logger;
import org.junit.Test;

public class IMConfigTest {
    private Logger LOGGER=Logger.getLogger(IMConfigTest.class);
    @Test
    public void testGetProperty() throws Exception {
        String env=null;
        String key = "etl.hive-connection";

        LOGGER.info(key+"="+IMConfig.getProperty(key,env));
    }

   @Test
    public void testGetProperty1() throws Exception {
        String env=null;
        String key = "etl.hive-connection";

        LOGGER.info(key+"="+IMConfig.getProperty(key,env));

    }
}