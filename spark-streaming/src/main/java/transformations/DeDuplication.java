package transformations;

import com.wipro.ats.bdre.md.api.GetProperties;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.Time;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import util.WrapperMessage;

import java.util.*;
import java.util.Map;

/**
 * Created by cloudera on 8/23/17.
 */
public class DeDuplication implements Transformation {
    @Override
    public JavaPairDStream<String, WrapperMessage> transform(JavaRDD emptyRDD, Map<Integer, JavaPairDStream<String, WrapperMessage>> prevDStreamMap, Map<Integer, Set<Integer>> prevMap, Integer pid, StructType schema, Map<String, Broadcast<HashMap<String, String>>> broadcastMap, JavaStreamingContext jssc) {
        List<Integer> prevPidList = new ArrayList<>();
        prevPidList.addAll(prevMap.get(pid));
        Integer prevPid = prevPidList.get(0);
        System.out.println("Inside DeDuplication prevPid = " + prevPid);
        JavaPairDStream<String,WrapperMessage> prevDStream = prevDStreamMap.get(prevPid);
        GetProperties getProperties = new GetProperties();
        Properties dupProperties = getProperties.getProperties(String.valueOf(pid), "deduplication");
        String type = dupProperties.getProperty("type");
        prevDStream.foreachRDD(new Function2<JavaPairRDD<String, WrapperMessage>, Time, Void>() {
            @Override
            public Void call(JavaPairRDD<String, WrapperMessage> stringWrapperMessageJavaPairRDD, Time time) throws Exception {
                if(type.equalsIgnoreCase("WindowDeduplication"))
                    System.out.println("Beginning of Window deduplication = " + new Date().getTime() +"for pid = "+pid);
                else if(type.equalsIgnoreCase("HbaseDeduplication"))
                    System.out.println("Beginning of Hbase deduplication = " + new Date().getTime() +"for pid = "+pid);
                return null;
            }
        });
        JavaDStream<WrapperMessage> dStream = prevDStream.map(s -> s._2);

        JavaPairDStream<String, WrapperMessage> deDupPairDstream= null;

        if(type.equalsIgnoreCase("WindowDeduplication")){
            String colName = dupProperties.getProperty("windowDeDuplicationColumn");
            String windowDurationString = dupProperties.getProperty("windowDuration");
            long windowDuration = Long.parseLong(windowDurationString);
            MapToPair mapToPairClass = new MapToPair();
            JavaPairDStream<String,WrapperMessage> pairedDstream = mapToPairClass.mapToPair(dStream,colName);
            WindowDeDuplication windowDeDuplication = new WindowDeDuplication();
            deDupPairDstream = windowDeDuplication.convertJavaPairDstream(pairedDstream,windowDuration);

        }
        else if(type.equalsIgnoreCase("HbaseDeduplication")){
            String colName = dupProperties.getProperty("hbaseDeDuplicationColumn");
            String hbaseConnectionName = dupProperties.getProperty("hbaseConnectionName");
            String hbaseTableName = dupProperties.getProperty("hbaseTableName");

            MapToPair mapToPairClass = new MapToPair();
            JavaPairDStream<String,WrapperMessage> pairedDstream = mapToPairClass.mapToPair(dStream,colName);
            HBaseDeDuplication hBaseDeDuplication = new HBaseDeDuplication();
            deDupPairDstream = hBaseDeDuplication.convertJavaPairDstream(pairedDstream,jssc,hbaseConnectionName,hbaseTableName,schema);
        }
        deDupPairDstream.foreachRDD(new Function2<JavaPairRDD<String, WrapperMessage>, Time, Void>() {
            @Override
            public Void call(JavaPairRDD<String, WrapperMessage> stringWrapperMessageJavaPairRDD, Time time) throws Exception {
                if(type.equalsIgnoreCase("WindowDeduplication"))
                    System.out.println("End of Window deduplication = " + new Date().getTime() +"for pid = "+pid);
                else if(type.equalsIgnoreCase("HbaseDeduplication"))
                    System.out.println("End of Hbase deduplication = " + new Date().getTime() +"for pid = "+pid);
                return null;
            }
        });
        deDupPairDstream.print();
        return deDupPairDstream;
    }
}
