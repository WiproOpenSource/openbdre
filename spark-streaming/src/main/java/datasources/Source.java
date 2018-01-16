package datasources;

import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

/**
 * Created by cloudera on 6/7/17.
 */
public interface Source {
    public JavaPairDStream<String,String> execute(JavaStreamingContext ssc, Integer pid) throws Exception;
}
