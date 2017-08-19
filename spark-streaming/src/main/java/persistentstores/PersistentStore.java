package persistentstores;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import util.WrapperMessage;

import java.io.Serializable;

/**
 * Created by cloudera on 6/8/17.
 */
public interface PersistentStore extends Serializable{
    public void persist(JavaRDD emptyRDD, JavaPairDStream<String,WrapperMessage> wrapperMessageJavaDStream, Integer pid, Integer prevPid, StructType schema) throws Exception;
}
