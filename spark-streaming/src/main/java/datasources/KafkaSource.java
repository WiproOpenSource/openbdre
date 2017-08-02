package datasources;

import com.wipro.ats.bdre.md.api.GetConfigurationProperties;
import com.wipro.ats.bdre.md.api.GetConnectionProperties;
import com.wipro.ats.bdre.md.api.GetProperties;
import com.wipro.ats.bdre.md.api.StreamingMessagesAPI;
import com.wipro.ats.bdre.md.dao.jpa.Connections;
import com.wipro.ats.bdre.md.dao.jpa.Messages;
import kafka.serializer.StringDecoder;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import scala.Tuple2;

import java.util.*;

/**
 * Created by cloudera on 5/19/17.
 */
public class KafkaSource implements Source{

    Map<String,String> kafkaParams;
    Set<String> topics = new HashSet<>();
    String topicName=new String();


    public Map<String,String> getKafkaParams(Integer pid){
        GetProperties getProperties=new GetProperties();
        Map<String,String> kafkaParams = new  HashMap<String,String>();
        Properties properties=  getProperties.getProperties(pid.toString() ,"message");
        String messageName = properties.getProperty("messageName");

        StreamingMessagesAPI streamingMessagesAPI = new StreamingMessagesAPI();
        Messages messages=streamingMessagesAPI.getMessage(messageName);
        Connections connection = messages.getConnections();

        GetConnectionProperties getConnectionProperties = new GetConnectionProperties();
        Properties kafkaProperties=  getConnectionProperties.getConnectionProperties(connection.getConnectionName(),"source");
        Enumeration e = kafkaProperties.propertyNames();
        if (!kafkaProperties.isEmpty()) {
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                kafkaParams.put(key,kafkaProperties.getProperty(key));
            }
        }

        return kafkaParams;
    }

    public Set<String> getTopics(Integer pid){
        GetProperties getProperties=new GetProperties();
        Map<String,String> kafkaParams = new  HashMap<String,String>();
        Properties properties=  getProperties.getProperties(pid.toString() ,"message");
        String messageName = properties.getProperty("messageName");

        GetConfigurationProperties getConfigurationProperties = new GetConfigurationProperties();
        Properties configProperties = getConfigurationProperties.getConfigurationPropertiesByMessage(messageName);
        topicName = configProperties.getProperty("topicName");
        String[] topicArray = topicName.split(",");
        for(int i=0;i<topicArray.length;i++){
            System.out.println("topic = " + topicArray[i]);
            topics.add(topicArray[i]);
        }
        return topics;
    }

    @Override
    public JavaDStream execute(JavaStreamingContext ssc,Integer pid) throws Exception {
        try {
            System.out.println("pid = " + pid);
            Map<String, String> kafkaParams = getKafkaParams(pid);

            //TODO changenow
            Set<String> topics = getTopics(pid);
            //Set<String> topics = Collections.singleton("test");
            System.out.println("topics = " + topics);
            JavaPairInputDStream<String, String> directKafkaStream = KafkaUtils.createDirectStream(ssc, String.class, String.class, StringDecoder.class, StringDecoder.class, kafkaParams, topics);
            return directKafkaStream.map(Tuple2::_2);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("e = " + e);
            throw e;
        }
    }

}
