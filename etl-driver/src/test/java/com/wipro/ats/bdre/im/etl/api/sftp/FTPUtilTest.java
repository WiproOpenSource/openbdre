/*
 * Copyright (c) 2014 Wipro Limited
 * All Rights Reserved
 *
 * This code is protected by copyright and distributed under
 * licenses restricting copying, distribution and decompilation.
 */

package com.wipro.ats.bdre.im.etl.api.sftp;

import org.junit.Test;

import static org.junit.Assert.*;

public class FTPUtilTest {

    @Test
    public void testExtractFootPrint() throws Exception {
        String message="The authenticity of host '192.168.56.101' can't be established.\n" +
                "RSA key fingerprint is 2b:e0:2b:60:e7:63:9d:8d:06:da:53:f8:b8:27:b0:5f.\n" +
                "Are you sure you want to continue connecting?";
        assertEquals(FTPUtil.extractFootPrint(message),"2b:e0:2b:60:e7:63:9d:8d:06:da:53:f8:b8:27:b0:5f.");
    }
}