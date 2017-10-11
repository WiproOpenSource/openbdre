package transformations;

import com.wipro.ats.bdre.md.api.GetMessageColumns;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function3;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.Time;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import scala.Tuple2;
import scala.collection.JavaConversions;
import util.WrapperMessage;

import java.util.*;
import java.util.Map;

/**
 * Created by cloudera on 5/22/17.
 */
public class Union implements Transformation
{
    @Override
    public JavaPairDStream<String,WrapperMessage> transform(JavaRDD emptyRDD, Map<Integer, JavaPairDStream<String,WrapperMessage>> prevDStreamMap, Map<Integer, Set<Integer>> prevMap, Integer pid, StructType schema,Map<String,Broadcast<HashMap<String,String>>> broadcastMap,JavaStreamingContext jssc) {
        List<Integer> prevPidList = new ArrayList<>();
        prevPidList.addAll(prevMap.get(pid));
        Integer prevPid1 = prevPidList.get(0);
        System.out.println("before entering for loop first prevPid1 = " + prevPid1);
        JavaPairDStream<String,WrapperMessage> unionedDStream = prevDStreamMap.get(prevPid1);
        for(int i=1;i< prevPidList.size();i++){
            System.out.println("union of dstream of pid = " + prevPidList.get(i));
            JavaPairDStream<String,WrapperMessage> dStream1 = prevDStreamMap.get(prevPidList.get(i));
            if(unionedDStream!=null && dStream1!=null){

                System.out.println("showing dstream df1 before union ");
                dStream1.print(100);
                System.out.println("showing dataframe unionedDF before union ");
                unionedDStream.print(100);
                unionedDStream = unionedDStream.union(dStream1);
                System.out.println("showing dataframe unionedDF after union ");
                unionedDStream.print(100);

            }

        }
        return unionedDStream;
    }

}

