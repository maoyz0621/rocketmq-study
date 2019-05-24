package com.myz.rocketmq.boot.autoconfig;

import com.myz.rocketmq.boot.core.RocketMQTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;

@Configuration
@EnableConfigurationProperties(RocketMQProperties.class)
public class RocketMQAutoConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(RocketMQAutoConfiguration.class);

    @Autowired
    private Environment environment;

    @PostConstruct
    public void checkServer() {
        LOGGER.info("******************** RocketMQAutoConfiguration checkServer() ***********************");

        String nameServer = environment.getProperty("com.myz.rocketmq.namesrvAddr", String.class);
        if (nameServer == null) {
            LOGGER.warn("The necessary spring property 'com.myz.rocketmq.namesrvAdd' is not defined, all rockertmq beans creation are skipped!");
        }
    }

    /**
     * @param rocketMQProperties
     * @return
     */
    @Bean
    @ConditionalOnProperty(prefix = "rocketmq", value = {"namesrvAddr", "producer.groupName"})
    public DefaultMQProducer defaultMQProducer(RocketMQProperties rocketMQProperties) {
        RocketMQProperties.Producer producerConfig = rocketMQProperties.getProducer();
        String namesrvAddr = rocketMQProperties.getNamesrvAddr();
        String group = producerConfig.getGroupName();
        Assert.hasText(namesrvAddr, "com.myz.rocketmq.namesrvAddr can not be null");
        Assert.hasText(group, "com.myz.rocketmq.namesrvAddr can not be null");

        DefaultMQProducer producer;
        String accessKey = producerConfig.getAccessKey();
        String secretKey = producerConfig.getSecretKey();

        if (!StringUtils.isEmpty(accessKey) && !StringUtils.isEmpty(secretKey)) {
            //        <dependency>
            //            <groupId>org.apache.com.myz.rocketmq</groupId>
            //            <artifactId>com.myz.rocketmq-acl</artifactId>
            //            <version>4.4.0</version>
            //        </dependency>
            //  低版本不存在   AclClientRPCHook  SessionCredentials
            producer = new DefaultMQProducer(group, new AclClientRPCHook(new SessionCredentials()), producerConfig.isEnableMsgTrace(), producerConfig.getCustomizedTraceTopic());
        } else {
            producer = new DefaultMQProducer(group, producerConfig.isEnableMsgTrace(), producerConfig.getCustomizedTraceTopic());
        }

        producer.setNamesrvAddr(namesrvAddr);
        producer.setSendMsgTimeout(producerConfig.getSendMessageTimeout());
        producer.setRetryTimesWhenSendFailed(producerConfig.getRetryTimesWhenSendFailed());
        producer.setRetryTimesWhenSendAsyncFailed(producerConfig.getRetryTimesWhenSendAsyncFailed());
        producer.setMaxMessageSize(producerConfig.getMaxMessageSize());
        producer.setCompressMsgBodyOverHowmuch(producerConfig.getCompressMessageBodyThreshold());
        producer.setRetryAnotherBrokerWhenNotStoreOK(producerConfig.isRetryNextServer());
        return producer;
    }


    @Bean(destroyMethod = "destroy")
    public RocketMQTemplate rocketMQTemplate(DefaultMQProducer mqProducer, ObjectMapper rocketMQMessageObjectMapper) {
        RocketMQTemplate rocketMQTemplate = new RocketMQTemplate();
        rocketMQTemplate.setObjectMapper(rocketMQMessageObjectMapper);
        rocketMQTemplate.setDefaultMQProducer(mqProducer);
        return rocketMQTemplate;
    }
}
