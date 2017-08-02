package datasources;

import com.rabbitmq.client.QueueingConsumer;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.rabbitmq.*;
import org.apache.spark.api.java.function.Function;
import scala.Array;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cloudera on 5/19/17.
 */
public class RabbitMQSource implements Source{




    @Override
    public JavaDStream execute(JavaStreamingContext ssc, Integer pid) throws Exception {
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
            return receiverStream;
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    //createJavaDistributedStream[R](javaSparkStreamingContext, params, JFunction[Array[Byte], R]);

}
