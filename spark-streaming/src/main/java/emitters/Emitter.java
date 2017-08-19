package emitters;

import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;

import java.io.Serializable;

/**
 * Created by cloudera on 6/8/17.
 */
public interface Emitter extends Serializable{
    public void persist(JavaPairDStream df, Integer pid, Integer prevPid, StructType schema) throws Exception;
}
