package com.luo.boot.rocketmq;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author xiangnan
 * date 2018/8/3 16:31
 */
@ConfigurationProperties(prefix = "spring.rocketmq")
@Data
public class RocketMQProperties {

    // name server for rocketMQ, formats: `host:port;host:port`
    private String nameServer;

    // serialize type
    private String serializer = "fastjson";

    // vip channel
    private boolean vipChannelEnabled = true;

    // topic prefix, just for test/dev environment
    private String topicPrefix;

    private Producer producer = new Producer();

    @Data
    public static class Producer {

        // producer group name
        private String groupName = "";

        // millis of send message timeout
        private int sendMsgTimeout = 3000;

        // retry times (sync) send failed
        private int retryTimesWhenSendFailed = 2;

        // retry times async send failed
        private int retryTimesWhenSendAsyncFailed = 2;
    }

}
