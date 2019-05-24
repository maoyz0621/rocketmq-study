package com.myz.rocketmq.boot.autoconfig;


import org.apache.rocketmq.common.MixAll;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "com/myz/rocketmq")
public class RocketMQProperties {

    /**
     * host:port;host:port
     */
    private String namesrvAddr = "localhost:9876";

    private Producer producer;

    public String getNamesrvAddr() {
        return namesrvAddr;
    }

    public void setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }

    public RocketMQProperties.Producer getProducer() {
        return producer;
    }

    public void setProducer(RocketMQProperties.Producer producer) {
        this.producer = producer;
    }

    public static class Producer {

        private String groupName;

        private int sendMessageTimeout = 3000;

        private int compressMessageBodyThreshold = 1024 * 4;

        /**
         * 同步发送
         */
        private int retryTimesWhenSendFailed = 2;

        /**
         * 异步发送
         */
        private int retryTimesWhenSendAsyncFailed = 2;

        /**
         * 尝试下一台服务器
         */
        private boolean retryNextServer = false;

        private int maxMessageSize = 1024 * 1024 * 4;

        /**
         * The property of "access-key".
         */
        private String accessKey;

        /**
         * The property of "secret-key".
         */
        private String secretKey;

        /**
         * 消息軌跡
         */
        private boolean enableMsgTrace = true;

        /**
         * 4.4.0  低版本不存在屬性  RMQ_SYS_TRACE_TOPIC
         */
        private String customizedTraceTopic = MixAll.RMQ_SYS_TRACE_TOPIC;

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        public int getSendMessageTimeout() {
            return sendMessageTimeout;
        }

        public void setSendMessageTimeout(int sendMessageTimeout) {
            this.sendMessageTimeout = sendMessageTimeout;
        }

        public int getCompressMessageBodyThreshold() {
            return compressMessageBodyThreshold;
        }

        public void setCompressMessageBodyThreshold(int compressMessageBodyThreshold) {
            this.compressMessageBodyThreshold = compressMessageBodyThreshold;
        }

        public int getRetryTimesWhenSendFailed() {
            return retryTimesWhenSendFailed;
        }

        public void setRetryTimesWhenSendFailed(int retryTimesWhenSendFailed) {
            this.retryTimesWhenSendFailed = retryTimesWhenSendFailed;
        }

        public int getRetryTimesWhenSendAsyncFailed() {
            return retryTimesWhenSendAsyncFailed;
        }

        public void setRetryTimesWhenSendAsyncFailed(int retryTimesWhenSendAsyncFailed) {
            this.retryTimesWhenSendAsyncFailed = retryTimesWhenSendAsyncFailed;
        }

        public boolean isRetryNextServer() {
            return retryNextServer;
        }

        public void setRetryNextServer(boolean retryNextServer) {
            this.retryNextServer = retryNextServer;
        }

        public int getMaxMessageSize() {
            return maxMessageSize;
        }

        public void setMaxMessageSize(int maxMessageSize) {
            this.maxMessageSize = maxMessageSize;
        }

        public String getAccessKey() {
            return accessKey;
        }

        public void setAccessKey(String accessKey) {
            this.accessKey = accessKey;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }

        public boolean isEnableMsgTrace() {
            return enableMsgTrace;
        }

        public void setEnableMsgTrace(boolean enableMsgTrace) {
            this.enableMsgTrace = enableMsgTrace;
        }

        public String getCustomizedTraceTopic() {
            return customizedTraceTopic;
        }

        public void setCustomizedTraceTopic(String customizedTraceTopic) {
            this.customizedTraceTopic = customizedTraceTopic;
        }
    }

}
