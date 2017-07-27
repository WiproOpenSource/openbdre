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

package com.wipro.ats.bdre.md.rest.ext;

import com.wipro.ats.bdre.md.beans.FileMonitorInfo;
import com.wipro.ats.bdre.md.dao.ProcessDAO;
import com.wipro.ats.bdre.md.dao.UserRolesDAO;
import com.wipro.ats.bdre.md.dao.jpa.Process;
import com.wipro.ats.bdre.md.dao.jpa.Properties;
import com.wipro.ats.bdre.md.dao.jpa.Users;
import com.wipro.ats.bdre.md.rest.RestWrapper;
import com.wipro.ats.bdre.md.rest.util.BindingResultError;
import com.wipro.ats.bdre.md.rest.util.Dao2TableUtil;
import com.wipro.ats.bdre.md.rest.util.DateConverter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by PR324290 on 12/22/2015.
 */
@Controller
@RequestMapping("/filemonitor")
@Scope("session")
public class FileMoniterAPI {
    private static final Logger LOGGER = Logger.getLogger(FileMoniterAPI.class);
    private static final String FILEMON = "fileMon";
    @Autowired
    private ProcessDAO processDAO;
    @Autowired
    UserRolesDAO userRolesDAO;
    @RequestMapping(value = {"/", ""}, method = RequestMethod.POST)
    @ResponseBody public
    RestWrapper createFileMonitorProperties(@ModelAttribute("fileMonitorInfo")
                                            @Valid FileMonitorInfo fileMonitorInfo, BindingResult bindingResult, Principal principal) {
        RestWrapper restWrapper = null;
        if (bindingResult.hasErrors()) {
            BindingResultError bindingResultError = new BindingResultError();
            return bindingResultError.errorMessage(bindingResult);
        }
        //making process
        Process parentProcess = Dao2TableUtil.buildJPAProcess(26, fileMonitorInfo.getProcessName(), fileMonitorInfo.getProcessDescription(), 2,fileMonitorInfo.getBusDomainId());
        Users users=new Users();
        users.setUsername(principal.getName());
        parentProcess.setUsers(users);
        parentProcess.setUserRoles(userRolesDAO.minUserRoleId(principal.getName()));
        Process childProcess = Dao2TableUtil.buildJPAProcess(27, "SubProcess of " + fileMonitorInfo.getProcessName(), fileMonitorInfo.getProcessDescription(), 0,fileMonitorInfo.getBusDomainId());
        List<Properties> childProps=new ArrayList<>();
        //inserting in properties table
        Properties jpaProperties = Dao2TableUtil.buildJPAProperties(FILEMON, "deleteCopiedSrc", fileMonitorInfo.getDeleteCopiedSource(), "Delete copied source");
        childProps.add(jpaProperties);
        jpaProperties = Dao2TableUtil.buildJPAProperties(FILEMON, "filePattern", fileMonitorInfo.getFilePattern(), "pattern of file");
        childProps.add(jpaProperties);
        jpaProperties = Dao2TableUtil.buildJPAProperties(FILEMON, "hdfsUploadDir", fileMonitorInfo.getHdfsUploadDir(), "hdfc upload dir");
        childProps.add(jpaProperties);
        jpaProperties = Dao2TableUtil.buildJPAProperties(FILEMON, "monitoredDirName", fileMonitorInfo.getMonitoredDirName(), "file monitored dir");
        childProps.add(jpaProperties);
        jpaProperties = Dao2TableUtil.buildJPAProperties(FILEMON, "sleepTime", Integer.toString(fileMonitorInfo.getSleepTime()), "sleeptime of thread");
        childProps.add(jpaProperties);
        jpaProperties = Dao2TableUtil.buildJPAProperties(FILEMON, "kerberos", fileMonitorInfo.getKerberosEnabled(), "kerberos authenticatio enabled or not");
        childProps.add(jpaProperties);
        List<Process> processList = processDAO.createOneChildJob(parentProcess,childProcess,null,childProps);
        List<com.wipro.ats.bdre.md.beans.table.Process>tableProcessList=Dao2TableUtil.jpaList2TableProcessList(processList);
        Integer counter=tableProcessList.size();
        for(com.wipro.ats.bdre.md.beans.table.Process process:tableProcessList){
            process.setCounter(counter);
            process.setTableAddTS(DateConverter.dateToString(process.getAddTS()));
            process.setTableEditTS(DateConverter.dateToString(process.getEditTS()));
        }
        restWrapper = new RestWrapper(tableProcessList, RestWrapper.OK);
        LOGGER.info("Process and properties inserted for File Monitor Process by " + principal.getName());
        return restWrapper;
    }


}
