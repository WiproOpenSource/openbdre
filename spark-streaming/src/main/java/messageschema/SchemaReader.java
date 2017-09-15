package messageschema;

import com.wipro.ats.bdre.md.api.GetProcess;
import com.wipro.ats.bdre.md.api.GetProperties;
import com.wipro.ats.bdre.md.api.StreamingMessagesAPI;
import com.wipro.ats.bdre.md.beans.ProcessInfo;
import com.wipro.ats.bdre.md.dao.MessagesDAO;
import com.wipro.ats.bdre.md.dao.jpa.Messages;
import org.apache.commons.collections.map.LinkedMap;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Created by cloudera on 5/20/17.
 */
public class SchemaReader {


    @Autowired
    MessagesDAO messagesDAO;
    //TODO: update the schema automatically
    //String schemaString = "ipAddress clientIdentd userID dateTimeString method endpoint protocol responseCode contentSize";
    public static final Map<String,DataType> dataTypesMap = new SGDataTypes().dataTypesMap;

    public StructType generateSchema(int pid) throws Exception{
        try {
            GetProcess getProcess = new GetProcess();
            ProcessInfo processInfo = getProcess.getProcess(pid);
            GetProperties getProperties = new GetProperties();

            Properties properties = getProperties.getProperties(processInfo.getProcessId().toString(), "message");
            String messageName = properties.getProperty("messageName");
            StreamingMessagesAPI streamingMessagesAPI = new StreamingMessagesAPI();
            Messages messages = streamingMessagesAPI.getMessage(messageName);
            String format = messages.getFormat();
            String schemaString = messages.getMessageSchema();
            //Generate the schema based on the string of schema

            System.out.println("schemaString = " + schemaString);
            Map<String,String> columnDataTypeMap = new LinkedMap();
            for (String fieldName : schemaString.split(",")) {
                String columnName = fieldName.split(":")[0];
                String dataType = fieldName.split(":")[1];
                columnDataTypeMap.put(columnName,dataType);
            }
            System.out.println("columnDataTypeMap = " + columnDataTypeMap);

            List<StructField> fields = new ArrayList<>();
            StructType schema = new StructType();
            if(format.equalsIgnoreCase("Json") || format.equalsIgnoreCase("XML")){
                System.out.println(" Inside json or xml parser ");
                JsonSchemaReader jsonSchemaReader = new JsonSchemaReader();
                schema = jsonSchemaReader.generateJsonSchema(columnDataTypeMap);
            }
            else {
                for(String column: columnDataTypeMap.keySet()){
                    StructField field = DataTypes.createStructField(column, dataTypesMap.get(columnDataTypeMap.get(column)), true);
                    fields.add(field);
                }
                schema = DataTypes.createStructType(fields);
            }
            columnDataTypeMap.clear();

            return schema;
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("e = " + e);
            throw e;
        }
    }
}