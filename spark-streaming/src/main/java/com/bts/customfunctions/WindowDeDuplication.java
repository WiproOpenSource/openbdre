package com.bts.customfunctions;

import com.google.common.base.Optional;
import org.apache.spark.api.java.function.Function4;
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
import transformations.Custom;
import util.WrapperMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by cloudera on 8/3/17.
 */
public class WindowDeDuplication extends Custom {
    @Override
    public JavaPairDStream<String, WrapperMessage> convertJavaPairDstream(JavaPairDStream<String, WrapperMessage> inputDstream,Map<String,Broadcast<HashMap<String,String>>> broadcastMap,JavaStreamingContext jssc) {
        JavaPairDStream<String,Row> idValueStream = inputDstream.mapValues(s -> s.getRow());
        JavaPairDStream<String,Row> idStatusCodeStream = idValueStream.mapToPair(s -> new Tuple2<String, Row>(s._2.getString(s._2.fieldIndex("Id"))+"_"+s._2.getLong(s._2.fieldIndex("Status")), s._2 ));
        JavaMapWithStateDStream<String,Row,String,Row> mappedStream= idStatusCodeStream.mapWithState(StateSpec.function(new DuplicateChecker()).timeout(new Duration(100)));
        // Start the computation
        JavaPairDStream<String,WrapperMessage> deduplicatedStream = mappedStream.mapToPair(s -> new Tuple2<String, WrapperMessage>(null,new WrapperMessage(s)));
        return deduplicatedStream;

    }

    @Override
    public JavaPairDStream<String, WrapperMessage> convertMultiplePairDstream(Map<Integer, JavaPairDStream<String, WrapperMessage>> prevDStreamMap, Map<Integer, Set<Integer>> prevMap, Integer pid, StructType schema, Map<String, Broadcast<HashMap<String, String>>> broadcastMap, JavaStreamingContext jssc) {
        return null;
    }
}

class DuplicateChecker implements Function4<Time, String,Optional<Row>,State<String>, Optional<Row>> {

    @Override
    public Optional<Row> call(Time time, String key, Optional<Row> value, State<String> state) throws Exception {
        String existingState = (state.exists() ? state.get() : new String()) ;
        System.out.println("existingState = " + existingState);
        System.out.println("key = " + key);
        if(existingState.equals(key)){
            System.out.println(" Duplicate found" );
            return Optional.absent();
        }
        else {
            System.out.println(" New Record found" );
            state.update(key);
            return value;
        }
    }
}
