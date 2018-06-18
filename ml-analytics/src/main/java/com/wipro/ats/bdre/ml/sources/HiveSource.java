package com.wipro.ats.bdre.ml.sources;

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.hive.HiveContext;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.sql.SQLContext;

/**
 * Created by cloudera on 11/19/17.
 */
public class HiveSource {
    public DataFrame getDataFrame(JavaSparkContext jsc, String metastoreURI, String dbName, String tableName, StructType schema){
int i;
        HiveContext hiveContext = new org.apache.spark.sql.hive.HiveContext(jsc);
        //hiveContext.setConf("hive.metastore.uris", metastoreURI);
        System.out.println("dbName = " + dbName);
        System.out.println("tableName = " + tableName);
        String[] tableNames = hiveContext.tableNames(dbName);
        System.out.println("ciej");
        System.out.println(tableNames.length);
        for(i=0;i<tableNames.length;i++){
            System.out.println("ciej");
        System.out.println(tableNames[i]);}
        DataFrame df = hiveContext.table(dbName+"."+tableName);
        return df;
    }
}
