package com.bts.customfunctions;

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
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import scala.Tuple2;
import transformations.Custom;
import util.WrapperMessage;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by cloudera on 8/17/17.
 */
public class TransactionHBaseDedup extends Custom {

    @Override
    public JavaPairDStream<String, WrapperMessage> convertJavaPairDstream(JavaPairDStream<String, WrapperMessage> inputDstream, Map<String, Broadcast<HashMap<String, String>>> broadcastMap, JavaStreamingContext jssc) {
        JavaPairDStream<String,Row> idValueStream = inputDstream.mapValues(s -> s.getRow());
        JavaPairDStream<String,WrapperMessage> businesskeyvaluestream =  idValueStream.mapToPair(new BusinessKeyAttribution2());
        businesskeyvaluestream.print();

        JavaDStream<String> buskeyStream = businesskeyvaluestream.map(s -> s._1);
        buskeyStream.print();

        JavaPairDStream<String, Integer> existingDataInResolvedHBase =
                buskeyStream.transform(
                        new TransactionBulkGetRowKeyByKey(getHBaseContext(jssc.sparkContext()), "resolved"))
                        .mapToPair(feSpi -> new Tuple2<String, Integer>(feSpi,1));

        System.out.println(" HBASE data ");
        existingDataInResolvedHBase.print();

        JavaPairDStream<String, Integer> existingDataInUnResolvedHBase =
                buskeyStream.transform(
                        new TransactionBulkGetRowKeyFromUnresolved(getHBaseContext(jssc.sparkContext()), "Unresolved"))
                        .mapToPair(feSpi -> new Tuple2<String, Integer>(feSpi,1));

        System.out.println(" HBASE data ");
        existingDataInUnResolvedHBase.print();

        JavaPairDStream<String, Integer> existingDataInHBase = existingDataInResolvedHBase.union(existingDataInUnResolvedHBase);

        businesskeyvaluestream.leftOuterJoin(existingDataInHBase).print();

        JavaPairDStream<String, WrapperMessage> finalNonDuplicateInBatch = businesskeyvaluestream.leftOuterJoin(existingDataInHBase)
                .filter(tpl -> !tpl._2._2.isPresent())
                .mapToPair(tpl -> new Tuple2<String, WrapperMessage>(tpl._1, tpl._2._1));

        System.out.println(" Final Data ");
        finalNonDuplicateInBatch.print();
        return finalNonDuplicateInBatch;
    }



    @Override
    public JavaPairDStream<String, WrapperMessage> convertMultiplePairDstream(Map<Integer, JavaPairDStream<String, WrapperMessage>> prevDStreamMap, Map<Integer, Set<Integer>> prevMap, Integer pid, StructType schema, Map<String, Broadcast<HashMap<String, String>>> broadcastMap, JavaStreamingContext jssc) {
        return null;
    }

    protected static JavaHBaseContext getHBaseContext(JavaSparkContext jsc) {
        JavaHBaseContext hbaseContext = new JavaHBaseContext(jsc, HbaseUtils.getConfiguration("localhost", "2181", "localhost", "60000"));
        return hbaseContext;
    }
}

class TransactionBusinessKeyAttribution2 implements Serializable,PairFunction<Tuple2<String, Row>, String, WrapperMessage> {
    @Override
    public Tuple2<String, WrapperMessage> call(Tuple2<String, Row> input) throws Exception {
        Row inputRow = input._2;
        Row headerRow = inputRow.getStruct(inputRow.fieldIndex("Header"));
        String businessKey = headerRow.getString(headerRow.fieldIndex("BusinessKey"));
        return new Tuple2<String, WrapperMessage>(businessKey, new WrapperMessage(input._2));
    }
}

class TransactionHbaseUtils {

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

class TransactionBulkGetRowKeyByKey implements Function<JavaRDD<String>, JavaRDD<String>> {


    private JavaHBaseContext hbaseContext;
    private String tableName;

    public TransactionBulkGetRowKeyByKey(JavaHBaseContext hbaseContext, String tableName) {
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

class TransactionBulkGetRowKeyFromUnresolved implements Function<JavaRDD<String>, JavaRDD<String>> {


    private JavaHBaseContext hbaseContext;
    private String tableName;

    public TransactionBulkGetRowKeyFromUnresolved(JavaHBaseContext hbaseContext, String tableName) {
        this.hbaseContext = hbaseContext;
        this.tableName = tableName;
    }

    @Override
    public JavaRDD<String> call(JavaRDD<String> keys) throws Exception {
        return hbaseContext.bulkGet(TableName.valueOf(tableName), 2, keys,
                new RowKeyGetFunction(),
                new TransactionRowKeyResultFunctionUnresolved()).filter(key -> (key != null));
    }
}

class TransactionRowKeyGetFunction implements Function<String, Get> {

    public Get call(String id) throws Exception {
        return new Get((id != null) ? id.getBytes() : " ".getBytes());
    }
}

class TransactionRowKeyResultFunction implements Function<Result, String> {

    public String call(Result result) throws Exception {

        return Bytes.toString(result.getRow());
    }
}

class TransactionRowKeyResultFunctionUnresolved implements Function<Result, String> {

    public String call(Result result) throws Exception {
        String dealTuple = Bytes.toString(result.getValue("transactions".getBytes(),"event".getBytes()));
        if(dealTuple != null){
            return Bytes.toString(result.getRow());
        }
        else {
            return null;
        }
    }
}
