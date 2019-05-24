package com.myz.rocketmq.boot.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.rocketmq.common.message.MessageConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.MessageHeaders;
import org.springframework.util.StringUtils;

import java.nio.charset.Charset;
import java.util.Objects;

public class RocketMQUtil {
    private static final Logger log = LoggerFactory.getLogger(RocketMQUtil.class);

//    public static org.springframework.messaging.Message convertToSpringMessage(
//            org.apache.com.myz.rocketmq.common.message.MessageExt message) {
//        MessageBuilder messageBuilder =
//                MessageBuilder.withPayload(message.getBody()).
//                        setHeader(toRocketHeaderKey(RocketMQHeaders.KEYS), message.getKeys()).
//                        setHeader(toRocketHeaderKey(RocketMQHeaders.TAGS), message.getTags()).
//                        setHeader(toRocketHeaderKey(RocketMQHeaders.TOPIC), message.getTopic()).
//                        setHeader(toRocketHeaderKey(RocketMQHeaders.MESSAGE_ID), message.getMsgId()).
//                        setHeader(toRocketHeaderKey(RocketMQHeaders.BORN_TIMESTAMP), message.getBornTimestamp()).
//                        setHeader(toRocketHeaderKey(RocketMQHeaders.BORN_HOST), message.getBornHostString()).
//                        setHeader(toRocketHeaderKey(RocketMQHeaders.FLAG), message.getFlag()).
//                        setHeader(toRocketHeaderKey(RocketMQHeaders.QUEUE_ID), message.getQueueId()).
//                        setHeader(toRocketHeaderKey(RocketMQHeaders.SYS_FLAG), message.getSysFlag()).
//                        setHeader(toRocketHeaderKey(RocketMQHeaders.TRANSACTION_ID), message.getTransactionId());
//        addUserProperties(message.getProperties(), messageBuilder);
//        return messageBuilder.build();
//    }

    public static String toRocketHeaderKey(String rawKey) {
        return RocketMQHeaders.PREFIX + rawKey;
    }

    /**
     *
     * @param objectMapper
     * @param charset
     * @param destination
     * @param message
     * @return
     */
    public static org.apache.rocketmq.common.message.Message convertToRocketMessage(ObjectMapper objectMapper,
                                                                                    String charset,
                                                                                    String destination,
                                                                                    org.springframework.messaging.Message<?> message) {
        // 消息体，因为rocketmq的消息内容必须为byte[]
        Object payloadObj = message.getPayload();
        byte[] payloads;

        if (payloadObj instanceof String) {
            payloads = ((String) payloadObj).getBytes(Charset.forName(charset));
        } else if (payloadObj instanceof byte[]) {
            payloads = (byte[]) message.getPayload();
        } else {
            try {
                String jsonObj = objectMapper.writeValueAsString(payloadObj);
                payloads = jsonObj.getBytes(Charset.forName(charset));
            } catch (Exception e) {
                throw new RuntimeException("convert to RocketMQ message failed.", e);
            }
        }

        // 拼装topic和tag
        String[] tempArr = destination.split(":", 2);
        String topic = tempArr[0];
        String tags = "";
        if (tempArr.length > 1) {
            tags = tempArr[1];
        }

        org.apache.rocketmq.common.message.Message rocketMsg = new org.apache.rocketmq.common.message.Message(topic, tags, payloads);

        MessageHeaders headers = message.getHeaders();
        if (Objects.nonNull(headers) && !headers.isEmpty()) {
            Object keys = headers.get(RocketMQHeaders.KEYS);
            if (!StringUtils.isEmpty(keys)) { // if headers has 'KEYS', set rocketMQ message key
                rocketMsg.setKeys(keys.toString());
            }

            Object flagObj = headers.getOrDefault("FLAG", "0");
            int flag = 0;
            try {
                flag = Integer.parseInt(flagObj.toString());
            } catch (NumberFormatException e) {
                // Ignore it
                log.info("flag must be integer, flagObj:{}", flagObj);
            }
            rocketMsg.setFlag(flag);

            Object waitStoreMsgOkObj = headers.getOrDefault("WAIT_STORE_MSG_OK", "true");
            boolean waitStoreMsgOK = Boolean.TRUE.equals(waitStoreMsgOkObj);
            rocketMsg.setWaitStoreMsgOK(waitStoreMsgOK);

            headers.entrySet().stream()
                    .filter(entry -> !Objects.equals(entry.getKey(), "FLAG")
                            && !Objects.equals(entry.getKey(), "WAIT_STORE_MSG_OK")) // exclude "FLAG", "WAIT_STORE_MSG_OK"
                    .forEach(entry -> {
                        if (!MessageConst.STRING_HASH_SET.contains(entry.getKey())) {
                            rocketMsg.putUserProperty(entry.getKey(), String.valueOf(entry.getValue()));
                        }
                    });

        }

        return rocketMsg;
    }
}
