/*package datasources;

import com.rabbitmq.client.QueueingConsumer;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.rabbitmq.RabbitMQUtils;
import scala.Tuple2;

import java.util.HashMap;
import java.util.Map;

public class RabbitMQSource implements Source{




    @Override
    public JavaPairDStream<String, String> execute(JavaStreamingContext ssc, Integer pid) throws Exception {
        try {
            Map<String, String> rabbitMqConParams = new HashMap<String, String>();
            rabbitMqConParams.put("host", "localhost");
            rabbitMqConParams.put("queueName", "hello");

            JavaReceiverInputDStream<String> receiverStream = RabbitMQUtils.createJavaStream(ssc, String.class, rabbitMqConParams, new Function<QueueingConsumer.Delivery, String>() {
                @Override
                public String call(QueueingConsumer.Delivery delivery) throws Exception {
                    System.out.println("delivery.toString() = " + delivery.toString());
                    return delivery.toString();
                }
            });
            System.out.println("receiverStream.toString() = " + receiverStream.toString());
            return receiverStream.mapToPair(s -> new Tuple2<String, String>(null,s));
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    //createJavaDistributedStream[R](javaSparkStreamingContext, params, JFunction[Array[Byte], R]);

}*/
