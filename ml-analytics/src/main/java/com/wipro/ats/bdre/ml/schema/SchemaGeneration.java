package com.wipro.ats.bdre.ml.schema;

import org.apache.commons.collections.map.LinkedMap;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by cloudera on 11/19/17.
 */
public class SchemaGeneration {
    public static final Map<String,DataType> dataTypesMap = new SGDataTypes().dataTypesMap;

    public StructType generateSchemaFromString(String schemaString){
        Map<String,String> columnDataTypeMap = new LinkedMap();
        for (String fieldName : schemaString.split(",")) {
            String columnName = fieldName.split(":")[0];
            String dataType = fieldName.split(":")[1];
            columnDataTypeMap.put(columnName,dataType);
        }
        List<StructField> fields = new ArrayList<>();
        StructType schema = new StructType();
        for(String column: columnDataTypeMap.keySet()){
            StructField field = DataTypes.createStructField(column, dataTypesMap.get(columnDataTypeMap.get(column)), true);
            fields.add(field);
        }

        schema = DataTypes.createStructType(fields);
        columnDataTypeMap.clear();
        return schema;
    }
}
