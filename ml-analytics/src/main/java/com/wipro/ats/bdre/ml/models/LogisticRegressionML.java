package com.wipro.ats.bdre.ml.models;

import org.apache.spark.ml.classification.LogisticRegressionModel;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.linalg.Vectors;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import scala.collection.Seq;

import java.util.*;

/**
 * Created by cloudera on 11/20/17.
 */
public class LogisticRegressionML {
    public Dataset<Row> productionalizeModel(Dataset<Row> dataFrame, LinkedHashMap<String,Double> columnCoefficientMap, double intercept, JavaSparkContext jsc){
        dataFrame.show();
        Set<String> columnsSet = columnCoefficientMap.keySet();
        List<String> columnsList = new LinkedList<>(columnsSet);
        Object[] coefficients = columnCoefficientMap.values().toArray();
        System.out.println("coefficients is "+coefficients);
        String[] columnsArray = columnsSet.toArray(new String[columnsSet.size()]);
        VectorAssembler assembler=new VectorAssembler().setInputCols(columnsArray).setOutputCol("features");
        Dataset<Row> testDataFrame=assembler.transform(dataFrame);
        Seq<String> seq = scala.collection.JavaConversions.asScalaBuffer(columnsList).toSeq();
        dataFrame.selectExpr(seq);

        double[] coeff = new double[coefficients.length];
        for(int i=0; i<coefficients.length; i++){
            coeff[i] = new Double(coefficients[i].toString());
        }

        LogisticRegressionModel logisticRegressionModel = new LogisticRegressionModel(UUID.randomUUID().toString(), Vectors.dense(coeff), intercept);
        logisticRegressionModel = logisticRegressionModel.setThreshold(0.5);
        Dataset<Row> predictionDF = logisticRegressionModel.transform(testDataFrame);
        return predictionDF;

    }
}
