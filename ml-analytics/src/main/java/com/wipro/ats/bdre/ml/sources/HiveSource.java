package com.wipro.ats.bdre.ml.sources;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
//import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.hive.HiveContext;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import java.util.List;
import java.util.ArrayList;
/**
 * Created by cloudera on 11/19/17.
 */
public class HiveSource {
    public Dataset<Row> getDataFrame(JavaSparkContext jsc, String metastoreURI, String dbName, String tableName, StructType schema){
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
        //Dataset<Row> df = hiveContext.table(dbName+"."+tableName);
        SQLContext sqlContext=new SQLContext(jsc);
        ArrayList<Double> data=new ArrayList<>();
        data.add(1.2);
        data.add(2.0);
        JavaRDD<Row> rowRDD = jsc.parallelize(data).map((Double row) -> RowFactory.create(row));
        StructType schema1 = DataTypes.createStructType(
            new StructField[] { DataTypes.createStructField("number", DataTypes.DoubleType, false) });
        Dataset<Row> df = sqlContext.createDataFrame(rowRDD, schema1);
        return df;
    }
}
