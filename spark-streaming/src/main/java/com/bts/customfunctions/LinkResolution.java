package com.bts.customfunctions;

import com.google.common.base.Optional;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.spark.JavaHBaseContext;
import org.apache.hadoop.hbase.spark.example.hbasecontext.JavaHBaseBulkDeleteExample;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.Function3;
import org.apache.spark.api.java.function.Function4;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.State;
import org.apache.spark.streaming.StateSpec;
import org.apache.spark.streaming.Time;
import org.apache.spark.streaming.api.java.JavaMapWithStateDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import scala.Tuple2;
import scala.Tuple3;
import scala.Tuple4;
import transformations.Custom;
import transformations.MapToPair;
import util.WrapperMessage;

import java.io.Serializable;
import java.util.*;

/**
 * Created by cloudera on 8/8/17.
 */
public class LinkResolution extends Custom{

    @Override
    public JavaPairDStream<String, WrapperMessage> convertJavaPairDstream(JavaPairDStream<String, WrapperMessage> inputDstream, Map<String, Broadcast<HashMap<String, String>>> broadcastMap, JavaStreamingContext ssc) {
System.out.println("Inside convertJavaPairDStream");        
return null;
    }

   /* public  static String printTuple2(Tuple2<String,Row> s, String type) {
        System.out.println(type + " type = " + s.toString());
        return s._2.toString();
    }*/

