package com.bts.customfunctions;

import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import scala.Tuple2;
import transformations.Custom;
import util.WrapperMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Created by cloudera on 8/3/17.
 */
public class IdStatusCodeAttribution extends Custom {
    @Override
    public JavaPairDStream<String, WrapperMessage> convertJavaPairDstream(JavaPairDStream<String, WrapperMessage> inputDstream, Map<String,Broadcast<HashMap<String,String>>> broadcastMap,JavaStreamingContext jssc) {
        JavaPairDStream<String,Row> idValueStream = inputDstream.mapValues(s -> s.getRow());
        return idValueStream.mapToPair(s -> new Tuple2<String, WrapperMessage>(s._2.getString(s._2.fieldIndex("Id"))+"_"+s._2.getString(s._2.fieldIndex("Status")), new WrapperMessage(s._2) ));
    }

    @Override
    public JavaPairDStream<String, WrapperMessage> convertMultiplePairDstream(Map<Integer, JavaPairDStream<String, WrapperMessage>> prevDStreamMap, Map<Integer, Set<Integer>> prevMap, Integer pid, StructType schema, Map<String, Broadcast<HashMap<String, String>>> broadcastMap, JavaStreamingContext jssc) {
        return null;
    }
    /*@Override
    public Object call(Object inputRecord) throws Exception {
        Tuple2<String, Row> input = (Tuple2<String, Row>) inputRecord;
        Row inputRow = input._2;
        String businessKey = inputRow.getStruct(7).getString(1);
        System.out.println("businessKey = " + businessKey);
        System.out.println("inputRow = " + inputRow.toString());
        System.out.println("Row schema = " + inputRow.schema());
        return inputRow;
    }*/


}
