package transformations;

import com.wipro.ats.bdre.md.api.GetProperties;
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
import java.util.Map;

/**
 * Created by cloudera on 6/9/17.
 */
public class Sort implements Transformation {
    @Override
    public JavaPairRDD<String,WrapperMessage> transform(JavaRDD emptyRDD, Map<Integer, JavaPairRDD<String,WrapperMessage>> prevRDDMap, Map<Integer, Set<Integer>> prevMap, Integer pid, StructType schema,Map<String,Broadcast<HashMap<String,String>>> broadcastMap,JavaSparkContext jssc) {
        List<Integer> prevPidList = new ArrayList<>();
        prevPidList.addAll(prevMap.get(pid));
        Integer prevPid = prevPidList.get(0);
        System.out.println("Inside sort prevPid = " + prevPid);
        JavaPairRDD<String,WrapperMessage> prevRDD = prevRDDMap.get(prevPid);
        JavaRDD<WrapperMessage> rdd = prevRDD.map(s -> s._2);

        GetProperties getProperties = new GetProperties();
        Properties sortProperties=  getProperties.getProperties(String.valueOf(pid),"default");

        String colName=sortProperties.getProperty("column").split(":")[0];
        String order = sortProperties.getProperty("order");


        System.out.println("colName = " + colName);

        //JavaDStream<WrapperMessage> finalDStream = dStream.transform(new Function<JavaRDD<WrapperMessage>, JavaRDD<WrapperMessage>>() {
        //    @Override
        //    public JavaRDD<WrapperMessage> call(JavaRDD<WrapperMessage> rddWrapperMessage) throws Exception {

                JavaRDD<Row> rddRow = rdd.map(new Function<WrapperMessage, Row>() {
                                                                @Override
                                                                public Row call(WrapperMessage wrapperMessage) throws Exception {
                                                                    return wrapperMessage.getRow();
                                                                }
                                                            }
                );


                SQLContext sqlContext = SQLContext.getOrCreate(rddRow.context());
                DataFrame prevDataFrame = sqlContext.createDataFrame(rddRow, schema);
                DataFrame sortedDF = null;


                if(prevDataFrame!=null && !prevDataFrame.rdd().isEmpty()){

                    System.out.println("showing dataframe before sort ");
                    prevDataFrame.show(100);
                    if(order.equalsIgnoreCase("descending")) {
                        sortedDF = prevDataFrame.sort(prevDataFrame.col(colName).desc());
                    }else{
                        sortedDF = prevDataFrame.sort(prevDataFrame.col(colName).asc());
                    }
                    sortedDF.show(100);
                    System.out.println("showing dataframe after sort ");

                }

                JavaRDD<WrapperMessage> finalRDD = emptyRDD;
                if (sortedDF != null) {
                    finalRDD = sortedDF.javaRDD().map(new Function<Row, WrapperMessage>() {
                                                          @Override
                                                          public WrapperMessage call(Row row) throws Exception {
                                                              return new WrapperMessage(row);
                                                          }
                                                      }
                    );

                }
                return finalRDD.mapToPair(s -> new Tuple2<String, WrapperMessage>(null,s));
                // return filteredDF.javaRDD();
            }
        //});
        //return finalDStream.mapToPair(s -> new Tuple2<String, WrapperMessage>(null,s));
    }

//}
