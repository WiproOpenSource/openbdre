package transformations;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import util.WrapperMessage;

import java.util.*;
import java.util.Map;

/**
 * Created by cloudera on 7/7/17.
 */
public class SortByKey implements Transformation {
    @Override
    public JavaPairRDD<String, WrapperMessage> transform(JavaRDD emptyRDD, Map<Integer, JavaPairRDD<String, WrapperMessage>> prevRDDMap, Map<Integer, Set<Integer>> prevMap, Integer pid, StructType schema,Map<String,Broadcast<HashMap<String,String>>> broadcastMap,JavaSparkContext sc) {
        List<Integer> prevPidList = new ArrayList<>();
        prevPidList.addAll(prevMap.get(pid));
        Integer prevPid = prevPidList.get(0);
        System.out.println("Inside filter prevPid = " + prevPid);
        JavaPairRDD<String,WrapperMessage> prevRDD = prevRDDMap.get(prevPid);

                return prevRDD.sortByKey();
            }
}
