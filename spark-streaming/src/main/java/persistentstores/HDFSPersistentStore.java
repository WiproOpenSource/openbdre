package persistentstores;

import com.wipro.ats.bdre.md.api.GetProperties;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import util.WrapperMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by cloudera on 5/21/17.
 */
public class HDFSPersistentStore implements PersistentStore {

    @Override
    public void persist(JavaRDD emptyRDD, JavaPairDStream<String,WrapperMessage> inputDStream, Integer pid, Integer prevPid, StructType schema, Map<String,Broadcast<HashMap<String,String>>> broadcastMap, JavaStreamingContext jssc) throws Exception {
        try {
            final String hdfsPath = "/user/cloudera/spark-streaming-data/";
            System.out.println("Inside emitter hdfs, persisting");
            GetProperties getProperties = new GetProperties();
            Properties hdfsProperties = getProperties.getProperties(String.valueOf(pid), "persistentStore");
            System.out.println(" Printing Pair dstream" );
            inputDStream.print();
            inputDStream.dstream().saveAsTextFiles(hdfsPath,"stream");
            JavaDStream<WrapperMessage> dStream = inputDStream.map(s -> s._2);

          /*  JavaDStream<WrapperMessage> finalDStream =  dStream.transform(new Function<JavaRDD<WrapperMessage>,JavaRDD<WrapperMessage>>() {
                @Override
                public JavaRDD<WrapperMessage> call(JavaRDD<WrapperMessage> wrapperMessageJavaRDD) throws Exception {
                    JavaRDD<Row> rowJavaRDD = wrapperMessageJavaRDD.map(record->WrapperMessage.convertToRow(record));
                    SQLContext sqlContext = SQLContext.getOrCreate(rowJavaRDD.context());
                    DataFrame df = sqlContext.createDataFrame(rowJavaRDD, schema);
                    if (df != null && !df.rdd().isEmpty()) {
                        System.out.println("showing dataframe df before writing to hdfs  ");
                        df.show(100);
                        System.out.println("df.rdd().count() = " + df.rdd().count());
                        Long date = new Date().getTime();
                        String inputPathName = hdfsPath + date + "_" + pid + "/";
                        String finalOutputPathName = hdfsPath + date + "-" + pid + "/";
                        df.rdd().saveAsTextFile(inputPathName);
                        System.out.println("showing dataframe df after writing to hdfs  ");
                        df.show(100);

                    }
                    JavaRDD<WrapperMessage> finalRDD = emptyRDD;
                    if (df != null) {
                        finalRDD = df.javaRDD().map(record->WrapperMessage.convertToWrapperMessage(record));
                    }
                    return finalRDD;
                }
            }); */

            //adding empty output operation to finish flow, else spark would never execute the DAG
            dStream.foreachRDD(new Function<JavaRDD<WrapperMessage>, Void>() {
                @Override
                public Void call(JavaRDD<WrapperMessage> rowJavaRDD) throws Exception {
                    return null;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

}