/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wipro.ats.bdre.md.rest;

import com.wipro.ats.bdre.exception.MetadataException;
import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.beans.table.BusDomain;
import com.wipro.ats.bdre.md.dao.BusDomainDAO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by arijit on 1/9/15.
 */
@Controller
@RequestMapping("/busdomain")


public class BusDomainAPI extends MetadataAPIBase {
    private static final Logger LOGGER = Logger.getLogger(BusDomainAPI.class);
    private static final String RECORDWITHID = "Record with ID:";
    @Autowired
    BusDomainDAO busDomainDAO;

    /**
     * This method calls proc GetBusDomain and fetches a record corresponding to the busDomainId passed.
     *
     * @param busDomainId
     * @return restWrapper Instance of BusDomain corresponding to busDomainId passed.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody public
    RestWrapper get(
            @PathVariable("id") Integer busDomainId, Principal principal
    ) {
        RestWrapper restWrapper = null;
        try {
            com.wipro.ats.bdre.md.dao.jpa.BusDomain jpaBusDomain = busDomainDAO.get(busDomainId);
            BusDomain busDomain = new BusDomain();
            if (jpaBusDomain != null) {
                busDomain.setBusDomainId(jpaBusDomain.getBusDomainId());
                busDomain.setBusDomainName(jpaBusDomain.getBusDomainName());
                busDomain.setBusDomainOwner(jpaBusDomain.getBusDomainOwner());
                busDomain.setDescription(jpaBusDomain.getDescription());
            }
            restWrapper = new RestWrapper(busDomain, RestWrapper.OK);
            LOGGER.info(RECORDWITHID + busDomainId + " selected from BusDomain by User:" + principal.getName());
        } catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;

    }

    /**
     * This method calls proc DeleteBusDomain and deletes the record corresponding to busDomainId passed.
     *
     * @param busDomainId
     * @return nothing.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody public
    RestWrapper delete(
            @PathVariable("id") Integer busDomainId, Principal principal) {
        RestWrapper restWrapper = null;
        try {
            busDomainDAO.delete(busDomainId);
            restWrapper = new RestWrapper(null, RestWrapper.OK);
            LOGGER.info(RECORDWITHID + busDomainId + " deleted from BusDomain by User:" + principal.getName());
        } catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc GetBusDomains and fetches the list of all records in BusDomain table.
     *
     * @param
     * @return restWrapper List of instances of BusDomain.
     */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    @ResponseBody public
    RestWrapper list(@RequestParam(value = "page", defaultValue = "0") int startPage,
                     @RequestParam(value = "size", defaultValue = "10") int pageSize, Principal principal) {
        RestWrapper restWrapper = null;
        try {
            Integer counter=busDomainDAO.totalRecordCount().intValue();
            List<com.wipro.ats.bdre.md.dao.jpa.BusDomain> jpaBusDoaminList = busDomainDAO.list(startPage, pageSize);
            List<BusDomain> busDomains = new ArrayList<BusDomain>();

            for (com.wipro.ats.bdre.md.dao.jpa.BusDomain busDomain : jpaBusDoaminList) {
                BusDomain returnBusDomain = new BusDomain();
                returnBusDomain.setBusDomainId(busDomain.getBusDomainId());
                returnBusDomain.setBusDomainName(busDomain.getBusDomainName());
                returnBusDomain.setBusDomainOwner(busDomain.getBusDomainOwner());
                returnBusDomain.setDescription(busDomain.getDescription());
                returnBusDomain.setCounter(counter);
                busDomains.add(returnBusDomain);
            }
            restWrapper = new RestWrapper(busDomains, RestWrapper.OK);
            LOGGER.info("All records listed from BusDomain by User:" + principal.getName());
        } catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc UpdateBusDomain and updates the record passed. It also validates the values passed.
     *
     * @param busDomain     Instance of BusDomain.
     * @param bindingResult
     * @return restWrapper Updated record passed.
     */
    @RequestMapping(value = {"/", ""}, method = RequestMethod.POST)
    @ResponseBody public
    RestWrapper update(@ModelAttribute("busdomain")
                       @Valid BusDomain busDomain, BindingResult bindingResult, Principal principal) {
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
        try {
            com.wipro.ats.bdre.md.dao.jpa.BusDomain jpaBusDomain = new com.wipro.ats.bdre.md.dao.jpa.BusDomain();
            jpaBusDomain.setBusDomainId(busDomain.getBusDomainId());
            jpaBusDomain.setBusDomainName(busDomain.getBusDomainName());
            jpaBusDomain.setBusDomainOwner(busDomain.getBusDomainOwner());
            jpaBusDomain.setDescription(busDomain.getDescription());
            busDomainDAO.update(jpaBusDomain);
            // BusDomain busDomains = s.selectOne("call_procedures.UpdateBusDomain", busDomain);

            restWrapper = new RestWrapper(busDomain, RestWrapper.OK);
            LOGGER.info(RECORDWITHID + busDomain.getBusDomainId() + " updated in BusDomain by User:" + principal.getName() + busDomain);
        } catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc InsertBusDomain and adds a record bus domain table. It also validates
     * the values passed.
     *
     * @param busDomain     Instance of BusDomain.
     * @param bindingResult
     * @return restWrapper Instance of BusDomain inserted.
     */
    @RequestMapping(value = {"/", ""}, method = RequestMethod.PUT)
    @ResponseBody public
    RestWrapper insert(@ModelAttribute("busdomain")
                       @Valid BusDomain busDomain, BindingResult bindingResult, Principal principal) {
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

        try {
            com.wipro.ats.bdre.md.dao.jpa.BusDomain jpaBusDomain = new com.wipro.ats.bdre.md.dao.jpa.BusDomain();
            jpaBusDomain.setBusDomainId(busDomain.getBusDomainId());
            jpaBusDomain.setBusDomainName(busDomain.getBusDomainName());
            jpaBusDomain.setBusDomainOwner(busDomain.getBusDomainOwner());
            jpaBusDomain.setDescription(busDomain.getDescription());
            Integer busDomainId = busDomainDAO.insert(jpaBusDomain);
            // BusDomain busDomains = s.selectOne("call_procedures.InsertBusDomain", busDomain);
            jpaBusDomain.setBusDomainId(busDomainId);
            restWrapper = new RestWrapper(busDomain, RestWrapper.OK);
            LOGGER.info(RECORDWITHID + busDomain.getBusDomainId() + " inserted in BusDomain by User:" + principal.getName() + busDomain);
        } catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * @return
     */
    @RequestMapping(value = {"/options", "/options/"}, method = RequestMethod.POST)
    @ResponseBody public
    RestWrapperOptions listOptions() {
        RestWrapperOptions restWrapperOptions = null;
        try {

            List<com.wipro.ats.bdre.md.dao.jpa.BusDomain> jpaBusDoaminList = busDomainDAO.list(0, 0);
            List<BusDomain> busDomains = new ArrayList<BusDomain>();
            for (com.wipro.ats.bdre.md.dao.jpa.BusDomain busDomain : jpaBusDoaminList) {
                BusDomain returnBusDomain = new BusDomain();
                returnBusDomain.setBusDomainId(busDomain.getBusDomainId());
                returnBusDomain.setBusDomainName(busDomain.getBusDomainName());
                returnBusDomain.setBusDomainOwner(busDomain.getBusDomainOwner());
                returnBusDomain.setDescription(busDomain.getDescription());
                returnBusDomain.setCounter(jpaBusDoaminList.size());
                busDomains.add(returnBusDomain);
            }
            List<RestWrapperOptions.Option> options = new ArrayList<RestWrapperOptions.Option>();

            for (BusDomain busDomain1 : busDomains) {
                RestWrapperOptions.Option option = new RestWrapperOptions.Option(busDomain1.getBusDomainName(), busDomain1.getBusDomainId());
                options.add(option);
            }
            restWrapperOptions = new RestWrapperOptions(options, RestWrapperOptions.OK);
        } catch (MetadataException e) {
            LOGGER.error(e);
            restWrapperOptions = new RestWrapperOptions(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapperOptions;
    }

    @Override
    public Object execute(String[] params) {
        return null;
    }
}




