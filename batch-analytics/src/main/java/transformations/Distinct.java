package transformations;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;
import util.WrapperMessage;

import java.util.*;

/**
 * Created by cloudera on 6/8/17.
 */
public class Distinct implements Transformation {
    @Override
    public JavaPairRDD<String,WrapperMessage> transform(JavaRDD emptyRDD, Map<Integer, JavaPairRDD<String,WrapperMessage>> prevRDDMap, Map<Integer, Set<Integer>> prevMap, Integer pid, StructType schema,Map<String,Broadcast<HashMap<String,String>>> broadcastMap,JavaSparkContext sc) {
        List<Integer> prevPidList = new ArrayList<>();
        prevPidList.addAll(prevMap.get(pid));
        Integer prevPid = prevPidList.get(0);
        System.out.println("Inside distinct prevPid = " + prevPid);
        JavaPairRDD<String,WrapperMessage> prevRDD = prevRDDMap.get(prevPid);
        JavaRDD<WrapperMessage> rdd = prevRDD.map(s -> s._2);
        //JavaRDD<WrapperMessage> finalRDD = rdd.transform(new Function<JavaRDD<WrapperMessage>, JavaRDD<WrapperMessage>>() {
        //    @Override
        //    public JavaRDD<WrapperMessage> call(JavaRDD<WrapperMessage> rddWrapperMessage) throws Exception {

                JavaRDD<Row> rddRow = rdd.map(new Function<WrapperMessage, Row>() {
                                                                @Override
                                                                public Row call(WrapperMessage wrapperMessage) throws Exception {
                                                                    return wrapperMessage.getRow();
                                                                }
                                                            }
                );

                SQLContext sqlContext = SQLContext.getOrCreate(rdd.context());
                DataFrame dataFrame = sqlContext.createDataFrame(rddRow, schema);
                DataFrame filteredDF = null;

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
                //return finalRDD;
                // return filteredDF.javaRDD();
            //}
        //});
        return finalRDD.mapToPair(s -> new Tuple2<String, WrapperMessage>(null,s));
    }

}
