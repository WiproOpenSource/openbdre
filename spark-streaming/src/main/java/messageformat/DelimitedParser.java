package messageformat;

import com.wipro.ats.bdre.md.api.GetProperties;
import com.wipro.ats.bdre.md.api.StreamingMessagesAPI;
import com.wipro.ats.bdre.md.dao.jpa.Messages;

import java.util.Properties;

/**
 * Created by cloudera on 5/21/17.
 */
public class DelimitedParser implements MessageParser{
     public Object[] parseRecord(String record, Integer pid) throws Exception{
         try {

             System.out.println("pid inside delimited log parser = " + pid);

             GetProperties getProperties = new GetProperties();
             Properties properties = getProperties.getProperties(pid.toString(), "message");
             String messageName = properties.getProperty("messageName");
             StreamingMessagesAPI streamingMessagesAPI = new StreamingMessagesAPI();
             Messages messages = streamingMessagesAPI.getMessage(messageName);

             String delimiter = messages.getDelimiter();
             String[] parsedRecords = record.split(delimiter);
             String schemaString = messages.getMessageSchema();
             Object[] returnRecords=new Object[parsedRecords.length];
             int i=0;
             for (String fieldName : schemaString.split(",")) {
                 //String columnName = fieldName.split(":")[0];
                 String dataType = fieldName.split(":")[1];
                 switch(dataType)
                 {
                     case "Integer":
                         returnRecords[i]=Integer.parseInt(parsedRecords[i]);
                         break;
                     case "Double":
                         returnRecords[i]=Double.parseDouble(parsedRecords[i]);
                         break;
                     case "Float":
                         returnRecords[i]=Float.parseFloat(parsedRecords[i]);
                         break;
                     case "Long":
                         returnRecords[i]=Long.parseLong(parsedRecords[i]);
                         break;
                     default:
                         returnRecords[i]=parsedRecords[i];
                         break;

                 }
                 i++;
             }

             return returnRecords;
         }catch (Exception e){
             System.out.println("e = " + e);
             e.printStackTrace();
             throw e;
         }
     }
}

