
package driver;

import com.databricks.spark.xml.XmlReader;
import kafka.serializer.StringDecoder;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import scala.Tuple2;
import util.WrapperMessage;

import java.util.*;
import java.util.regex.Pattern;


/**
 * Consumes messages from one or more topics in Kafka and does wordcount.
 * Usage: JavaDirectKafkaWordCount <brokers> <topics>
 *   <brokers> is a list of one or more Kafka brokers
 *   <topics> is a list of one or more kafka topics to consume from
 *
 * Example:
 *    $ bin/run-example streaming.JavaDirectKafkaWordCount broker1-host:port,broker2-host:port \
 *      topic1,topic2
 */


public final class Test {
    private static final Pattern SPACE = Pattern.compile(" ");

    public static void main(String[] args) throws Exception {

        //StreamingExamples.setStreamingLogLevels();

        String brokers = "localhost:9092";
        String topics = "test";

        // Create context with a 2 seconds batch interval
        SparkConf sparkConf = new SparkConf().setAppName("JavaDirectKafkaWordCount");
        sparkConf.setMaster("local[*]");
        JavaStreamingContext jssc = new JavaStreamingContext(sparkConf, Durations.seconds(20));

        Set<String> topicsSet = new HashSet<String>(Arrays.asList(topics.split(",")));
        Map<String, String> kafkaParams = new HashMap<String, String>();
        kafkaParams.put("metadata.broker.list", brokers);

        // Create direct kafka stream with brokers and topics
        JavaPairInputDStream<String, String> msgDataStream = KafkaUtils.createDirectStream(
                jssc,
                String.class,
                String.class,
                StringDecoder.class,
                StringDecoder.class,
                kafkaParams,
                topicsSet
        );
        msgDataStream.print();

        SQLContext sqlContext = new SQLContext(jssc.sparkContext());
        JavaPairDStream<String, WrapperMessage> wrapperDStream = msgDataStream.transformToPair(new transformToWrapper(sqlContext));
        wrapperDStream.print();

        jssc.start();
        jssc.awaitTermination();
    }
}

class transformToWrapper implements Function<JavaPairRDD<String, String>, JavaPairRDD<String, WrapperMessage>>{
    SQLContext sqlContext;
    public transformToWrapper( SQLContext sqlContext ){
        this.sqlContext = sqlContext;
    }

    @Override
    public JavaPairRDD<String, WrapperMessage> call(JavaPairRDD<String, String> inputPairRDD) throws Exception {
        JavaPairRDD<String, WrapperMessage> outputPairRdd = null;
        JavaRDD<String> javaRDD = inputPairRDD.map(s -> s._2);
        DataFrame df = new XmlReader().xmlRdd(sqlContext, javaRDD.rdd());
        System.out.println("df.schema() = " + df.schema());
        JavaRDD<Row> rowJavaRDD = df.javaRDD();
        rowJavaRDD.take(15);
        outputPairRdd = rowJavaRDD.mapToPair(row -> new Tuple2<String, WrapperMessage>(null,new WrapperMessage(row)));
        return outputPairRdd ;
    }
}






