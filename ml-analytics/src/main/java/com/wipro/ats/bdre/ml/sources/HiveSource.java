package com.wipro.ats.bdre.ml.sources;
import org.apache.spark.sql.*;
import org.apache.spark.sql.types.StructType;
/**
 * Created by cloudera on 11/19/17.
 */
public class HiveSource {
    public Dataset<Row> getDataFrame(SparkSession sparkSession, String metastoreURI, String dbName, String tableName, StructType schema){
        sparkSession.sqlContext().setConf("mapreduce.input.fileinputformat.input.dir.recursive","true");
        System.out.println("dbName = " + dbName);
        System.out.println("tableName = " + tableName);
        sparkSession.sql("show tables").show();
        Dataset<Row> df = sparkSession.read().table(dbName+"."+tableName);
        //df.rea
        return df;

    }
}
