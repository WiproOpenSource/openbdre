package com.wipro.ats.bdre.ml.models;


import org.apache.spark.ml.classification.LogisticRegressionModel;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.mllib.linalg.SparseVector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;
import scala.collection.JavaConverters;
import scala.collection.Seq;

import java.util.*;

/**
 * Created by cloudera on 11/20/17.
 */
public class LogisticRegressionML {
    public DataFrame productionalizeModel(DataFrame dataFrame, LinkedHashMap<String,Double> columnCoefficientMap, double intercept, JavaSparkContext jsc){
        Set<String> columnsSet = columnCoefficientMap.keySet();
        List<String> columnsList = new LinkedList<>(columnsSet);
        Object[] coefficients = columnCoefficientMap.values().toArray();
        String[] columnsArray = columnsSet.toArray(new String[columnsSet.size()]);
        VectorAssembler assembler=new VectorAssembler().setInputCols(columnsArray).setOutputCol("features");
        DataFrame testDataFrame=assembler.transform(dataFrame);
        Seq<String> seq = scala.collection.JavaConversions.asScalaBuffer(columnsList).toSeq();
        dataFrame.selectExpr(seq);

        double[] coeff = new double[coefficients.length];
        for(int i=0; i<coefficients.length; i++){
            coeff[i] = new Double(coefficients[i].toString());
        }

        LogisticRegressionModel linearRegressionModel = new LogisticRegressionModel(UUID.randomUUID().toString(), Vectors.dense(coeff), intercept);
        DataFrame predictionDF = linearRegressionModel.transform(testDataFrame);
        return predictionDF;
    }
}
