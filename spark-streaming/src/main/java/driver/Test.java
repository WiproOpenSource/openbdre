/*
package driver;

import com.bts.customfunctions.BulkGetRowKeyByKey;
import com.bts.customfunctions.HbaseUtils;
import com.bts.customfunctions.RowKeyGetFunction;
import com.bts.customfunctions.RowKeyResultFunction;
import com.google.common.base.Optional;
import kafka.serializer.StringDecoder;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.spark.JavaHBaseContext;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function4;
import org.apache.spark.streaming.*;
import org.apache.spark.streaming.api.java.*;
import org.apache.spark.streaming.kafka.KafkaUtils;
import scala.Tuple2;

import java.util.*;
import java.util.regex.Pattern;

*/
/**
 * Consumes messages from one or more topics in Kafka and does wordcount.
 * Usage: JavaDirectKafkaWordCount <brokers> <topics>
 *   <brokers> is a list of one or more Kafka brokers
 *   <topics> is a list of one or more kafka topics to consume from
 *
 * Example:
 *    $ bin/run-example streaming.JavaDirectKafkaWordCount broker1-host:port,broker2-host:port \
 *      topic1,topic2
 *//*


public final class Test {
    private static final Pattern SPACE = Pattern.compile(" ");

    public static void main(String[] args) throws Exception {

        //StreamingExamples.setStreamingLogLevels();

        String brokers = "localhost:9092";
        String topics = "test";

        // Create context with a 2 seconds batch interval
        SparkConf sparkConf = new SparkConf().setAppName("JavaDirectKafkaWordCount");
        sparkConf.setMaster("local[*]");
        JavaStreamingContext jssc = new JavaStreamingContext(sparkConf, Durations.seconds(15));

        Set<String> topicsSet = new HashSet<String>(Arrays.asList(topics.split(",")));
        Map<String, String> kafkaParams = new HashMap<String,String>();
        kafkaParams.put("metadata.broker.list", brokers);

        // Create direct kafka stream with brokers and topics
        JavaPairInputDStream<String, String> messages = KafkaUtils.createDirectStream(
                jssc,
                String.class,
                String.class,
                StringDecoder.class,
                StringDecoder.class,
                kafkaParams,
                topicsSet
        );

        // Get the lines, split them into words, count the words and print
       */
/* JavaDStream<String> lines = messages.map(Tuple2::_2);
        JavaDStream<String> words = lines.flatMap(x -> Arrays.asList(SPACE.split(x)));
        JavaPairDStream<String, Integer> wordCounts = words.mapToPair(s -> new Tuple2<>(s, 1))
                .reduceByKey((i1, i2) -> i1 + i2);
        wordCounts.print();*//*


        JavaDStream<String> lines = messages.map(s -> s._2);
        lines.print();
        JavaPairDStream<String,String> idValueStream = lines.mapToPair(s -> new Tuple2<String, String>(s.split(",")[0]+s.split(",")[1], s));

        JavaMapWithStateDStream<String,String,String,String> mappedStream= null;
                //JavaMapWithStateDStream<String,String,String,String> mappedStream= idValueStream.mapWithState(StateSpec.function(new DuplicateChecker()).timeout(new Duration(100)));
        // Start the computation
        JavaDStream<String> deduplicatedStream = mappedStream.map(s->s.toString());
        deduplicatedStream.print();

        JavaPairDStream<String,String> busKeyValueStream = deduplicatedStream.mapToPair(s->new Tuple2<String, String>(s.split(",")[2],s));
        JavaDStream<String> buskeyStream = busKeyValueStream.map(s -> s._1);

        JavaPairDStream<String, Integer> existingFeSpisInHBase =
                buskeyStream.transform(
                        new BulkGetRowKeyByKey(getHBaseContext(jssc.sparkContext()), "event"))
                        .mapToPair(feSpi -> new Tuple2<String, Integer>(feSpi,1));

        JavaPairDStream<String, String> finalNonDuplicateInBatch = busKeyValueStream.leftOuterJoin(existingFeSpisInHBase)
                        .filter(tpl -> !tpl._2._2.isPresent())
                        .mapToPair(tpl -> new Tuple2<String, String>(tpl._1, tpl._2._1));

        finalNonDuplicateInBatch.print();
        //jssc.checkpoint("hdfs://localhost:8020/user/cloudera/checkpoint");
        jssc.start();
        jssc.awaitTermination();
    }

    protected static JavaHBaseContext getHBaseContext(JavaSparkContext jsc) {
        JavaHBaseContext hbaseContext = new JavaHBaseContext(jsc, HbaseUtils.getConfiguration("localhost", "2181", "localhost", "60000"));
        return hbaseContext;
    }


}

 class HbaseUtils2 {

    */
/**
     * This method is used to create and return HBase configuration
     * @param zkIp zookeeper IP
     * @param zkPort zookeeper port
     * @param hbIp HBase master IP
     * @param hbPort HBase master port
     * @return
     *//*

    public static Configuration getConfiguration(String zkIp, String zkPort, String hbIp, String hbPort) {
        //Create HBase configuration object
        final Configuration hconf = HBaseConfiguration.create();
        hconf.set("hbase.zookeeper.quorum", zkIp);
        hconf.set("hbase.zookeeper.property.clientPort", zkPort);
        hconf.set("hbase.master", hbIp + ":" + hbPort);
        return hconf;
    }
}


class DuplicateChecker2 implements Function4<Time, String,Optional<String>,State<String>, Optional<String>>{

    @Override
    public Optional<String> call(Time time, String key, Optional<String> value, State<String> state) throws Exception {
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

class BulkGetRowKeyByKey2 implements Function<JavaRDD<String>, JavaRDD<String>> {


    private JavaHBaseContext hbaseContext;
    private String tableName;

    public BulkGetRowKeyByKey2(JavaHBaseContext hbaseContext, String tableName) {
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

class RowKeyGetFunction2 implements Function<String, Get> {

    private static final long serialVersionUID = 4460954280615221489L;

    public Get call(String id) throws Exception {
        return new Get((id != null) ? id.getBytes() : " ".getBytes());
    }
}

class RowKeyResultFunction2 implements Function<Result, String> {

    private static final long serialVersionUID = -8916204131378426986L;

    public String call(Result result) throws Exception {
        return Bytes.toString(result.getRow());
    }
}



*/
