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
        StringBuilder s1=new StringBuilder("");
        if(!gc.equals("None")){
            for(String s :gc.split(",")){
                System.out.println("columnn for grouping is " + s.split(":")[0]);;
                groupColumns.add(new Column(s.split(":")[0]));
                s1.append(s.split(":")[0]+",");
            }
        }
        String group=s1.toString().substring(0,s1.length()-1);
        //Seq<Column> g=JavaConverters.asScalaIterableConverter(groupColumns).asScala().toSeq();
        Properties aggProperties = getProperties.getProperties(String.valueOf(pid), "default");
        String columnAggr = aggProperties.getProperty("column:aggType");
        String[] columnsAggrArray = columnAggr.split(",");
        StringBuilder s2=new StringBuilder("");
        for(String s: columnsAggrArray){
            String[] sArray = s.split(":::");
            fieldAggrMap.put(sArray[0].substring(0,sArray[0].indexOf(":")),sArray[1]);
            s2.append(sArray[1]+"(" + sArray[0].substring(0,sArray[0].indexOf(":"))+"),");
        }
        String agg=s2.toString().substring(0,s2.length()-1);
        System.out.println("aggregation map is " + fieldAggrMap.toString());
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
                        String query="select " + group + "," + agg + " from table1 group by " + group;
                        System.out.println("sql query is " + query);
                        //aggregatedDF=dataFrame.select(r).groupBy(g).agg(fieldAggrMap);
                        dataFrame.registerTempTable("table1");
                        aggregatedDF= sqlContext.sql(query);
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
