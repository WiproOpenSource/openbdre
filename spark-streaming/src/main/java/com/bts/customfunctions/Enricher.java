package com.bts.customfunctions;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.rdd.RDD;
import org.apache.spark.sql.*;
import org.apache.spark.sql.catalyst.expressions.Expression;
import org.apache.spark.sql.catalyst.expressions.Literal;
import org.apache.spark.sql.types.StringType;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.h2.store.Data;
import transformations.Custom;
import transformations.Transformation;
import util.WrapperMessage;

import java.io.Serializable;
import java.util.*;

/**
 * Created by cloudera on 8/6/17.
 */
public class Enricher implements Transformation {
    @Override
    public JavaPairDStream<String,WrapperMessage> transform(JavaRDD emptyRDD, Map<Integer, JavaPairDStream<String,WrapperMessage>> prevDStreamMap, Map<Integer, Set<Integer>> prevMap, Integer pid, StructType schema,Map<String,Broadcast<HashMap<String,String>>> broadcastMap,JavaStreamingContext jssc) {
        //TODO: Fetch from DB props
        String[] fieldsToBeEnriched = {"Status"};
        //TODO: Fetch from DB props
        Broadcast<HashMap<String,String>> statusBroadCast = broadcastMap.get("enricher_message_status");
        System.out.println("statusBroadCast = " + statusBroadCast.value().toString());

        List<Integer> prevPidList = new ArrayList<>();
        prevPidList.addAll(prevMap.get(pid));
        Integer prevPid = prevPidList.get(0);
        System.out.println("Inside filter prevPid = " + prevPid);
        JavaPairDStream<String,WrapperMessage> prevDStream = prevDStreamMap.get(prevPid);
        JavaPairDStream<String,Row> inputRowStream = prevDStream.mapValues(s -> s.getRow());

        inputRowStream.print();
        JavaPairDStream<String,Row> enrichedRowStream = inputRowStream.mapValues(new Function<Row, Row>() {
            @Override
            public Row call(Row row) throws Exception {
                DataFrame partiallyEnrichedDataFrame = null;
                for (String field : fieldsToBeEnriched) {
                    List<Row> rowList = new ArrayList<>();
                    rowList.add(row);
                    JavaRDD<Row> rowRDD = jssc.sparkContext().parallelize(rowList);
                    SQLContext sqlContext = SQLContext.getOrCreate(rowRDD.context());

                    if(partiallyEnrichedDataFrame==null)
                        partiallyEnrichedDataFrame=sqlContext.createDataFrame(rowRDD, schema);
                    String enrichedValue = statusBroadCast.value().get(partiallyEnrichedDataFrame.select(field).head());
                     partiallyEnrichedDataFrame = partiallyEnrichedDataFrame.withColumn(field, functions.lit(enrichedValue));
                }
                DataFrame completelyEnrichedDataFrame = partiallyEnrichedDataFrame;
                return completelyEnrichedDataFrame.rdd().take(1)[0];
            }
        });
        enrichedRowStream.print();
        return enrichedRowStream.mapValues(s -> new WrapperMessage(s));
    }

}
