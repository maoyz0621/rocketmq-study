package com.myz.rocketmq.listeners;

import com.myz.rocketmq.boot.annotation.RocketMQMessageListener;
import com.myz.rocketmq.boot.core.RocketMQListener;
import com.myz.rocketmq.boot.exceptions.RocketMessageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@RocketMQMessageListener(topic = "Topic", consumerGroup = "first-group", enableMsgTrace = true, selectorExpression = "*", consumeMessageBatchMaxSize = 10)
@Component
public class FirstMessageListener implements RocketMQListener<String> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void onMessage(String message) throws RocketMessageException {
        logger.info("********************** FirstMessageListener :{} **************************", message);


        int i = 1 / 0;

    }
}
