package transformations;

import com.wipro.ats.bdre.md.api.GetProperties;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.types.StructType;
import scala.Tuple2;
import scala.collection.Seq;
import util.WrapperMessage;
import scala.collection.JavaConverters;
import java.util.*;
import java.util.Map;

/**
 * Created by cloudera on 7/4/17.
 */
public class Aggregation implements Transformation{



    @Override
    public JavaPairRDD<String,WrapperMessage> transform(JavaRDD emptyRDD, Map<Integer, JavaPairRDD<String,WrapperMessage>> prevRDDMap, Map<Integer, Set<Integer>> prevMap, Integer pid, StructType schema, Map<String,Broadcast<HashMap<String,String>>> broadcastMap, JavaSparkContext sc) {
        List<Integer> prevPidList = new ArrayList<>();
        prevPidList.addAll(prevMap.get(pid));
        Integer prevPid = prevPidList.get(0);
        System.out.println("Inside Aggregation prevPid = " + prevPid);
        JavaPairRDD<String,WrapperMessage> prevRDD = prevRDDMap.get(prevPid);
        Map<String, String> fieldAggrMap = new HashMap<>();
        GetProperties getProperties = new GetProperties();
        Properties groupProperties=getProperties.getProperties(String.valueOf(pid),"default");
        String gc=groupProperties.getProperty("groupingColumns");
        List<Column> groupColumns=new ArrayList<>();
        if(!gc.equals("None")){
            for(String s :gc.split(",")){
                groupColumns.add(new Column(s.split(":")[0]));
            }
        }
        Seq<Column> g=JavaConverters.asScalaIterableConverter(groupColumns).asScala().toSeq();
        Properties aggProperties = getProperties.getProperties(String.valueOf(pid), "default");
        String columnAggr = aggProperties.getProperty("column:aggType");
        String[] columnsAggrArray = columnAggr.split(",");
        for(String s: columnsAggrArray){
            String[] sArray = s.split(":::");
            fieldAggrMap.put(sArray[0].substring(0,sArray[0].indexOf(":")),sArray[1]);
        }
        //TODO: In ui, map should be of this format: Map("col1" -> "max", "col2" -> "avg", "col3" -> "sum", "col4" -> "min")

        JavaRDD<WrapperMessage> rdd = prevRDD.map(s -> s._2);


                JavaRDD<Row> rddRow = rdd.map(record -> WrapperMessage.convertToRow(record));
                SQLContext sqlContext = SQLContext.getOrCreate(rdd.context());
                DataFrame dataFrame = sqlContext.createDataFrame(rddRow, schema);
                DataFrame aggregatedDF = null;

                if (dataFrame != null && !dataFrame.rdd().isEmpty()) {

                    System.out.println("showing dataframe before filter ");
                    dataFrame.show(100);
                    if(!gc.equals("None")){
                        aggregatedDF=dataFrame.groupBy(g).agg(fieldAggrMap);
                    }
                    else {
                        aggregatedDF = dataFrame.agg(fieldAggrMap);
                    }
                    aggregatedDF.show(100);
                    System.out.println("showing dataframe after filter ");

                }
                JavaRDD<WrapperMessage> finalRDD = emptyRDD;
                if (aggregatedDF != null)
                    finalRDD = aggregatedDF.javaRDD().map(s -> WrapperMessage.convertToWrapperMessage(s));


        return finalRDD.mapToPair(s -> new Tuple2<String, WrapperMessage>(null,s));

    }
}
