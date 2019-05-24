package com.myz.rocketmq.service;

import com.example.rocketmq.boot.core.RocketMQTemplate;
import com.example.rocketmq.boot.support.RocketMQBuilder;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FirstProducerService {

    private static final Logger logger = LoggerFactory.getLogger(FirstProducerService.class);

    @Autowired
    private DefaultMQProducer defaultMQProducer;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;


    public String first() throws Exception {
        try {
            rocketMQTemplate.send("Topic:first", MessageBuilder.withPayload("Hello").build());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "1";
    }


    public String second() throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
        Message message = RocketMQBuilder.newMessage().topic("Topic").tag("second").key().body("second").build();
        message.setBuyerId(UUID.randomUUID().toString());
        SendResult result = defaultMQProducer.send(message);
        String name = result.getSendStatus().name();
        logger.info("{}", result);
        logger.info(name);
        return "2";
    }
}
