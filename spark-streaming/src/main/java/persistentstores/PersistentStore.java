package persistentstores;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import util.WrapperMessage;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cloudera on 6/8/17.
 */
public interface PersistentStore extends Serializable{
    public void persist(JavaRDD emptyRDD, JavaPairDStream<String,WrapperMessage> wrapperMessageJavaDStream, Integer pid, Integer prevPid, StructType schema, Map<String,Broadcast<HashMap<String,String>>> broadcastMap, JavaStreamingContext jssc) throws Exception;
}
