package analytics;

import com.wipro.ats.bdre.md.api.GetProperties;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.ml.classification.LogisticRegressionModel;
import org.apache.spark.ml.feature.IndexToString;
import org.apache.spark.ml.feature.StringIndexer;
import org.apache.spark.ml.feature.StringIndexerModel;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.DoubleType;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import scala.Tuple2;
import util.WrapperMessage;

import java.util.*;

/**
 * Created by cloudera on 10/12/17.
 */
public class LogisticRegression implements Analytics {
    @Override
    public JavaPairDStream<String, WrapperMessage> transform(JavaRDD emptyRDD, Map<Integer, JavaPairDStream<String, WrapperMessage>> prevDStreamMap, Map<Integer, Set<Integer>> prevMap, Integer pid, StructType schema, Map<String, Broadcast<HashMap<String, String>>> broadcastMap, JavaStreamingContext jssc) {
        List<Integer> prevPidList = new ArrayList<>();
        prevPidList.addAll(prevMap.get(pid));
        Integer prevPid = prevPidList.get(0);
        JavaPairDStream<String,WrapperMessage> prevDStream = prevDStreamMap.get(prevPid);

        GetProperties getProperties = new GetProperties();
        Properties lrProperties = getProperties.getProperties(String.valueOf(pid), "logistic-regression");
        String continuousColumns = lrProperties.getProperty("continuous-columns");
        String[] continuousColumnsArray = continuousColumns.split(",");
        String categoryColumns = lrProperties.getProperty("category-columns");
        String[] categoryColumnsArray = categoryColumns.split(",");
        String labelColumn = lrProperties.getProperty("label-column");
        Double elasticNetParam = Double.parseDouble(lrProperties.getProperty("elastic-net-param"));
        Integer maxIter = Integer.parseInt(lrProperties.getProperty("max-iterations"));
        Double regParam = Double.parseDouble(lrProperties.getProperty("reg-param"));
        String check = lrProperties.getProperty("type-of-data");
        String modelName = lrProperties.getProperty("model-name");

        ArrayList<String> features = new ArrayList<String>(Arrays.asList(continuousColumnsArray));
        for(String categoryCol : categoryColumnsArray) {
            if(!categoryCol.equals(""))
                features.add(categoryCol+"Index");
        }
        String[] featureColumns = new String[features.size()];
        for(int i=0; i< features.size(); i++){
            featureColumns[i] = features.get(i);
        }

        String labelColumnName = labelColumn.substring(0,labelColumn.indexOf(":"));
        String labelColumnDatatype = labelColumn.substring(labelColumn.indexOf(":")+1);


        JavaPairDStream<String,WrapperMessage> lrDstream = prevDStream.transformToPair(new Function<JavaPairRDD<String, WrapperMessage>, JavaPairRDD<String, WrapperMessage>>() {
            @Override
            public JavaPairRDD<String, WrapperMessage> call(JavaPairRDD<String, WrapperMessage> rddPairWrapperMessage) throws Exception {
                System.out.println("beginning of logistic regression = " + new Date().getTime() +"for pid = "+pid);
                JavaRDD<Row> rddRow = rddPairWrapperMessage.map(s -> s._2.getRow());
                SQLContext sqlContext = SQLContext.getOrCreate(rddRow.context());
                DataFrame dataFrame = sqlContext.createDataFrame(rddRow, schema);
                System.out.println("dataFrame lr= " + dataFrame);
                dataFrame.show();
                DataFrame outputDF = null;
                if(rddRow.count() > 0){
                    StringIndexer[] strIndexArray = new StringIndexer[categoryColumnsArray.length];
                    for(int i=0; i<categoryColumnsArray.length; i++) {
                        StringIndexer indexer = new StringIndexer().setInputCol(categoryColumnsArray[i]).setOutputCol(categoryColumnsArray[i]+"Index");
                        dataFrame = indexer.fit(dataFrame).transform(dataFrame);
                        strIndexArray[i] = indexer;
                    }

                    VectorAssembler assembler = new VectorAssembler().setInputCols(featureColumns).setOutputCol("features");
                    DataFrame assembyDF = assembler.transform(dataFrame);
                    assembyDF.show(10);

                    DataFrame newLabelDF = assembyDF;
                    String finalLabelColumn = labelColumnName;
                    StringIndexerModel labelIndexer = null;

                    if(labelColumnDatatype.equalsIgnoreCase("Integer") || labelColumnDatatype.equalsIgnoreCase("Long") || labelColumnDatatype.equalsIgnoreCase("Byte") || labelColumnDatatype.equalsIgnoreCase("Short") || labelColumnDatatype.equalsIgnoreCase("Double") || labelColumnDatatype.equalsIgnoreCase("Float")){
                        finalLabelColumn += "Index";
                        newLabelDF = assembyDF.withColumn(finalLabelColumn,assembyDF.col(labelColumnName).cast(DataTypes.DoubleType));
                    }
                    else if(labelColumnDatatype.equalsIgnoreCase("String")){
                        finalLabelColumn += "Index";
                        labelIndexer = new StringIndexer().setInputCol(labelColumnName).setOutputCol(finalLabelColumn).fit(assembyDF);
                        newLabelDF = labelIndexer.transform(assembyDF);
                    }
                    newLabelDF.show(10);
                    org.apache.spark.ml.classification.LogisticRegression lr = new org.apache.spark.ml.classification.LogisticRegression().setMaxIter(maxIter).setRegParam(regParam).setElasticNetParam(elasticNetParam).setLabelCol(finalLabelColumn).setFeaturesCol("features");

                    if(check.equalsIgnoreCase("training")) {
                        LogisticRegressionModel lrModel = null;
                        lrModel = lr.fit(newLabelDF);
                        lrModel.write().overwrite().save("/tmp/"+modelName);
                        System.out.println("lrModel.coefficients() = " + lrModel.coefficients());
                    }
                    else {
                        LogisticRegressionModel predictionLRModel = LogisticRegressionModel.load("/tmp/"+modelName);
                        predictionLRModel.coefficients();
                        predictionLRModel.featuresCol();
                        predictionLRModel.labelCol();
                        outputDF = predictionLRModel.transform(newLabelDF);
                        outputDF.show();
                    }

                    /*if(labelColumnDatatype.equalsIgnoreCase("String")){
                        IndexToString converter = new IndexToString().setInputCol(finalLabelColumn).setOutputCol(labelColumn+"Label").setLabels(labelIndexer.labels());
                        outputDF = converter.transform(outputDF);
                    }*/


                }
                System.out.println("End of logistic regression = " + new Date().getTime() +"for pid = "+pid);
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
