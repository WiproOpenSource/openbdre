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
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import scala.Tuple2;
import util.WrapperMessage;

import java.util.*;
import java.util.Map;

/**
 * Created by cloudera on 7/6/17.
 */
public class MapToPair implements Transformation{
    @Override
    public JavaPairDStream<String,WrapperMessage> transform(JavaRDD emptyRDD, Map<Integer, JavaPairDStream<String,WrapperMessage>> prevDStreamMap, Map<Integer, Set<Integer>> prevMap, Integer pid, StructType schema,Map<String,Broadcast<HashMap<String,String>>> broadcastMap,JavaStreamingContext jssc) {
        List<Integer> prevPidList = new ArrayList<>();
        prevPidList.addAll(prevMap.get(pid));
        Integer prevPid1 = prevPidList.get(0);
        System.out.println("Inside MapToPair prevPid1 = " + prevPid1);
        JavaPairDStream<String, WrapperMessage> inputDStream = prevDStreamMap.get(prevPid1);
        GetProperties getProperties = new GetProperties();
        Properties filterProperties = getProperties.getProperties(String.valueOf(pid), "default");

        String colName =filterProperties.getProperty("keyFields");

        JavaDStream<WrapperMessage> dStream = inputDStream.map(s -> s._2);
        JavaPairDStream<String, WrapperMessage> finalDStream = mapToPair(dStream,colName);


        finalDStream.print();
        return finalDStream;
    }

    public JavaPairDStream<String,WrapperMessage> mapToPair(JavaDStream<WrapperMessage> dStream, String colName){
        JavaPairDStream<String, WrapperMessage> finalDStream = null;

        String keyString = colName.substring(0,colName.indexOf(":"));
        System.out.println("keyString = " + keyString);
        String[] keyFields = keyString.split(",");
        System.out.println("Arrays.toString(keyFields) = " + Arrays.toString(keyFields));
        System.out.println(" Printing input dstream");
        dStream.print();

        if (dStream != null) {
            System.out.println(" dstream is not null");
            finalDStream = dStream.transformToPair(new Function<JavaRDD<WrapperMessage>, JavaPairRDD<String, WrapperMessage>>() {
                @Override
                public JavaPairRDD<String, WrapperMessage> call(JavaRDD<WrapperMessage> wrapperMessageJavaRDD) throws Exception {
                    JavaRDD<Row> rddRow = wrapperMessageJavaRDD.map(record -> record.getRow());

                    JavaPairRDD<String, WrapperMessage> pairRDD = rddRow.mapToPair(new PairFunction<Row, String, WrapperMessage>() {
                        @Override
                        public Tuple2<String, WrapperMessage> call(Row row) throws Exception {
                            String key = "";
                            if (row != null) {
                                for (String keyField : keyFields) {
                                    // System.out.println(keyField + " index is " + schema.fieldIndex(keyField));
                                    System.out.println("keyField = " + keyField);
                                    String[] fields = keyField.split("\\.");
                                    System.out.println("Arrays.toString(fields) = " + Arrays.toString(fields));
                                    int i=0;
                                    Row row2 = row;
                                    for(i=0; i<fields.length -1 ; i++){
                                        System.out.println("fields[i] = " + fields[i]);
                                        System.out.println("Before row = " + row2.toString());
                                        row2 = row2.getStruct(row2.fieldIndex(fields[i]));
                                        System.out.println("After row = " + row2.toString());
                                    }
                                    System.out.println("outside fields[i] = " + fields[i]);
                                    key += row2.getString(row2.fieldIndex(fields[i])) + "#";
                                }
                                key = key.substring(0,key.length()-1);
                            }
                            return new Tuple2<String, WrapperMessage>(key, WrapperMessage.convertToWrapperMessage(row));

                        }
                    });

                    return pairRDD;
                }
            });
        }
        return finalDStream;
    }
}
