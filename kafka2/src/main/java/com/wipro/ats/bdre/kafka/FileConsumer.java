package com.wipro.ats.bdre.kafka;


import org.apache.hadoop.io.IOUtils;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;


import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

/**
 * Created by madhav on 11/24/16.
 */
public class FileConsumer {

    private static boolean stop = false;

    public static void main(String[] argv)throws Exception{
        if (argv.length != 2) {
            System.err.printf("Usage: %s <topicName> <groupId>\n",
                    FileConsumer.class.getSimpleName());
            System.exit(-1);
        }
        String topicName = argv[0];
        String groupId = argv[1];

        ConsumerThread consumerRunnable = new ConsumerThread(topicName,groupId);
        consumerRunnable.start();

        consumerRunnable.getKafkaConsumer().wakeup();
        System.out.println("Stopping consumer .....");
        consumerRunnable.join();
    }

    private static class ConsumerThread extends Thread{
        private String topicName;
        private String groupId;
        private KafkaConsumer<String,Map<String,String> > kafkaConsumer;

        public ConsumerThread(String topicName, String groupId){
            this.topicName = topicName;
            this.groupId = groupId;
        }
        public void run() {
            Properties configProperties = new Properties();
            configProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
            configProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
            configProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "com.wipro.ats.bdre.kafka.MapDeserializer");
            configProperties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
            configProperties.put(ConsumerConfig.CLIENT_ID_CONFIG, "simple");

            //Figure out where to start processing messages from
            kafkaConsumer = new KafkaConsumer<String, Map<String,String>>(configProperties);
            kafkaConsumer.subscribe(Arrays.asList(topicName));
            //Start processing messages
            try {
                while (true) {
                    ConsumerRecords<String, Map<String,String>> records = kafkaConsumer.poll(1000);
                    for (ConsumerRecord<String, Map<String,String>> record1 : records) {
                        System.out.println(record1.key() );
                        String dst = "hdfs://quickstart.cloudera:8020/user/cloudera/dest2/";
                        String content = "";
                       // dst += record.value().keySet().toArray()[0];
                        String fileNam = record1.key();
                        dst += fileNam;
                        //content += record.value().values().toArray()[0];
                        content += record1.value().get(fileNam);
                        InputStream in = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));

                        //Get configuration of Hadoop system
                        Configuration conf = new Configuration();
                        try {

                            //Destination file in HDFS
                            FileSystem fs = FileSystem.get(URI.create(dst), conf);
                            OutputStream out = fs.create(new Path(dst));

                            //Copy file from local to HDFS
                            IOUtils.copyBytes(in, out, 4096, true);

                            System.out.println(dst + " copied to HDFS");
                        }catch (Exception e){
                            System.out.println(e);
                        }

                    }
                }
            }catch(WakeupException ex){
                System.out.println("Exception caught " + ex.getMessage());
            }finally{
                kafkaConsumer.close();
                System.out.println("After closing KafkaConsumer");
            }
        }
        public KafkaConsumer<String,Map<String,String>> getKafkaConsumer(){
            return this.kafkaConsumer;
        }
    }
}
