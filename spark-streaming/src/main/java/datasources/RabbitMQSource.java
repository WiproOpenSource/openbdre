 package datasources;

import com.rabbitmq.client.QueueingConsumer.Delivery;
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
            rabbitMqConParams.put("queueName", "trial");

            JavaReceiverInputDStream<String> receiverStream = RabbitMQUtils.createJavaStream(ssc, String.class, rabbitMqConParams, new Function<Delivery, String>() {
                @Override
                public String call(Delivery delivery) throws Exception {
                    //System.out.println("delivery.getBody() = " + delivery.getBody());
                    //return delivery.getBody();
                    return new String(delivery.getBody());
                }
            });
            System.out.println("receiverStream.toString() = " + receiverStream.toString());
            receiverStream.print();
            JavaPairDStream<String, String> outputPairDStream= receiverStream.mapToPair(s -> new Tuple2<String, String>(null,s));
            outputPairDStream.print();
            return outputPairDStream;
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    //createJavaDistributedStream[R](javaSparkStreamingContext, params, JFunction[Array[Byte], R]);

}
