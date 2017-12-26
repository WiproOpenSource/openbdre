package analytics;

import com.wipro.ats.bdre.md.api.GetProperties;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.ml.clustering.KMeansModel;
import org.apache.spark.ml.feature.StringIndexer;
import org.apache.spark.ml.feature.StringIndexerModel;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import scala.Tuple2;
import util.WrapperMessage;

import java.util.*;

/**
 * Created by cloudera on 10/16/17.
 */
public class KMeansClustering implements Analytics {
    @Override
    public JavaPairDStream<String, WrapperMessage> transform(JavaRDD emptyRDD, Map<Integer, JavaPairDStream<String, WrapperMessage>> prevDStreamMap, Map<Integer, Set<Integer>> prevMap, Integer pid, StructType schema, Map<String, Broadcast<HashMap<String, String>>> broadcastMap, JavaStreamingContext jssc) {
        List<Integer> prevPidList = new ArrayList<>();
        prevPidList.addAll(prevMap.get(pid));
        Integer prevPid = prevPidList.get(0);
        JavaPairDStream<String,WrapperMessage> prevDStream = prevDStreamMap.get(prevPid);

        GetProperties getProperties = new GetProperties();
        Properties kmProperties = getProperties.getProperties(String.valueOf(pid), "default");
        String modelInputMethod = kmProperties.getProperty("model-input-method");
        String features = kmProperties.getProperty("features");
        String[] columnNames=features.split(",");
        String centers = kmProperties.getProperty("clusters");

        String[] cen=centers.split(";");
        org.apache.spark.mllib.linalg.Vector[] vector= new Vector[cen.length];
        for(int j=0;j<cen.length;j++){
            String[] c=cen[j].split(",");
            double[] d=new double[c.length];
            for(int k=0;k<c.length;k++){
                d[k]=Double.parseDouble(c[k]);
            }
            vector[j]= Vectors.dense(d);


        }

        JavaPairDStream<String,WrapperMessage> lrDstream = prevDStream.transformToPair(new Function<JavaPairRDD<String, WrapperMessage>, JavaPairRDD<String, WrapperMessage>>() {
            @Override
            public JavaPairRDD<String, WrapperMessage> call(JavaPairRDD<String, WrapperMessage> rddPairWrapperMessage) throws Exception {
                System.out.println("beginning of kmeans = " + new Date().getTime() +"for pid = "+pid);
                JavaRDD<Row> rddRow = rddPairWrapperMessage.map(s -> s._2.getRow());
                SQLContext sqlContext = SQLContext.getOrCreate(rddRow.context());
                DataFrame dataFrame = sqlContext.createDataFrame(rddRow, schema);
                System.out.println("dataFrame lr= " + dataFrame);
                dataFrame.show();
                DataFrame outputDF = null;
                if(rddRow.count() > 0){

                    VectorAssembler assembler = new VectorAssembler().setInputCols(columnNames).setOutputCol("features");
                    DataFrame assembyDF = assembler.transform(dataFrame);
                    assembyDF.show(10);
                    org.apache.spark.mllib.clustering.KMeansModel m=new org.apache.spark.mllib.clustering.KMeansModel(vector);
                    KMeansModel kMeansModel=new KMeansModel(UUID.randomUUID().toString(),m);
                    Vector[] centers1 = kMeansModel.clusterCenters();
                    System.out.println("Cluster Centers: ");

                    for (Object center: centers1) {
                        System.out.println(center);

                    }
                    outputDF=kMeansModel.transform(assembyDF);
                    outputDF.show(20);

                }
                System.out.println("End of KMeans regression = " + new Date().getTime() +"for pid = "+pid);
                JavaPairRDD<String,WrapperMessage> finalRDD = null;
                if (outputDF != null) {
                    finalRDD = outputDF.javaRDD().mapToPair(s -> new Tuple2<String, WrapperMessage>(null, new WrapperMessage(s)));
                    return finalRDD;
                }
                else {
                    finalRDD = dataFrame.javaRDD().mapToPair(s -> new Tuple2<String, WrapperMessage>(null, new WrapperMessage(s)));
                    return finalRDD;
                }


            }

        });
        lrDstream.print();
        return lrDstream;
    }
}
