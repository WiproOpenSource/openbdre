package com.wipro.ats.bdre.pm;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by cloudera on 6/6/16.
 */
public class PluginManagerMainTest {
    @Ignore
    @Test
    public void testMain() throws Exception {
        PluginManagerMain pluginManagerMain = new PluginManagerMain();
        pluginManagerMain.main(new String[]{"-p","/home/cloudera/plugin.zip"});
    }
}