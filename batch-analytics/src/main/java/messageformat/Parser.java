package messageformat;

import com.wipro.ats.bdre.md.api.GetProperties;
import com.wipro.ats.bdre.md.api.StreamingMessagesAPI;
import com.wipro.ats.bdre.md.dao.jpa.Messages;

import java.io.Serializable;
import java.util.Properties;

/**
 * Created by cloudera on 6/8/17.
 */
public class Parser implements Serializable{
    public static final String MESSAGEFORMATPACKAGE = "messageformat.";
    public static Object[] parseMessage(String record,Integer pid) throws Exception{
        try {
            Object[] attributes = new Object[]{};

            String messageType="";
            GetProperties getProperties=new GetProperties();
            Properties properties=  getProperties.getProperties(pid.toString(),"message");
            String messageName = properties.getProperty("messageName");

            StreamingMessagesAPI streamingMessagesAPI = new StreamingMessagesAPI();
            Messages messages=streamingMessagesAPI.getMessage(messageName);
            messageType=messages.getFormat();

            System.out.println("messageType = " + messageType);
            String messageClassName = MESSAGEFORMATPACKAGE+messageType+"Parser";

            Class sourceClass = Class.forName(messageClassName);
            MessageParser messageObject = (MessageParser)sourceClass.newInstance();
            attributes = messageObject.parseRecord(record,pid);
            System.out.println("attributes = " + attributes);
            return attributes;
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("e = " + e);
            throw e;
        }

    }

}
