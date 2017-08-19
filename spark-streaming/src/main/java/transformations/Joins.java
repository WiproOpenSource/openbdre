package transformations;

import com.wipro.ats.bdre.md.api.GetProperties;
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
 * Created by cloudera on 7/4/17.
 */
public class Joins implements Transformation {
    @Override
    public JavaPairDStream<String,WrapperMessage> transform(JavaRDD emptyRDD, Map<Integer, JavaPairDStream<String,WrapperMessage>> prevDStreamMap, Map<Integer, Set<Integer>> prevMap, Integer pid, StructType schema,Map<String,Broadcast<HashMap<String,String>>> broadcastMap) {
        List<Integer> prevPidList = new ArrayList<>();
        prevPidList.addAll(prevMap.get(pid));
        Integer prevPid1 = prevPidList.get(0);
        System.out.println("before entering for loop first prevPid1 = " + prevPid1);
        GetProperties getProperties = new GetProperties();
        Properties filterProperties = getProperties.getProperties(String.valueOf(prevPid1), "default");
        final String joinMessage1 = filterProperties.getProperty("join-message");
        final String joinColumn1 = filterProperties.getProperty("join-column");
        //joinType - One of: inner, outer, left_outer, right_outer, leftsemi.
        final String joinType = filterProperties.getProperty("join-type");

        JavaPairDStream<String,WrapperMessage> joinPairDStream = prevDStreamMap.get(prevPid1);
        JavaDStream<WrapperMessage> joinDStream = joinPairDStream.map(s -> s._2);
        for(int i=1;i< prevPidList.size();i++){
            Integer currentPid = prevPidList.get(i);
            System.out.println("join of dstream of pid = " + currentPid);
            filterProperties = getProperties.getProperties(String.valueOf(currentPid), "default");
            final String joinMessage2 = filterProperties.getProperty("join-message");
            final String joinColumn2 = filterProperties.getProperty("join-column");
            JavaPairDStream<String,WrapperMessage> pairDStream1 = prevDStreamMap.get(prevPidList.get(i));
            JavaDStream<WrapperMessage> dStream1 = pairDStream1.map(s -> s._2);
            if(joinDStream!=null && dStream1!=null){


                joinDStream = joinDStream.transformWith(dStream1, new Function3<JavaRDD<WrapperMessage>,JavaRDD<WrapperMessage>,Time,JavaRDD<WrapperMessage>>() {
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
                            returnDF = dataFrame1.join(dataFrame2, dataFrame1.col(joinColumn1).equalTo(dataFrame2.col(joinColumn2)), joinType);
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
        return joinDStream.mapToPair(s -> new Tuple2<String, WrapperMessage>(null,s));
    }
}
