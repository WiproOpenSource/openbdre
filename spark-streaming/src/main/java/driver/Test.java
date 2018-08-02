
package driver;

import com.databricks.spark.xml.XmlReader;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import scala.Tuple2;
import scala.collection.JavaConversions;
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
        /*JavaStreamingContext jssc = new JavaStreamingContext(sparkConf, Durations.seconds(20));

        Set<String> topicsSet = new HashSet<String>(Arrays.asList(topics.split(",")));
        Map<String, String> kafkaParams = new HashMap<String, String>();
        kafkaParams.put("metadata.broker.list", brokers);

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
        */

        JavaSparkContext sc = new JavaSparkContext(sparkConf);

        SQLContext sqlContext = new org.apache.spark.sql.SQLContext(sc);
        Dataset<Row> df1 = sqlContext.read().json("hdfs://localhost:8020/user/cloudera/json1.json");
        df1.show();
        Dataset<Row> df2 = sqlContext.read().json("hdfs://localhost:8020/user/cloudera/json2.json");
       // df2.show();


        Set<String> df1ColSet =  new LinkedHashSet<String>(Arrays.asList(df1.columns()));
        Set<String> df2ColSet =  new LinkedHashSet<String>(Arrays.asList(df2.columns()));
        Set<String> allColumns = new LinkedHashSet<String>();
        allColumns.addAll(df1ColSet);
        allColumns.addAll(df2ColSet);


        df1.selectExpr(expr(df1ColSet, allColumns)).show();
        df2.selectExpr(expr(df2ColSet, allColumns)).show();

        df1.selectExpr(expr(df1ColSet, allColumns)).unionAll(df2.selectExpr(expr(df2ColSet, allColumns))).show();

    }

    public static scala.collection.Seq<java.lang.String> expr(Set<String> mycols, Set<String> allcols){
        List<String> finalList= new LinkedList<String>();
        System.out.println("mycols = " + mycols);
        System.out.println("allcols = " + allcols);
        for(String s: allcols){
            System.out.println("s = " + s);
            if(mycols.contains(s)) {
                finalList.add(s);
                System.out.println("s is present= " + s);
            }
            else {
                finalList.add(null +" as "+s);
                System.out.println("s is absent= " + s);
            }
        }
        return JavaConversions.asScalaBuffer(new LinkedList<String>(finalList)).toSeq();
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
        Dataset<Row> df = new XmlReader().xmlRdd(sqlContext, javaRDD.rdd());
        System.out.println("df.schema() = " + df.schema());
        JavaRDD<Row> rowJavaRDD = df.javaRDD();
        rowJavaRDD.take(15);
        outputPairRdd = rowJavaRDD.mapToPair(row -> new Tuple2<String, WrapperMessage>(null,new WrapperMessage(row)));
        return outputPairRdd ;
    }
}





