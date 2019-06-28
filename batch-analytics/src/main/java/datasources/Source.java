package datasources;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;

/**
 * Created by cloudera on 6/7/17.
 */
public interface Source {
    public JavaPairRDD<String,String> execute(JavaSparkContext sc, Integer pid) throws Exception;
}
