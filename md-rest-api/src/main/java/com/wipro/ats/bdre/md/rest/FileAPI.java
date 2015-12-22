/*
 * Copyright 2015 Wipro Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wipro.ats.bdre.md.rest;

import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.beans.table.File;
import com.wipro.ats.bdre.md.dao.FileDAO;
import com.wipro.ats.bdre.md.rest.util.DateConverter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
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
@RequestMapping("/file")


public class FileAPI extends MetadataAPIBase {
    private static final Logger LOGGER = Logger.getLogger(FileAPI.class);
    /**
     * This method calls proc GetFile and fetches a record corresponding to batchId passed.
     *
     * @param batchId
     * @return restWrapper It contains an instance of File.
     */
    @Autowired
    FileDAO fileDAO;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public
    @ResponseBody
    RestWrapper get(
            @PathVariable("id") Long batchId, Principal principal
    ) {
        RestWrapper restWrapper = null;
        try {
            File file = new File();
            file.setBatchId(batchId);
            file.setCreationTS(DateConverter.stringToDate(file.getTableCreationTS()));
            //file = s.selectOne("call_procedures.GetFile", file);
            file = fileDAO.getFile(file);
            restWrapper = new RestWrapper(file, RestWrapper.OK);
            LOGGER.info("Record with ID:" + batchId + " selected from File by User:" + principal.getName());
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc DeleteFile and deletes a record corresponding to batchId passed.
     *
     * @param batchId
     * @param model
     * @return nothing.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    RestWrapper delete(
            @PathVariable("id") Long batchId, Principal principal,
            ModelMap model) {
        RestWrapper restWrapper = null;
        try {
            File file = new File();
            file.setBatchId(batchId);
            // s.delete("call_procedures.DeleteFile", file);
            fileDAO.delete(batchId);
            restWrapper = new RestWrapper(null, RestWrapper.OK);
            LOGGER.info("Record with ID:" + batchId + " deleted from File by User:" + principal.getName());
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc ListFile and fetches a list of instances of File.
     *
     * @param
     * @return restWrapper It contains a list of instances of File.
     */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)

    public
    @ResponseBody
    RestWrapper list(@RequestParam(value = "page", defaultValue = "0") int startPage,
                     @RequestParam(value = "size", defaultValue = "10") int pageSize, Principal principal) {
        RestWrapper restWrapper = null;
        try {
            File file = new File();
            file.setPage(startPage);
            file.setPageSize(pageSize);
            file.setCreationTS(DateConverter.stringToDate(file.getTableCreationTS()));
            Integer counter=fileDAO.totalRecordCount().intValue();
            // List<File> files = s.selectList("call_procedures.ListFiles", file);
            List<com.wipro.ats.bdre.md.dao.jpa.File> jpaFileList = fileDAO.list(startPage, pageSize);
            LOGGER.info("size of the jpaFiles is " + jpaFileList.size());
            List<File> files = new ArrayList<File>();
            for (com.wipro.ats.bdre.md.dao.jpa.File jpaFile : jpaFileList) {
                File tableFile = new File();
                tableFile.setBatchId(jpaFile.getId().getBatchId());
                tableFile.setServerId(jpaFile.getId().getServerId());
                tableFile.setPath(jpaFile.getId().getPath());
                tableFile.setFileSize(jpaFile.getId().getFileSize());
                tableFile.setFileHash(jpaFile.getId().getFileHash());
                tableFile.setCreationTS(jpaFile.getId().getCreationTs());
                tableFile.setCounter(counter);
                files.add(tableFile);
                LOGGER.info("file added:" + tableFile);
            }
            LOGGER.info("size of the tableFiles is " + jpaFileList.size());
            for (File f : files) {
                f.setTableCreationTS(DateConverter.dateToString(f.getCreationTS()));
            }
            restWrapper = new RestWrapper(files, RestWrapper.OK);
            LOGGER.info("All records listed from File by User:" + principal.getName());
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }

        return restWrapper;
    }

    /**
     * This method calls proc UpdateFile and updates the values of the record passed. It also validates
     * the values passed.
     *
     * @param file          Instance of File.
     * @param bindingResult
     * @return restWrapper It contains updated instance of File.
     */
    @RequestMapping(value = {"/", ""}, method = RequestMethod.POST)
    public
    @ResponseBody
    RestWrapper update(@ModelAttribute("file")
                       @Valid File file, BindingResult bindingResult, Principal principal) {
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
            file.setCreationTS(DateConverter.stringToDate(file.getTableCreationTS()));

            //List<File> files = s.selectList("call_procedures.UpdateFile", file);
            fileDAO.update(file);
            restWrapper = new RestWrapper(file, RestWrapper.OK);
            LOGGER.info("Record with ID:" + file.getBatchId() + " updated in File by User:" + principal.getName() + file);
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc InsertFile and adds a record of File. It also validates the values passed.
     *
     * @param file          Instance of File.
     * @param bindingResult
     * @return restWrapper It contains an instance of File just added.
     */
    @RequestMapping(value = {"/", ""}, method = RequestMethod.PUT)
    public
    @ResponseBody
    RestWrapper insert(@ModelAttribute("file")
                       @Valid File file, BindingResult bindingResult, Principal principal) {
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
            file.setCreationTS(DateConverter.stringToDate(file.getTableCreationTS()));
            // List<File> files = s.selectList("call_procedures.InsertFile", file);
            fileDAO.insert(file);

            restWrapper = new RestWrapper(file, RestWrapper.OK);
            LOGGER.info("Record with ID:" + file.getBatchId() + " inserted in File by User:" + principal.getName() + file);
        } catch (Exception e) {
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    @Override
    public Object execute(String[] params) {
        return null;
    }
}
