package com.bts.customfunctions;

import org.apache.spark.api.java.function.Function;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import transformations.Custom;
import util.WrapperMessage;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by cloudera on 8/6/17.
 */
public class Enricher extends Custom implements Serializable {
    @Override
    public JavaPairDStream<String, WrapperMessage> convertJavaPairDstream(JavaPairDStream<String, WrapperMessage> inputDstream, Map<String, Broadcast<HashMap<String, String>>> broadcastMap, JavaStreamingContext ssc) {
        JavaPairDStream<String,Row> inputRowStream = inputDstream.mapValues(s -> s.getRow());
        Broadcast<HashMap<String,String>> statusBroadCast = broadcastMap.get("enricher_message_status");
        System.out.println("statusBroadCast = " + statusBroadCast.value().toString());
        inputDstream.print();
        JavaPairDStream<String,Row> encrichedRowStream = inputRowStream.mapValues(new Function<Row, Row>() {
            @Override
            public Row call(Row row) throws Exception {
                int indexOfStatus = row.fieldIndex("Status");
                Long statusId = row.getLong(indexOfStatus);
                System.out.println("statusId from input= " + statusId);

                String statusValue = statusBroadCast.value().get(statusId.toString());
                System.out.println("statusValue from hbase= " + statusValue);
                //scala.collection.Seq<Object> rowSeq = row.toSeq();

                int noOfElements = row.size();
                Object[] attributes = new Object[noOfElements];
                for(int i=0; i<noOfElements; i++){
                    attributes[i] = row.get(i);
                    if(i == indexOfStatus){
                        attributes[i] = statusValue;
                    }
                }
                Row outputRow = RowFactory.create(attributes);
                return outputRow;
            }
        });
        encrichedRowStream.print();
        return encrichedRowStream.mapValues(s -> new WrapperMessage(s));
    }

    @Override
    public JavaPairDStream<String, WrapperMessage> convertMultiplePairDstream(Map<Integer, JavaPairDStream<String, WrapperMessage>> prevDStreamMap, Map<Integer, Set<Integer>> prevMap, Integer pid, StructType schema, Map<String, Broadcast<HashMap<String, String>>> broadcastMap, JavaStreamingContext jssc) {
        return null;
    }
}

/*class EncrichFromHBase implements Function {

    @Override
    public Object call(Object o) throws Exception {
        return null;
    }
} */
