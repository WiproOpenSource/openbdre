package transformations;

import com.wipro.ats.bdre.md.api.GetProperties;
import org.apache.spark.sql.DataFrame;

import java.util.*;

/**
 * Created by cloudera on 6/8/17.
 */
public class Distinct implements Transformation {
    @Override
    public DataFrame transform(Map<Integer, DataFrame> prevDataFrameMap, Map<Integer, Set<Integer>> prevMap, Integer pid) {
        List<Integer> prevPidList = new ArrayList<>();
        prevPidList.addAll(prevMap.get(pid));
        Integer prevPid = prevPidList.get(0);
        System.out.println("Inside distinct prevPid = " + prevPid);
        DataFrame prevDataFrame = prevDataFrameMap.get(prevPid);
        DataFrame filteredDF =null;


        if(prevDataFrame!=null && !prevDataFrame.rdd().isEmpty()){
                System.out.println("showing dataframe before distinct ");
                prevDataFrame.show(100);
                filteredDF = prevDataFrame.distinct();
                filteredDF.show(100);
                System.out.println("showing dataframe after distinct ");
        }

        return filteredDF;
    }
}
