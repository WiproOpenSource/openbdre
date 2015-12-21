/*
 * Copyright (c) 2014 Wipro Limited
 * All Rights Reserved
 *
 * This code is protected by copyright and distributed under
 * licenses restricting copying, distribution and decompilation.
 */

package com.wipro.ats.bdre.im.mqimport;

/**
 * Created by arijit on 4/25/15.
 */

import backtype.storm.Config;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;
import com.wipro.ats.bdre.md.api.GetProperties;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class MQSpout extends BaseRichSpout implements ExceptionListener {
    Properties props=new GetProperties().getProperties(MQTopology.sPId,"mqimport");
    String mqBrokerURI=props.getProperty("broker.url");
    String queueName=props.getProperty("queue.name");
    String spoutName="mySpout";
    public static Logger LOG = LoggerFactory.getLogger(MQSpout.class);
    boolean _isDistributed;
    SpoutOutputCollector _collector;

    public MQSpout() {
        this(true);
    }

    public MQSpout(boolean isDistributed) {
        _isDistributed = isDistributed;
    }

    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        _collector = collector;
    }

    public void close() {

    }

    public void nextTuple() {
        Utils.sleep(100);
        try {

            // Create a ConnectionFactory
            ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(mqBrokerURI);

            // Create a Connection
            Connection connection = activeMQConnectionFactory.createConnection();
            connection.start();

            connection.setExceptionListener(this);

            // Create a Session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create the destination (Topic or Queue)
            Queue queue = session.createQueue(queueName);
            // Create a MessageConsumer from the Session to the Topic or
            // Queue
            QueueBrowser browser = session.createBrowser(queue);
            Enumeration msgs = browser.getEnumeration();
            if ( msgs.hasMoreElements() ) {
                MessageConsumer consumer = session.createConsumer(queue);

                // Wait for a message
                Message message = consumer.receive();
                TextMessage textMessage = (TextMessage) message;
                String text = textMessage.getText();

                _collector.emit(new Values(text));
                consumer.close();
            }
            session.close();
            connection.close();


        } catch (Exception e) {
            LOG.error("Caught exception: " + e);
            e.printStackTrace();
        }

    }
    public synchronized void onException(JMSException ex) {
        System.out.println("ActiveMQ JMS Exception occured.  Shutting down client.");
    }
    public void ack(Object msgId) {

    }

    public void fail(Object msgId) {

    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields(spoutName));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        if(!_isDistributed) {
            Map<String, Object> ret = new HashMap<String, Object>();
            ret.put(Config.TOPOLOGY_MAX_TASK_PARALLELISM, 1);
            return ret;
        } else {
            return null;
        }
    }
}
