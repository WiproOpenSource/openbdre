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
// import org.apache.spark.streaming.Time;

import org.apache.spark.api.java.JavaSparkContext;
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
    public JavaPairRDD<String,WrapperMessage> transform(JavaRDD emptyRDD, Map<Integer, JavaPairRDD<String,WrapperMessage>> prevRDDMap, Map<Integer, Set<Integer>> prevMap, Integer pid, StructType schema,Map<String,Broadcast<HashMap<String,String>>> broadcastMap,JavaSparkContext sc) {
        List<Integer> prevPidList = new ArrayList<>();
        prevPidList.addAll(prevMap.get(pid));
        Integer prevPid1 = prevPidList.get(0);
        System.out.println("before entering for loop first prevPid1 = " + prevPid1);
        JavaPairRDD<String,WrapperMessage> unionedRDD = prevRDDMap.get(prevPid1);
        for(int i=1;i< prevPidList.size();i++){
            System.out.println("union of RDD of pid = " + prevPidList.get(i));
            JavaPairRDD<String,WrapperMessage> RDD1 = prevRDDMap.get(prevPidList.get(i));
            if(unionedRDD!=null && RDD1!=null){

                System.out.println("showing RDD df1 before union ");
                RDD1.foreach(data -> {
                    System.out.println("key="+data._1() + "value=" + data._2());
                });
                System.out.println("showing dataframe unionedDF before union ");
                unionedRDD.foreach(data -> {
                    System.out.println("key="+data._1() + "value=" + data._2());
                });
                unionedRDD = unionedRDD.union(RDD1);
                System.out.println("showing dataframe unionedDF after union ");
                unionedRDD.foreach(data -> {
                    System.out.println("key="+data._1() + "value=" + data._2());
                });

            }

        }
        return unionedRDD;
    }

}

