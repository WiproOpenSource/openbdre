package analytics.training;

import org.apache.spark.ml.feature.StringIndexer;
import org.apache.spark.ml.feature.StringIndexerModel;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.classification.LogisticRegressionModel;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.types.DataTypes;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by cloudera on 10/17/17.
 */
public class LogisticRegressionTraining {
    public void trainLogisticRegr(DataFrame df, String modelName){

        //Get below properties based on modelName
        String labelColumn = "";
        Double elasticNetParam = 0.0;
        Integer maxIter = 100;
        Double regParam = 0.0;

        String continuousFeatures = "";
        String categoryFeatures = "";

        String[] continuousColumnsArray = continuousFeatures.split(",");
        String[] categoryColumnsArray = categoryFeatures.split(",");
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

        if (df.count() > 0) {
            StringIndexer[] strIndexArray = new StringIndexer[categoryColumnsArray.length];
            for (int i = 0; i < categoryColumnsArray.length; i++) {
                StringIndexer indexer = new StringIndexer().setInputCol(categoryColumnsArray[i]).setOutputCol(categoryColumnsArray[i] + "Index");
                df = indexer.fit(df).transform(df);
                strIndexArray[i] = indexer;
            }

            VectorAssembler assembler = new VectorAssembler().setInputCols(featureColumns).setOutputCol("features");
            DataFrame assembyDF = assembler.transform(df);
            assembyDF.show(10);

            DataFrame newLabelDF = assembyDF;
            String finalLabelColumn = labelColumnName;
            StringIndexerModel labelIndexer = null;

            if (labelColumnDatatype.equalsIgnoreCase("Integer") || labelColumnDatatype.equalsIgnoreCase("Long") || labelColumnDatatype.equalsIgnoreCase("Byte") || labelColumnDatatype.equalsIgnoreCase("Short") || labelColumnDatatype.equalsIgnoreCase("Double") || labelColumnDatatype.equalsIgnoreCase("Float")) {
                finalLabelColumn += "Index";
                newLabelDF = assembyDF.withColumn(finalLabelColumn, assembyDF.col(labelColumnName).cast(DataTypes.DoubleType));
            } else if (labelColumnDatatype.equalsIgnoreCase("String")) {
                finalLabelColumn += "Index";
                labelIndexer = new StringIndexer().setInputCol(labelColumnName).setOutputCol(finalLabelColumn).fit(assembyDF);
                newLabelDF = labelIndexer.transform(assembyDF);
            }
            newLabelDF.show(10);
            org.apache.spark.ml.classification.LogisticRegression lr = new org.apache.spark.ml.classification.LogisticRegression().setMaxIter(maxIter).setRegParam(regParam).setElasticNetParam(elasticNetParam).setLabelCol(finalLabelColumn).setFeaturesCol("features");

            LogisticRegressionModel lrModel = null;
            lrModel = lr.fit(newLabelDF);
            try {
                lrModel.write().overwrite().save("/tmp/" + modelName);
                System.out.println("lrModel.coefficients() = " + lrModel.coefficients());
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }
}
