package com.wipro.ats.bdre.imcrawler.model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

/**
 * Created by AS294216 on 08-09-2015.
 */
public class PMF {

    private static PersistenceManagerFactory factory;

    protected PMF() {
    }

    public static PersistenceManagerFactory getInstance() {
        if (factory == null) {
            factory = JDOHelper.getPersistenceManagerFactory("datanucleus.properties");
        }

        return factory;
    }
}
