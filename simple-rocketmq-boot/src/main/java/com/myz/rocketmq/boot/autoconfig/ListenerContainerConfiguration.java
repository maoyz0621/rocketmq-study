package com.myz.rocketmq.boot.autoconfig;


import com.myz.rocketmq.boot.annotation.ConsumeMode;
import com.myz.rocketmq.boot.annotation.RocketMQMessageListener;
import com.myz.rocketmq.boot.core.RocketMQListener;
import com.myz.rocketmq.boot.support.DefaultRocketMQListenerContainer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.support.BeanDefinitionValidationException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.StandardEnvironment;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 消费监听
 */
@Configuration
public class ListenerContainerConfiguration implements ApplicationContextAware, SmartInitializingSingleton {

    private static final Logger LOGGER = LoggerFactory.getLogger(ListenerContainerConfiguration.class);

    /**
     *
     */
    private ConfigurableApplicationContext applicationContext;

    private AtomicLong atomicLong = new AtomicLong(0);

    private RocketMQProperties rocketMQProperties;

    private StandardEnvironment environment;

    private ObjectMapper objectMapper;

    public ListenerContainerConfiguration(RocketMQProperties rocketMQProperties, StandardEnvironment environment, ObjectMapper objectMapper) {
        this.rocketMQProperties = rocketMQProperties;
        this.environment = environment;
        this.objectMapper = objectMapper;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        LOGGER.info("******************** ListenerContainerConfiguration setApplicationContext() ***********************");

        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
    }


    @Override
    public void afterSingletonsInstantiated() {
        LOGGER.info("******************** ListenerContainerConfiguration afterSingletonsInstantiated() ***********************");
        //
        Map<String, Object> beans = this.applicationContext.getBeansWithAnnotation(RocketMQMessageListener.class);

        if (Objects.nonNull(beans)) {
            beans.forEach(this::registerContainer);
        }
    }


    /**
     * 注册Listener
     *
     * @param beanName
     * @param bean
     */
    private void registerContainer(String beanName, Object bean) {
        // 最终  ultimate
        Class<?> clazz = AopProxyUtils.ultimateTargetClass(bean);
        //
        if (!RocketMQListener.class.isAssignableFrom(bean.getClass())) {

        }

        RocketMQMessageListener annotation = clazz.getAnnotation(RocketMQMessageListener.class);

        // todo ???
        if (annotation.consumeMode() == ConsumeMode.ORDERLY &&
                annotation.messageModel() == MessageModel.BROADCASTING) {
            throw new BeanDefinitionValidationException(
                    "Bad annotation definition in @RocketMQMessageListener, messageModel BROADCASTING does not support ORDERLY message!");
        }

        // 命名空间 DefaultRocketMQListenerContainer
        String containerBeanName = String.format("%s_%s", DefaultRocketMQListenerContainer.class.getName(), atomicLong.incrementAndGet());

        GenericApplicationContext genericApplicationContext = (GenericApplicationContext) applicationContext;
        // 注册bean
        genericApplicationContext.registerBean(containerBeanName, DefaultRocketMQListenerContainer.class, () -> createRocketMQListenerContainer(bean, annotation));

        DefaultRocketMQListenerContainer container = genericApplicationContext.getBean(containerBeanName, DefaultRocketMQListenerContainer.class);

        if (!container.isRunning()) {
            container.start();
        }

    }

    /**
     * @param bean
     * @param annotation
     * @return
     */
    private DefaultRocketMQListenerContainer createRocketMQListenerContainer(Object bean, RocketMQMessageListener annotation) {
        DefaultRocketMQListenerContainer rocketMQListenerContainer = new DefaultRocketMQListenerContainer();
        rocketMQListenerContainer.setNameServer(rocketMQProperties.getNamesrvAddr());
        rocketMQListenerContainer.setTopic(environment.resolveRequiredPlaceholders(annotation.topic()));
        rocketMQListenerContainer.setConsumerGroup(annotation.consumerGroup());
        rocketMQListenerContainer.setRocketMQMessageListener(annotation);
        rocketMQListenerContainer.setRocketMQListener((RocketMQListener) bean);
        rocketMQListenerContainer.setObjectMapper(objectMapper);
        rocketMQListenerContainer.setDelayLevelWhenNextConsume(annotation.delayLevelWhenNextConsume());
        return rocketMQListenerContainer;
    }

}
