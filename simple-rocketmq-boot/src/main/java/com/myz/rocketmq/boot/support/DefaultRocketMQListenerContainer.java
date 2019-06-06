package com.myz.rocketmq.boot.support;

import com.myz.rocketmq.boot.annotation.ConsumeMode;
import com.myz.rocketmq.boot.annotation.RocketMQMessageListener;
import com.myz.rocketmq.boot.annotation.SelectorType;
import com.myz.rocketmq.boot.core.RocketMQListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.*;
import org.apache.rocketmq.client.consumer.rebalance.AllocateMessageQueueAveragely;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.SmartLifecycle;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * https://www.jianshu.com/p/7b8f2a97c8f5  当Spring容器加载所有bean并完成初始化之后，会接着回调实现该接口的类中对应的方法（start()方法）。
 */
public class DefaultRocketMQListenerContainer implements RocketMQListenerContainer, InitializingBean, SmartLifecycle, ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultRocketMQListenerContainer.class);

    private ApplicationContext applicationContext;

    /**
     * The name of the DefaultRocketMQListenerContainer instance
     */
    private String name;

    private long suspendCurrentQueueTimeMillis = 1000;

    /**
     * Message consume retry strategy<br> -1,no retry,put into DLQ directly<br> 0,broker control retry frequency<br>
     * >0,client control retry frequency.
     */
    private int delayLevelWhenNextConsume = 0;

    private String nameServer;

    private String consumerGroup;

    private String topic;

    private String charset = "UTF-8";

    private ObjectMapper objectMapper;

    private RocketMQListener rocketMQListener;

    private RocketMQMessageListener rocketMQMessageListener;

    private DefaultMQPushConsumer consumer;

    private Class messageType;

    private boolean running;

    // The following properties come from @RocketMQMessageListener.
    private int consumeThreadMax = 64;
    private ConsumeMode consumeMode;
    private SelectorType selectorType;
    private String selectorExpression;
    private MessageModel messageModel;
    private long consumeTimeout;
    private int consumeMessageBatchMaxSize;

    public int getConsumeMessageBatchMaxSize() {
        return consumeMessageBatchMaxSize;
    }

    public void setConsumeMessageBatchMaxSize(int consumeMessageBatchMaxSize) {
        this.consumeMessageBatchMaxSize = consumeMessageBatchMaxSize;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSuspendCurrentQueueTimeMillis() {
        return suspendCurrentQueueTimeMillis;
    }

    public void setSuspendCurrentQueueTimeMillis(long suspendCurrentQueueTimeMillis) {
        this.suspendCurrentQueueTimeMillis = suspendCurrentQueueTimeMillis;
    }

    public int getDelayLevelWhenNextConsume() {
        return delayLevelWhenNextConsume;
    }

    public void setDelayLevelWhenNextConsume(int delayLevelWhenNextConsume) {
        this.delayLevelWhenNextConsume = delayLevelWhenNextConsume;
    }

    public String getNameServer() {
        return nameServer;
    }

    public void setNameServer(String nameServer) {
        this.nameServer = nameServer;
    }

    public String getConsumerGroup() {
        return consumerGroup;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getConsumeThreadMax() {
        return consumeThreadMax;
    }

    public void setConsumeThreadMax(int consumeThreadMax) {
        this.consumeThreadMax = consumeThreadMax;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public RocketMQListener getRocketMQListener() {
        return rocketMQListener;
    }

    public void setRocketMQListener(RocketMQListener rocketMQListener) {
        this.rocketMQListener = rocketMQListener;
    }

    public RocketMQMessageListener getRocketMQMessageListener() {
        return rocketMQMessageListener;
    }

    public void setRocketMQMessageListener(RocketMQMessageListener rocketMQMessageListener) {
        this.rocketMQMessageListener = rocketMQMessageListener;

        this.consumeMode = rocketMQMessageListener.consumeMode();
        this.consumeThreadMax = rocketMQMessageListener.consumeThreadMax();
        this.messageModel = rocketMQMessageListener.messageModel();
        this.selectorExpression = rocketMQMessageListener.selectorExpression();
        this.selectorType = rocketMQMessageListener.selectorType();
        this.consumeTimeout = rocketMQMessageListener.consumeTimeout();
        this.consumeMessageBatchMaxSize = rocketMQMessageListener.consumeMessageBatchMaxSize();
    }

    public DefaultMQPushConsumer getConsumer() {
        return consumer;
    }

    public void setConsumer(DefaultMQPushConsumer consumer) {
        this.consumer = consumer;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public ConsumeMode getConsumeMode() {
        return consumeMode;
    }

    public void setConsumeMode(ConsumeMode consumeMode) {
        this.consumeMode = consumeMode;
    }

    public SelectorType getSelectorType() {
        return selectorType;
    }

    public void setSelectorType(SelectorType selectorType) {
        this.selectorType = selectorType;
    }

    public String getSelectorExpression() {
        return selectorExpression;
    }

    public void setSelectorExpression(String selectorExpression) {
        this.selectorExpression = selectorExpression;
    }

    public MessageModel getMessageModel() {
        return messageModel;
    }

    public void setMessageModel(MessageModel messageModel) {
        this.messageModel = messageModel;
    }

    public long getConsumeTimeout() {
        return consumeTimeout;
    }

    public void setConsumeTimeout(long consumeTimeout) {
        this.consumeTimeout = consumeTimeout;
    }

    @Override
    public void setupMessageListener(RocketMQListener rocketMQListener) {
        this.rocketMQListener = rocketMQListener;
    }

    @Override
    public void destroy() throws Exception {
        LOGGER.info("**************** DefaultRocketMQListenerContainer destroy() ******************");
        consumer.shutdown();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        initRocketMQPushConsumer();

        this.messageType = getMessageType();
        LOGGER.info("**************** DefaultRocketMQListenerContainer afterPropertiesSet() , messageType = {} ******************", messageType);
    }

    private Class getMessageType() {
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(rocketMQListener);
        Type[] interfaces = targetClass.getGenericInterfaces();
        Class<?> superclass = targetClass.getSuperclass();
        while ((Objects.isNull(interfaces) || 0 == interfaces.length) && Objects.nonNull(superclass)) {
            interfaces = superclass.getGenericInterfaces();
            superclass = targetClass.getSuperclass();
        }
        if (Objects.nonNull(interfaces)) {
            for (Type type : interfaces) {
                if (type instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) type;
                    if (Objects.equals(parameterizedType.getRawType(), RocketMQListener.class)) {
                        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                        if (Objects.nonNull(actualTypeArguments) && actualTypeArguments.length > 0) {
                            return (Class) actualTypeArguments[0];
                        } else {
                            return Object.class;
                        }
                    }
                }
            }

            return Object.class;
        } else {
            return Object.class;
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        LOGGER.info("**************** DefaultRocketMQListenerContainer setApplicationContext() ******************");
        this.applicationContext = applicationContext;
    }

    /**
     * @return
     */
    @Override
    public boolean isAutoStartup() {
        LOGGER.info("**************** DefaultRocketMQListenerContainer isAutoStartup() ******************");
        return true;
    }

    @Override
    public void stop(Runnable runnable) {
        LOGGER.info("**************** DefaultRocketMQListenerContainer stop() ******************");
        stop();
        runnable.run();
    }

    @Override
    public void start() {
        LOGGER.info("**************** DefaultRocketMQListenerContainer start() ******************");
        try {
            consumer.start();

            setRunning(true);
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        if (this.isRunning()) {
            if (consumer != null) {
                consumer.shutdown();
            }
        }
        setRunning(false);
    }

    @Override
    public boolean isRunning() {
        LOGGER.info("**************** DefaultRocketMQListenerContainer isRunning() ******************");
        return running;
    }

    @Override
    public int getPhase() {
        LOGGER.info("**************** DefaultRocketMQListenerContainer getPhase() ******************");
        return 0;
    }

    /**
     * MessageListenerConcurrently的默认实现
     */
    public class DefaultMessageListenerConcurrently implements MessageListenerConcurrently {

        @SuppressWarnings("unchecked")
        @Override
        public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
            for (MessageExt msg : msgs) {
                LOGGER.debug("{}", msg);
                try {
                    long start = System.currentTimeMillis();

                    rocketMQListener.onMessage(doConvertMessage(msg));

                    long end = System.currentTimeMillis() - start;
                    LOGGER.info("消费消息成功，耗时 {} ms, msgId = {}", end, msg.getMsgId());
                } catch (Exception e) {
                    // todo 业务异常，服务器异常
                    // -1直接放到死信队列，0又broker每次对重试消费次数加1来控制重试策略，大于0由consumer控制重试消费策略
                    // （在listener的consumeMessage方法里面有个context:context.setDelayLevelWhenNextConsume(4)设置为1分钟延时消费），
                    // 默认值为0。
                    // 死信Topic的命名为：%DLQ% + Consumer组名。
                    context.setDelayLevelWhenNextConsume(delayLevelWhenNextConsume);
                    // 重复消费3次默认成功
                    if (3 == msg.getReconsumeTimes()) {
                        LOGGER.error("*************** 重试次数达到上限 【{}】 ****************", msg.getReconsumeTimes());
                        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                    } else {
                        LOGGER.error("*************** 消息消费失败,进行重试 【{}】****************", msg.getReconsumeTimes());
                        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                    }

                }
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
    }

    /**
     * MessageListenerOrderly的默认实现
     */
    public class DefaultMessageListenerOrderly implements MessageListenerOrderly {

        @SuppressWarnings("unchecked")
        @Override
        public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
            for (MessageExt msg : msgs) {
                LOGGER.debug("************** {} ****************", msg);
                try {
                    long start = System.currentTimeMillis();

                    rocketMQListener.onMessage(doConvertMessage(msg));

                    long end = System.currentTimeMillis() - start;
                    LOGGER.info("消费消息成功，耗时 {} ms, msgId = {}", end, msg.getMsgId());
                } catch (Exception e) {
                    // 串行消费使用，如果返回ROLLBACK或者SUSPEND_CURRENT_QUEUE_A_MOMENT，再次消费的时间间隔
                    context.setSuspendCurrentQueueTimeMillis(suspendCurrentQueueTimeMillis);
                    if (3 == msg.getReconsumeTimes()) {
                        LOGGER.error("*************** 重试次数达到上限 【{}】 ****************", msg.getReconsumeTimes());
                        return ConsumeOrderlyStatus.SUCCESS;
                    } else {
                        LOGGER.error("*************** 消息消费失败,进行重试 【{}】****************", msg.getReconsumeTimes());
                        return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
                    }

                }
            }
            return ConsumeOrderlyStatus.SUCCESS;
        }
    }

    private Object doConvertMessage(MessageExt messageExt) throws UnsupportedEncodingException {
        if (Objects.equals(messageType, MessageExt.class)) {
            return messageExt;
        } else {
            String msg = new String(messageExt.getBody(), RemotingHelper.DEFAULT_CHARSET);
            if (Objects.equals(messageType, String.class)) {
                LOGGER.info("********** {} **********", msg);
                return msg;
            } else {
                try {
                    return objectMapper.readValue(msg, messageType);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException();
                }
            }
        }
    }

    /**
     * 初始化Consumer
     */
    private void initRocketMQPushConsumer() throws MQClientException {
        boolean enableMsgTrace = rocketMQMessageListener.enableMsgTrace();
        // AllocateMessageQueueAveragely  均衡
        consumer = new DefaultMQPushConsumer(consumerGroup, null, new AllocateMessageQueueAveragely(),
                enableMsgTrace,
                this.applicationContext.getEnvironment().resolveRequiredPlaceholders(this.rocketMQMessageListener.customizedTraceTopic()));
        consumer.setNamesrvAddr(nameServer);
        consumer.setVipChannelEnabled(false);
        consumer.setInstanceName(consumerGroup + UUID.randomUUID().toString());
        consumer.setConsumeThreadMax(consumeThreadMax);
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.setConsumeMessageBatchMaxSize(consumeMessageBatchMaxSize);
        // 如果设置的最大线程数比默认最小线程数小
        if (this.consumeThreadMax < consumer.getConsumeThreadMin()) {
            consumer.setConsumeThreadMin(consumeThreadMax);
        }

        // message models -> clusterting broadcasting
        switch (messageModel) {
            case CLUSTERING:
                consumer.setMessageModel(MessageModel.CLUSTERING);
                break;
            case BROADCASTING:
                consumer.setMessageModel(MessageModel.BROADCASTING);
                break;
            default:
                throw new IllegalArgumentException("");
        }


        switch (selectorType) {
            case TAG:
                consumer.subscribe(topic, selectorExpression);
                break;
            default:
                consumer.subscribe(topic, "*");
        }

        switch (consumeMode) {
            case CONCURRENTLY:
                consumer.setMessageListener(new DefaultMessageListenerConcurrently());
                break;
            case ORDERLY:
                consumer.setMessageListener(new DefaultMessageListenerOrderly());
                break;
            default:
                throw new IllegalArgumentException("");
        }

    }

    @Override
    public String toString() {
        return "DefaultRocketMQListenerContainer{" +
                "consumerGroup='" + consumerGroup + '\'' +
                ", nameServer='" + nameServer + '\'' +
                ", topic='" + topic + '\'' +
                ", consumeMode=" + consumeMode +
                ", selectorType=" + selectorType +
                ", selectorExpression='" + selectorExpression + '\'' +
                ", messageModel=" + messageModel +
                '}';
    }
}
