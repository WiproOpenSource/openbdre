/*
 * Copyright (c) 2014 Wipro Limited
 * All Rights Reserved
 *
 * This code is protected by copyright and distributed under
 * licenses restricting copying, distribution and decompilation.
 */

package com.wipro.ats.bdre.im.eventing;

import com.wipro.ats.bdre.BaseStructure;
import com.wipro.ats.bdre.IMConfig;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Properties;
import java.util.Random;

/**
 * Created by arijit on 1/9/15.
 */
@Controller
@RequestMapping("/event")


public class ProducerAPI extends BaseStructure {
    private static Producer<String, String> producer;

     static {
        Properties props = new Properties();
        props.put("metadata.broker.list", IMConfig.getProperty("eventing.metadata-broker-list",null));
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        //props.put("partitioner.class", "example.producer.SimplePartitioner");
        props.put("request.required.acks", "1");
        ProducerConfig config = new ProducerConfig(props);
        producer = new Producer<String, String>(config);
    }

    @RequestMapping(value = "/produce/{topic}/{key}", method = RequestMethod.GET)
    public
    @ResponseBody
    RestWrapper put(@PathVariable("topic") String topic,@PathVariable("key") String key,
                    @RequestParam("message") String message) {

        KeyedMessage<String, String> data = new KeyedMessage<String, String>(topic, key, message);
        producer.send(data);
        RestWrapper restWrapper = new RestWrapper(topic, RestWrapper.OK);
        return restWrapper;
    }

}




