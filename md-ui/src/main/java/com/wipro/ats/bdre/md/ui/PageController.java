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
package com.wipro.ats.bdre.md.ui;

import com.wipro.ats.bdre.md.api.GetProcess;
import com.wipro.ats.bdre.md.beans.ProcessInfo;
import com.wipro.ats.bdre.wgen.PythonWorkflowPrinter;
import com.wipro.ats.bdre.wgen.Workflow;
import com.wipro.ats.bdre.wgen.WorkflowPrinter;
import org.apache.log4j.Logger;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * Created by arijit on 1/10/15.
 */

/**
 * This class is used to direct the web pages according to RequestMapping.
 */
@Controller
@RequestMapping("/pages")
public class PageController {
    private static final String PARENTPROCESSID = "--parent-process-id";
    private static final String WORKFLOWCON = "workflow-";
    private static final Logger LOGGER = Logger.getLogger(PageController.class);


    @RequestMapping(value = "/{page}.page", method = RequestMethod.GET)
    public String welcome(@PathVariable("page") String page) {
        return page;
    }

    @RequestMapping(value = "/workflow/{pid}.page", method = RequestMethod.GET)
    @ResponseBody
    public String getWorkflowDot(@PathVariable("pid") String pid, java.security.Principal principal) {
        Workflow workflow=new Workflow();
        try{
            List<ProcessInfo> processInfos = new GetProcess().execute(new String[]{PARENTPROCESSID, pid,"--username",principal.getName()});
             workflow = new WorkflowPrinter().execute(processInfos, WORKFLOWCON + pid);
        } catch (SecurityException e) {
            LOGGER.info(e);
            workflow.setDot(new StringBuilder("not allowed"));
        }
        return workflow.getDot().toString();

    }

    @RequestMapping(value = "/details/{pid}/{ieid}.page", method = RequestMethod.GET)
    @ResponseBody
    public String getDashboardDot(@PathVariable("pid") String pid, @PathVariable("ieid") String ieid) {

        List<ProcessInfo> processInfos = new GetProcess().execInfo(new String[]{PARENTPROCESSID, pid, "--instance-exec-id", ieid});
        Workflow workflow = new WorkflowPrinter().execInfo(processInfos, WORKFLOWCON + pid);
        return workflow.getDot().toString();

    }

    @RequestMapping(value = "/workflowxml/{pid}.page", method = RequestMethod.GET)
    @ResponseBody
    public String getWorkflowXML(@PathVariable("pid") String pid,java.security.Principal principal) {
        Workflow workflow=new Workflow();
        try{
        List<ProcessInfo> processInfos = new GetProcess().execute(new String[]{PARENTPROCESSID, pid,"--username",principal.getName()});
         workflow = new WorkflowPrinter().execute(processInfos, WORKFLOWCON + pid);
        } catch (SecurityException e) {
           LOGGER.info(e);
            workflow.setXml(new StringBuilder("not allowed"));
        }
        return workflow.getXml().toString();
    }

    @RequestMapping(value = "/workflowdag/{pid}.page", method = RequestMethod.GET)
    @ResponseBody
    public String getWorkflowDag(@PathVariable("pid") String pid,java.security.Principal principal) {
        Workflow workflow=new Workflow();
        try{
            List<ProcessInfo> processInfos = new GetProcess().execute(new String[]{PARENTPROCESSID, pid,"--username",principal.getName()});
            workflow = new PythonWorkflowPrinter().execute(processInfos, WORKFLOWCON + pid);
        } catch (SecurityException e) {
            LOGGER.info(e);
            workflow.setXml(new StringBuilder("not allowed"));
        }
        return workflow.getXml().toString();
    }

    @RequestMapping(value = "/auth/login.page", method = RequestMethod.GET)
    public ModelAndView login(@RequestParam(value = "error", required = false) String error,
                              @RequestParam(value = "logout", required = false) String logout) {

        ModelAndView model = new ModelAndView();
        if (error != null) {
            model.addObject("error", "Invalid username and password!");
        }

        if (logout != null) {
            model.addObject("msg", "You've been logged out successfully.");
        }
        model.setViewName("login");

        return model;

    }

    //for 403 access denied page
    @RequestMapping(value = "/auth/403.page", method = RequestMethod.GET)
    public ModelAndView accesssDenied() {
        ModelAndView model = new ModelAndView();
        //check if user is login
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            Object userDetail = auth.getPrincipal();
            model.addObject("username", userDetail);
        }
        model.setViewName("403");
        return model;

    }

}
