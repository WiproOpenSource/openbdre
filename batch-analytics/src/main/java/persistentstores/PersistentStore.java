package persistentstores;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.types.StructType;
import util.WrapperMessage;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cloudera on 6/8/17.
 */
public interface PersistentStore extends Serializable{
    public void persist(JavaRDD emptyRDD, JavaPairRDD<String,WrapperMessage> wrapperMessageJavaRDD, Integer pid, Integer prevPid, StructType schema, Map<String,Broadcast<HashMap<String,String>>> broadcastMap, JavaSparkContext jsc) throws Exception;
}
