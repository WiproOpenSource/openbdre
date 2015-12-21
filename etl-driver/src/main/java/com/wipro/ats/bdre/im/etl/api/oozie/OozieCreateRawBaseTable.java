/*
 * Copyright (c) 2014 Wipro Limited
 * All Rights Reserved
 * This code is protected by copyright and distributed under licenses restricting copying, distribution and decompilation.
 */

package com.wipro.ats.bdre.im.etl.api.oozie;

import com.wipro.ats.bdre.im.etl.api.CreateRawBaseTables;

/**
 * Created by Arijit on 12/24/14.
 */
public class OozieCreateRawBaseTable {

    public static void main(String[] args) {
        CreateRawBaseTables createRawBaseTables =new CreateRawBaseTables();
        createRawBaseTables.execute(args);
    }
}
