package transformations;

import com.wipro.ats.bdre.md.api.GetProperties;
import com.wipro.ats.bdre.md.beans.GetPropertiesInfo;
import org.apache.spark.sql.DataFrame;

import java.util.*;

/**
 * Created by cloudera on 5/21/17.
 */
public class Filter implements Transformation {
    @Override
    public DataFrame transform(Map<Integer,DataFrame> prevDataFrameMap, Map<Integer,Set<Integer>> prevMap, Integer pid){
        //TODO: fetch the filter logic from DB
        List<Integer> prevPidList = new ArrayList<>();
        prevPidList.addAll(prevMap.get(pid));
        Integer prevPid = prevPidList.get(0);
        System.out.println("Inside filter prevPid = " + prevPid);
        DataFrame prevDataFrame = prevDataFrameMap.get(prevPid);
        DataFrame filteredDF =null;
        GetProperties getProperties=new GetProperties();

        Properties filterProperties=  getProperties.getProperties(String.valueOf(pid),"default");
        String check="";
        String filterValue=new String();
        String colName=new String();
        check = filterProperties.getProperty("operator");
        System.out.println("operator = " + check);
        filterValue = filterProperties.getProperty("filtervalue");
        System.out.println("filtervalue = " + filterValue);
        colName = filterProperties.getProperty("column");
        System.out.println("colName = " + colName);

        if(prevDataFrame!=null && !prevDataFrame.rdd().isEmpty()){
            if (check.equals("equals")) {
                System.out.println("showing dataframe before filter ");
                prevDataFrame.show(100);
                filteredDF = prevDataFrame.filter(prevDataFrame.col(colName).equalTo(filterValue));
                filteredDF.show(100);
                System.out.println("showing dataframe after filter ");
            }
            else {
                System.out.println("showing dataframe before filter ");
                prevDataFrame.show(100);
                filteredDF = prevDataFrame.filter(prevDataFrame.col(colName).gt(filterValue));
                filteredDF.show(100);
                System.out.println("showing dataframe after filter ");
            }
        }

        return filteredDF;
    }
}