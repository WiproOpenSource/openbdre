package messageschema;

import com.wipro.ats.bdre.md.api.GetProcess;
import com.wipro.ats.bdre.md.api.GetProperties;
import com.wipro.ats.bdre.md.api.StreamingMessagesAPI;
import com.wipro.ats.bdre.md.beans.GetPropertiesInfo;
import com.wipro.ats.bdre.md.beans.ProcessInfo;
import com.wipro.ats.bdre.md.dao.MessagesDAO;
import com.wipro.ats.bdre.md.dao.jpa.Messages;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by cloudera on 5/20/17.
 */
public class SchemaReader {


    @Autowired
    MessagesDAO messagesDAO;
    //TODO: update the schema automatically
    //String schemaString = "ipAddress clientIdentd userID dateTimeString method endpoint protocol responseCode contentSize";

    public StructType generateSchema(int pid) throws Exception{
        try {
            GetProcess getProcess = new GetProcess();
            ProcessInfo processInfo = getProcess.getProcess(pid);
            GetProperties getProperties = new GetProperties();

            Properties properties = getProperties.getProperties(processInfo.getProcessId().toString(), "message");
            String messageName = properties.getProperty("messageName");
            StreamingMessagesAPI streamingMessagesAPI = new StreamingMessagesAPI();
            Messages messages = streamingMessagesAPI.getMessage(messageName);
            String schemaString = messages.getMessageSchema();
            //Generate the schema based on the string of schema
            List<StructField> fields = new ArrayList<>();
            for (String fieldName : schemaString.split(",")) {
                String columnName = fieldName.split(":")[0];
                StructField field = DataTypes.createStructField(columnName, DataTypes.StringType, true);
                fields.add(field);
            }
            StructType schema = DataTypes.createStructType(fields);
            return schema;
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("e = " + e);
            throw e;
        }
    }
}