package emitters;

import org.apache.spark.sql.DataFrame;

/**
 * Created by cloudera on 6/8/17.
 */
public interface Emitter {
    public void persist(DataFrame df, Integer pid, Integer prevPid) throws Exception;
}