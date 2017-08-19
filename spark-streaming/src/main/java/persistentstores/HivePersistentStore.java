package persistentstores;

import com.wipro.ats.bdre.md.api.GetProperties;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import util.WrapperMessage;

import java.util.Properties;

/**
 * Created by cloudera on 7/4/17.
 */
public class HivePersistentStore implements PersistentStore {
    /*@Override
    public void persist(DataFrame df, Integer pid, Integer prevPid) throws Exception {
        GetProperties getProperties = new GetProperties();
        Properties hiveProperties = getProperties.getProperties(String.valueOf(pid), "hive");
        SparkConf conf = new SparkConf().setAppName("SparkHive Example").setMaster("local");
        SparkContext sc = new SparkContext(conf);
        HiveContext hiveContext = new org.apache.spark.sql.hive.HiveContext(sc);
        //Add code to get the hive table schema from properties and create table if not exist
         hiveContext.sql("create table if not exist table_name ...");

        //String hdfsPath = hdfsProperties.getProperty("hdfs_path");

        df.show();
        df.write().mode("append").saveAsTable("table_name");

    }*/


    public static void main(String[] args) {

    }

    @Override
    public void persist(JavaRDD emptyRDD, JavaPairDStream<String, WrapperMessage> wrapperMessageJavaDStream, Integer pid, Integer prevPid, StructType schema) throws Exception {

    }
}
