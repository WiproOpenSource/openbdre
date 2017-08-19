package transformations;

import com.wipro.ats.bdre.md.api.GetProperties;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import scala.Tuple2;
import util.WrapperMessage;

import java.util.*;
import java.util.Map;

/**
 * Created by cloudera on 7/6/17.
 */
public class MapToPair implements Transformation{
    @Override
    public JavaPairDStream<String,WrapperMessage> transform(JavaRDD emptyRDD, Map<Integer, JavaPairDStream<String,WrapperMessage>> prevDStreamMap, Map<Integer, Set<Integer>> prevMap, Integer pid, StructType schema,Map<String,Broadcast<HashMap<String,String>>> broadcastMap) {
        List<Integer> prevPidList = new ArrayList<>();
        prevPidList.addAll(prevMap.get(pid));
        Integer prevPid1 = prevPidList.get(0);
        System.out.println("Inside MapToPair prevPid1 = " + prevPid1);
        JavaPairDStream<String, WrapperMessage> inputDStream = prevDStreamMap.get(prevPid1);
        GetProperties getProperties = new GetProperties();
        Properties filterProperties = getProperties.getProperties(String.valueOf(pid), "default");
        String keyString = filterProperties.getProperty("keyFields");

        JavaPairDStream<String, WrapperMessage> finalDStream = null;
        JavaDStream<WrapperMessage> dStream = inputDStream.map(s -> s._2);

            String[] keyFields = keyString.split(",");
            System.out.println(" Printing input dstream");
            dStream.print();

            if (dStream != null) {
                System.out.println(" dstream is not null");
                finalDStream = dStream.transformToPair(new Function<JavaRDD<WrapperMessage>, JavaPairRDD<String, WrapperMessage>>() {
                    @Override
                    public JavaPairRDD<String, WrapperMessage> call(JavaRDD<WrapperMessage> wrapperMessageJavaRDD) throws Exception {
                        JavaRDD<Row> rddRow = wrapperMessageJavaRDD.map(record -> WrapperMessage.convertToRow(record));

                        JavaPairRDD<String, WrapperMessage> pairRDD = rddRow.mapToPair(new PairFunction<Row, String, WrapperMessage>() {
                            @Override
                            public Tuple2<String, WrapperMessage> call(Row row) throws Exception {
                                String key = "";
                                if (row != null) {
                                    for (String keyField : keyFields) {
                                        System.out.println(keyField + " index is " + schema.fieldIndex(keyField));
                                        key += row.getString(schema.fieldIndex(keyField)) + "#";
                                    }
                                }
                                return new Tuple2<String, WrapperMessage>(key, WrapperMessage.convertToWrapperMessage(row));

                            }
                        });

                        return pairRDD;
                    }
                });
            }



        System.out.println("printing output dstream " );
        finalDStream.print();
        return finalDStream;
    }
}
