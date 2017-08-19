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
 * Created by cloudera on 8/4/17.
 */
public class BusinessKeyAttribution extends Custom {
    @Override
    public JavaPairDStream<String, WrapperMessage> convertJavaPairDstream(JavaPairDStream<String, WrapperMessage> inputDstream, Map<String,Broadcast<HashMap<String,String>>> broadcastMap,JavaStreamingContext jssc) {
        JavaPairDStream<String,Row> idValueStream = inputDstream.mapValues(s -> s.getRow());
        return idValueStream.mapToPair(s -> new Tuple2<String, WrapperMessage>(s._2.getStruct(s._2.fieldIndex("Header")).getString(1), new WrapperMessage(s._2) ));
    }

    @Override
    public JavaPairDStream<String, WrapperMessage> convertMultiplePairDstream(Map<Integer, JavaPairDStream<String, WrapperMessage>> prevDStreamMap, Map<Integer, Set<Integer>> prevMap, Integer pid, StructType schema, Map<String, Broadcast<HashMap<String, String>>> broadcastMap, JavaStreamingContext jssc) {
        return null;
    }
}
