package datasources;

import com.wipro.ats.bdre.md.api.GetConnectionProperties;
import com.wipro.ats.bdre.md.api.GetProperties;
import com.wipro.ats.bdre.md.api.StreamingMessagesAPI;
import com.wipro.ats.bdre.md.dao.jpa.Connections;
import com.wipro.ats.bdre.md.dao.jpa.Messages;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.Time;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import scala.Tuple2;
import scala.collection.Seq;

/**
 * Created by SH387936 on 12/15/17.
 */

public class HDFSSource implements Source{
    public String getHDFSPath(Integer pid){
        GetProperties getProperties=new GetProperties();
        Properties properties=getProperties.getProperties(pid.toString(),"message");
        String messageName=properties.getProperty("messageName");
        StreamingMessagesAPI streamingMessagesAPI = new StreamingMessagesAPI();
        Messages messages=streamingMessagesAPI.getMessage(messageName);
        Connections connection = messages.getConnections();

        GetConnectionProperties getConnectionProperties = new GetConnectionProperties();
        Properties hdfsProperties=  getConnectionProperties.getConnectionProperties(connection.getConnectionName(),"source");
        String hdfsDirectory=hdfsProperties.getProperty("hdfsPath");
        System.out.println("hdfsDirectory = " + hdfsDirectory);
        String nameNodeHost=hdfsProperties.getProperty("nameNodeHost");
        System.out.println("nameNodeHost = " + nameNodeHost);
        String nameNodePort=hdfsProperties.getProperty("nameNodePort");
        System.out.println("nameNodePort = " + nameNodePort);
        String hdfsPath="hdfs://"+nameNodeHost+":"+nameNodePort+hdfsDirectory;
        System.out.println("hdfsPath = " + hdfsPath);
        return  hdfsPath;
    }
    @Override
    public JavaPairDStream<String, String> execute(JavaStreamingContext ssc, Integer pid) throws Exception{
        String hdfsDirectory=getHDFSPath(pid);
        JavaDStream<String> textDStream=ssc.textFileStream(hdfsDirectory);
        textDStream.print();
        JavaPairDStream<String,String> outputPairDStream = textDStream.mapToPair(s -> new Tuple2<>(null,s));

        return outputPairDStream;
    }
}

