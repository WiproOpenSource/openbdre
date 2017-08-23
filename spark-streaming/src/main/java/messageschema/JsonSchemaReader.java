package messageschema;

import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cloudera on 7/18/17.
 */
public class JsonSchemaReader {
    public  Node root = new Node("");
    public static final Map<String,DataType> dataTypesMap = new SGDataTypes().dataTypesMap;

    public static void main(String[] args) {
        JsonSchemaReader jsonSchemaReader = new JsonSchemaReader();
        Map<String,String> columnDataTypesMap = new HashMap<>();
        columnDataTypesMap.put("e","String");
        columnDataTypesMap.put("a.c.1","Integer");
        columnDataTypesMap.put("a.d.2","String");
        columnDataTypesMap.put("b","Float");
        jsonSchemaReader.generateJsonSchema(columnDataTypesMap);
    }

    public StructType generateJsonSchema(Map<String,String> columnDataTypesMap) {
        System.out.println("columnDataTypesMap in json parser = " + columnDataTypesMap);
        for(String column: columnDataTypesMap.keySet()){
            generateTree(root,column,columnDataTypesMap.get(column));
        }
        StructType st = generateStructType(root);
        Node root = new Node("");
        System.out.println("st = " + st);
        return st;
    }

    public StructType generateStructType(Node node){
        
        StructType structType = new StructType();
        List<StructField> structFieldList = new ArrayList<>();

        for (Node each : node.getChildNodes()) {
            if(each.isLeaf()){
                String dataType = each.getDataType();
                StructField structField = null;
                if(dataType.equalsIgnoreCase("Integer") ||dataType.equalsIgnoreCase("Long") || dataType.equalsIgnoreCase("Short") || dataType.equalsIgnoreCase("Byte") ) {
                    structField = DataTypes.createStructField(each.getColumn(), dataTypesMap.get("Long"), true);
                    System.out.println("Long datatype for "+each.getColumn());
                }
                else
                    structField= DataTypes.createStructField(each.getColumn(), dataTypesMap.get(dataType),true);
                structFieldList.add(structField);
            }
            else {
                StructField structField = DataTypes.createStructField(each.getColumn(),  generateStructType(each),true);
                structFieldList.add(structField);
            }
            structType = DataTypes.createStructType(structFieldList);
        }
        return structType;
    }

    public void generateTree(Node node,String column,String dataType){
        if(!column.contains(".")){
            Node childNode = Node.addChild(node,column);
            childNode.setDataType(dataType);
        }
        else {
            String column2 = column.substring(0, column.indexOf("."));
            String childs2 = column.substring(column.indexOf(".") + 1);
            boolean flag = false;
            List<Node> childNodes = node.getChildNodes();
            for(Node node1: childNodes){
                if(node1.getColumn().equals(column2)) {
                    flag = true;
                    generateTree(node1, childs2,dataType);
                }
            }
            if(!flag) {
                Node newNode = Node.addChild(node, column2);
                generateTree(newNode, childs2,dataType);
            }
        }
    }
}
