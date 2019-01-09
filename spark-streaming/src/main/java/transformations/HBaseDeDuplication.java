package transformations;

import com.wipro.ats.bdre.md.api.GetConnectionProperties;
import com.wipro.ats.bdre.md.api.GetConnections;
import com.wipro.ats.bdre.md.dao.jpa.Connections;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.spark.JavaHBaseContext;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import scala.Tuple2;
import util.WrapperMessage;

import java.util.Properties;

/**
 * Created by cloudera on 8/4/17.
 */
public class HBaseDeDuplication {

    public JavaPairDStream<String, WrapperMessage> convertJavaPairDstream(JavaPairDStream<String, WrapperMessage> businesskeyvaluestream,JavaStreamingContext jssc, String hbaseConnectionName, String hbaseTableName, StructType schema) {

        JavaDStream<String> buskeyStream = businesskeyvaluestream.map(s -> s._1);

        JavaPairDStream<String, Integer> existingDataInHBase =
                buskeyStream.transform(
                        new BulkGetRowKeyByKey(getHBaseContext(jssc.sparkContext(),hbaseConnectionName), hbaseTableName))
                        .mapToPair(feSpi -> new Tuple2<String, Integer>(feSpi,1));


        String colFamily = schema.fields()[0].name();
        String colName = "event";

       /* JavaPairDStream<String, Integer> existingDataInUnResolvedHBase =
                buskeyStream.transform(
                        new BulkGetRowKeyFromUnresolved(getHBaseContext(jssc.sparkContext(),hbaseConnectionName), "Unresolved",colFamily,colName))
                        .mapToPair(feSpi -> new Tuple2<String, Integer>(feSpi,1));

        JavaPairDStream<String, Integer> existingDataInHBase2 = existingDataInHBase.union(existingDataInUnResolvedHBase);
        */

        JavaPairDStream<String, WrapperMessage> finalNonDuplicateInBatch = businesskeyvaluestream.leftOuterJoin(existingDataInHBase)
                .filter(tpl -> !tpl._2._2.isPresent())
                .mapToPair(tpl -> new Tuple2<String, WrapperMessage>(tpl._1, tpl._2._1));

        return finalNonDuplicateInBatch;
    }

    public static JavaHBaseContext getHBaseContext(JavaSparkContext jsc, String connectionName) {
        GetConnections getConnections = new GetConnections();
        Connections conn = getConnections.getConnection(connectionName);
        GetConnectionProperties getConnectionProperties = new GetConnectionProperties();
        Properties hbaseProperties=  getConnectionProperties.getConnectionProperties(connectionName,"persistentStore");

        String masterAddress = hbaseProperties.getProperty("hbaseMasterAddress");
        String[] masterIpAndPort = masterAddress.split(":");

        JavaHBaseContext hbaseContext = new JavaHBaseContext(jsc, HbaseUtils.getConfiguration(hbaseProperties.getProperty("zKHost"), hbaseProperties.getProperty("zKPort"),masterIpAndPort[0], masterIpAndPort[1]));
        return hbaseContext;
    }
}

class HbaseUtils {

    /**
     * This method is used to create and return HBase configuration
     * @param zkIp zookeeper IP
     * @param zkPort zookeeper port
     * @param hbIp HBase master IP
     * @param hbPort HBase master port
     * @return
     */
    public static Configuration getConfiguration(String zkIp, String zkPort, String hbIp, String hbPort) {
        //Create HBase configuration object
        final Configuration hconf = HBaseConfiguration.create();
        hconf.set("hbase.zookeeper.quorum", zkIp);
        hconf.set("hbase.zookeeper.property.clientPort", zkPort);
        hconf.set("hbase.master", hbIp + ":" + hbPort);
        return hconf;
    }
}

class BulkGetRowKeyByKey implements Function<JavaRDD<String>, JavaRDD<String>> {


    private JavaHBaseContext hbaseContext;
    private String tableName;

    public BulkGetRowKeyByKey(JavaHBaseContext hbaseContext, String tableName) {
        this.hbaseContext = hbaseContext;
        this.tableName = tableName;
    }

    @Override
    public JavaRDD<String> call(JavaRDD<String> keys) throws Exception {
        return hbaseContext.bulkGet(TableName.valueOf(tableName), 2, keys,
                new RowKeyGetFunction(),
                new RowKeyResultFunction()).filter(key -> (key != null));
    }
}


class RowKeyGetFunction implements Function<String, Get> {

    public Get call(String id) throws Exception {
        return new Get((id != null) ? id.getBytes() : " ".getBytes());
    }
}

class RowKeyResultFunction implements Function<Result, String> {

    public String call(Result result) throws Exception {

        return Bytes.toString(result.getRow());
    }
}

class BulkGetRowKeyFromUnresolved implements Function<JavaRDD<String>, JavaRDD<String>> {


    private JavaHBaseContext hbaseContext;
    private String tableName;
    private String colFamily;
    private String colName;

    public BulkGetRowKeyFromUnresolved(JavaHBaseContext hbaseContext, String tableName, String colFamily, String colName) {
        this.hbaseContext = hbaseContext;
        this.tableName = tableName;
        this.colFamily = colFamily;
        this.colName = colName;
    }

    @Override
    public JavaRDD<String> call(JavaRDD<String> keys) throws Exception {
        return hbaseContext.bulkGet(TableName.valueOf(tableName), 2, keys,
                new RowKeyGetFunction(),
                new RowKeyResultFunctionUnresolved(colFamily,colName)).filter(key -> (key != null));
    }
}

class RowKeyResultFunctionUnresolved implements Function<Result, String> {

    private String colFamily;
    private String colName;
    
    public RowKeyResultFunctionUnresolved(String colFamily, String colName){
        this.colFamily = colFamily;
        this.colName = colName;
    }
    public String call(Result result) throws Exception {
        String dealTuple = Bytes.toString(result.getValue(colFamily.getBytes(),colName.getBytes()));
        if(dealTuple != null){
            return Bytes.toString(result.getRow());
        }
        else {
            return null;
        }
    }
}
