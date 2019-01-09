package persistentstores;

import com.wipro.ats.bdre.md.api.GetProperties;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.mapreduce.TableOutputFormat;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.mapreduce.Job;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import persistentstores.persistIdgeneration.UUIDGenerator;
import scala.Tuple2;
import util.WrapperMessage;

import java.util.Date;
import java.util.Properties;
import java.util.HashMap;
import java.util.Map;
import org.apache.spark.broadcast.Broadcast;


/**
 * Created by cloudera on 5/21/17.
 */

public class HBasePersistentStore implements PersistentStore {
    @Override
    public void persist(JavaRDD emptyRDD, JavaPairDStream<String, WrapperMessage> inputDStream, Integer pid, Integer prevPid, StructType schema, Map<String,Broadcast<HashMap<String,String>>> broadcastMap,JavaStreamingContext jssc) throws Exception {
        try {

            JavaDStream<WrapperMessage> dStream = inputDStream.map(s -> s._2);

            JavaDStream<WrapperMessage> finalDStream = dStream.transform(new Function<JavaRDD<WrapperMessage>, JavaRDD<WrapperMessage>>() {
                @Override
                public JavaRDD<WrapperMessage> call(JavaRDD<WrapperMessage> wrapperMessageJavaRDD) throws Exception {
                    JavaRDD<Row> rowJavaRDD = wrapperMessageJavaRDD.map(record -> WrapperMessage.convertToRow(record));
                    Configuration config = null;
                    try {
                        config = HBaseConfiguration.create();
                        config.set("hbase.zookeeper.quorum", "127.0.0.1"); //Fetch from Connection
                        config.set("hbase.zookeeper.property.clientPort", "2181"); //Fetch from Connection
                        config.set("hbase.master", "127.0.0.1:60000"); //Fetch from Connection
                        HBaseAdmin.checkHBaseAvailable(config);
                        System.out.println("HBase is running!");
                    } catch (MasterNotRunningException e) {
                        System.out.println("HBase is not running!");
                        System.exit(1);
                    } catch (Exception ce) {
                        ce.printStackTrace();
                    }
                    String tableName = "apachelogs"; //Fetch from Connection
                    String columnFamily = "colfam"; //Fetch from Connection
                    config.set(TableInputFormat.INPUT_TABLE, tableName);
                    //Check if table exists and create if it doesn't exist

                    HBaseAdmin admin = new HBaseAdmin(config);
                    if (!admin.tableExists(tableName)) {
                        System.out.println("Table " + tableName + " not present, hence creating");
                        HTableDescriptor table = new HTableDescriptor(Bytes.toBytes(tableName));
                        HColumnDescriptor family = new HColumnDescriptor(Bytes.toBytes(columnFamily));
                        table.addFamily(family);
                        admin.createTable(table);
                    }
                    // new Hadoop API configuration
                    Job newAPIJobConfiguration1 = Job.getInstance(config);
                    newAPIJobConfiguration1.getConfiguration().set(TableOutputFormat.OUTPUT_TABLE, tableName);
                    newAPIJobConfiguration1.setOutputFormatClass(org.apache.hadoop.hbase.mapreduce.TableOutputFormat.class);
                    // create Key, Value pair to store in HBase
                    JavaPairRDD<ImmutableBytesWritable, Put> hbasePuts = rowJavaRDD.mapToPair(new PairFunction<Row, ImmutableBytesWritable, Put>() {
                        @Override
                        public Tuple2<ImmutableBytesWritable, Put> call(Row row) throws Exception {
                            Put put = new Put(Bytes.toBytes(new UUIDGenerator().generatePersistID(row)));
                            int index = 0;
                            for (String fieldName : row.schema().fieldNames()) {
                                System.out.println("fieldName = " + fieldName);
                                put.add(Bytes.toBytes(columnFamily), Bytes.toBytes(fieldName), Bytes.toBytes(row.get(index).toString()));
                                index++;
                            }
                            return new Tuple2<ImmutableBytesWritable, Put>(new ImmutableBytesWritable(), put);
                        }
                    });
                    // save to HBase- Spark built-in API method
                    hbasePuts.saveAsNewAPIHadoopDataset(newAPIJobConfiguration1.getConfiguration());
                    return emptyRDD;
                }
            });


            //adding empty output operation to finish flow, else spark would never execute the DAG
            finalDStream.foreachRDD(new Function<JavaRDD<WrapperMessage>, Void>() {
                @Override
                public Void call(JavaRDD<WrapperMessage> rowJavaRDD) throws Exception {
                    return null;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
