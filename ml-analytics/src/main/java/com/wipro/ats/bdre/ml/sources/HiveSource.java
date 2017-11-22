package com.wipro.ats.bdre.ml.sources;

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.hive.HiveContext;
import org.apache.spark.sql.types.StructType;

/**
 * Created by cloudera on 11/19/17.
 */
public class HiveSource {
    public DataFrame getDataFrame(JavaSparkContext jsc, String metastoreURI, String dbName, String tableName, StructType schema){
        HiveContext hiveContext = new org.apache.spark.sql.hive.HiveContext(jsc);
        hiveContext.setConf("hive.metastore.uris", metastoreURI);

        DataFrame df = hiveContext.table(dbName+"."+tableName);
        return df;
    }
}
