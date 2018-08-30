package com.wipro.ats.bdre.ml.models;

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.ml.clustering.KMeansModel;
import org.apache.spark.ml.classification.OneVsRest;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.param.Param;
import org.apache.spark.ml.linalg.Vectors;
import org.apache.spark.ml.regression.LinearRegressionModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import scala.collection.Seq;

import java.util.*;

/**
 * Created by cloudera on 11/19/17.
 */
public class LinearRegressionML {
    public Dataset<Row> productionalizeModel(Dataset<Row> dataFrame, LinkedHashMap<String,Double> columnCoefficientMap, double intercept, JavaSparkContext jsc){
        Set<String> columnsSet = columnCoefficientMap.keySet();
        List<String> columnsList = new LinkedList<>(columnsSet);
        Object[] coefficients = columnCoefficientMap.values().toArray();
        String[] columnsArray = columnsSet.toArray(new String[columnsSet.size()]);
        VectorAssembler assembler=new VectorAssembler().setInputCols(columnsArray).setOutputCol("features");
        Dataset<Row> testDataFrame=assembler.transform(dataFrame);
        Seq<String> seq = scala.collection.JavaConversions.asScalaBuffer(columnsList).toSeq();
        testDataFrame.selectExpr(seq);

        double[] coeff = new double[coefficients.length];
        for(int i=0; i<coefficients.length; i++){
            coeff[i] = new Double(coefficients[i].toString());
        }
        String uid=UUID.randomUUID().toString();
        LinearRegressionModel linearRegressionModel = new LinearRegressionModel(uid,Vectors.dense(coeff), intercept);
        Param<String> param=new Param(uid,"loss","logistic");
        linearRegressionModel.set(param,"logistic");
        System.out.println("checking something......");
        System.out.println(linearRegressionModel.getLoss());
        Dataset<Row> predictionDF = linearRegressionModel.transform(testDataFrame);
        return predictionDF;
    }
}
