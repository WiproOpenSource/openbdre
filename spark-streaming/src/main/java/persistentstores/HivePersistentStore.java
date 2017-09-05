package persistentstores;

import com.wipro.ats.bdre.md.api.GetProperties;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.hive.HiveContext;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import util.WrapperMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by cloudera on 7/4/17.
 */
public class HivePersistentStore implements PersistentStore {

    public void persist(DataFrame df, Integer pid, Integer prevPid) throws Exception {
        GetProperties getProperties = new GetProperties();
        Properties hiveProperties = getProperties.getProperties(String.valueOf(pid), "persistentStore");
        SparkConf conf = new SparkConf().setAppName("SparkHive Example").setMaster("local");
        SparkContext sc = new SparkContext(conf);
        HiveContext hiveContext = new org.apache.spark.sql.hive.HiveContext(sc);
        //Add code to get the hive table schema from properties and create table if not exist
        hiveContext.sql("create table if not exist table_name ...");

        //String hdfsPath = hdfsProperties.getProperty("hdfs_path");

        df.show();
        df.write().mode(SaveMode.Append).saveAsTable("table_name");

    }

    @Override
    public void persist(JavaRDD emptyRDD, JavaPairDStream<String, WrapperMessage> inputWrapperDStream, Integer pid, Integer prevPid, StructType schema, Map<String,Broadcast<HashMap<String,String>>> broadcastMap, JavaStreamingContext jssc) throws Exception {
        GetProperties getProperties = new GetProperties();
        Properties hiveProperties = getProperties.getProperties(String.valueOf(pid), "persistentStore");
        String metastoreURI = hiveProperties.getProperty("metastoreURI");
        String metaStoreWareHouseDir = hiveProperties.getProperty("metastoreWarehouseDir");
        String hiveDBName = hiveProperties.getProperty("hiveDBName");
        String hiveTableName = hiveProperties.getProperty("hiveTableName");
        String format = hiveProperties.getProperty("format");

        HiveContext hiveContext = new org.apache.spark.sql.hive.HiveContext(jssc.sparkContext().sc());
        hiveContext.setConf("hive.metastore.uris", metastoreURI);
        hiveContext.setConf("hive.metastore.warehouse.dir",metaStoreWareHouseDir);
        inputWrapperDStream.foreachRDD(new Function<JavaPairRDD<String, WrapperMessage>, Void>() {
            @Override
            public Void call(JavaPairRDD<String, WrapperMessage> pairRDD) throws Exception {

                JavaRDD<Row> inputRowRDD = pairRDD.map(s -> s._2.getRow());
                if(inputRowRDD.count() != 0) {
                    StructType schema1 = inputRowRDD.take(1).get(0).schema();
                    DataFrame df = hiveContext.createDataFrame(inputRowRDD, schema1);
                    df.write().format(format).mode(SaveMode.Append).saveAsTable(hiveDBName+"."+hiveTableName);
                }
                return null;
            }
        });

    }
}
