package com.wipro.ats.bdre.augen;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by cloudera on 4/20/16.
 */
public class AnalyticUIGeneratorMainTest {

    @Test
    public void testMain() throws Exception {
        AnalyticUIGeneratorMain analyticUIGeneratorMain = new AnalyticUIGeneratorMain();
        String[] args = {"-p", "144","-u","admin"};
        analyticUIGeneratorMain.main(args);
    }
}