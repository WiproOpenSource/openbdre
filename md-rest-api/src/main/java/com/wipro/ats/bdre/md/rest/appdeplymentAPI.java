package com.wipro.ats.bdre.md.rest;

import com.wipro.ats.bdre.md.beans.table.AppDeploymentQueue;
import com.wipro.ats.bdre.md.dao.AppDeploymentQueueDAO;
import com.wipro.ats.bdre.md.rest.util.BindingResultError;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.security.Principal;

/**
 * Created by cloudera on 3/8/16.
 */
@Controller
@RequestMapping("/appdeployment")

public class appdeplymentAPI {

    private static final Logger LOGGER = Logger.getLogger(appdeplymentAPI.class);
    @Autowired
    AppDeploymentQueueDAO appDeploymentQueueDAO;

    @RequestMapping(value = {"/", ""}, method = RequestMethod.PUT)
    @ResponseBody
    public
    RestWrapper insert(@ModelAttribute("acq")
                       @Valid AppDeploymentQueue appDeploymentQueue, BindingResult bindingResult, Principal principal) {
        RestWrapper restWrapper = null;
        if (bindingResult.hasErrors()) {
            BindingResultError bindingResultError = new BindingResultError();
            return bindingResultError.errorMessage(bindingResult);
        }
        return null;
    }




}
