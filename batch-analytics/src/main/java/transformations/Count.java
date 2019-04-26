package transformations;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;
import util.WrapperMessage;

import java.util.*;
import java.util.Map;

/**
 * Created by cloudera on 7/13/17.
 */
public class Count implements Transformation {
    @Override
    public JavaPairRDD<String, WrapperMessage> transform(JavaRDD emptyRDD, Map<Integer, JavaPairRDD<String, WrapperMessage>> prevRDDMap, Map<Integer, Set<Integer>> prevMap, Integer pid, StructType schema,Map<String,Broadcast<HashMap<String,String>>> broadcastMap,JavaSparkContext sc) {
        List<Integer> prevPidList = new ArrayList<>();
        prevPidList.addAll(prevMap.get(pid));
        Integer prevPid = prevPidList.get(0);
        System.out.println("Inside Count prevPid = " + prevPid);
        JavaPairRDD<String,WrapperMessage> prevRDD = prevRDDMap.get(prevPid);
        JavaRDD<WrapperMessage> rdd = prevRDD.map(s -> s._2);

        //JavaRDD<String> countRDD = rdd.count();
        List<String> count = new ArrayList<String>();
        Long cv=rdd.count();
        String countvalue=Long.toString(cv);
        count.add(countvalue);
        JavaRDD<String> countRDD = sc.parallelize(count);
        JavaRDD<WrapperMessage> finalRDD = countRDD.map(s -> new WrapperMessage(RowFactory.create(s)));
        return finalRDD.mapToPair(s -> new Tuple2<String, WrapperMessage>(null,s));

    }
}
