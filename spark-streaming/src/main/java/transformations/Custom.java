package transformations;

import com.wipro.ats.bdre.md.api.GetProperties;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import util.WrapperMessage;

import java.util.*;
import java.util.Map;

/**
 * Created by cloudera on 8/3/17.
 */
public abstract class Custom  {
    //@Override
    public static JavaPairDStream<String, WrapperMessage> transform(JavaRDD emptyRDD, Map<Integer, JavaPairDStream<String, WrapperMessage>> prevDStreamMap, Map<Integer, Set<Integer>> prevMap, Integer pid, StructType schema,Map<String,Broadcast<HashMap<String,String>>> broadcastMap,JavaStreamingContext jssc) {
        List<Integer> prevPidList = new ArrayList<>();
        prevPidList.addAll(prevMap.get(pid));
        Integer prevPid = prevPidList.get(0);
        JavaPairDStream<String,WrapperMessage> prevDStream = prevDStreamMap.get(prevPid);

        GetProperties getProperties = new GetProperties();
        Properties filterProperties = getProperties.getProperties(String.valueOf(pid), "default");
        String executorPlugin = filterProperties.getProperty("executor-plugin");
        JavaPairDStream<String,WrapperMessage> dStreamPostTransformation = null;
        try {
            Class customClass = Class.forName(executorPlugin);
            Custom customClassObject = (Custom) customClass.newInstance();

            if(prevPidList.size() == 1)
                dStreamPostTransformation =  customClassObject.convertJavaPairDstream(prevDStream,broadcastMap,jssc);
            else
                dStreamPostTransformation = customClassObject.convertMultiplePairDstream(prevDStreamMap,prevMap,pid,schema,broadcastMap,jssc);

        }catch (Exception e){
            e.printStackTrace();

        }
        return dStreamPostTransformation;
    }

    public abstract JavaPairDStream<String, WrapperMessage> convertJavaPairDstream(JavaPairDStream<String, WrapperMessage> inputDstream, Map<String,Broadcast<HashMap<String,String>>> broadcastMap ,JavaStreamingContext ssc);

    public abstract JavaPairDStream<String, WrapperMessage> convertMultiplePairDstream( Map<Integer, JavaPairDStream<String, WrapperMessage>> prevDStreamMap, Map<Integer, Set<Integer>> prevMap,Integer pid, StructType schema, Map<String,Broadcast<HashMap<String,String>>> broadcastMap,JavaStreamingContext jssc);
}
