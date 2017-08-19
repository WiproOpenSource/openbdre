package transformations;

import com.wipro.ats.bdre.md.api.GetProperties;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import scala.Tuple2;
import util.WrapperMessage;

import java.util.*;
import java.util.Map;

/**
 * Created by cloudera on 7/7/17.
 */
public class FlatMap implements Transformation {
    @Override
    public JavaPairDStream<String, WrapperMessage> transform(JavaRDD emptyRDD, Map<Integer, JavaPairDStream<String, WrapperMessage>> prevDStreamMap, Map<Integer, Set<Integer>> prevMap, Integer pid, StructType schema,Map<String,Broadcast<HashMap<String,String>>> broadcastMap) {
        List<Integer> prevPidList = new ArrayList<>();
        prevPidList.addAll(prevMap.get(pid));
        Integer prevPid = prevPidList.get(0);
        System.out.println("Inside Take prevPid = " + prevPid);
        JavaPairDStream<String,WrapperMessage> prevDStream = prevDStreamMap.get(prevPid);

        GetProperties getProperties = new GetProperties();
        Properties filterProperties = getProperties.getProperties(String.valueOf(pid), "default");
        String operator = filterProperties.getProperty("operator");
        String mapper = filterProperties.getProperty("flat-mapper");
        String executorPlugin = filterProperties.getProperty("executor-plugin");

        JavaPairDStream<String,WrapperMessage> finalDStream = null ;
        if(mapper.equalsIgnoreCase("IdentityMapper")){
            finalDStream = prevDStream;
        }
        else {

            if(operator.equalsIgnoreCase("FlatMap")){
                try {
                    Class userClass =  Class.forName(executorPlugin);
                    FlatMapFunction function = (FlatMapFunction) userClass.newInstance();
                    finalDStream = prevDStream.flatMap(function).mapToPair(s -> new Tuple2<String, WrapperMessage>(null,(WrapperMessage)s));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else{
                try {
                    Class userClass =  Class.forName(executorPlugin);
                    PairFlatMapFunction function = (PairFlatMapFunction) userClass.newInstance();
                    finalDStream = prevDStream.flatMapToPair(function);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }
        return finalDStream;

    }
}
