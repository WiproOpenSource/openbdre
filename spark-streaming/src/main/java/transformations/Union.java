package transformations;

import org.apache.spark.sql.DataFrame;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by cloudera on 5/22/17.
 */
public class Union implements Transformation {
    @Override
    public DataFrame transform(Map<Integer,DataFrame> prevDataFrameMap, Map<Integer,Set<Integer>> prevMap, Integer pid){
        List<Integer> prevPidList = new ArrayList<>();
        prevPidList.addAll(prevMap.get(pid));
        Integer prevPid1 = prevPidList.get(0);
        System.out.println("before entering for loop first prevPid1 = " + prevPid1);
        DataFrame unionedDF = prevDataFrameMap.get(prevPid1);
        for(int i=1;i< prevPidList.size();i++){
            System.out.println("union of dataframe of pid = " + prevPidList.get(i));
            DataFrame df1 = prevDataFrameMap.get(prevPidList.get(i));
            if(unionedDF!=null && df1!=null){
                if(!unionedDF.rdd().isEmpty() && !df1.rdd().isEmpty()){
                    System.out.println("showing dataframe df1 before union ");
                    df1.show(100);
                    System.out.println("showing dataframe unionedDF before union ");
                    unionedDF.show(100);
                    unionedDF = unionedDF.unionAll(df1);
                    System.out.println("showing dataframe df1 after union ");
                    df1.show(100);
                    System.out.println("showing dataframe unionedDF after union ");
                    unionedDF.show(100);
                }
            }

        }
        return unionedDF;
    }
}

/*
public class Union implements Transformation {
    @Override
    public DataFrame transform(Map<Integer,DataFrame> prevDataFrameMap, Map<Integer,Set<Integer>> prevMap, Integer pid){
        List<Integer> prevPidList = new ArrayList<>();
        prevPidList.addAll(prevMap.get(pid));
        Integer prevPid1 = prevPidList.get(0);
        Integer prevPid2 = prevPidList.get(1);
        System.out.println("Inside join prevPid1 = " + prevPid1);
        System.out.println("Inside join prevPid2 = " + prevPid2);
        DataFrame prevDF1 = prevDataFrameMap.get(prevPid1);
        DataFrame prevDF2 = prevDataFrameMap.get(prevPid2);
        DataFrame unionedDF = null;
        if(prevDF1!=null && prevDF2!=null)
            unionedDF = prevDF1.unionAll(prevDF2);
        //unionedDF.show();

        return unionedDF;
    }
}*/
