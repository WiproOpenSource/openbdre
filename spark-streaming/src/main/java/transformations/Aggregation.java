package transformations;

import com.wipro.ats.bdre.md.api.GetProperties;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import scala.Tuple2;
import util.WrapperMessage;

import java.util.*;
import java.util.Map;

/**
 * Created by cloudera on 7/4/17.
 */
public class Aggregation implements Transformation{



    @Override
    public JavaPairDStream<String,WrapperMessage> transform(JavaRDD emptyRDD, Map<Integer, JavaPairDStream<String,WrapperMessage>> prevDStreamMap, Map<Integer, Set<Integer>> prevMap, Integer pid, StructType schema,Map<String,Broadcast<HashMap<String,String>>> broadcastMap,JavaStreamingContext jssc) {
        List<Integer> prevPidList = new ArrayList<>();
        prevPidList.addAll(prevMap.get(pid));
        Integer prevPid = prevPidList.get(0);
        System.out.println("Inside Aggregation prevPid = " + prevPid);
        JavaPairDStream<String,WrapperMessage> prevDStream = prevDStreamMap.get(prevPid);

        Map<String, String> fieldAggrMap = new HashMap<>();
        GetProperties getProperties = new GetProperties();
        Properties aggProperties = getProperties.getProperties(String.valueOf(pid), "default");
        String columnAggr = aggProperties.getProperty("column:aggType");
        String[] columnsAggrArray = columnAggr.split(",");
        for(String s: columnsAggrArray){
            String[] sArray = s.split(":::");
            fieldAggrMap.put(sArray[0].substring(0,sArray[0].indexOf(":")),sArray[1]);
        }
        //TODO: In ui, map should be of this format: Map("col1" -> "max", "col2" -> "avg", "col3" -> "sum", "col4" -> "min")

        JavaDStream<WrapperMessage> dStream = prevDStream.map(s -> s._2);

        JavaDStream<WrapperMessage> finalDStream = dStream.transform(new Function<JavaRDD<WrapperMessage>, JavaRDD<WrapperMessage>>() {
            @Override
            public JavaRDD<WrapperMessage> call(JavaRDD<WrapperMessage> rddWrapperMessage) throws Exception {
                JavaRDD<Row> rddRow = rddWrapperMessage.map(record -> WrapperMessage.convertToRow(record));
                SQLContext sqlContext = SQLContext.getOrCreate(rddWrapperMessage.context());
                Dataset<Row> dataFrame = sqlContext.createDataFrame(rddRow, schema);
                Dataset<Row> aggregatedDF = null;

                if (dataFrame != null && !dataFrame.rdd().isEmpty()) {

                        System.out.println("showing dataframe before filter ");
                        dataFrame.show(100);
                        aggregatedDF = dataFrame.agg(fieldAggrMap);
                        aggregatedDF.show(100);
                        System.out.println("showing dataframe after filter ");

                }
                JavaRDD<WrapperMessage> finalRDD = emptyRDD;
                if (aggregatedDF != null)
                    finalRDD = aggregatedDF.javaRDD().map(s -> WrapperMessage.convertToWrapperMessage(s));
                return finalRDD;
            }
        });
        return finalDStream.mapToPair(s -> new Tuple2<String, WrapperMessage>(null,s));

    }
}
