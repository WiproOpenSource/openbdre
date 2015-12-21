/*
 * Copyright (c) 2014 Wipro Limited
 * All Rights Reserved
 * This code is protected by copyright and distributed under licenses restricting copying, distribution and decompilation.
 */

package com.wipro.ats.bdre.im.etl.api.exception;

import com.wipro.ats.bdre.exception.BDREException;

/**
 * Created by vishnu on 12/19/14.
 */
public class ETLException extends BDREException {
    public ETLException() {
        super();
    }

    public ETLException(String msg) {
        super(msg);
    }

    public ETLException(Exception e) {
        super(e);
    }

    public ETLException(String msg, Exception e) {
        super(msg, e);
    }
}