    @Override
    public JavaPairDStream<String, WrapperMessage> convertMultiplePairDstream(Map<Integer, JavaPairDStream<String, WrapperMessage>> prevDStreamMap, Map<Integer, Set<Integer>> prevMap, Integer pid, StructType schema, Map<String, Broadcast<HashMap<String, String>>> broadcastMap, JavaStreamingContext jssc) {
        List<Integer> prevPidList = new ArrayList<>();
        prevPidList.addAll(prevMap.get(pid));
        System.out.println("prevPidList in custom join= " + prevPidList);
        /*JavaPairDStream<String, WrapperMessage> prevDStream =prevDStreamMap.get(prevPidList.get(0));
        prevDStream.foreachRDD(new Function2<JavaPairRDD<String, WrapperMessage>, Time, Void>() {
            @Override
            public Void call(JavaPairRDD<String, WrapperMessage> stringWrapperMessageJavaPairRDD, Time time) throws Exception {
                System.out.println("Beginning of Link Resolution = " + new Date());
                return null;
            }
        });*/
        MapToPair mapToPair = new MapToPair();
        JavaPairDStream<String,Row> dealDStream = mapToPair.mapToPair(prevDStreamMap.get(prevPidList.get(0)).map(s -> s._2), "Deal.Header.BusinessKey:String").mapValues(s -> s.getRow());
        /*dealDStream.transform(new Function2<JavaPairRDD<String, Row>, Time, JavaRDD<Object>>() {
            @Override
            public JavaRDD<Object> call(JavaPairRDD<String, Row> stringRowJavaPairRDD, Time time) throws Exception {
                System.out.println("Beginning of Link Resolution = " + new Date().getTime());
                return null;
            }
        });
        dealDStream.foreachRDD(new Function2<JavaPairRDD<String, Row>, Time, Void>() {
            @Override
            public Void call(JavaPairRDD<String, Row> stringRowJavaPairRDD, Time time) throws Exception {
                System.out.println("Beginning of Link Resolution = " + new Date().getTime());
                return null;
            }
        });*/
        dealDStream.map(s -> new Tuple2<String,String>(s._1,s._2.toString())).transform(new BulkPutMessage(getHBaseContext(jssc.sparkContext()) , "Deal")).print();

        JavaPairDStream<String,Row> transactionDStream = mapToPair.mapToPair(prevDStreamMap.get(prevPidList.get(1)).map(s -> s._2), "Transaction.Header.BusinessKey:String").mapValues(s -> s.getRow());
        transactionDStream.map(s -> new Tuple2<String,String>(s._1,s._2.toString())).transform(new BulkPutMessage(getHBaseContext(jssc.sparkContext()) , "Transaction")).print();

        JavaPairDStream<String,Row> trnxElementDStream = mapToPair.mapToPair(prevDStreamMap.get(prevPidList.get(2)).map(s -> s._2), "TransactionElement.Header.BusinessKey:String").mapValues(s -> s.getRow());
        trnxElementDStream.map(s -> new Tuple2<String,String>(s._1,s._2.toString())).transform(new BulkPutMessage(getHBaseContext(jssc.sparkContext()) , "TransactionElement")).print();
/*

        JavaPairDStream<String, Tuple2<Row,Row>> dealTransactionJoinDstream = dealDStream.fullOuterJoin(transactionDStream)
                                                                                               .mapValues(tpl -> new Tuple2<Row, Row>(tpl._1.orNull(),tpl._2.orNull()));
*//*
        dealTransactionJoinDstream.print();
*//*
      //  trnxElementDStream.print();
        JavaPairDStream<String, Tuple3<Row, Row, Row>> threeStreamsJoinDstream = null;
        if(dealTransactionJoinDstream != null) {
            dealTransactionJoinDstream.fullOuterJoin(trnxElementDStream).print();
            threeStreamsJoinDstream  = dealTransactionJoinDstream.fullOuterJoin(trnxElementDStream)
                    .mapValues(tpl -> new Tuple3<Row, Row, Row>(((tpl._1.orNull() == null) ? null : tpl._1.orNull()._1), ((tpl._1.orNull() == null) ? null : tpl._1.orNull()._2), tpl._2.orNull()));
        }
        else {
            threeStreamsJoinDstream  = trnxElementDStream.mapValues(s -> new Tuple3<Row, Row, Row>(null, null, s));
        }
*//*
        threeStreamsJoinDstream.print();
*//*

        JavaMapWithStateDStream<String,Tuple3<Row,Row,Row>,Tuple3<Row,Row,Row>,Tuple2<String,Tuple3<Row,Row,Row>>> mapWithStateDStream = threeStreamsJoinDstream.mapWithState(StateSpec.function(new LinkResolverInMemory()).timeout(new Duration(100)));
        JavaPairDStream<String, Tuple3<Row,Row,Row>> inMemoryDstream = mapWithStateDStream.mapToPair(tpl -> new Tuple2<String, Tuple3<Row, Row, Row>>(tpl._1(),tpl._2));
*//*
        inMemoryDstream.print();
*//*

        JavaPairDStream<String, Tuple3<Row,Row,Row>>  inMemoryResolvedDstream = inMemoryDstream.filter(tpl -> (tpl._2._1() != null && tpl._2._2()!= null && tpl._2._3()!= null));
        JavaPairDStream<String, Tuple3<Row,Row,Row>>  inMemoryUnResolvedDstream = inMemoryDstream.filter(tpl -> (tpl._2._1() == null || tpl._2._2()== null || tpl._2._3()== null));

    *//*   inMemoryResolvedDstream.print();
        inMemoryUnResolvedDstream.print();*//*
        
        JavaPairDStream<String, Tuple3<String,String,String>> existingDataInHBase = inMemoryUnResolvedDstream.transformToPair(new  BulkGetRowKeyByKey2(getHBaseContext(jssc.sparkContext()), "Unresolved"));
*//*
        existingDataInHBase.print();
*//*

        //JavaPairDStream<String, Tuple3<String,String,String>> joinWithHBase= inMemoryUnResolvedDstream.leftOuterJoin(existingDataInHBase).mapToPair(new HBaseLinkResolver());
        JavaPairDStream<String, Tuple3<String,String,String>> unResolvedWithHBase = inMemoryUnResolvedDstream.leftOuterJoin(existingDataInHBase).filter(s -> !s._2._2.isPresent()).mapToPair(s -> new Tuple2<String, Tuple3<String,String,String>>(s._1, new Tuple3<String,String,String>((s._2._1._1() != null ? s._2._1._1().toString() : null) , (s._2._1._2() != null ? s._2._1._2().toString() : null) ,(s._2._1._3() != null ? s._2._1._3().toString() : null))));
        JavaPairDStream<String, Tuple3<String,String,String>> partiallyResolvedWithHBase = inMemoryUnResolvedDstream.leftOuterJoin(existingDataInHBase).filter(s -> s._2._2.isPresent()).mapToPair(new HBaseLinkResolver());
        JavaPairDStream<String, Tuple3<String,String,String>> fullyResolvedWithHBase = partiallyResolvedWithHBase.filter(s -> s._2._1()!= null &&  s._2._2()!= null &&  s._2._3()!= null );
        JavaPairDStream<String, Tuple3<String,String,String>> unResolvedWithHBase2 = partiallyResolvedWithHBase.filter(s -> s._2._1()== null ||  s._2._2()== null ||  s._2._3()== null );

        inMemoryResolvedDstream.map(s -> Bytes.toBytes(s._1())).foreachRDD(new RemoveResolvedFromUnResolvedTable(getHBaseContext(jssc.sparkContext())));
        fullyResolvedWithHBase.map(s -> Bytes.toBytes(s._1())).foreachRDD(new RemoveResolvedFromUnResolvedTable(getHBaseContext(jssc.sparkContext())));

        inMemoryResolvedDstream.map(s -> new Tuple4(s._1, s._2._1().toString(), s._2._2().toString(),s._2._3().toString())).transform(new BulkPut(getHBaseContext(jssc.sparkContext()) , "Resolved")).print();
        fullyResolvedWithHBase.map(s-> new Tuple4(s._1,s._2._1(),s._2._2(),s._2._3())).transform(new BulkPut(getHBaseContext(jssc.sparkContext()) , "Resolved")).print();
        unResolvedWithHBase.map(s-> new Tuple4(s._1,s._2._1(),s._2._2(),s._2._3())).transform(new BulkPut(getHBaseContext(jssc.sparkContext()) , "Unresolved")).print();
        unResolvedWithHBase2.map(s-> new Tuple4(s._1,s._2._1(),s._2._2(),s._2._3())).transform(new BulkPut(getHBaseContext(jssc.sparkContext()) , "Unresolved")).print();


        return dealDStream.mapValues(s -> new WrapperMessage(s));*/

	return null;
    }



