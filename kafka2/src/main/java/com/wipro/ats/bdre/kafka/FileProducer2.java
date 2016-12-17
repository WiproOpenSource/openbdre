package com.wipro.ats.bdre.kafka;

import org.apache.commons.io.FileUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import com.wipro.ats.bdre.BaseStructure;
import com.wipro.ats.bdre.exception.BDREException;
import com.wipro.ats.bdre.md.api.GetGeneralConfig;
import com.wipro.ats.bdre.md.api.GetProcess;
import com.wipro.ats.bdre.md.api.GetProperties;
import com.wipro.ats.bdre.md.beans.ProcessInfo;
import com.wipro.ats.bdre.md.beans.table.GeneralConfig;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by cloudera on 12/1/16.
 */
public class FileProducer2 {
    private static final Logger LOGGER = Logger.getLogger(FileProducer2.class);

    public static void main(String[] argv){
        FileProducer2 fileProducer = new FileProducer2();
        fileProducer.execute(argv);
    }

    public void execute(String[] argv){

        GetProcess getProcess = new GetProcess();
        List<ProcessInfo> subProcessList = getProcess.getSubProcesses(argv);
        String subProcessId = "";
        subProcessId = subProcessList.get(0).getProcessId().toString();
        LOGGER.info("subProcessId="+subProcessId);
        GetProperties getProperties = new GetProperties();
        Properties properties = getProperties.getProperties(subProcessId, "kafkaproducer");
        LOGGER.info("property is " + properties);
        String topicName = properties.getProperty("topicName");
        String zkConnectionString = properties.getProperty("zkConnectionString");
        String brokersList = properties.getProperty("brokersList");
       // monitoredDirName = properties.getProperty("monitoredDirName");
       // filePattern = properties.getProperty("filePattern");


        //Configure the Producer
        Properties configProperties = new Properties();
        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,brokersList);
        configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        org.apache.kafka.clients.producer.Producer producer = new KafkaProducer<String, String>(configProperties);

       // String topicName = argv[0];
       // String zkConnectionString = "localhost:2181";
        CreateTopic createTopic = new CreateTopic(zkConnectionString);
        createTopic.createTopic(topicName, 1, 1);
        //Map<String, String> messageMap = new HashMap<String, String>();
        while(true) {
            File[] files = new File("/home/cloudera/src").listFiles();
            //If this pathname does not denote a directory, then listFiles() returns null.

            for (File file : files) {
                if (file.isFile()) {
                    try {
                        String fileName = file.getName();
                        String fileContent = FileUtils.readFileToString(file);
                        // messageMap.put(fileName,fileContent);

                        ProducerRecord<String, String> kafkaMessage = new ProducerRecord<String, String>(topicName, fileName, fileContent);

                        producer.send(kafkaMessage);
                        file.delete();
                        System.out.println("producer event is = " + fileName);
                    }catch (Exception e){
                        System.out.println("e = " + e);
                    }

                }
            }
        }
       // producer.close();
    }
}
