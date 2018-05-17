package com.wipro.ats.bdre.ml.sources;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.hive.HiveContext;
import org.apache.spark.sql.types.StructType;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by cloudera on 11/20/17.
 */
public class HDFSSource implements Serializable{
    public DataFrame getDataFrame(JavaSparkContext jsc, String hdfsPath, String nameNodeHost, String nameNodePort, String fileFormat, String delimiter, StructType schema){
        HiveContext sqlContext = new org.apache.spark.sql.hive.HiveContext(jsc);
        DataFrame df = null;
        if(fileFormat.equalsIgnoreCase("Delimited")){
            JavaRDD<String> data= jsc.textFile(hdfsPath);
            System.out.println("hdfsPath2 = " + hdfsPath);
            JavaRDD<Row> dataMain = data.map(new Function<String,Row>(){
                 public Row call(String s){
                     System.out.println("s = " + s);
                     String[] sarray = s.trim().split(delimiter);
                     Double[] values = new Double[sarray.length];
                     for (int i = 0; i < sarray.length; i++) {
                         values[i] = Double.parseDouble(sarray[i]);
                     }
                     System.out.println("Arrays.toString(values) = " + Arrays.toString(values));
                     return RowFactory.create(values);
                 }
             }
            );
            dataMain.take(2);
            df= sqlContext.createDataFrame(dataMain, schema);
            df.show();
        }
        else if(fileFormat.equalsIgnoreCase("Json")){
            df = sqlContext.read().json(hdfsPath);
        }
        return df;
    }
}