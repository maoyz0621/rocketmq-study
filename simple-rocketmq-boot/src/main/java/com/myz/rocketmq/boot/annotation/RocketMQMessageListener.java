package com.myz.rocketmq.boot.annotation;

import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RocketMQMessageListener {

    String NAME_SERVER_PLACEHOLDER = "${com.myz.rocketmq.namesrvAddr:}";

    String ACCESS_KEY_PLACEHOLDER = "${com.myz.rocketmq.consumer.access-key:}";

    String SECRET_KEY_PLACEHOLDER = "${com.myz.rocketmq.consumer.secret-key:}";

    String TRACE_TOPIC_PLACEHOLDER = "${com.myz.rocketmq.consumer.customized-trace-topic:}";

    String consumerGroup();

    /**
     * 主题
     */
    String topic();

    SelectorType selectorType() default SelectorType.TAG;

    /**
     * 默认 所有*
     * @return
     */
    String selectorExpression() default "*";

    ConsumeMode consumeMode() default ConsumeMode.CONCURRENTLY;

    MessageModel messageModel() default MessageModel.CLUSTERING;

    int consumeThreadMax() default 64;

    long consumeTimeout() default 30000L;

    String accessKey() default ACCESS_KEY_PLACEHOLDER;

    String secretKey() default SECRET_KEY_PLACEHOLDER;

    boolean enableMsgTrace() default true;

    String customizedTraceTopic() default TRACE_TOPIC_PLACEHOLDER;

    String namesrvAddr() default NAME_SERVER_PLACEHOLDER;

    int delayLevelWhenNextConsume() default 0;

    int consumeMessageBatchMaxSize() default 1;

}
