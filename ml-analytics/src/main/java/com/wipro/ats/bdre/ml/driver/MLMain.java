package com.wipro.ats.bdre.ml.driver;

import com.wipro.ats.bdre.md.api.GetProcess;
import com.wipro.ats.bdre.md.api.GetProperties;
import com.wipro.ats.bdre.md.api.InstanceExecAPI;
import com.wipro.ats.bdre.md.beans.ProcessInfo;
import com.wipro.ats.bdre.ml.models.LinearRegressionML;
import com.wipro.ats.bdre.ml.models.LogisticRegressionML;
import com.wipro.ats.bdre.ml.schema.SchemaGeneration;
import com.wipro.ats.bdre.ml.sources.HiveSource;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.types.StructType;

import java.util.*;

/**
 * Created by cloudera on 11/19/17.
 */
public class MLMain {
    public static final Logger LOGGER = Logger.getLogger(MLMain.class);
    public static Integer parentProcessId;

    public static void main(String[] args) {

        try {

            parentProcessId = Integer.parseInt(args[0]);
            String username = (args[1]);
            com.wipro.ats.bdre.md.api.InstanceExecAPI instanceExecAPI = new com.wipro.ats.bdre.md.api.InstanceExecAPI();
            instanceExecAPI.insertInstanceExec(parentProcessId, null);
            GetProcess getProcess = new GetProcess();
            String[] processDetailsArgs = new String[]{"-p", args[0], "-u", username};
            List<ProcessInfo> subProcessList = getProcess.execute(processDetailsArgs);

            ProcessInfo subProcessInfo = subProcessList.get(1);
            Integer processId = subProcessInfo.getProcessId();
            System.out.println("processId = " + processId);
            GetProperties getProperties = new GetProperties();
            Properties properties = getProperties.getProperties(processId.toString(), "ml");
            String sourceType = properties.getProperty("source");

            SparkConf conf = new SparkConf().setAppName("BDRE-ML-" + parentProcessId);
            JavaSparkContext jsc = new JavaSparkContext(conf);

            String applicationId = jsc.sc().applicationId();
            System.out.println("applicationId = " + applicationId);
            InstanceExecAPI instanceExecAPI1 = new InstanceExecAPI();
            instanceExecAPI1.updateInstanceExecToRunning(parentProcessId, applicationId);


            String schemaString = properties.getProperty("schema");
            System.out.println("schemaString = "+schemaString);
            //schemaString = "label:Double,feature_1:Double,feature_2:Double,feature_3:Double,feature_4:Double";
            StructType schema = new SchemaGeneration().generateSchemaFromString(schemaString);
            DataFrame dataFrame = null;
            DataFrame predictionDF = null;

            if (sourceType.equalsIgnoreCase("Hive")) {
                String metastoreURI = properties.getProperty("metastoreURI");
                String dbName = properties.getProperty("hive-db");
                String tableName = properties.getProperty("hive-table");

                HiveSource hiveSource = new HiveSource();
                dataFrame = hiveSource.getDataFrame(jsc, metastoreURI, dbName, tableName, schema);

            } else if (sourceType.equalsIgnoreCase("HDFS")) {

            }

            String mlAlgo = properties.getProperty("ml-algo");
            String modelInputMethod = properties.getProperty("model-input-method");

            if (modelInputMethod.equalsIgnoreCase("ModelInformation")) {
                String coefficients = properties.getProperty("coefficients");
                LinkedHashMap<String, Double> columnCoefficientMap = new LinkedHashMap<String, Double>();
                for (String s : coefficients.split(",")) {
                    String[] arr = s.split(":");
                    String columnName = arr[0];
                    Double coefficient = Double.parseDouble(arr[1]);
                    columnCoefficientMap.put(columnName, coefficient);
                }
                double intercept = Double.parseDouble(properties.getProperty("intercept"));

                if (mlAlgo.equalsIgnoreCase("LinearRegression")) {
                    LinearRegressionML linearRegressionML = new LinearRegressionML();
                    predictionDF = linearRegressionML.productionalizeModel(dataFrame, columnCoefficientMap, intercept, jsc);
                } else if (mlAlgo.equalsIgnoreCase("LogisticRegression")) {
                    LogisticRegressionML logisticRegressionML = new LogisticRegressionML();
                    predictionDF=logisticRegressionML.productionalizeModel(dataFrame, columnCoefficientMap, intercept, jsc);
                }

            } else if (modelInputMethod.equalsIgnoreCase("PMML")) {
                String pmmlPath = properties.getProperty("pmml-file-path");


            } else if (modelInputMethod.equalsIgnoreCase("Serialized")) {
                String serializedFilePath = properties.getProperty("serialized-file-path");
                String progLanguage = properties.getProperty("prog-lang");
            }

            predictionDF.show();
            System.out.println("data predicted");
            InstanceExecAPI instanceExecAPI2 = new InstanceExecAPI();
            instanceExecAPI2.updateInstanceExecToFinished(parentProcessId, applicationId);
            System.out.println("status changed to success");
            predictionDF.write().format("json").save("/user/cloudera/ml-batch/"+parentProcessId);
           // predictionDF.write().saveAsTable("demo_table");
        }catch (Exception e){
            LOGGER.info("final exception = " + e);
            e.printStackTrace();
            InstanceExecAPI instanceExecAPI = new InstanceExecAPI();
            instanceExecAPI.updateInstanceExecToFailed(parentProcessId);
            e.printStackTrace();
        }

    }
}