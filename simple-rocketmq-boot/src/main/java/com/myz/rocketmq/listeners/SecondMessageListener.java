package com.myz.rocketmq.listeners;

import com.example.rocketmq.boot.core.RocketMQListener;
import com.example.rocketmq.boot.exceptions.RocketMessageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


// @RocketMQMessageListener(topic = "Topic", consumerGroup = "second-group", enableMsgTrace = true, selectorExpression = "*")
@Component
public class SecondMessageListener implements RocketMQListener<String> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void onMessage(String message) throws RocketMessageException {
        logger.info("********************** SecondMessageListener :{} **************************", message);
    }
}
