package com.myz.rocketmq.boot.support;

import org.apache.rocketmq.common.message.Message;

import java.util.UUID;

public class RocketMQBuilder extends Message {

    private Message message;

    private RocketMQBuilder() {
        message = new Message();
    }

    public static RocketMQBuilder newMessage() {
        return new RocketMQBuilder();
    }

    public RocketMQBuilder topic(String topic) {
        message.setTopic(topic);
        return this;
    }

    public RocketMQBuilder tag(String tag) {
        message.setTags(tag);
        return this;
    }

    public RocketMQBuilder key() {
        message.setKeys(UUID.randomUUID().toString());
        return this;
    }

    public RocketMQBuilder body(String json) {
        message.setBody(json.getBytes());
        return this;
    }

    public Message build() {
        return message;
    }

}