    protected static JavaHBaseContext getHBaseContext(JavaSparkContext jsc) {
        JavaHBaseContext hbaseContext = new JavaHBaseContext(jsc, HbaseUtils.getConfiguration("172.31.31.241,172.31.28.247,172.31.29.55", "2181", "172.31.28.247", "16000"));
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

class RemoveResolvedFromUnResolvedTable implements Serializable,Function<JavaRDD<byte[]>, Void>{

    private JavaHBaseContext hbaseContext;

    public RemoveResolvedFromUnResolvedTable(JavaHBaseContext hbaseContext) {
        this.hbaseContext = hbaseContext;
    }

    @Override
    public Void call(JavaRDD<byte[]> javaRDD) throws Exception {

        hbaseContext.bulkDelete(javaRDD, TableName.valueOf("Unresolved"), new JavaHBaseBulkDeleteExample.DeleteFunction(), 2);
        return null;
    }
}

class HBaseLinkResolver implements Serializable,PairFunction<Tuple2<String, Tuple2<Tuple3<Row, Row, Row>, com.google.common.base.Optional<Tuple3<String, String, String>>>>, String, Tuple3<String, String, String>> {
    @Override
    public Tuple2<String, Tuple3<String, String, String>> call(Tuple2<String, Tuple2<Tuple3<Row, Row, Row>, Optional<Tuple3<String, String, String>>>> stringTuple2Tuple2) throws Exception {

        Tuple3<Row, Row, Row> inputTuple = stringTuple2Tuple2._2._1;
        Tuple3<String, String, String> hbaseTuple = stringTuple2Tuple2._2._2.orNull();
        //System.out.println("hbaseTuple = " + hbaseTuple);
            String dealHBase = hbaseTuple._1();
            String tnxHBase = hbaseTuple._2();
            String teHBase = hbaseTuple._3();

            String deal = ((dealHBase != null) ? dealHBase : inputTuple._1() != null ? inputTuple._1().toString() : null);
            String tnx = ((tnxHBase != null)? tnxHBase : inputTuple._2() != null ? inputTuple._2().toString() : null);
            String te = ((teHBase != null) ? teHBase : inputTuple._3() != null ? inputTuple._3().toString() : null);

            Tuple3<String, String, String> updatedTuple = hbaseTuple.copy(deal, tnx, te);

          /*  if(deal != null && tnx != null && te != null){
                Connection conn = ConnectionFactory.createConnection(getConfiguration("localhost", "2181", "localhost", "60000"));
                Table table = conn.getTable(TableName.valueOf("Resolved"));
               // Put put = new Put(deal,)
            }
            else{
                //put into hbase UnResolved table
            }*/

        return new Tuple2<>(stringTuple2Tuple2._1,updatedTuple);
    }

    public static Configuration getConfiguration(String zkIp, String zkPort, String hbIp, String hbPort) {
        //Create HBase configuration object
        final Configuration hconf = HBaseConfiguration.create();
        hconf.set("hbase.zookeeper.quorum", zkIp);
        hconf.set("hbase.zookeeper.property.clientPort", zkPort);
        hconf.set("hbase.master", hbIp + ":" + hbPort);
        return hconf;
    }

}




class LinkResolverInMemory implements Function4<Time, String,com.google.common.base.Optional<Tuple3<Row,Row,Row>>,State<Tuple3<Row,Row,Row>>, com.google.common.base.Optional<Tuple2<String,Tuple3<Row,Row,Row>>>> {

    @Override
    public com.google.common.base.Optional<Tuple2<String,Tuple3<Row,Row,Row>>> call(Time time, String key, com.google.common.base.Optional<Tuple3<Row,Row,Row>> value, State<Tuple3<Row,Row,Row>> state) throws Exception {


        Tuple3<Row, Row, Row> existingState = (state.exists() ? state.get() : new Tuple3<>(null, null, null));
        Tuple3<Row, Row, Row> updatedValue =  new Tuple3<>(null, null, null);

            Row dealRow = null;
            Row txnRow = null;
            Row teRow = null;
            if(value.orNull() != null) {
                if ((value.orNull()._1() == null)) {
                    dealRow = existingState._1();
                } else {
                    dealRow = value.orNull()._1();
                }
                if ((value.orNull()._2() == null)) {
                    txnRow = existingState._2();
                } else {
                    txnRow = value.orNull()._2();
                }
                if ((value.orNull()._3() == null)) {
                    teRow = existingState._3();
                } else {
                    teRow = value.orNull()._3();
                }
            }

            updatedValue = existingState.copy(dealRow, txnRow, teRow);
        System.out.println("updatedValue = " + updatedValue);
        if(!state.isTimingOut()){
            state.update(updatedValue);
        }
        System.out.println("key = " + key);
        Tuple2<String,Tuple3<Row,Row,Row>> output = new Tuple2<>(key,updatedValue);
        return com.google.common.base.Optional.of(output);

    }
}



class BulkGetRowKeyByKey2 implements Serializable,Function2<JavaPairRDD<String, Tuple3<Row, Row, Row>>, Time, JavaPairRDD<String, Tuple3<String, String, String>>> {


    private JavaHBaseContext hbaseContext;
    private String tableName;

    public BulkGetRowKeyByKey2(JavaHBaseContext hbaseContext, String tableName) {
        this.hbaseContext = hbaseContext;
        this.tableName = tableName;
    }

    @Override
    public JavaPairRDD<String, Tuple3<String, String, String>> call(JavaPairRDD<String, Tuple3<Row, Row, Row>> stringTuple3JavaPairRDD, Time time) throws Exception {
        JavaRDD<String> bytesrdd = stringTuple3JavaPairRDD.map(tpl -> tpl._1);
        return hbaseContext.bulkGet(TableName.valueOf(tableName), 2, bytesrdd, new GetFunction(),
                new RowKeyResultFunction2()).mapToPair(value -> new Tuple2<String, Tuple3<String, String, String>>(value._1(), new Tuple3<String, String, String>(value._2(),value._3(),value._4())));
    }


    class RowKeyResultFunction2 implements Function<Result, Tuple4<String, String, String,String>> {

        public Tuple4<String, String, String,String> call(Result result) throws Exception {

            String key = Bytes.toString(result.getRow());
            String dealTuple = Bytes.toString(result.getValue("Deal".getBytes(),"event".getBytes()));
            //System.out.println("dealTuple = " + dealTuple);
            String transTuple = Bytes.toString(result.getValue("Transaction".getBytes(),"event".getBytes()));
            //System.out.println("transTuple = " + transTuple);
            String teTuple = Bytes.toString(result.getValue("TransactionElement".getBytes(),"event".getBytes()));
            //System.out.println("teTuple = " + teTuple);

            return new Tuple4<String, String, String,String>(key,dealTuple,transTuple,teTuple);
        }

    }

    class GetFunction implements Function<String, Get> {

        public Get call(String id) throws Exception {

            return new Get((id != null) ? id.getBytes() : " ".getBytes());
        }
    }
}

 class  BulkPut implements Serializable,Function<JavaRDD<Tuple4>, JavaRDD<Object>> {

     private JavaHBaseContext hbaseContext;
     private String tableName;

     public BulkPut(JavaHBaseContext hbaseContext, String tableName) {
         this.hbaseContext = hbaseContext;
         this.tableName = tableName;
     }

     public JavaRDD<Object> call(JavaRDD<Tuple4> tuple4JavaRDD) throws Exception {
         hbaseContext.bulkPut(tuple4JavaRDD, TableName.valueOf(tableName), new PutFun());
         return tuple4JavaRDD.map(s->s._1());
     }


     class PutFun implements Function<Tuple4, Put>{

         @Override
         public Put call(Tuple4 tuple4) throws Exception {
             Put put = new Put(Bytes.toBytes(tuple4._1().toString()));
             if(tuple4._2() != null) {
                 System.out.println(" Writing deal to " +tableName );
                 put.addColumn(Bytes.toBytes("Deal"), Bytes.toBytes("event"), Bytes.toBytes(tuple4._2().toString()));
             }
             if(tuple4._3() != null) {
                 System.out.println(" Writing trnx to " +tableName );
                 put.addColumn(Bytes.toBytes("Transaction"), Bytes.toBytes("event"), Bytes.toBytes(tuple4._3().toString()));
             }
             if(tuple4._4() != null) {
                 System.out.println(" Writing te to " +tableName );
                 put.addColumn(Bytes.toBytes("TransactionElement"), Bytes.toBytes("event"), Bytes.toBytes(tuple4._4().toString()));
             }
             return put;
         }
     }
 }

class  BulkPutMessage implements Serializable,Function<JavaRDD<Tuple2<String,String>>, JavaRDD<Object>> {

    private JavaHBaseContext hbaseContext;
    private String tableName;

    public BulkPutMessage(JavaHBaseContext hbaseContext, String tableName) {
        this.hbaseContext = hbaseContext;
        this.tableName = tableName;
    }

    public JavaRDD<Object> call(JavaRDD<Tuple2<String,String>> tuple2JavaRDD) throws Exception {
        hbaseContext.bulkPut(tuple2JavaRDD, TableName.valueOf(tableName), new PutFun2());
        return tuple2JavaRDD.map(s->s._1());
    }


    class PutFun2 implements Function<Tuple2<String,String>, Put>{

        @Override
        public Put call(Tuple2<String,String> tuple2) throws Exception {
            Put put = new Put(Bytes.toBytes(tuple2._1().toString()));
            if(tuple2._2() != null) {
                System.out.println(" Writing deal to " +tableName );
                put.addColumn(Bytes.toBytes("message"), Bytes.toBytes("event"), Bytes.toBytes(tuple2._2().toString()));
            }
            return put;
        }
    }
}




