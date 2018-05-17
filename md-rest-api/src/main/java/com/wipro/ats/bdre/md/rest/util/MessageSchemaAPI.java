package com.wipro.ats.bdre.md.rest.util;

import com.wipro.ats.bdre.exception.MetadataException;
import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.beans.table.MessageColumnSchema;
import com.wipro.ats.bdre.md.dao.ConfigurationPropertiesDAO;
import com.wipro.ats.bdre.md.dao.ConnectionsDAO;
import com.wipro.ats.bdre.md.dao.MessagesDAO;
import com.wipro.ats.bdre.md.dao.jpa.ConfigurationProperties;
import com.wipro.ats.bdre.md.dao.jpa.ConfigurationPropertiesId;
import com.wipro.ats.bdre.md.dao.jpa.Connections;
import com.wipro.ats.bdre.md.dao.jpa.Messages;
import com.wipro.ats.bdre.md.rest.RestWrapper;
import com.wipro.ats.bdre.md.rest.RestWrapperOptions;
import com.wipro.ats.bdre.md.rest.ext.DataLoadAPI;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by cloudera on 5/21/17.
 */
@Controller
@RequestMapping("/message")
public class MessageSchemaAPI extends MetadataAPIBase {

    private static final Logger LOGGER = Logger.getLogger(DataLoadAPI.class);

    @Autowired
    MessagesDAO messagesDAO;
    @Autowired
    ConnectionsDAO connectionsDAO;
   @Autowired
    ConfigurationPropertiesDAO configurationPropertiesDAO;
    @RequestMapping(value = {"/createjobs"}, method = RequestMethod.POST)
    @ResponseBody
    public RestWrapper createJob(HttpServletRequest request, Principal principal) {
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
        RestWrapper restWrapper = null;
        String messageName="";
        String messageFormat="";
        String schema="";
        String connectionName="";
        String delimiter="";
        String topicName="";
        for (String string : map.keySet()) {
            if (string.startsWith("rawtablecolumn_")) {
                String columnName = string.replaceAll("rawtablecolumn_", "");
                String dataType = map.get(string);
                LOGGER.info("column name is " + columnName + " datatype is " + dataType);
                    schema = schema + "," + columnName + ":" + dataType;
                LOGGER.info("schema is " + schema);
            }
            if (string.startsWith("fileformat_")) {
                if (string.endsWith("messageName")) {
                    messageName = map.get(string);
                    LOGGER.info("messageName is " + messageName);
                }
                if (string.endsWith("connectionName")) {
                    connectionName = map.get(string);
                    LOGGER.info("connectionName is " + connectionName);
                }
                  if (string.endsWith("delimiter")) {
                    delimiter = map.get(string);
                    LOGGER.info("delimiter is " + delimiter);
                }
                 if (string.endsWith("delimiter")) {
                    delimiter = map.get(string);
                    LOGGER.info("delimiter is " + delimiter);
                }
                if (string.endsWith("fileformat")){
                    messageFormat = map.get(string);
                    LOGGER.info("messageFormat is " + messageFormat);
                }
                if (string.endsWith("topicName")){
                    topicName = map.get(string);
                    LOGGER.info("topicName is " + topicName);
                }

            }
        }

            Connections jpaConnections=connectionsDAO.get(connectionName);
            Messages messages=new Messages();
            messages.setConnections(jpaConnections);
            messages.setMessageName(messageName);
            messages.setFormat(messageFormat);
            messages.setDelimiter(delimiter);
            messages.setMessageSchema(schema.substring(1,schema.length()));
            LOGGER.info(messageName+" "+messageFormat+" "+schema+" "+connectionName+" "+delimiter);
            messagesDAO.insert(messages);

            String key="";
            String configGroup="";
            if(jpaConnections.getConnectionType().contains("Kafka")){
                key="topicName";
                configGroup="Kafka";
            }
             ConfigurationPropertiesId configurationPropertiesId=new ConfigurationPropertiesId();
             configurationPropertiesId.setMessageName(messageName);
             configurationPropertiesId.setPropKey(key);
             ConfigurationProperties configurationProperties=new ConfigurationProperties();
             configurationProperties.setId(configurationPropertiesId);
             configurationProperties.setPropValue(topicName);
             configurationProperties.setConfigGroup(configGroup);
             configurationPropertiesDAO.insert(configurationProperties);

        restWrapper = new RestWrapper(null, RestWrapper.OK);
        LOGGER.info("Process and Properties for data load process inserted by" + principal.getName());


        return restWrapper;
    }



