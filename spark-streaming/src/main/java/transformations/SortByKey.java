package transformations;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import util.WrapperMessage;

import java.util.*;
import java.util.Map;

/**
 * Created by cloudera on 7/7/17.
 */
public class SortByKey implements Transformation {
    @Override
    public JavaPairDStream<String, WrapperMessage> transform(JavaRDD emptyRDD, Map<Integer, JavaPairDStream<String, WrapperMessage>> prevDStreamMap, Map<Integer, Set<Integer>> prevMap, Integer pid, StructType schema,Map<String,Broadcast<HashMap<String,String>>> broadcastMap,JavaStreamingContext jssc) {
        List<Integer> prevPidList = new ArrayList<>();
        prevPidList.addAll(prevMap.get(pid));
        Integer prevPid = prevPidList.get(0);
        System.out.println("Inside filter prevPid = " + prevPid);
        JavaPairDStream<String,WrapperMessage> prevDStream = prevDStreamMap.get(prevPid);

        JavaPairDStream<String,WrapperMessage> outputDStream = prevDStream.transformToPair(new Function<JavaPairRDD<String, WrapperMessage>, JavaPairRDD<String, WrapperMessage>>() {
            @Override
            public JavaPairRDD<String, WrapperMessage> call(JavaPairRDD<String, WrapperMessage> stringWrapperMessageJavaPairRDD) throws Exception {
                return stringWrapperMessageJavaPairRDD.sortByKey();
            }
        });
        return outputDStream;
    }
}
