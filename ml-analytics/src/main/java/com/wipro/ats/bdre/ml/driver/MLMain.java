package com.wipro.ats.bdre.ml.driver;

import com.wipro.ats.bdre.md.api.GetProcess;
import com.wipro.ats.bdre.md.api.GetProperties;
import com.wipro.ats.bdre.md.api.InstanceExecAPI;
import com.wipro.ats.bdre.md.beans.ProcessInfo;
import com.wipro.ats.bdre.ml.models.KMeansML;
import com.wipro.ats.bdre.ml.models.LinearRegressionML;
import com.wipro.ats.bdre.ml.models.LogisticRegressionML;
import com.wipro.ats.bdre.ml.models.PMMLModel;
import com.wipro.ats.bdre.ml.schema.SchemaGeneration;
import com.wipro.ats.bdre.ml.sources.HDFSSource;
import com.wipro.ats.bdre.ml.sources.HiveSource;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.types.StructType;
import java.sql.*;
import java.util.*;
import java.util.regex.Pattern;

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
                String hdfsDirectory = properties.getProperty("hdfsPath");
                String nameNodeHost = properties.getProperty("nameNodeHost");
                String nameNodePort = properties.getProperty("nameNodePort");
                String hdfsPath="hdfs://"+nameNodeHost+":"+nameNodePort+hdfsDirectory;
                System.out.println("hdfsPath = " + hdfsPath);
                String fileFormat = properties.getProperty("fileformat");
                HDFSSource hdfsSource = new HDFSSource();
                if(fileFormat.equalsIgnoreCase("Delimited")){
                    String delimiter=properties.getProperty("Delimiter");
                    dataFrame = hdfsSource.getDataFrame(jsc, hdfsPath, nameNodeHost, nameNodePort, fileFormat, delimiter, schema);
                }
                else if(fileFormat.equalsIgnoreCase("Json")){
                    String schemaFilePath=properties.getProperty("schema-file-path");
                    dataFrame = hdfsSource.getDataFrame(jsc, hdfsPath, nameNodeHost, nameNodePort, fileFormat, schemaFilePath, schema);
                }
                dataFrame.show();
            }

            String mlAlgo = properties.getProperty("ml-algo");
            String modelInputMethod = properties.getProperty("model-input-method");

            if (modelInputMethod.equalsIgnoreCase("ModelInformation")) {
                if(mlAlgo.equalsIgnoreCase("LinearRegression") || mlAlgo.equalsIgnoreCase("LogisticRegression")) {
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
                        predictionDF = logisticRegressionML.productionalizeModel(dataFrame, columnCoefficientMap, intercept, jsc);

                    }
                }
                else if (mlAlgo.equalsIgnoreCase("KMeans")){
                    String centers = properties.getProperty("clusters");
                    String features = properties.getProperty("features");
                    KMeansML kMeansML=new KMeansML();
                    predictionDF=kMeansML.productionalizeModel(dataFrame, centers, features, jsc);

                }

            } else if (modelInputMethod.equalsIgnoreCase("pmmlFile")) {
                String pmmlPath = properties.getProperty("pmml-file-path");
                String pmmlFilePath="/home/cloudera/bdre-wfd/model/" + pmmlPath ;
                predictionDF=new PMMLModel().productionalizeModel(dataFrame,pmmlFilePath);


            } else if (modelInputMethod.equalsIgnoreCase("Serialized")) {
                String serializedFilePath = properties.getProperty("serialized-file-path");
                String progLanguage = properties.getProperty("prog-lang");
            }

            predictionDF.show(1000);
            System.out.println("data predicted");


            predictionDF.write().mode(SaveMode.Overwrite).saveAsTable("ML_"+parentProcessId);


            InstanceExecAPI instanceExecAPI2 = new InstanceExecAPI();
            instanceExecAPI2.updateInstanceExecToFinished(parentProcessId, applicationId);
            System.out.println("status changed to success");

        }catch (Exception e){
            LOGGER.info("final exception = " + e);
            e.printStackTrace();
            InstanceExecAPI instanceExecAPI = new InstanceExecAPI();
            instanceExecAPI.updateInstanceExecToFailed(parentProcessId);
            e.printStackTrace();
        }

    }
    // this method checks if hive table exists for the parent process id and deletes it if present
    public static void checkTable(int parentProcessId) {
        String driverName = "org.apache.hive.jdbc.HiveDriver";
        Connection connection;
        String srcEnv="localhost:10000";
        String srcDB="default";
        try {
            Class.forName(driverName);
            connection = DriverManager.getConnection("jdbc:hive2://" + srcEnv + "/" + srcDB.toLowerCase(), "", "");
            ResultSet rs=connection.createStatement().executeQuery("DROP TABLE IF EXISTS ML_" + parentProcessId);

    /*        List<String> tables = new ArrayList<String>();
            while (rs.next()) {
                String tableName = rs.getString(1);
                tables.add(tableName.toUpperCase());
            }
            if(tables.contains("ML_" + parentProcessId)){

            }*/
        }
        catch (Exception e){
            e.printStackTrace();
        }
        }

}