    @RequestMapping(value = {"/optionslist"}, method = RequestMethod.POST)


    @ResponseBody
    public RestWrapperOptions listOptions() {

        RestWrapperOptions restWrapperOptions = null;
        try {
              List<Messages> messagesList=messagesDAO.list(0,0);

            List<RestWrapperOptions.Option> options = new ArrayList<RestWrapperOptions.Option>();

            for (Messages messages : messagesList) {
                RestWrapperOptions.Option option = new RestWrapperOptions.Option(messages.getMessageName(),messages.getMessageName());
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




    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    @ResponseBody public
    RestWrapper list(@RequestParam(value = "page", defaultValue = "0") int startPage,
                     @RequestParam(value = "size", defaultValue = "10") int pageSize, Principal principal) {
        RestWrapper restWrapper = null;
        try {

            List<com.wipro.ats.bdre.md.dao.jpa.Messages> jpaMessageList = messagesDAO.list(startPage, pageSize);
            Integer counter=jpaMessageList.size();
            List<com.wipro.ats.bdre.md.beans.table.Messages> messagesList = new ArrayList<>();
            for (com.wipro.ats.bdre.md.dao.jpa.Messages messages : jpaMessageList) {
                com.wipro.ats.bdre.md.beans.table.Messages messages1=new com.wipro.ats.bdre.md.beans.table.Messages();
                messages1.setMessageSchema(messages.getMessageSchema());
                messages1.setMessagename(messages.getMessageName());
                messages1.setFormat(messages.getFormat());
                messages1.setConnectionName(messages.getConnections().getConnectionName());
                messages1.setCounter(counter);
                messagesList.add(messages1);
            }
            restWrapper = new RestWrapper(messagesList, RestWrapper.OK);
            LOGGER.info("All records listed from Message list by User:" + principal.getName());

        } catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }



    @RequestMapping(value = {"/{id}"}, method = RequestMethod.GET)


    @ResponseBody
    public RestWrapper list(@RequestParam(value = "page", defaultValue = "0") int startPage,
                            @RequestParam(value = "size", defaultValue = "10") int pageSize,
                            @PathVariable("id") String messageId, Principal principal) {

        RestWrapper restWrapper = null;
        try{
            Messages message = messagesDAO.get(messageId);
            String schema = message.getMessageSchema();
            String[] columnAndDataTypes = schema.split(",");
           List<MessageColumnSchema> messageColumnSchemaList=new ArrayList<>();
            int counter=columnAndDataTypes.length;
            for(String s : columnAndDataTypes){
                if(s!=null || s!="") {
                    String tmp[] = s.split(":");
                    MessageColumnSchema messageColumnSchema = new MessageColumnSchema();
                    messageColumnSchema.setColumnName(tmp[0]);
                    messageColumnSchema.setDataType(tmp[1]);
                    messageColumnSchema.setCounter(counter);
                    messageColumnSchemaList.add(messageColumnSchema);
                }
            }
            restWrapper = new RestWrapper(messageColumnSchemaList, RestWrapperOptions.OK);
        } catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }


    @RequestMapping(value = {"/{id}/{k}"}, method = RequestMethod.DELETE)
    @ResponseBody
    public RestWrapper delete (@PathVariable("id") String messageId,
                               @PathVariable("k") String column, Principal principal) {

        RestWrapper restWrapper = null;
        String messageSchema;
        try{
            int i,j;
            Messages message = messagesDAO.get(messageId);
            String schema = message.getMessageSchema();
            String[] columnAndDataTypes = schema.split(",");
            int count = columnAndDataTypes.length;
            if(count==1){
                messageSchema="";
            }
            else {
                for (i = 0; i < (count-1); i++) {
                    String[] tmp = columnAndDataTypes[i].split(":");
                    if (tmp[0].equals(column)) {
                        for (j = i; j < (count - 1); j++) {
                            columnAndDataTypes[j] = columnAndDataTypes[j + 1];
                        }
                        break;
                    }
                }
                StringBuilder builder = new StringBuilder(columnAndDataTypes[0]);
                for (i = 1; i < (count-1); i++) {
                    builder.append(",");
                    builder.append(columnAndDataTypes[i]);
                }

                 messageSchema = builder.toString();
            }
            Messages updatedMessage = new Messages();
            updatedMessage.setMessageName(message.getMessageName());
            updatedMessage.setConnections(message.getConnections());
            updatedMessage.setFormat(message.getFormat());
            updatedMessage.setDelimiter(message.getDelimiter());
            updatedMessage.setMessageSchema(messageSchema);

            messagesDAO.update(updatedMessage);
            restWrapper = new RestWrapper(null, RestWrapper.OK);
            LOGGER.info("Record with ID:" + messageId + "," + column + " deleted from Message by User:" + principal.getName());

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



    @RequestMapping (value = {"/{id}"}, method = RequestMethod.DELETE)
    @ResponseBody
    public RestWrapper delete (@PathVariable("id") String messageId, Principal principal) {
        RestWrapper restWrapper = null;
        try{
            messagesDAO.delete(messageId);
            restWrapper = new RestWrapper(null, RestWrapper.OK);
            LOGGER.info("Record  with ID:" + messageId + " deleted from Message by User:" + principal.getName());

        } catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        } catch (SecurityException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    @RequestMapping(value = {"/{id}/"}, method = RequestMethod.PUT)
    @ResponseBody
    public RestWrapper insert (@PathVariable("id") String messageId,
                               @RequestParam(value = "columnName") String column,
                               @RequestParam(value = "dataType") String type, Principal principal) {
        RestWrapper restWrapper = null;
        int flag=0;
        String messageSchema = "";
        try{
            Messages message = messagesDAO.get(messageId);
            String schema = message.getMessageSchema();
            if(!schema.equals("")) {
                String[] columnAndDataTypes = schema.split(",");
                for (String s : columnAndDataTypes) {
                    String[] tmp = s.split(":");
                    if (tmp[0].equals(column)) {
                        messageSchema = schema;
                        flag = 1;
                        break;
                    }
                }
            }
            if(flag==0){
                StringBuilder builder = new StringBuilder(schema);
                if(!schema.equals("")){ builder.append(",");}
                builder.append(column);
                builder.append(":");
                builder.append(type);
                messageSchema = builder.toString();
            }

            Messages updatedMessage = new Messages();
            updatedMessage.setMessageName(message.getMessageName());
            updatedMessage.setMessageSchema(messageSchema);
            updatedMessage.setDelimiter(message.getDelimiter());
            updatedMessage.setFormat(message.getFormat());
            updatedMessage.setConnections(message.getConnections());
            messagesDAO.update(updatedMessage);
            restWrapper = new RestWrapper(messageSchema, RestWrapper.OK);
            LOGGER.info("Record with ID:" + messageId + " inserted in Messages by User:" + principal.getName());

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


    @RequestMapping(value = {"/{id}"}, method = RequestMethod.POST)
    @ResponseBody
    public RestWrapper update (@PathVariable("id") String messageId,
                               @RequestParam(value = "jtRecordKey") String columnName,
                               @RequestParam(value = "columnName") String column,
                               @RequestParam(value = "dataType") String type, Principal principal) {
        RestWrapper restWrapper = null;
        try{
            int i;
            Messages message = messagesDAO.get(messageId);
            String schema = message.getMessageSchema();
            String[] columnAndDataTypes = schema.split(",");
            int count = columnAndDataTypes.length;
            for(i=0;i<count;i++){
                String[] tmp = columnAndDataTypes[i].split(":");
                if(columnName.equals(tmp[0])){
                    tmp[0] = column;
                    tmp[1] = type;
                    StringBuilder builder = new StringBuilder(tmp[0]);
                    builder.append(":");
                    builder.append(tmp[1]);
                    columnAndDataTypes[i]=builder.toString();
                    break;
                }
            }
            StringBuilder builder = new StringBuilder(columnAndDataTypes[0]);

            for (i = 1; i < count; i++) {
                builder.append(",");
                builder.append(columnAndDataTypes[i]);
            }

            String messageSchema = builder.toString();

            Messages updatedMessage = new Messages();
            updatedMessage.setFormat(message.getFormat());
            updatedMessage.setMessageName(messageId);
            updatedMessage.setConnections(message.getConnections());
            updatedMessage.setDelimiter(message.getDelimiter());
            updatedMessage.setMessageSchema(messageSchema);
            messagesDAO.update(updatedMessage);
            restWrapper = new RestWrapper("success", RestWrapper.OK);
            LOGGER.info("Record with ID:" + messageId + " updated in Messages by User:" + principal.getName());

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
    // Read from request
}

