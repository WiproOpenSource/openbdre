package transformations;

import org.apache.spark.sql.DataFrame;

import java.util.Map;
import java.util.Set;

/**
 * Created by cloudera on 5/21/17.
 */
public interface Transformation {
    public DataFrame transform(Map<Integer,DataFrame> prevDataFrameMap, Map<Integer,Set<Integer>> prevMap, Integer pid);
}