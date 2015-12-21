/*
 * Copyright (c) 2014 Wipro Limited
 * All Rights Reserved
 *
 * This code is protected by copyright and distributed under
 * licenses restricting copying, distribution and decompilation.
 */

package com.wipro.ats.bdre.hiveplugin.hook;

import org.junit.Ignore;
import org.junit.Test;

public class LineageHookTest {
    @Ignore
    @Test
    public void testRun() {
        LineageHook lineageHook=new LineageHook();
        lineageHook.run(null);

        assert lineageHook.isSuccess();
    }
}