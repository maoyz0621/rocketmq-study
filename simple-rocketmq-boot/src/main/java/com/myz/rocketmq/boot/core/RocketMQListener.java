package com.myz.rocketmq.boot.core;

import com.myz.rocketmq.boot.exceptions.RocketMessageException;

/**
 * 消費者监听器，由具体业务实现接口
 *
 * @param <T>
 */
public interface RocketMQListener<T> {

    void onMessage(T message) throws RocketMessageException;
}
