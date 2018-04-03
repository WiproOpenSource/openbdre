package com.wipro.ats.bdre.ml.models;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.ml.Transformer;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by SH387936 on 02/05/18.
 */

public class PMMLModel {
    public DataFrame productionalizeModel(DataFrame dataFrame,String filePath){
        DataFrame output=null;
        try {
/*            File pmmlFile = new File(filePath);
            System.out.println("pmml file location is : " + filePath);
            Evaluator evaluator = EvaluatorUtil.createEvaluator(pmmlFile);
            TransformerBuilder pmmlTransformerBuilder = new TransformerBuilder(evaluator)
                    .withLabelCol("target") // Double column
                    .exploded(true);
            Transformer pmmlTransformer = pmmlTransformerBuilder.build();*//*

             output = pmmlTransformer.transform(dataFrame);*/
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return output;
    }
}
