package transformations;

import com.wipro.ats.bdre.md.api.GetProperties;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import util.WrapperMessage;

import java.util.*;
import java.util.Map;

/**
 * Created by cloudera on 8/6/17.
 */
public class CustomFilter implements Transformation {
    @Override
    public JavaPairDStream<String, WrapperMessage> transform(JavaRDD emptyRDD, Map<Integer, JavaPairDStream<String, WrapperMessage>> prevDStreamMap, Map<Integer, Set<Integer>> prevMap, Integer pid, StructType schema, Map<String, Broadcast<HashMap<String, String>>> broadcastMap,JavaStreamingContext jssc) {
        List<Integer> prevPidList = new ArrayList<>();
        prevPidList.addAll(prevMap.get(pid));
        Integer prevPid = prevPidList.get(0);
        System.out.println("Inside Custom Filter prevPid = " + prevPid);
        JavaPairDStream<String,WrapperMessage> prevDStream = prevDStreamMap.get(prevPid);


        GetProperties getProperties = new GetProperties();
        Properties filterProperties = getProperties.getProperties(String.valueOf(pid), "default");
        String executorPlugin = filterProperties.getProperty("executor-plugin");
        JavaPairDStream<String,WrapperMessage> finalDStream = null;
        try {
            Class userClass = Class.forName(executorPlugin);
            Function function = (Function) userClass.newInstance();
            JavaPairDStream<String,Row> prevRowDstream = prevDStream.mapValues(s -> s.getRow());
            JavaPairDStream<String,Row> filteredRowDstream = prevRowDstream.filter(function);
            finalDStream = filteredRowDstream.mapValues(s -> new WrapperMessage(s));
        } catch (Exception e){
            e.printStackTrace();
        }
        finalDStream.print();
        return finalDStream;

    }
}
