package com.wipro.ats.bdre.md.rest.util;

import com.wipro.ats.bdre.md.rest.RestWrapper;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

/**
 * Created by cloudera on 3/1/16.
 */
public class BindingResultError {
    public RestWrapper errorMessage(BindingResult bindingResult){
        RestWrapper restWrapper = null;
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
}
