package messageformat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.avro.data.Json;
import org.apache.commons.collections.map.HashedMap;
import org.apache.spark.sql.SQLContext;
import org.jsonschema2pojo.SchemaGenerator;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by cloudera on 6/22/17.
 */
public class JsonParser implements MessageParser{
    @Override
    public Object[] parseRecord(String line, Integer sourcePid) throws Exception {
       // SQLContext sqlContext = new SQLContext();
        return new Object[0];
    }
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static String columnName;
    private static List<String> columnsList = new ArrayList<>();
    private static Map<String, String> columnsDataTypesMap = new LinkedHashMap<>();

    public static void main(String[] args) throws Exception {
        /*try {
            SchemaGenerator schemaGenerator = new SchemaGenerator();
            ObjectNode jsonSchema = schemaGenerator.schemaFromExample(new File("/home/cloudera/tweet.json").toURI().toURL());
            System.out.println("jsonSchema.toString() = " + jsonSchema.toString());

            JsonNode rootJson = objectMapper.readTree(jsonSchema.toString());
            System.out.println("rootJson.toString() = " + rootJson.toString());
            JsonParser jsonParser = new JsonParser();
            jsonParser.recurvisefx(rootJson,"");
            System.out.println("columnNames = " + columnsList);
            System.out.println("columnsDataTypesMap = " + columnsDataTypesMap.toString());

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }*/
    }


    public void parseJson(String filePath) throws Exception {
        try {
            SchemaGenerator schemaGenerator = new SchemaGenerator();
            ObjectNode jsonSchema = schemaGenerator.schemaFromExample(new File(filePath).toURI().toURL());
            System.out.println("jsonSchema.toString() = " + jsonSchema.toString());

            JsonNode rootJson = objectMapper.readTree(jsonSchema.toString());
            System.out.println("rootJson.toString() = " + rootJson.toString());
            JsonParser jsonParser = new JsonParser();
            jsonParser.recurvisefx(rootJson,"");
            System.out.println("columnNames = " + columnsList);
            System.out.println("columnsDataTypesMap = " + columnsDataTypesMap.toString());

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }



    public void recurvisefx(JsonNode jsonNode, String key) {
        JsonNode jsonNode2 = jsonNode.path("properties");
        Iterator i = jsonNode2.fields();
        while (i.hasNext()) {
            Map.Entry<String, JsonNode> me = (Map.Entry<String, JsonNode>) i.next();
            System.out.println("me.getKey() = " + me.getKey());
            JsonNode childJson = (JsonNode) me.getValue();
            System.out.println("me.getValue() = " + childJson.toString());
            if (childJson.get("type").asText().equalsIgnoreCase("object")) {
                recurvisefx(childJson, key+"."+me.getKey());
            } else if (childJson.get("type").asText().equalsIgnoreCase("array") && !childJson.path("items").isMissingNode()) {
                recurvisefx(childJson.path("items"),key+"."+me.getKey());
            } else {
                columnsList.add(me.getKey());
                String columnName = (key+"."+me.getKey()).substring(1);
                columnsDataTypesMap.put(columnName, childJson.get("type").asText());
            }

        }
    }
}