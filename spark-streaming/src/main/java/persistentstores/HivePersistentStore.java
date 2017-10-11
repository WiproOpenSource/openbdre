package persistentstores;

import com.wipro.ats.bdre.md.api.GetConnectionProperties;
import com.wipro.ats.bdre.md.api.GetMessageColumns;
import com.wipro.ats.bdre.md.api.GetProperties;
import messageschema.SGDataTypes;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.hive.HiveContext;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import util.WrapperMessage;

import java.util.*;

/**
 * Created by cloudera on 7/4/17.
 */
public class HivePersistentStore implements PersistentStore {

    public static final Map<String,DataType> dataTypesMap = new SGDataTypes().dataTypesMap;


    @Override
    public void persist(JavaRDD emptyRDD, JavaPairDStream<String, WrapperMessage> inputWrapperDStream, Integer pid, Integer prevPid, StructType schema, Map<String,Broadcast<HashMap<String,String>>> broadcastMap, JavaStreamingContext jssc) throws Exception {

        Set<String> columnsDataTypesSet = new GetMessageColumns().getMessageColumnNames(pid);

        List<StructField> fields = new ArrayList<>();
        for(String columnDatatype: columnsDataTypesSet){
            String[] columnDatatypeArray = columnDatatype.split(":");
            StructField field = DataTypes.createStructField(columnDatatypeArray[0], dataTypesMap.get(columnDatatypeArray[1]), true);
            fields.add(field);
        }
        StructType schema1 = DataTypes.createStructType(fields);

        GetProperties getProperties = new GetProperties();
        Properties hiveProperties = getProperties.getProperties(String.valueOf(pid), "hive");

        String hiveTableName = hiveProperties.getProperty("tableName");
        String format = hiveProperties.getProperty("format");
        String connectionName = hiveProperties.getProperty("connectionName");
        System.out.println("connectionName = " + connectionName);
        GetConnectionProperties getConnectionProperties = new GetConnectionProperties();
        Properties hiveConfProperties=  getConnectionProperties.getConnectionProperties(connectionName,"persistentStore");
        String metastoreURI = hiveConfProperties.getProperty("metastoreURI");
        String metaStoreWareHouseDir = hiveConfProperties.getProperty("metastoreWarehouseDir");
        String hiveDBName = hiveConfProperties.getProperty("dbName");

        HiveContext hiveContext = new org.apache.spark.sql.hive.HiveContext(jssc.sparkContext().sc());
        hiveContext.setConf("hive.metastore.uris", metastoreURI);
        hiveContext.setConf("hive.metastore.warehouse.dir",metaStoreWareHouseDir);
        inputWrapperDStream.foreachRDD(new Function<JavaPairRDD<String, WrapperMessage>, Void>() {
            @Override
            public Void call(JavaPairRDD<String, WrapperMessage> pairRDD) throws Exception {

                JavaRDD<Row> inputRowRDD = pairRDD.map(s -> s._2.getRow());
                if(inputRowRDD.count() != 0) {
                    System.out.println("schema1 = " + schema1);
                    DataFrame df = hiveContext.createDataFrame(inputRowRDD, schema);
                    df.write().format(format).mode(SaveMode.Append).saveAsTable(hiveDBName+"."+hiveTableName);
                }
                return null;
            }
        });

    }
}
