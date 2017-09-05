package transformations;

import com.wipro.ats.bdre.md.api.GetProperties;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import scala.Tuple2;
import util.WrapperMessage;

import java.util.*;
import java.util.Map;

/**
 * Created by cloudera on 5/21/17.
 */
public class Filter implements Transformation {
    @Override
    public JavaPairDStream<String,WrapperMessage> transform(JavaRDD emptyRDD, Map<Integer, JavaPairDStream<String,WrapperMessage>> prevDStreamMap, Map<Integer, Set<Integer>> prevMap, Integer pid, StructType schema,Map<String,Broadcast<HashMap<String,String>>> broadcastMap,JavaStreamingContext jssc) {
        List<Integer> prevPidList = new ArrayList<>();
        prevPidList.addAll(prevMap.get(pid));
        Integer prevPid = prevPidList.get(0);
        JavaPairDStream<String,WrapperMessage> prevDStream = prevDStreamMap.get(prevPid);

        GetProperties getProperties = new GetProperties();
        Properties filterProperties = getProperties.getProperties(String.valueOf(pid), "filter");
        int count = Integer.parseInt(filterProperties.getProperty("count"));

       // JavaDStream<WrapperMessage> dStream = prevDStream.map(s -> s._2);

        JavaPairDStream<String,WrapperMessage> filteredDstream = prevDStream.transformToPair(new Function<JavaPairRDD<String, WrapperMessage>, JavaPairRDD<String, WrapperMessage>>() {
            @Override
            public JavaPairRDD<String, WrapperMessage> call(JavaPairRDD<String, WrapperMessage> rddPairWrapperMessage) throws Exception {
                System.out.println("beginning of filter/validation = " + new Date() +"for pid = "+pid);
                JavaRDD<Row> rddRow = rddPairWrapperMessage.map(s -> s._2.getRow());
                SQLContext sqlContext = SQLContext.getOrCreate(rddRow.context());
                DataFrame dataFrame = sqlContext.createDataFrame(rddRow, schema);
                DataFrame filteredDF = null;

                if (dataFrame != null ) {
                    System.out.println("showing dataframe before filter ");
                    dataFrame.show(100);
                    Column sqlDataFrame = null;
                    for (int i=1;i<=count;i++)
                    {
                        String logicalOperator = filterProperties.getProperty("logicalOperator_"+i);
                        String check = filterProperties.getProperty("operator_"+i);
                        String colNameProperty = filterProperties.getProperty("column_"+i);
                        String colName = colNameProperty.substring(0,colNameProperty.indexOf(":"));
                        String filterValue = filterProperties.getProperty("filterValue_"+i);
                        System.out.println("logicalOperator = " + logicalOperator);
                        System.out.println("operator = " + check);
                        System.out.println("filtervalue = " + filterValue);
                        System.out.println("colName = " + colName);



                        switch (logicalOperator)
                        {
                            case "NONE":
                                switch (check)
                                {
                                    case "equals":
                                        sqlDataFrame = dataFrame.col(colName).equalTo(filterValue);
                                        break;
                                    case "begins with":
                                        sqlDataFrame = sqlDataFrame.and(dataFrame.col(colName).startsWith(filterValue));
                                        break;
                                    case "ends with":
                                        sqlDataFrame = sqlDataFrame.and(dataFrame.col(colName).endsWith(filterValue));
                                        break;
                                    case "is null":
                                        sqlDataFrame = dataFrame.col(colName).isNull();
                                        break;
                                    case "is not null":
                                        sqlDataFrame = dataFrame.col(colName).notEqual("");
                                        break;
                                    case "greater than":
                                        sqlDataFrame = dataFrame.col(colName).gt(filterValue);
                                        break;
                                }
                                break;


                            case "AND":
                                switch (check)
                                {
                                    case "equals":
                                        sqlDataFrame = sqlDataFrame.and(dataFrame.col(colName).equalTo(filterValue));
                                        break;
                                    case "begins with":
                                        sqlDataFrame = sqlDataFrame.and(dataFrame.col(colName).startsWith(filterValue));
                                        break;
                                    case "ends with":
                                        sqlDataFrame = sqlDataFrame.and(dataFrame.col(colName).endsWith(filterValue));
                                        break;
                                    case "is null":
                                        sqlDataFrame = sqlDataFrame.and(dataFrame.col(colName).isNull());
                                        break;
                                    case "is not null":
                                        sqlDataFrame = sqlDataFrame.and(dataFrame.col(colName).isNotNull());
                                        break;
                                    case "greater than":
                                        sqlDataFrame = sqlDataFrame.and(dataFrame.col(colName).gt(filterValue));
                                        break;
                                }
                                break;


                            case "OR":
                                switch (check)
                                {
                                    case "equals":
                                        sqlDataFrame = sqlDataFrame.or(dataFrame.col(colName).equalTo(filterValue));
                                        break;
                                    case "begins with":
                                        sqlDataFrame = sqlDataFrame.and(dataFrame.col(colName).startsWith(filterValue));
                                        break;
                                    case "ends with":
                                        sqlDataFrame = sqlDataFrame.and(dataFrame.col(colName).endsWith(filterValue));
                                        break;
                                    case "is null":
                                        sqlDataFrame = sqlDataFrame.or(dataFrame.col(colName).isNull());
                                        break;
                                    case "is not null":
                                        sqlDataFrame = sqlDataFrame.or(dataFrame.col(colName).isNotNull());
                                        break;
                                    case "greater than":
                                        sqlDataFrame = sqlDataFrame.or(dataFrame.col(colName).gt(filterValue));
                                        break;
                                }
                                break;
                        }




                    }

                    filteredDF = dataFrame.filter(sqlDataFrame);
                    System.out.println("showing dataframe after filter ");
                    filteredDF.show(100);



                }
                JavaPairRDD<String,WrapperMessage> finalRDD = null;
                if (filteredDF != null)
                    finalRDD = filteredDF.javaRDD().mapToPair(s -> new Tuple2<String,WrapperMessage>(null,new WrapperMessage(s)));
                System.out.println("End of filter/validation = " + new Date() +"for pid = "+pid);
                return finalRDD;

            }
        });

        filteredDstream.print();
        return filteredDstream;

    }


}