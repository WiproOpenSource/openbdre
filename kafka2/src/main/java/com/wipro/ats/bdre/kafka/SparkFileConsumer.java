package com.wipro.ats.bdre.kafka;

/**
 * Created by cloudera on 11/27/16.
 *
 */

import kafka.serializer.StringDecoder;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;


import java.util.*;

public class SparkFileConsumer {
    public static void main(String[] args) {

        SparkConf conf = new SparkConf().setAppName("SparkFileConsumer").setMaster("local[*]");
        JavaSparkContext sc = new JavaSparkContext(conf);
        JavaStreamingContext ssc = new JavaStreamingContext(sc, new Duration(2000));

        // TODO: processing pipeline
        Map<String, String> kafkaParams = new HashMap<String, String>();
        kafkaParams.put("metadata.broker.list", "localhost:9092");
        Set<String> topics = Collections.singleton("test");

        JavaPairInputDStream<String, String> directKafkaStream = KafkaUtils.createDirectStream(ssc,String.class, String.class, StringDecoder.class, StringDecoder.class, kafkaParams,topics);

        System.out.println("count"+directKafkaStream.count());
        directKafkaStream.print();


        ssc.start();
        ssc.awaitTermination();
    }
}
