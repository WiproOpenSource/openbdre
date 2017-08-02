package transformations;

import org.apache.spark.sql.DataFrame;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by cloudera on 6/9/17.
 */
public class Intersection implements Transformation {
    @Override
    public DataFrame transform(Map<Integer,DataFrame> prevDataFrameMap, Map<Integer,Set<Integer>> prevMap, Integer pid){
        List<Integer> prevPidList = new ArrayList<>();
        prevPidList.addAll(prevMap.get(pid));
        Integer prevPid1 = prevPidList.get(0);
        System.out.println("before entering for loop first prevPid1 = " + prevPid1);
        DataFrame intersectionDF = prevDataFrameMap.get(prevPid1);
        for(int i=1;i< prevPidList.size();i++){
            System.out.println("Intersection of dataframe of pid = " + prevPidList.get(i));
            DataFrame df1 = prevDataFrameMap.get(prevPidList.get(i));
            if(intersectionDF!=null && df1!=null){
                if(!intersectionDF.rdd().isEmpty() && !df1.rdd().isEmpty()){
                    System.out.println("showing dataframe df1 before Intersection ");
                    df1.show(100);
                    System.out.println("showing dataframe IntersectionDF before Intersection ");
                    intersectionDF.show(100);
                    intersectionDF = intersectionDF.intersect(df1);
                    System.out.println("showing dataframe df1 after Intersection ");
                    df1.show(100);
                    System.out.println("showing dataframe unionedDF after Intersection ");
                    intersectionDF.show(100);
                }
            }

        }
        return intersectionDF;
    }
}
