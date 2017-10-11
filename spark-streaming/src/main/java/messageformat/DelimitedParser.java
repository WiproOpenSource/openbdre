package messageformat;

import com.wipro.ats.bdre.md.api.GetProperties;
import com.wipro.ats.bdre.md.api.StreamingMessagesAPI;
import com.wipro.ats.bdre.md.dao.jpa.Messages;

import java.util.Properties;

/**
 * Created by cloudera on 5/21/17.
 */
public class DelimitedParser implements MessageParser{
     public String[] parseRecord(String record, Integer pid) throws Exception{
         try {

             System.out.println("pid inside delimited log parser = " + pid);

             GetProperties getProperties = new GetProperties();
             Properties properties = getProperties.getProperties(pid.toString(), "message");
             String messageName = properties.getProperty("messageName");
             StreamingMessagesAPI streamingMessagesAPI = new StreamingMessagesAPI();
             Messages messages = streamingMessagesAPI.getMessage(messageName);

             String delimiter = messages.getDelimiter();
             String[] parsedRecords = record.split(delimiter);
             return parsedRecords;
         }catch (Exception e){
             System.out.println("e = " + e);
             e.printStackTrace();
             throw e;
         }
     }
}

