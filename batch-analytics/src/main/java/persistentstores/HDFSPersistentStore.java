package persistentstores;

import com.wipro.ats.bdre.md.api.GetProperties;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.types.StructType;
import util.WrapperMessage;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by cloudera on 5/21/17.
 */
public class HDFSPersistentStore implements PersistentStore {

    @Override
    public void persist(JavaRDD emptyRDD, JavaPairRDD<String,WrapperMessage> inputRDD, Integer pid, Integer prevPid, StructType schema, Map<String,Broadcast<HashMap<String,String>>> broadcastMap, JavaSparkContext jsc) throws Exception {
        try {
            final String hdfsPath = "/user/cloudera/spark-streaming-data/";
            System.out.println("Inside emitter hdfs, persisting");
            GetProperties getProperties = new GetProperties();
            Properties hdfsProperties = getProperties.getProperties(String.valueOf(pid), "persistentStore");
            //System.out.println(" Printing Pair dstream" );
            //inputDStream.print();

            //inputDStream.dstream().saveAsTextFiles(hdfsPath,"stream");
            JavaRDD<WrapperMessage> rdd = inputRDD.map(s -> s._2);


                    System.out.println(" inside hdfs ");
                    //JavaRDD<Row> rowJavaRDD = wrapperMessageJavaRDD.map(record->WrapperMessage.convertToRow(record));
                    JavaRDD<Row> rowJavaRDD = rdd.map(s -> s.getRow());
                    if(!rowJavaRDD.isEmpty()){
                        rowJavaRDD.saveAsTextFile(hdfsPath+"/"+pid.toString()+"_"+new Date().getTime());
                    }
                    /*SQLContext sqlContext = SQLContext.getOrCreate(rowJavaRDD.context());
                    DataFrame df = sqlContext.createDataFrame(rowJavaRDD, schema);
                    if (!df.rdd().isEmpty() && !rowJavaRDD.isEmpty()) {
                        System.out.println("showing dataframe df before writing to hdfs  ");
                        df.show(100);
                        System.out.println("df.rdd().count() = " + df.rdd().count());
                        Long date = new Date().getTime();
                        String inputPathName = hdfsPath + date + "_" + pid + "/";
                        String finalOutputPathName = hdfsPath + date + "-" + pid + "/";
                        df.rdd().saveAsTextFile(inputPathName);
                        System.out.println("showing dataframe df after writing to hdfs  ");
                        df.show(100);

                    }*/

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

}