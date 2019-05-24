package com.myz.rocketmq.boot.core;

import com.myz.rocketmq.boot.exceptions.RocketMessageException;
import com.myz.rocketmq.boot.support.RocketMQUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.messaging.Message;
import org.springframework.messaging.core.AbstractMessageSendingTemplate;
import org.springframework.util.Assert;

import java.util.UUID;

public class RocketMQTemplate extends AbstractMessageSendingTemplate<String> implements InitializingBean, DisposableBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(RocketMQTemplate.class);

    private DefaultMQProducer defaultMQProducer;

    private ObjectMapper objectMapper;

    public DefaultMQProducer getDefaultMQProducer() {
        return defaultMQProducer;
    }

    public void setDefaultMQProducer(DefaultMQProducer defaultMQProducer) {
        this.defaultMQProducer = defaultMQProducer;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        LOGGER.info("*********************** RocketMQTemplate afterPropertiesSet() ***************************");

        Assert.notNull(defaultMQProducer, "defaultMQProducer can not be null");
        defaultMQProducer.start();
    }

    @Override
    public void destroy() throws Exception {
        LOGGER.info("*********************** RocketMQTemplate destroy() ***************************");

        if (defaultMQProducer != null) {
            defaultMQProducer.shutdown();
        }
    }

    /**
     * AbstractMessageSendingTemplate中不同MQ具体发送实现方式
     *
     * @param destination
     * @param message
     */
    @Override
    protected void doSend(String destination, Message<?> message) {
        SendResult sendResult = syncSend(destination, message);
        LOGGER.info("Send message 【{}】 success , result = 【{}】 ", destination, sendResult);

    }

    public SendResult syncSend(String destination, Message<?> message) {
        return syncSend(destination, message, defaultMQProducer.getSendMsgTimeout());
    }

    public SendResult syncSend(String destination, Message<?> message, long timeout) {
        return syncSend(destination, message, timeout, 0);
    }

    /**
     * 同步发送消息
     *
     * @param destination topicName:tags
     * @param message     org.springframework.messaging提供的message模板
     * @param timeout     过期时间
     * @param delayLevel  网络延迟
     * @return
     */
    public SendResult syncSend(String destination, Message<?> message, long timeout, int delayLevel) {

        try {
            // 将rocketMessage -> springMessage
            org.apache.rocketmq.common.message.Message rocketMessage = RocketMQUtil.convertToRocketMessage(objectMapper, RemotingHelper.DEFAULT_CHARSET, destination, message);

            rocketMessage.setKeys(UUID.randomUUID().toString());
            //
            if (delayLevel > 0) {
                rocketMessage.setDelayTimeLevel(delayLevel);
            }

            long start = System.currentTimeMillis();
            // 调用DefaultMQProducer 底层发送消息
            SendResult sendResult = defaultMQProducer.send(rocketMessage, timeout);
            long end = System.currentTimeMillis() - start;

            LOGGER.info("发送消息成功，耗时 {} ms, msgId = {}", end, sendResult.getMsgId());
            return sendResult;

        } catch (Exception e) {
            LOGGER.error("syncSend failed. destination:{}, message:{} ", destination, message);
            throw new RocketMessageException(e.getMessage(), e);
        }

    }
}
