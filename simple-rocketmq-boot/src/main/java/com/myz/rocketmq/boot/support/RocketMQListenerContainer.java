package com.myz.rocketmq.boot.support;

import com.myz.rocketmq.boot.core.RocketMQListener;
import org.springframework.beans.factory.DisposableBean;

public interface RocketMQListenerContainer<T> extends DisposableBean {

    void setupMessageListener(RocketMQListener<T> rocketMQListener);

}
