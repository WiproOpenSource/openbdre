package com.wipro.ats.bdre.md.rest;

import com.wipro.ats.bdre.exception.MetadataException;
import com.wipro.ats.bdre.md.dao.ConnectionPropertiesDAO;
import com.wipro.ats.bdre.md.dao.ConnectionsDAO;
import com.wipro.ats.bdre.md.dao.jpa.ConnectionProperties;
import com.wipro.ats.bdre.md.dao.jpa.ConnectionPropertiesId;
import com.wipro.ats.bdre.md.dao.jpa.Connections;
import com.wipro.ats.bdre.md.rest.util.Dao2TableUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by cloudera on 5/30/17.
 */
@Controller
@RequestMapping("/connections")
public class ConnectionsAPI {
    private static final Logger LOGGER = Logger.getLogger(ConnectionsAPI.class);
    @Autowired
    private ConnectionsDAO connectionsDAO;
    @Autowired
    private ConnectionPropertiesDAO connectionPropertiesDAO;

    @RequestMapping(value = {"/createconnection"}, method = RequestMethod.POST)

    @ResponseBody
    public
    RestWrapper createJob(@RequestParam Map<String, String> map, Principal principal) {
        LOGGER.debug(" value of map is " + map.size());
        RestWrapper restWrapper = null;

        List<ConnectionProperties> connectionPropertiesList = new ArrayList<>();
        ConnectionProperties connectionProperties = null;
        String connectionName = "";
        String connectionType = "";
        for (String string : map.keySet()) {
            LOGGER.debug("String is" + string);
            if (map.get(string) == null || ("").equals(map.get(string))) {
                continue;
            }

            Integer splitIndex = string.lastIndexOf("_");
            String key = string.substring(splitIndex + 1, string.length());
            LOGGER.debug("key is " + key);
            if(string.startsWith("type")){
                connectionType =  map.get(string);
            }
            else  if(string.startsWith("connectionName")){
                connectionName =  map.get(string);
            }
            else if (string.startsWith("source_")) {
                connectionProperties = Dao2TableUtil.buildJPAConnectionProperties("source", key, map.get(string), "Properties for source");
                connectionPropertiesList.add(connectionProperties);
            } else if (string.startsWith("emitter_")) {
                connectionProperties = Dao2TableUtil.buildJPAConnectionProperties("emitter", key, map.get(string), "Properties for emitter");
                connectionPropertiesList.add(connectionProperties);
            } else if (string.startsWith("persistentStores_")) {
                connectionProperties = Dao2TableUtil.buildJPAConnectionProperties("persistentStore",  key, map.get(string), "Properties for persistentStore");
                connectionPropertiesList.add(connectionProperties);
            }
        }

        Connections connections = new Connections();
        connections.setConnectionName(connectionName);
        connections.setConnectionType(connectionType);

        connectionsDAO.insert(connections);

        for(ConnectionProperties jpaConnectionProperties: connectionPropertiesList){
            jpaConnectionProperties.setConnections(connections);
            jpaConnectionProperties.getId().setConnectionName(connectionName);
            connectionPropertiesDAO.insert(jpaConnectionProperties);
        }


        restWrapper = new RestWrapper(connections, RestWrapper.OK);
        LOGGER.info("Connection saved by " + principal.getName());


        return restWrapper;
    }

    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    @ResponseBody
    public RestWrapper list(
                            @RequestParam(value = "page", defaultValue = "0") int startPage,
                            @RequestParam(value = "size", defaultValue = "10") int pageSize, Principal principal) {

        RestWrapper restWrapper = null;
        try {
            List<Connections> connectionsList = connectionsDAO.list(startPage, pageSize);
            Long counter = connectionsDAO.totalRecordCount();
            List<com.wipro.ats.bdre.md.beans.table.Connections> beanConnectionsList = new ArrayList<>();
            for(Connections connection: connectionsList){
                com.wipro.ats.bdre.md.beans.table.Connections beanConnection = new com.wipro.ats.bdre.md.beans.table.Connections();
                beanConnection.setConnectionName(connection.getConnectionName());
                beanConnection.setConnectionType(connection.getConnectionType());
                beanConnection.setCounter(counter.intValue());
                if(connection.getDescription() != null)
                    beanConnection.setDescription(connection.getDescription());
                beanConnectionsList.add(beanConnection);
            }

            restWrapper = new RestWrapper(beanConnectionsList, RestWrapper.OK);
            LOGGER.info("All records listed from Connections by User:" + principal.getName());
        }catch (SecurityException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        } catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    @RequestMapping(value = {"/listbytype/{type}"}, method = RequestMethod.GET)
    @ResponseBody
    public RestWrapper listByConnectionType(
            @PathVariable("type") String type,
            @RequestParam(value = "page", defaultValue = "0") int startPage,
            @RequestParam(value = "size", defaultValue = "10") int pageSize, Principal principal) {

        RestWrapper restWrapper = null;
        try {
            List<Connections> connectionsList = connectionsDAO.listByConnectionType(type,startPage, pageSize);
            LOGGER.info("inside md-rest-api "+Arrays.asList(connectionsList));
            Long counter = connectionsDAO.totalRecordCount();
            List<com.wipro.ats.bdre.md.beans.table.Connections> beanConnectionsList = new ArrayList<>();
            for(Connections connection: connectionsList){
                com.wipro.ats.bdre.md.beans.table.Connections beanConnection = new com.wipro.ats.bdre.md.beans.table.Connections();
                beanConnection.setConnectionName(connection.getConnectionName());
                beanConnection.setConnectionType(connection.getConnectionType());
                beanConnection.setCounter(counter.intValue());
                if(connection.getDescription() != null)
                    beanConnection.setDescription(connection.getDescription());
                beanConnectionsList.add(beanConnection);
            }

            restWrapper = new RestWrapper(beanConnectionsList, RestWrapper.OK);
            LOGGER.info("All records listed from Connections by User:" + principal.getName());
        }catch (SecurityException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        } catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }


    @RequestMapping(value = {"/list/{id}"}, method = RequestMethod.GET)
    @ResponseBody
    public RestWrapper list(@RequestParam(value = "page", defaultValue = "0") int startPage,
                            @RequestParam(value = "size", defaultValue = "10") int pageSize,
                            @PathVariable("id") String connectionName, Principal principal) {

        RestWrapper restWrapper = null;
        try{
            Connections connections = connectionsDAO.get(connectionName);
            List<com.wipro.ats.bdre.md.beans.table.ConnectionProperties> returnBeanPropertiesList = new ArrayList<>();
            List<ConnectionProperties> connectionPropertiesList = new ArrayList<>();
            connectionPropertiesList = connectionPropertiesDAO.getConnectionsByConnectionName(connectionName,startPage,pageSize);
            Integer counter = connectionPropertiesDAO.recordCountByConnectionName(connectionName);
            for (ConnectionProperties connectionProperties: connectionPropertiesList){
                com.wipro.ats.bdre.md.beans.table.ConnectionProperties returnProperties = new com.wipro.ats.bdre.md.beans.table.ConnectionProperties();
                returnProperties.setConnectionName(connectionProperties.getId().getConnectionName());
                returnProperties.setConfigGroup(connectionProperties.getConfigGroup());
                returnProperties.setPropKey(connectionProperties.getId().getPropKey());
                returnProperties.setPropValue(connectionProperties.getPropValue());
                returnProperties.setDescription(connectionProperties.getDescription());
                returnProperties.setCounter(counter);

                returnBeanPropertiesList.add(returnProperties);
            }

                restWrapper = new RestWrapper(returnBeanPropertiesList, RestWrapper.OK);
                LOGGER.info("Record with ID:" + connectionName + "selected from Properties by User:" + principal.getName());

                } catch (MetadataException e) {
                    LOGGER.error(e);
                    restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
                }

                return restWrapper;
            }


    @RequestMapping(value = {"/optionslist/{type}"}, method = RequestMethod.POST)
    @ResponseBody
    public RestWrapperOptions listOptions(@PathVariable("type") String type) {

        RestWrapperOptions restWrapperOptions = null;
        try {
            List<Connections> connectionsList=connectionsDAO.listByConnectionType(type,0,0);

            List<RestWrapperOptions.Option> options = new ArrayList<RestWrapperOptions.Option>();

            for (Connections connections : connectionsList) {
                RestWrapperOptions.Option option = new RestWrapperOptions.Option(connections.getConnectionName(),connections.getConnectionName());
                options.add(option);
                LOGGER.info(option.getDisplayText());
            }

            restWrapperOptions = new RestWrapperOptions(options, RestWrapperOptions.OK);
        } catch (Exception e) {
            LOGGER.error(e);
            restWrapperOptions = new RestWrapperOptions(e.getMessage(), RestWrapperOptions.ERROR);
        }
        return restWrapperOptions;
    }

    @RequestMapping(value = {"/{id}/{key}"}, method = RequestMethod.GET)
    @ResponseBody
    public RestWrapperOptions getConnection(@PathVariable("id") String connectionName,
                                            @PathVariable("key") String key, Principal principal) {

        RestWrapperOptions restWrapperOptions = null;
        try{
            LOGGER.info("connectionName is "+connectionName+" key is "+key);
            ConnectionPropertiesId connectionPropertiesId=new ConnectionPropertiesId();
            connectionPropertiesId.setConnectionName(connectionName);
            connectionPropertiesId.setPropKey(key);
            ConnectionProperties connectionProperties=connectionPropertiesDAO.getConnectionsById(connectionPropertiesId);
            String topics=connectionProperties.getPropValue();
            String[] topicList=topics.split(",");
            LOGGER.info("topic is "+topics);


            List<RestWrapperOptions.Option> options = new ArrayList<RestWrapperOptions.Option>();

            for (String tmp : topicList) {
                RestWrapperOptions.Option option = new RestWrapperOptions.Option(tmp,tmp);
                options.add(option);
                LOGGER.info(option.getDisplayText());
            }

            restWrapperOptions = new RestWrapperOptions(options, RestWrapperOptions.OK);
            LOGGER.info("Record with ID:" + connectionName + "selected from Properties by User:" + principal.getName());

        } catch (MetadataException e) {
            LOGGER.error(e);
            restWrapperOptions = new RestWrapperOptions(e.getMessage(), RestWrapperOptions.ERROR);
        }

        return restWrapperOptions;
    }



    @RequestMapping(value = {"/{id}"}, method = RequestMethod.DELETE)
    @ResponseBody
    public RestWrapper deleteConnection(@PathVariable("id") String connectionName,
                                             Principal principal) {

        RestWrapper restWrapper = null;
        try {
            connectionsDAO.delete(connectionName);
            restWrapper = new RestWrapper("success", RestWrapper.OK);
        }
        catch (Exception e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }



    @RequestMapping(value = {"/update/{id}"}, method = RequestMethod.POST)
    @ResponseBody
    public RestWrapper updateConnection(@PathVariable("id") String connectionName,
                                        @RequestParam(value = "propKey") String key,
                                        @RequestParam(value = "propValue") String value,
                                        Principal principal) {

        RestWrapper restWrapper = null;
        try {
            LOGGER.info("connectionName is "+connectionName);
            LOGGER.info("propKey is "+key);
            LOGGER.info("propValue is "+value);
            restWrapper = new RestWrapper("success", RestWrapper.OK);
        }
        catch (Exception e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }


}
