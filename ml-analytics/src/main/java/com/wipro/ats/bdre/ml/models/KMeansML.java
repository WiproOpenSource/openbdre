package com.wipro.ats.bdre.ml.models;

import org.apache.spark.ml.classification.LogisticRegressionModel;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.mllib.linalg.SparseVector;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;
import scala.collection.JavaConverters;
import scala.collection.Seq;
import org.apache.spark.ml.clustering.KMeansModel;
import java.util.*;

/**
 * Created by cloudera on 12/05/17.
 */
public class KMeansML {
    public DataFrame productionalizeModel(DataFrame dataFrame, String centers, String features,JavaSparkContext jsc){

        String[] columnNames=features.split(",");

        VectorAssembler assembler=new VectorAssembler().setInputCols(columnNames).setOutputCol("features");
        DataFrame testDataFrame=assembler.transform(dataFrame);



        String[] cen=centers.split(":");

        org.apache.spark.mllib.linalg.Vector[] vector= new Vector[cen.length];
        for(int j=0;j<cen.length;j++){
            String[] c=cen[j].split(",");
            double[] d=new double[c.length];
            for(int k=0;k<c.length;k++){
                d[k]=Double.parseDouble(c[k]);
            }
            vector[j]=Vectors.dense(d);


        }
        org.apache.spark.mllib.clustering.KMeansModel m=new org.apache.spark.mllib.clustering.KMeansModel(vector);
        KMeansModel model1=new KMeansModel(UUID.randomUUID().toString(),m);
        Vector[] centers1 = model1.clusterCenters();
        System.out.println("Cluster Centers: ");


        for (Object center: centers1) {
            System.out.println(center);

        }
        DataFrame predictionDF=model1.transform(testDataFrame);

        //predictionDF.show(20);
        return predictionDF;
    }
}



