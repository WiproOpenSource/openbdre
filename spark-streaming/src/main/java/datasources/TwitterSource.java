package datasources;

import com.wipro.ats.bdre.md.api.GetConfigurationProperties;
import com.wipro.ats.bdre.md.api.GetProperties;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.twitter.TwitterUtils;
import scala.Tuple2;
import twitter4j.Status;
import util.WrapperMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by cloudera on 6/19/17.
 */
public class TwitterSource implements Source{
    @Override
    public JavaPairDStream<String, String> execute(JavaStreamingContext ssc, Integer pid) throws Exception {
        GetProperties getProperties=new GetProperties();
        Map<String,String> twitterParams = new  HashMap<String,String>();
        Properties properties=  getProperties.getProperties(pid.toString() ,"message");
        String messageName = properties.getProperty("messageName");

        GetConfigurationProperties getConfigurationProperties = new GetConfigurationProperties();
        Properties configProperties = getConfigurationProperties.getConfigurationPropertiesByMessage(messageName);
        String accessToken = configProperties.getProperty("accessToken");
        String accessTokenSecret = configProperties.getProperty("accessTokenSecret");
        String consumerKey = configProperties.getProperty("consumerKey");
        String consumerSecret = configProperties.getProperty("consumerSecret");
        String keywords = configProperties.getProperty("keywords");
        String[] filters = keywords.split(",");

        System.setProperty("twitter4j.oauth.consumerKey", consumerKey);
        System.setProperty("twitter4j.oauth.consumerSecret", consumerSecret);
        System.setProperty("twitter4j.oauth.accessToken", accessToken);
        System.setProperty("twitter4j.oauth.accessTokenSecret", accessTokenSecret);

        JavaReceiverInputDStream<twitter4j.Status> statuses = TwitterUtils.createStream(ssc, filters);

        JavaDStream<String> tweets = statuses.map(
                new Function<Status, String>() {
                    public String call(twitter4j.Status status) { return status.getText(); }
                }
        );
        return tweets.mapToPair(s -> new Tuple2<String, String>(null,s));

    }
}
