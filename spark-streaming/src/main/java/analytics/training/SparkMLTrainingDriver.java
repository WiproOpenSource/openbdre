package analytics.training;

import com.wipro.ats.bdre.md.api.StreamingMessagesAPI;
import com.wipro.ats.bdre.md.dao.jpa.Messages;
import messageschema.SchemaReader;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.ml.feature.StringIndexer;
import org.apache.spark.ml.feature.StringIndexerModel;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.regression.LinearRegressionModel;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;
import scala.Tuple2;
import util.WrapperMessage;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by cloudera on 10/17/17.
 */
public class SparkMLTrainingDriver {
    public static String modelName;

    public static void main(String[] args) {
        modelName = args[0];
        String username = (args[1]);

        SparkConf conf = new SparkConf().setAppName("BDRE-ML Training: " + modelName).setMaster("local[*]");
        JavaSparkContext jssc = new JavaSparkContext(conf);

        //TODO To be fetched from db
        String modelType = "LinearRegression";
        String messageName = "csv";
        String filePath = "/home/cloudera/bdre-wfd/trainingdata/xml1.xml";


        StreamingMessagesAPI streamingMessagesAPI = new StreamingMessagesAPI();
        Messages messages = streamingMessagesAPI.getMessage(messageName);
        String format = messages.getFormat();
        String delimiter = messages.getDelimiter();

        SchemaReader schemaReader = new SchemaReader();
        StructType schema = schemaReader.generateSchemaFromMessage(messageName);

        SQLContext sqlContext = new SQLContext(jssc);
        DataFrame df = null;
        if (format.equalsIgnoreCase("Json")) {
            df = sqlContext.read().schema(schema).json(filePath);
        } else if (format.equalsIgnoreCase("XML")) {
            df = sqlContext.read().format("com.databricks.spark.xml").schema(schema).option("rowTag", "root").load(filePath);
        } else if (format.equalsIgnoreCase("Delimited")) {
            df = sqlContext.read().format("com.databricks.spark.csv").schema(schema).option("header", "false").option("mode", "DROPMALFORMED").option("delimiter", delimiter).load(filePath);
        }
        df.show();

        if(modelType.equalsIgnoreCase("LinearRegression")){
            new LinearRegressionTraining().trainLinearRegr(df, modelName);
        }

        else if(modelType.equalsIgnoreCase("LogisticRegression")){
            new LogisticRegressionTraining().trainLogisticRegr(df, modelName);
        }

    }
}
