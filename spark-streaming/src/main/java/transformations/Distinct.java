package transformations;

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
 * Created by cloudera on 6/8/17.
 */
public class Distinct implements Transformation {
    @Override
    public JavaPairDStream<String,WrapperMessage> transform(JavaRDD emptyRDD, Map<Integer, JavaPairDStream<String,WrapperMessage>> prevDStreamMap, Map<Integer, Set<Integer>> prevMap, Integer pid, StructType schema,Map<String,Broadcast<HashMap<String,String>>> broadcastMap,JavaStreamingContext jssc) {
        List<Integer> prevPidList = new ArrayList<>();
        prevPidList.addAll(prevMap.get(pid));
        Integer prevPid = prevPidList.get(0);
        System.out.println("Inside distinct prevPid = " + prevPid);
        JavaPairDStream<String,WrapperMessage> prevDStream = prevDStreamMap.get(prevPid);
        JavaDStream<WrapperMessage> dStream = prevDStream.map(s -> s._2);
        JavaDStream<WrapperMessage> finalDStream = dStream.transform(new Function<JavaRDD<WrapperMessage>, JavaRDD<WrapperMessage>>() {
            @Override
            public JavaRDD<WrapperMessage> call(JavaRDD<WrapperMessage> rddWrapperMessage) throws Exception {

                JavaRDD<Row> rddRow = rddWrapperMessage.map(new Function<WrapperMessage, Row>() {
                                                                @Override
                                                                public Row call(WrapperMessage wrapperMessage) throws Exception {
                                                                    return wrapperMessage.getRow();
                                                                }
                                                            }
                );


                SQLContext sqlContext = SQLContext.getOrCreate(rddWrapperMessage.context());
                Dataset<Row> dataFrame = sqlContext.createDataFrame(rddRow, schema);
                Dataset<Row> filteredDF = null;

                if(dataFrame!=null && !dataFrame.rdd().isEmpty()){
                    System.out.println("showing dataframe before distinct ");
                    dataFrame.show(100);
                    filteredDF = dataFrame.distinct();
                    filteredDF.show(100);
                    System.out.println("showing dataframe after distinct ");
                }

                JavaRDD<WrapperMessage> finalRDD = emptyRDD;
                if (filteredDF != null) {
                    finalRDD = filteredDF.javaRDD().map(new Function<Row, WrapperMessage>() {
                                                            @Override
                                                            public WrapperMessage call(Row row) throws Exception {
                                                                return new WrapperMessage(row);
                                                            }
                                                        }
                    );

                }
                return finalRDD;
                // return filteredDF.javaRDD();
            }
        });
        return finalDStream.mapToPair(s -> new Tuple2<String, WrapperMessage>(null,s));
    }

}
