package analytics;

import com.wipro.ats.bdre.md.api.GetProperties;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.classification.LogisticRegression;
import org.apache.spark.ml.classification.LogisticRegressionModel;
import org.apache.spark.ml.feature.StringIndexer;
import org.apache.spark.ml.feature.StringIndexerModel;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.regression.LinearRegressionModel;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import scala.Tuple2;
import scala.collection.Seq;
import util.WrapperMessage;

import java.util.*;

/**
 * Created by cloudera on 10/11/17.
 */
public class LinearRegression implements Analytics {
    static   LinkedHashMap<String, Double> columnCoefficientMap = new LinkedHashMap<String,Double>();
    static double intercept;
    @Override
    public JavaPairDStream<String, WrapperMessage> transform(JavaRDD emptyRDD, Map<Integer, JavaPairDStream<String, WrapperMessage>> prevDStreamMap, Map<Integer, Set<Integer>> prevMap, Integer pid, StructType schema, Map<String, Broadcast<HashMap<String, String>>> broadcastMap, JavaStreamingContext jssc) {
        List<Integer> prevPidList = new ArrayList<>();
        prevPidList.addAll(prevMap.get(pid));
        Integer prevPid = prevPidList.get(0);
        JavaPairDStream<String,WrapperMessage> prevDStream = prevDStreamMap.get(prevPid);

        GetProperties getProperties = new GetProperties();
        System.out.println(pid);
        Properties lrProperties = getProperties.getProperties(String.valueOf(pid), "default");
        String modelInputMethod = lrProperties.getProperty("model-input-method");
        columnCoefficientMap.clear();
        if(modelInputMethod.equalsIgnoreCase("ModelInformation")){
            String coefficientString = lrProperties.getProperty("coefficients");
            System.out.println("coefficients are "+coefficientString);
            for(String s : coefficientString.split(",")){
                String[] arr = s.split(":");
                String columnName = arr[0];
                Double coefficient = Double.parseDouble(arr[1]);
                columnCoefficientMap.put(columnName,coefficient);
            }
            intercept = Double.parseDouble(lrProperties.getProperty("intercept"));




              //  predictionDF = linearRegressionML.productionalizeModel(dataFrame, columnCoefficientMap, intercept, jsc);

        }


        else if(modelInputMethod.equalsIgnoreCase("PMML")){
            String pmmlPath = lrProperties.getProperty("pmml-file-path");



        }

        else if(modelInputMethod.equalsIgnoreCase("Serialized")){
            String serializedFilePath = lrProperties.getProperty("serialized-file-path");
            String progLanguage = lrProperties.getProperty("prog-lang");
        }
        JavaPairDStream<String,WrapperMessage> lrDstream = prevDStream.transformToPair(new Function<JavaPairRDD<String, WrapperMessage>, JavaPairRDD<String, WrapperMessage>>() {
            @Override
            public JavaPairRDD<String, WrapperMessage> call(JavaPairRDD<String, WrapperMessage> rddPairWrapperMessage) throws Exception {
                System.out.println("beginning of linear regression = " + new Date().getTime() +"for pid = "+pid);
                JavaRDD<Row> rddRow = rddPairWrapperMessage.map(s -> s._2.getRow());
                SQLContext sqlContext = SQLContext.getOrCreate(rddRow.context());
                DataFrame dataFrame = sqlContext.createDataFrame(rddRow, schema);
                System.out.println("dataFrame lr= " + dataFrame);
               //dataFrame.withColumn("",dataFrame.column)

                dataFrame.show();
                DataFrame outputDF = null;
                Set<String> columnsSet = columnCoefficientMap.keySet();
                List<String> columnsList = new LinkedList<>(columnsSet);
                Object[] coefficients = columnCoefficientMap.values().toArray();
                String[] columnsArray = columnsSet.toArray(new String[columnsSet.size()]);
                VectorAssembler assembler=new VectorAssembler().setInputCols(columnsArray).setOutputCol("features");
                DataFrame testDataFrame=assembler.transform(dataFrame);


                Seq<String> seq = scala.collection.JavaConversions.asScalaBuffer(columnsList).toSeq();
                testDataFrame.selectExpr(seq);


                double[] coeff = new double[coefficients.length];
                for(int i=0; i<coefficients.length; i++){
                    coeff[i] = new Double(coefficients[i].toString());
                }

                LinearRegressionModel linearRegressionModel = new LinearRegressionModel(UUID.randomUUID().toString(), Vectors.dense(coeff), intercept);
                outputDF = linearRegressionModel.transform(testDataFrame);
                System.out.println("End of linear regression = " + new Date().getTime() +"for pid = "+pid);
                outputDF.show();
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
