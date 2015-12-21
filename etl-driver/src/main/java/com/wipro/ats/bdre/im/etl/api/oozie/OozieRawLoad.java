/*
 * Copyright (c) 2014 Wipro Limited
 * All Rights Reserved
 * This code is protected by copyright and distributed under licenses restricting copying, distribution and decompilation.
 */

package com.wipro.ats.bdre.im.etl.api.oozie;

import com.wipro.ats.bdre.im.etl.api.RawLoad;

/**
 * Created by vishnu on 12/24/14.
 */
public class OozieRawLoad {

    public static void main(String[] args) {
        RawLoad rawLoad = new RawLoad();
        rawLoad.execute(args);
    }
}
