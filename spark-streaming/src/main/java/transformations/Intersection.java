package transformations;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function3;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.Time;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import scala.Tuple2;
import util.WrapperMessage;

import java.util.*;
import java.util.Map;

/**
 * Created by cloudera on 6/9/17.
 */
public class Intersection implements Transformation {
    @Override
    public JavaPairDStream<String,WrapperMessage> transform(JavaRDD emptyRDD, Map<Integer, JavaPairDStream<String,WrapperMessage>> prevDStreamMap, Map<Integer, Set<Integer>> prevMap, Integer pid, StructType schema,Map<String,Broadcast<HashMap<String,String>>> broadcastMap) {
        List<Integer> prevPidList = new ArrayList<>();
        prevPidList.addAll(prevMap.get(pid));
        Integer prevPid1 = prevPidList.get(0);
        System.out.println("before entering for loop first prevPid1 = " + prevPid1);

        JavaPairDStream<String,WrapperMessage> intersectionPairDStream = prevDStreamMap.get(prevPid1);
        JavaDStream<WrapperMessage> intersectionDStream = intersectionPairDStream.map(s -> s._2);
        for(int i=1;i< prevPidList.size();i++){
            System.out.println("intersection of dstream of pid = " + prevPidList.get(i));

            JavaPairDStream<String,WrapperMessage> pairDStream1 = prevDStreamMap.get(prevPidList.get(i));
            JavaDStream<WrapperMessage> dStream1 = pairDStream1.map(s -> s._2);

            if(intersectionDStream!=null && dStream1!=null){


                intersectionDStream = intersectionDStream.transformWith(dStream1, new Function3<JavaRDD<WrapperMessage>,JavaRDD<WrapperMessage>,Time,JavaRDD<WrapperMessage>>() {
                    @Override
                    public JavaRDD<WrapperMessage> call(JavaRDD<WrapperMessage> rddWrapperMessage1, JavaRDD<WrapperMessage> rddWrapperMessage2, Time time) throws Exception {

                        JavaRDD<Row> rddRow1 = rddWrapperMessage1.map(new Function<WrapperMessage, Row>() {
                                                                        @Override
                                                                        public Row call(WrapperMessage wrapperMessage) throws Exception {
                                                                            return wrapperMessage.getRow();
                                                                        }
                                                                    }
                        );

                        JavaRDD<Row> rddRow2 = rddWrapperMessage2.map(new Function<WrapperMessage, Row>() {
                                                                          @Override
                                                                          public Row call(WrapperMessage wrapperMessage) throws Exception {
                                                                              return wrapperMessage.getRow();
                                                                          }
                                                                      }
                        );


                        SQLContext sqlContext1 = SQLContext.getOrCreate(rddWrapperMessage1.context());
                        DataFrame dataFrame1 = sqlContext1.createDataFrame(rddRow1, schema);

                        SQLContext sqlContext2 = SQLContext.getOrCreate(rddWrapperMessage2.context());
                        DataFrame dataFrame2 = sqlContext2.createDataFrame(rddRow2, schema);

                        DataFrame returnDF = null;

                        if(dataFrame1!=null && !dataFrame1.rdd().isEmpty() && dataFrame2!=null && !dataFrame2.rdd().isEmpty()){
                            System.out.println("showing dataframe before intersect ");
                            dataFrame1.show(100);
                            dataFrame2.show(100);
                            returnDF = dataFrame1.intersect(dataFrame2);
                            System.out.println("showing dataframe after intersect ");
                            returnDF.show(100);

                        }

                        JavaRDD<WrapperMessage> finalRDD = emptyRDD;
                        if (returnDF != null) {
                            finalRDD = returnDF.javaRDD().map(new Function<Row, WrapperMessage>() {
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

            }

        }
        return intersectionDStream.mapToPair(s -> new Tuple2<String, WrapperMessage>(null,s));
    }
}
