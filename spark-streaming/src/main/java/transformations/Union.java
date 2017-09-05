package transformations;

import com.wipro.ats.bdre.md.api.GetMessageColumns;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function3;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.Time;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import scala.Tuple2;
import scala.collection.JavaConversions;
import util.WrapperMessage;

import java.util.*;
import java.util.Map;

/**
 * Created by cloudera on 5/22/17.
 */
public class Union implements Transformation
{
    @Override
    public JavaPairDStream<String,WrapperMessage> transform(JavaRDD emptyRDD, Map<Integer, JavaPairDStream<String,WrapperMessage>> prevDStreamMap, Map<Integer, Set<Integer>> prevMap, Integer pid, StructType schema,Map<String,Broadcast<HashMap<String,String>>> broadcastMap,JavaStreamingContext jssc) {
        List<Integer> prevPidList = new ArrayList<>();
        prevPidList.addAll(prevMap.get(pid));
        Integer prevPid1 = prevPidList.get(0);
        JavaPairDStream<String,WrapperMessage> unionedDStream = prevDStreamMap.get(prevPid1);

        Set<String> columnsDataTypesSet = new GetMessageColumns().getMessageColumnNames(pid);
        for(int i=1;i< prevPidList.size();i++){
            JavaPairDStream<String,WrapperMessage> dStream1 = prevDStreamMap.get(prevPidList.get(i));
            if(unionedDStream!=null && dStream1!=null){
                unionedDStream = unionedDStream.transformWithToPair(dStream1, new Function3<JavaPairRDD<String, WrapperMessage>, JavaPairRDD<String, WrapperMessage>, Time, JavaPairRDD<String, WrapperMessage>>() {
                    @Override
                    public JavaPairRDD<String, WrapperMessage> call(JavaPairRDD<String, WrapperMessage> pairRDD1, JavaPairRDD<String, WrapperMessage> pairRDD2, Time time) throws Exception {
                        JavaRDD<Row> rowRdd1 = pairRDD1.map(s -> s._2.getRow());
                        JavaRDD<Row> rowRdd2 = pairRDD2.map(s -> s._2.getRow());

                        SQLContext sqlContext1 = SQLContext.getOrCreate(rowRdd1.context());
                        SQLContext sqlContext2 = SQLContext.getOrCreate(rowRdd1.context());

                        DataFrame df1= null;
                        DataFrame df2 = null;
                        DataFrame unionedDf = null;
                        Set<String> allColumns = new LinkedHashSet<String>();
                        for(String columnDatatype :columnsDataTypesSet){
                            allColumns.add(columnDatatype.substring(0,columnDatatype.indexOf(":")));
                        }
                        Set<String> df1ColSet =  new LinkedHashSet<String>();
                        Set<String> df2ColSet =  new LinkedHashSet<String>();


                        if(rowRdd1.count() != 0){
                            StructType schema1 = rowRdd1.take(1).get(0).schema();
                            df1 = sqlContext1.createDataFrame(rowRdd1, schema1);
                            df1ColSet.addAll(Arrays.asList(df1.columns()));
                        }

                        if(rowRdd2.count() != 0){
                            StructType schema2 = rowRdd2.take(1).get(0).schema();
                            df2 = sqlContext2.createDataFrame(rowRdd2, schema2);
                            df2ColSet.addAll(Arrays.asList(df2.columns()));
                        }


                        if(df1 == null && df2 == null){
                            return pairRDD1;
                        }
                        if(df1 ==null){
                            unionedDf = df2.selectExpr(expr(df2ColSet, allColumns));
                        }
                        else if(df2 == null){
                            unionedDf = df1.selectExpr(expr(df1ColSet, allColumns));
                        }
                        else if(df1 !=null && df2 != null){
                            unionedDf = df1.selectExpr(expr(df1ColSet, allColumns)).unionAll(df2.selectExpr(expr(df2ColSet, allColumns)));
                        }
                        if(unionedDf != null) {
                            unionedDf.show();
                            return unionedDf.toJavaRDD().mapToPair(s -> new Tuple2<String, WrapperMessage>(null, new WrapperMessage(s)));
                        }
                        return pairRDD1;
                    }

                    public scala.collection.Seq<java.lang.String> expr(Set<String> mycols, Set<String> allcols){
                        List<String> finalList= new LinkedList<String>();
                        for(String s: allcols){
                            if(mycols.contains(s)) {
                                finalList.add(s);
                            }
                            else {
                                finalList.add(null +" as "+s);
                            }
                        }
                        return JavaConversions.asScalaBuffer(new LinkedList<String>(finalList)).toSeq();
                    }

                });




               /* System.out.println("showing dstream df1 before union ");
                dStream1.print(100);
                System.out.println("showing dataframe unionedDF before union ");
                unionedDStream.print(100);
                unionedDStream = unionedDStream.union(dStream1);
                System.out.println("showing dataframe unionedDF after union ");
                unionedDStream.print(100);*/

            }

        }
        unionedDStream.print();
        return unionedDStream;
    }

}

