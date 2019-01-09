package com.wipro.ats.bdre.im.jsonschema;

import com.wipro.ats.bdre.im.etl.api.CreateRawBaseTables;
import com.wipro.ats.bdre.md.api.GetProperties;
import jdk.nashorn.internal.runtime.options.LoggingOption;

import java.util.*;
import java.util.logging.Logger;

/**
 * Created by cloudera on 7/18/17.
 */
public class JsonSchemaReader {
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(JsonSchemaReader.class);
    public  Node root = new Node("");

    public String generateJsonSchema(String rawColumnsWithDataTypes) {

        LOGGER.info("Passed column and DataType"+rawColumnsWithDataTypes);
        LinkedHashMap<String, String> columnDataTypesMap = new LinkedHashMap<String, String>();
        for (String s : rawColumnsWithDataTypes.split(",")) {
            String[] arr = s.split(" ");
            LOGGER.info("column and datatypes"+arr.toString());
            columnDataTypesMap.put(arr[0],arr[1]);
        }
        LOGGER.info("columnDataTypesMap"+columnDataTypesMap.toString());

        for(String column: columnDataTypesMap.keySet()){
            generateTree(root,column,columnDataTypesMap.get(column));
        }
        Node.printTree(root);

        List<Node> childNodes = root.getChildNodes();
        StringBuilder hiveSchema = new StringBuilder();

        for(Node each: childNodes){
            if(each.isLeaf()){
                hiveSchema.append(each.getColumn()+" "+each.getDataType());
                hiveSchema.append(",");
            }
            else{
                hiveSchema.append(each.getColumn()+" ");
                hiveSchema.append(generateStructType(each));
                hiveSchema.append(",");
            }
        }
        hiveSchema.deleteCharAt(hiveSchema.length()-1);

        Node root = new Node("");
        LOGGER.info("st = " + hiveSchema.toString());
        return hiveSchema.toString();
    }

    public String generateStructType(Node node){
        StringBuilder structSchema = new StringBuilder("struct<");
        String leaf=null;
            for (Node each : node.getChildNodes()) {
                if (each.isLeaf()) {
                    leaf = each.getColumn() + ":" + each.getDataType();
                } else {
                    String columnName=each.getColumn();
                    leaf=columnName+":"+generateStructType(each);
                }
                structSchema.append(leaf);
                structSchema.append(",");
            }
            structSchema.deleteCharAt(structSchema.length()-1);
        structSchema.append(">");

        return structSchema.toString();
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