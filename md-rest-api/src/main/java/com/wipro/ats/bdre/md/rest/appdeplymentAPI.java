package com.wipro.ats.bdre.md.rest;

import com.wipro.ats.bdre.md.dao.jpa.AppDeploymentQueue;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by cloudera on 3/8/16.
 */
@Controller
@RequestMapping("/appdeployment")

public class appdeplymentAPI {

    private static final Logger LOGGER = Logger.getLogger(appdeplymentAPI.class);
    @Autowired
    AppDeploymentQueue appDeploymentQueue;






}
