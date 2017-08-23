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
import com.wipro.ats.bdre.md.beans.table.Properties;
import com.wipro.ats.bdre.md.dao.ProcessDAO;
import com.wipro.ats.bdre.md.dao.PropertiesDAO;
import com.wipro.ats.bdre.md.dao.jpa.Process;
import com.wipro.ats.bdre.md.dao.jpa.PropertiesId;
import com.wipro.ats.bdre.md.rest.util.BindingResultError;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.*;

/**
 * Created by arijit on 1/9/15.
 */
@Controller
@RequestMapping("/properties")


public class PropertiesAPI extends MetadataAPIBase {
    private static final Logger LOGGER = Logger.getLogger(PropertiesAPI.class);
    private static final String WRITE="write";

    @Autowired
    private PropertiesDAO propertiesDAO;
    @Autowired
    private ProcessDAO processDAO;
    /**
     * This method calls proc DeleteProperties and deletes  records from Properties table
     * corresponding to processId passed.
     *
     * @param processId
     * @return nothing.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public RestWrapper delete(@PathVariable("id") Integer processId, Principal principal) {
        RestWrapper restWrapper = null;
        try {
            Process parentProcess=processDAO.get(processId);
            if (parentProcess.getProcess()!=null)
                processDAO.securityCheck(parentProcess.getProcess().getProcessId(),principal.getName(),WRITE);
            else
                processDAO.securityCheck(processId,principal.getName(),WRITE);
            com.wipro.ats.bdre.md.dao.jpa.Process process = new Process();
            process.setProcessId(processId);
            propertiesDAO.deleteByProcessId(process);
            restWrapper = new RestWrapper(null, RestWrapper.OK);
            LOGGER.info("Record with ID:" + processId + " deleted from Properties by User:" + principal.getName());

        } catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }catch (SecurityException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    @ResponseBody
    public RestWrapper addStremingProperties(@PathVariable("id") Integer processId, HttpServletRequest request, Principal principal) {
        RestWrapper restWrapper = null;
        try {

            LOGGER.info("id is "+processId);
            // Read from request
            String query="";
            String tmp1="";
            StringBuilder buffer = null;
            try {
                buffer = new StringBuilder();
                BufferedReader reader = request.getReader();
                while ((tmp1 = reader.readLine()) != null) {
                    buffer.append(tmp1);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                query = java.net.URLDecoder.decode(new String(buffer), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String[] linkedList=query.split("&");
            LinkedHashMap<String, String> map=new LinkedHashMap<>();
            for (int i=0;i<linkedList.length;i++)
            {
                String[] tmp=linkedList[i].split("=");
                if (tmp.length==2)
                    map.put(tmp[0],tmp[1]);
                else
                    map.put(tmp[0],"");
            }
            LOGGER.info(" value of map is " + map.toString());
            Process process=processDAO.get(processId);
            for (String string : map.keySet()) {
                System.out.println("string = " + string);
                com.wipro.ats.bdre.md.dao.jpa.Properties properties=new com.wipro.ats.bdre.md.dao.jpa.Properties();
                PropertiesId propertiesId=new PropertiesId();
                propertiesId.setProcessId(processId);
                propertiesId.setPropKey(string);
                com.wipro.ats.bdre.md.dao.jpa.Properties alreadyPresentProperties=propertiesDAO.get(propertiesId);
                if(alreadyPresentProperties==null){
                    properties.setId(propertiesId);
                    properties.setConfigGroup("default");
                    if (string.equals("messageName"))
                        properties.setConfigGroup("message");
                    else if(string.equals("connectionName"))
                        properties.setConfigGroup("connection");
                    properties.setProcess(process);
                    properties.setPropValue(map.get(string));
                    properties.setDescription("addition of kafka properties");
                    propertiesDAO.insert(properties);
                }
                else
                {
                    alreadyPresentProperties.setPropValue(map.get(string));
                    propertiesDAO.update(alreadyPresentProperties);
                }

            }

            restWrapper = new RestWrapper(null, RestWrapper.OK);
            LOGGER.info("Record with ID:" + processId + " deleted from Properties by User:" + principal.getName());

        } catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }catch (SecurityException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }








    @RequestMapping(value = "/addBroadcastProperties/{id}", method = RequestMethod.POST)
    @ResponseBody
    public RestWrapper addBroadcastProperties(@PathVariable("id") Integer processId, HttpServletRequest request, Principal principal) {
        RestWrapper restWrapper = null;
        try {
            String broadcastString=request.getParameter("connectionName:tableName:columnFamily:columnName:broadcastIdentifier");
            LOGGER.info("processId is "+processId);
            LOGGER.info("broadcastString is "+broadcastString);
            String[] broadcastArray=broadcastString.split(",");
            LOGGER.info(broadcastArray.toString());
            int count=1;
            Process process=processDAO.get(processId);
            LOGGER.info(process.toString());
            for(int i=0;i<broadcastArray.length;i++)
            {
                String[] tmpArray=broadcastArray[i].split(":::");

                com.wipro.ats.bdre.md.dao.jpa.Properties jpaProperties = new com.wipro.ats.bdre.md.dao.jpa.Properties();

                PropertiesId jpaPropertiesId=new PropertiesId();
                jpaPropertiesId.setProcessId(processId);
                jpaPropertiesId.setPropKey("connectionName_"+count);

                jpaProperties.setId(jpaPropertiesId);
                jpaProperties.setConfigGroup("broadcast");
                jpaProperties.setDescription("Connection Name");
                jpaProperties.setProcess(process);
                jpaProperties.setPropValue(tmpArray[0]);


                com.wipro.ats.bdre.md.dao.jpa.Properties jpaProperties1 = new com.wipro.ats.bdre.md.dao.jpa.Properties();
                PropertiesId jpaPropertiesId1=new PropertiesId();
                jpaPropertiesId1.setProcessId(processId);
                jpaPropertiesId1.setPropKey("tableName_"+count);

                jpaProperties1.setId(jpaPropertiesId1);
                jpaProperties1.setConfigGroup("broadcast");
                jpaProperties1.setDescription("Table Name");
                jpaProperties1.setProcess(process);
                jpaProperties1.setPropValue(tmpArray[1]);



                com.wipro.ats.bdre.md.dao.jpa.Properties jpaProperties2 = new com.wipro.ats.bdre.md.dao.jpa.Properties();
                PropertiesId jpaPropertiesId2=new PropertiesId();
                jpaPropertiesId2.setProcessId(processId);
                jpaPropertiesId2.setPropKey("columnFamily_"+count);

                jpaProperties2.setId(jpaPropertiesId2);
                jpaProperties2.setConfigGroup("broadcast");
                jpaProperties2.setDescription("Column Family");
                jpaProperties2.setProcess(process);
                jpaProperties2.setPropValue(tmpArray[2]);



                com.wipro.ats.bdre.md.dao.jpa.Properties jpaProperties3= new com.wipro.ats.bdre.md.dao.jpa.Properties();
                PropertiesId jpaPropertiesId3=new PropertiesId();
                jpaPropertiesId3.setProcessId(processId);
                jpaPropertiesId3.setPropKey("columnName_"+count);

                jpaProperties3.setId(jpaPropertiesId3);
                jpaProperties3.setProcess(process);
                jpaProperties3.setConfigGroup("broadcast");
                jpaProperties3.setDescription("Column Name");
                jpaProperties3.setPropValue(tmpArray[3]);



                com.wipro.ats.bdre.md.dao.jpa.Properties jpaProperties4 = new com.wipro.ats.bdre.md.dao.jpa.Properties();
                PropertiesId jpaPropertiesId4=new PropertiesId();
                jpaPropertiesId4.setProcessId(processId);
                jpaPropertiesId4.setPropKey("broadcastIdentifier_"+count);

                jpaProperties4.setId(jpaPropertiesId4);
                jpaProperties4.setConfigGroup("broadcast");
                jpaProperties4.setDescription("Broadcast Identifier");
                jpaProperties4.setProcess(process);
                jpaProperties4.setPropValue(tmpArray[4]);

                propertiesDAO.insert(jpaProperties);
                propertiesDAO.insert(jpaProperties1);
                propertiesDAO.insert(jpaProperties2);
                propertiesDAO.insert(jpaProperties3);
                propertiesDAO.insert(jpaProperties4);
                count++;
            }

            restWrapper = new RestWrapper(null, RestWrapper.OK);
            LOGGER.info("Record with ID:" + processId + " deleted from Properties by User:" + principal.getName());

        } catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }catch (SecurityException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }





    @RequestMapping(value = "/addFilterProperties/{id}", method = RequestMethod.POST)
    @ResponseBody
    public RestWrapper addFilterProperties(@PathVariable("id") Integer processId, HttpServletRequest request, Principal principal) {
        RestWrapper restWrapper = null;
        try {
            String broadcastString=request.getParameter("filterData");
            LOGGER.info("processId is "+processId);
            LOGGER.info("broadcastString is "+broadcastString);
            String[] filterArray=broadcastString.split(",");
            LOGGER.info(filterArray.toString());
            int count=1;
            Process process=processDAO.get(processId);
            LOGGER.info(process.toString());
            for(int i=0;i<filterArray.length;i++)
            {
                String[] tmpArray=filterArray[i].split("::");

                com.wipro.ats.bdre.md.dao.jpa.Properties jpaProperties = new com.wipro.ats.bdre.md.dao.jpa.Properties();

                PropertiesId jpaPropertiesId=new PropertiesId();
                jpaPropertiesId.setProcessId(processId);
                jpaPropertiesId.setPropKey("logicalOperator_"+count);

                jpaProperties.setId(jpaPropertiesId);
                jpaProperties.setConfigGroup("filter");
                jpaProperties.setDescription("Logical Operator");
                jpaProperties.setProcess(process);
                jpaProperties.setPropValue(tmpArray[0]);


                com.wipro.ats.bdre.md.dao.jpa.Properties jpaProperties1 = new com.wipro.ats.bdre.md.dao.jpa.Properties();
                PropertiesId jpaPropertiesId1=new PropertiesId();
                jpaPropertiesId1.setProcessId(processId);
                jpaPropertiesId1.setPropKey("column_"+count);

                jpaProperties1.setId(jpaPropertiesId1);
                jpaProperties1.setConfigGroup("filter");
                jpaProperties1.setDescription("Column Name");
                jpaProperties1.setProcess(process);
                jpaProperties1.setPropValue(tmpArray[1]);



                com.wipro.ats.bdre.md.dao.jpa.Properties jpaProperties2 = new com.wipro.ats.bdre.md.dao.jpa.Properties();
                PropertiesId jpaPropertiesId2=new PropertiesId();
                jpaPropertiesId2.setProcessId(processId);
                jpaPropertiesId2.setPropKey("operator_"+count);

                jpaProperties2.setId(jpaPropertiesId2);
                jpaProperties2.setConfigGroup("filter");
                jpaProperties2.setDescription("Operator");
                jpaProperties2.setProcess(process);
                jpaProperties2.setPropValue(tmpArray[2]);

                propertiesDAO.insert(jpaProperties);
                propertiesDAO.insert(jpaProperties1);
                propertiesDAO.insert(jpaProperties2);


                if (tmpArray.length>=4){
                  LOGGER.info("tmpArray[3] is "+tmpArray[3]);
                com.wipro.ats.bdre.md.dao.jpa.Properties jpaProperties3= new com.wipro.ats.bdre.md.dao.jpa.Properties();
                PropertiesId jpaPropertiesId3=new PropertiesId();
                jpaPropertiesId3.setProcessId(processId);
                jpaPropertiesId3.setPropKey("filterValue_"+count);

                jpaProperties3.setId(jpaPropertiesId3);
                jpaProperties3.setProcess(process);
                jpaProperties3.setConfigGroup("filter");
                jpaProperties3.setDescription("Filter Value");
                jpaProperties3.setPropValue(tmpArray[3]);
                propertiesDAO.insert(jpaProperties3);
                }

                count++;
            }

            com.wipro.ats.bdre.md.dao.jpa.Properties jpaProperties4= new com.wipro.ats.bdre.md.dao.jpa.Properties();
            PropertiesId jpaPropertiesId4=new PropertiesId();
            jpaPropertiesId4.setProcessId(processId);
            jpaPropertiesId4.setPropKey("count");

            jpaProperties4.setId(jpaPropertiesId4);
            jpaProperties4.setProcess(process);
            jpaProperties4.setConfigGroup("filter");
            jpaProperties4.setDescription("Count Value");
            jpaProperties4.setPropValue(String.valueOf(count-1));
            propertiesDAO.insert(jpaProperties4);


            restWrapper = new RestWrapper(null, RestWrapper.OK);
            LOGGER.info("Record with ID:" + processId + " deleted from Properties by User:" + principal.getName());

        } catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }catch (SecurityException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }





    /**
     * This method calls proc ListProperty and fetches a list of instances of Properties.
     *
     * @param
     * @return restWrapper It contains a list of instances of Properties.
     */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)


    @ResponseBody
    public RestWrapper list(@RequestParam(value = "page", defaultValue = "0") int startPage,
                            @RequestParam(value = "size", defaultValue = "10") int pageSize, Principal principal) {

        RestWrapper restWrapper = null;
        try {
            Integer counter=propertiesDAO.totalRecordCount();
            List<Properties> getProperties = new ArrayList<Properties>();
            for (Integer processId : propertiesDAO.list(startPage, pageSize)) {
                com.wipro.ats.bdre.md.beans.table.Properties returnProperties = new com.wipro.ats.bdre.md.beans.table.Properties();
                returnProperties.setProcessId(processId);
                returnProperties.setCounter(counter);
                getProperties.add(returnProperties);
            }


            restWrapper = new RestWrapper(getProperties, RestWrapper.OK);
            LOGGER.info("All records listed from Properties by User:" + principal.getName());

        } catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc DeleteProperty and deletes an entry corresponding to  particular processId and key passed.
     *
     * @param processId
     * @param key
     * @return nothing.
     */
    @RequestMapping(value = "/{id}/{k}/", method = RequestMethod.DELETE)

    @ResponseBody
    public RestWrapper delete(
            @PathVariable("id") Integer processId,
            @PathVariable("k") String key, Principal principal) {

        RestWrapper restWrapper = null;
        try {
            Process parentProcess=processDAO.get(processId);
            if (parentProcess.getProcess()!=null)
                processDAO.securityCheck(parentProcess.getProcess().getProcessId(),principal.getName(),WRITE);
            else
                processDAO.securityCheck(processId,principal.getName(),WRITE);
            com.wipro.ats.bdre.md.dao.jpa.PropertiesId propertiesId = new com.wipro.ats.bdre.md.dao.jpa.PropertiesId();
            propertiesId.setProcessId(processId);
            propertiesId.setPropKey(key);
            propertiesDAO.delete(propertiesId);
            restWrapper = new RestWrapper(null, RestWrapper.OK);
            LOGGER.info("Record with ID:" + processId + "," + key + " deleted from Properties by User:" + principal.getName());

        } catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        catch (SecurityException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc ListProperties and fetches a list of records from properties table corresponding to
     * processId passed.
     *
     * @param
     * @return restWrapper It contains list of instances of properties corresponding to processId passed.
     */
    @RequestMapping(value = {"/{id}"}, method = RequestMethod.GET)


    @ResponseBody
    public RestWrapper list(@RequestParam(value = "page", defaultValue = "0") int startPage,
                            @RequestParam(value = "size", defaultValue = "10") int pageSize,
                            @PathVariable("id") Integer processId, Principal principal) {

        RestWrapper restWrapper = null;
        try {
            LOGGER.info("startpage is "+startPage+" pagesize is "+pageSize);
            Process parentProcess=processDAO.get(processId);
            if (parentProcess.getProcess()!=null)
                processDAO.securityCheck(parentProcess.getProcess().getProcessId(),principal.getName(),"read");
            else
                processDAO.securityCheck(processId,principal.getName(),"read");
            List<Properties> propertiesList = new ArrayList<Properties>();
            Process process = new Process();
            process.setProcessId(processId);

            List<com.wipro.ats.bdre.md.dao.jpa.Properties> propertiesList1=new ArrayList<com.wipro.ats.bdre.md.dao.jpa.Properties>();
            propertiesList1=propertiesDAO.getByProcessIdCopy(process,startPage,pageSize);
            Integer counter=propertiesDAO.totalRecordCount(process);
            for (com.wipro.ats.bdre.md.dao.jpa.Properties properties : propertiesList1) {
                com.wipro.ats.bdre.md.beans.table.Properties returnProperties = new com.wipro.ats.bdre.md.beans.table.Properties();
                returnProperties.setProcessId(properties.getProcess().getProcessId());
                returnProperties.setConfigGroup(properties.getConfigGroup());
                returnProperties.setKey(properties.getId().getPropKey());
                returnProperties.setValue(properties.getPropValue());
                returnProperties.setDescription(properties.getDescription());
                returnProperties.setCounter(counter);
                propertiesList.add(returnProperties);
            }

            restWrapper = new RestWrapper(propertiesList, RestWrapper.OK);
            LOGGER.info("Record with ID:" + processId + "selected from Properties by User:" + principal.getName());

        } catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        catch (SecurityException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }


    /**
     * This method calls proc ListProperties and fetches a list of records from properties table corresponding to
     * processId passed.
     *
     * @param
     * @return restWrapper It contains list of instances of properties corresponding to processId passed.
     */
    @RequestMapping(value = {"/{id}/{cg}", "/{id}/{cg}/"}, method = RequestMethod.GET)


    @ResponseBody
    public RestWrapper listConfigGroup(@PathVariable("id") Integer processId,
                                       @PathVariable("cg") String configGroup,
                                       Principal principal) {

        RestWrapper restWrapper = null;
        try {
            Process parentProcess=processDAO.get(processId);
            if (parentProcess.getProcess()!=null)
                processDAO.securityCheck(parentProcess.getProcess().getProcessId(),principal.getName(),"read");
            else
                processDAO.securityCheck(processId,principal.getName(),"read");
            List<Properties> propertiesList = new ArrayList<Properties>();
            List<com.wipro.ats.bdre.md.dao.jpa.Properties>jpaPropertiesList=new ArrayList<com.wipro.ats.bdre.md.dao.jpa.Properties>();
            jpaPropertiesList=propertiesDAO.getPropertiesForConfig(processId, configGroup);
            Integer counter=jpaPropertiesList.size();
            for (com.wipro.ats.bdre.md.dao.jpa.Properties properties : jpaPropertiesList) {
                com.wipro.ats.bdre.md.beans.table.Properties returnProperties = new com.wipro.ats.bdre.md.beans.table.Properties();
                returnProperties.setProcessId(properties.getProcess().getProcessId());
                returnProperties.setConfigGroup(properties.getConfigGroup());
                returnProperties.setKey(properties.getId().getPropKey());
                returnProperties.setValue(properties.getPropValue());
                returnProperties.setDescription(properties.getDescription());
                returnProperties.setCounter(counter);
                propertiesList.add(returnProperties);
            }
            restWrapper = new RestWrapper(propertiesList, RestWrapper.OK);
            LOGGER.info("Record with ID:" + processId + "and config group" + configGroup + "selected from Properties by User:" + principal.getName());

        } catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        catch (SecurityException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc UpdateProperties and updates the values passed. It also validates the values passed.
     *
     * @param properties    Instance of Properties.
     * @param bindingResult
     * @return restWrapper It contains updated instance of Properties.
     */
    @RequestMapping(value = {"/", ""}, method = RequestMethod.POST)

    @ResponseBody
    public RestWrapper update(@ModelAttribute("properties")
                              @Valid Properties properties, BindingResult bindingResult, Principal principal) {

        RestWrapper restWrapper = null;
        if (bindingResult.hasErrors()) {
            BindingResultError bindingResultError = new BindingResultError();
            return bindingResultError.errorMessage(bindingResult);
        }
        try {
            Process parentProcess=processDAO.get(properties.getProcessId());
            if (parentProcess.getProcess()!=null)
                processDAO.securityCheck(parentProcess.getProcess().getProcessId(),principal.getName(),WRITE);
            else
                processDAO.securityCheck(properties.getProcessId(),principal.getName(),WRITE);
            com.wipro.ats.bdre.md.dao.jpa.Properties updateProperties = new com.wipro.ats.bdre.md.dao.jpa.Properties();
            PropertiesId propertiesId = new PropertiesId();
            propertiesId.setPropKey(properties.getKey());
            propertiesId.setProcessId(properties.getProcessId());
            updateProperties.setId(propertiesId);
            Process process = new Process();
            process.setProcessId(properties.getProcessId());
            updateProperties.setProcess(process);
            updateProperties.setConfigGroup(properties.getConfigGroup());
            updateProperties.setPropValue(properties.getValue());
            updateProperties.setDescription(properties.getDescription());
            propertiesDAO.update(updateProperties);
            restWrapper = new RestWrapper(properties, RestWrapper.OK);
            LOGGER.info("Record with ID:" + properties.getProcessId() + " updated in Properties by User:" + principal.getName() + properties);

        } catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        catch (SecurityException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc InsertProperties and adds a record in properties table. It also validates the
     * values passed.
     *
     * @param properties    Instance of properties.
     * @param bindingResult
     * @return restWrapper It contains instance of Properties passed.
     */
    @RequestMapping(value = {"/", ""}, method = RequestMethod.PUT)

    @ResponseBody
    public RestWrapper insert(@ModelAttribute("properties")
                              @Valid Properties properties, BindingResult bindingResult, Principal principal) {

        RestWrapper restWrapper = null;
        if (bindingResult.hasErrors()) {
            BindingResultError bindingResultError = new BindingResultError();
            return bindingResultError.errorMessage(bindingResult);
        }
        try {
            Process parentProcess=processDAO.get(properties.getProcessId());
            if (parentProcess.getProcess()!=null)
                processDAO.securityCheck(parentProcess.getProcess().getProcessId(),principal.getName(),WRITE);
            else
                processDAO.securityCheck(properties.getProcessId(),principal.getName(),WRITE);
            com.wipro.ats.bdre.md.dao.jpa.Properties insertProperties = new com.wipro.ats.bdre.md.dao.jpa.Properties();
            PropertiesId propertiesId = new PropertiesId();
            propertiesId.setPropKey(properties.getKey());
            propertiesId.setProcessId(properties.getProcessId());
            insertProperties.setId(propertiesId);
            Process process = new Process();
            process.setProcessId(properties.getProcessId());
            insertProperties.setProcess(process);
            insertProperties.setConfigGroup(properties.getConfigGroup());
            insertProperties.setPropValue(properties.getValue());
            insertProperties.setDescription(properties.getDescription());
            propertiesDAO.insert(insertProperties);
            restWrapper = new RestWrapper(properties, RestWrapper.OK);
            LOGGER.info("Record with ID:" + properties.getProcessId() + " inserted in Properties by User:" + principal.getName() + properties);

        } catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        catch (SecurityException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc DeleteProperties and deletes  records from Properties table
     * corresponding to processId passed.
     *
     * @param parentProcessId
     * @return
     */
    @RequestMapping(value = "/all/{id}", method = RequestMethod.GET)

    @ResponseBody
    public RestWrapper getAll(@PathVariable("id") Integer parentProcessId, Principal principal) {

        RestWrapper restWrapper = null;
        try {
            Process parentProcess=processDAO.get(parentProcessId);
            if (parentProcess.getProcess()!=null)
                processDAO.securityCheck(parentProcess.getProcess().getProcessId(),principal.getName(),WRITE);
            else
                processDAO.securityCheck(parentProcessId,principal.getName(),WRITE);
            List<Properties> propertiesList = new ArrayList<Properties>();
            Process process = new Process();
            process.setProcessId(parentProcessId);
            List<com.wipro.ats.bdre.md.dao.jpa.Properties>jpaPropertiesList=new ArrayList<com.wipro.ats.bdre.md.dao.jpa.Properties>();
            jpaPropertiesList=propertiesDAO.getByProcessId(process);
            Integer counter=jpaPropertiesList.size();
            for (com.wipro.ats.bdre.md.dao.jpa.Properties properties :jpaPropertiesList ) {
                com.wipro.ats.bdre.md.beans.table.Properties returnProperties = new com.wipro.ats.bdre.md.beans.table.Properties();
                returnProperties.setProcessId(properties.getProcess().getProcessId());
                returnProperties.setConfigGroup(properties.getConfigGroup());
                returnProperties.setKey(properties.getId().getPropKey());
                returnProperties.setValue(properties.getPropValue());
                returnProperties.setDescription(properties.getDescription());
                returnProperties.setCounter(counter);
                propertiesList.add(returnProperties);
            }

            restWrapper = new RestWrapper(propertiesList, RestWrapper.OK);
            LOGGER.debug("Records fetched:" + propertiesList);
            LOGGER.info("All records with parent process ID:" + parentProcessId + " selected from Properties by User:" + principal.getName());

        } catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        catch (SecurityException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    @RequestMapping(value = "/addDeDuplicationProperties/{id}", method = RequestMethod.POST)
    @ResponseBody
    public RestWrapper addDeDuplicationProperties(@PathVariable("id") Integer processId, @RequestParam Map<String,String> map,HttpServletRequest request, Principal principal) {
        RestWrapper restWrapper = null;
        try {
            LOGGER.info("processId is "+processId);
            LOGGER.info("map data is "+map);
            Process process=processDAO.get(processId);
            for (Map.Entry<String, String> entry : map.entrySet())
            {

                com.wipro.ats.bdre.md.dao.jpa.Properties jpaProperties= new com.wipro.ats.bdre.md.dao.jpa.Properties();
                PropertiesId jpaPropertiesId=new PropertiesId();
                jpaPropertiesId.setProcessId(processId);
                jpaPropertiesId.setPropKey(entry.getKey());

                jpaProperties.setId(jpaPropertiesId);
                jpaProperties.setProcess(process);
                jpaProperties.setConfigGroup("deduplication");
                jpaProperties.setDescription("deduplication properties");
                jpaProperties.setPropValue(entry.getValue());
                propertiesDAO.insert(jpaProperties);
            }
            restWrapper = new RestWrapper(null, RestWrapper.OK);
        } catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        catch (SecurityException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }


    @Override
    public Object execute(String[] params) {
        return null;
    }
}
