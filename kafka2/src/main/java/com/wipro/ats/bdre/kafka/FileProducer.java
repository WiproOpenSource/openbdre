package com.wipro.ats.bdre.kafka;

import org.apache.commons.io.FileUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.File;
import java.util.*;

/**
 * Created by madhav on 11/24/16.
 */
public class FileProducer {

    public static void main(String[] argv)throws Exception {
        if (argv.length != 1) {
            System.err.println("Please specify 1 parameters ");
            System.exit(-1);
        }

        //Configure the Producer
        Properties configProperties = new Properties();
        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "com.wipro.ats.bdre.kafka.FileMapSerializer");
        org.apache.kafka.clients.producer.Producer producer = new KafkaProducer<String, Map>(configProperties);

        Map<String, String> messageMap = new HashMap<String, String>();
        File[] files = new File("/home/cloudera/src").listFiles();
        //If this pathname does not denote a directory, then listFiles() returns null.

        for (File file : files) {
            if (file.isFile()) {
                String fileName = file.getName();
                String fileContent = FileUtils.readFileToString(file);
                messageMap.put(fileName,fileContent);

                String topicName = argv[0];
                ProducerRecord<String, Map<String,String>> kafkaMessage = new ProducerRecord<String, Map<String,String>>(topicName,fileName, messageMap);

                producer.send(kafkaMessage);

            }
        }
        producer.close();
    }
}
