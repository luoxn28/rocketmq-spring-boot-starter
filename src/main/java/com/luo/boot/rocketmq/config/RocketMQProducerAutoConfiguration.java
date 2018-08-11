package com.luo.boot.rocketmq.config;

import com.luo.boot.rocketmq.RocketMQProperties;
import com.luo.boot.rocketmq.exception.RocketmqParamException;
import com.luo.boot.rocketmq.util.SystemUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import java.net.UnknownHostException;
import java.util.Objects;

/**
 * @author xiangnan
 * date 2018/8/6 12:41
 */
@Slf4j
@Configuration
@ConditionalOnBean(value = BaseAutoConfiguration.class)
@AutoConfigureAfter(value = {BaseAutoConfiguration.class})
public class RocketMQProducerAutoConfiguration extends BaseAutoConfiguration {

    @Setter
    private static DefaultMQProducer producer;

    @Bean
    public DefaultMQProducer defaultMQProducer() throws MQClientException {
        Assert.hasText(mqProperties.getNameServer(), "[rocketmq.nameServer] must not be null");

        RocketMQProperties.Producer producerConfig = mqProperties.getProducer();
        if (Objects.isNull(producerConfig)) {
            // no producer configuration
            log.info("[rocketmq.producer] is null, will not init DefaultMQProducer");
            return null;
        }

        if (RocketMQProducerAutoConfiguration.producer == null) {
            String groupName = producerConfig.getGroupName();
            if (StringUtils.isBlank(groupName)) {
                groupName = applicationContext.getEnvironment().getProperty("spring.application.name");
            }
            if (StringUtils.isBlank(groupName)) {
                try {
                    groupName = SystemUtil.localHostName();
                    groupName = StringUtils.replace(groupName, ".", "");
                } catch (UnknownHostException e) {
                    throw new RocketmqParamException("unknown localhost name!!!");
                }
            }

            DefaultMQProducer producer = new DefaultMQProducer(groupName);
            producer.setNamesrvAddr(mqProperties.getNameServer());
            producer.setSendMsgTimeout(producerConfig.getSendMsgTimeout());
            producer.setRetryTimesWhenSendFailed(producerConfig.getRetryTimesWhenSendFailed());
            producer.setRetryTimesWhenSendAsyncFailed(producerConfig.getRetryTimesWhenSendAsyncFailed());
            producer.setVipChannelEnabled(mqProperties.isVipChannelEnabled());

            RocketMQProducerAutoConfiguration.producer = producer;
            producer.start();

            log.info("defaultMQProducer: {}", producer);
            log.info("defaultMQProducer start success");
        }

        return producer;
    }

    synchronized protected void init() {
    }

}
