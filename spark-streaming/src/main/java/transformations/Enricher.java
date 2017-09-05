package transformations;

import com.wipro.ats.bdre.md.api.GetProperties;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import util.WrapperMessage;

import java.util.*;
import java.util.Map;

/**
 * Created by cloudera on 8/6/17.
 */
public class Enricher implements Transformation {
    @Override
    public JavaPairDStream<String, WrapperMessage> transform(JavaRDD emptyRDD, java.util.Map<Integer, JavaPairDStream<String, WrapperMessage>> prevDStreamMap, java.util.Map<Integer, Set<Integer>> prevMap, Integer pid, StructType schema, Map<String, Broadcast<HashMap<String, String>>> broadcastMap, JavaStreamingContext jssc) {
        GetProperties getProperties = new GetProperties();
        Properties enrichProperties = getProperties.getProperties(String.valueOf(pid), "enricher");
        int broadcastCount = Integer.parseInt(enrichProperties.getProperty("count"));
        String[] fieldsToBeEnriched = new String[broadcastCount];
        String[] broadcastIdentifiers = new String[broadcastCount];
        for(int i=1;i<=broadcastCount;i++){
            fieldsToBeEnriched[i-1] = enrichProperties.getProperty("enricherColumn_"+i).split(":")[0];
            broadcastIdentifiers[i-1] = enrichProperties.getProperty("enricherBroadcastIdentifier_"+i);
        }
        List<Integer> prevPidList = new ArrayList<>();
        prevPidList.addAll(prevMap.get(pid));
        Integer prevPid = prevPidList.get(0);
        JavaPairDStream<String, WrapperMessage> prevDStream = prevDStreamMap.get(prevPid);
        prevDStream.foreachRDD(new Function<JavaPairRDD<String, WrapperMessage>, Void>() {
            @Override
            public Void call(JavaPairRDD<String, WrapperMessage> stringWrapperMessageJavaPairRDD) throws Exception {
                System.out.println("Beginning of Enricher = " + new Date() +"for pid = "+pid);
                return null;
            }
        });
        JavaPairDStream<String, Row> inputRowStream = prevDStream.mapValues(s -> s.getRow());


        JavaPairDStream<String, Row> encrichedRowStream = inputRowStream.mapValues(new Function<Row, Row>() {
            @Override
            public Row call(Row row) throws Exception {
                int[] indicesOfFieldsToBeEnriched = new int[fieldsToBeEnriched.length];
                String[] valuesAfterEnriching = new String[fieldsToBeEnriched.length];
                HashMap<Integer, String> enrichMap = new HashMap<Integer, String>();
                int parentIndex = row.fieldIndex(fieldsToBeEnriched[0].split("\\.")[0]);
                for (int i = 0; i < fieldsToBeEnriched.length; i++) {
                    System.out.println("inside field enrich loop = " + i);
                    System.out.println("broadcastMap = " + broadcastMap.get(broadcastIdentifiers[i]).value());
                    indicesOfFieldsToBeEnriched[i] = row.getStruct(parentIndex).fieldIndex(fieldsToBeEnriched[i].split("\\.")[1]);
                    valuesAfterEnriching[i] = broadcastMap.get(broadcastIdentifiers[i]).value().get(row.getStruct(parentIndex).get(indicesOfFieldsToBeEnriched[i]).toString());
                    enrichMap.put(indicesOfFieldsToBeEnriched[i], valuesAfterEnriching[i]);
                }

                int noOfElements = row.getStruct(parentIndex).size();
                Object[] attributes = new Object[noOfElements];
                for (int i = 0; i < noOfElements; i++){
                    attributes[i] = row.getStruct(parentIndex).get(i);
                    if (enrichMap.keySet().contains(i)) {
                        attributes[i] = enrichMap.get(i);
                    }
                }
                Row parentRow = new GenericRowWithSchema(attributes, (StructType) schema.fields()[0].dataType());
                System.out.println("parentRow = " + parentRow);
                System.out.println("(StructType) schema.fields()[0].dataType() = " + (StructType) schema.fields()[0].dataType());
                Row finalRow = new GenericRowWithSchema(new Object[]{parentRow},schema);
                System.out.println("finalRow = " + finalRow);
                System.out.println("final schema = " + schema);
              //  Object[] finalAttributes = new Object[]{finalRow};
               /* Row outputRow = RowFactory.create(attributes);
                int noOfFinalElements = row.size();
                Object[] finalAttributes = new Object[noOfElements];

                for (int i = 0; i < noOfFinalElements; i++) {
                    finalAttributes[i] = row.get(i);
                }
                Row finalRow = RowFactory.create(attributes); */
              //  Row finalSchemaRow = new GenericRowWithSchema(finalAttributes,schema);
                return finalRow;
            }
        });
        prevDStream.foreachRDD(new Function<JavaPairRDD<String, WrapperMessage>, Void>() {
            @Override
            public Void call(JavaPairRDD<String, WrapperMessage> stringWrapperMessageJavaPairRDD) throws Exception {
                System.out.println("End of Enricher = " + new Date() +"for pid = "+pid);
                return null;
            }
        });
        encrichedRowStream.print();
        return encrichedRowStream.mapValues(s -> new WrapperMessage(s));
    }
}
