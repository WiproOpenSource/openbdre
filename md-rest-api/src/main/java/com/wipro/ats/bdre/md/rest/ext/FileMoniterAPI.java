package com.wipro.ats.bdre.md.rest.ext;

import com.wipro.ats.bdre.md.beans.CrawlerInfo;
import com.wipro.ats.bdre.md.beans.FileMonitorInfo;
import com.wipro.ats.bdre.md.rest.RestWrapper;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

/**
 * Created by PR324290 on 12/22/2015.
 */
@Controller
@RequestMapping("/filemonitor")
@Scope("session")
public class FileMoniterAPI {
    private static final Logger LOGGER = Logger.getLogger(FileMoniterAPI.class);

    @RequestMapping(value = {"/", ""}, method = RequestMethod.POST)

    public
    @ResponseBody
    RestWrapper createFileMonitorProperties(@ModelAttribute("fileMonitorInfo")
                                     @Valid FileMonitorInfo fileMonitorInfo, BindingResult bindingResult, Principal principal) {

        RestWrapper restWrapper = null;

        if (bindingResult.hasErrors()) {
            StringBuilder errorMessages = new StringBuilder("<p>Please fix following errors and try again<p><ul>");
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMessages.append("<li>");
                errorMessages.append(error.getField());
                errorMessages.append(". Bad value: '");
                errorMessages.append(error.getRejectedValue());
                errorMessages.append("'</li>");
            }
            errorMessages.append("</ul>");
            restWrapper = new RestWrapper(errorMessages.toString(), RestWrapper.ERROR);
            return restWrapper;
        }
        LOGGER.info(fileMonitorInfo.getMonitoredDirName());
        return restWrapper;
    }
}
