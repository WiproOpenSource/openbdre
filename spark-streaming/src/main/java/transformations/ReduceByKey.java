package transformations;

import com.wipro.ats.bdre.md.api.GetProperties;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import util.WrapperMessage;

import java.util.*;
import java.util.Map;

/**
 * Created by cloudera on 7/7/17.
 */
public class ReduceByKey implements Transformation {
    @Override
    public JavaPairDStream<String, WrapperMessage> transform(JavaRDD emptyRDD, Map<Integer, JavaPairDStream<String, WrapperMessage>> prevDStreamMap, Map<Integer, Set<Integer>> prevMap, Integer pid, StructType schema,Map<String,Broadcast<HashMap<String,String>>> broadcastMap,JavaStreamingContext jssc) {
        List<Integer> prevPidList = new ArrayList<>();
        prevPidList.addAll(prevMap.get(pid));
        Integer prevPid = prevPidList.get(0);
        System.out.println("Inside Take prevPid = " + prevPid);
        JavaPairDStream<String,WrapperMessage> prevDStream = prevDStreamMap.get(prevPid);

        GetProperties getProperties = new GetProperties();
        Properties filterProperties = getProperties.getProperties(String.valueOf(pid), "default");
        //operator can be ReduceByKey or ReduceByKeyandWindow
        String operator = filterProperties.getProperty("operator");
        String executorPlugin = filterProperties.getProperty("executor-plugin");

        JavaPairDStream<String,WrapperMessage> outputDStream = prevDStream;
        Function2 function2 = null;
        try {
            Class userClass = Class.forName(executorPlugin);
            function2 = (Function2) userClass.newInstance();
        }catch (Exception e){
            e.printStackTrace();
        }

        if(operator.equalsIgnoreCase("ReduceByKey")){
            outputDStream = prevDStream.reduceByKey(function2);
        }
        else{
            String windowDurationString = filterProperties.getProperty("window-duration");
            String slideDurationString = filterProperties.getProperty("slide-duration");
            Duration windowDuration = new Duration(Long.parseLong(windowDurationString));
            Duration slideDuration = new Duration(Long.parseLong(slideDurationString));

            outputDStream = prevDStream.reduceByKeyAndWindow(function2 ,windowDuration,slideDuration);
        }
        return outputDStream;
    }
}
