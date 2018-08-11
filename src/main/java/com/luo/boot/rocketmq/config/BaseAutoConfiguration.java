package com.luo.boot.rocketmq.config;

import com.luo.boot.rocketmq.RocketMQProperties;
import com.luo.boot.rocketmq.exception.RocketmqParamException;
import com.luo.boot.rocketmq.serializer.IObjectSerializer;
import com.luo.boot.rocketmq.util.MQDefaultProperties;
import com.luo.boot.rocketmq.util.MessageBuilder;
import com.luo.boot.rocketmq.util.SerializerUtil;
import com.luo.boot.rocketmq.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.net.UnknownHostException;

/**
 * @author xiangnan
 * date 2018/8/6 12:03
 */
@Configuration
@EnableConfigurationProperties(RocketMQProperties.class)
@Slf4j
public class BaseAutoConfiguration implements ApplicationContextAware {

    protected RocketMQProperties mqProperties;

    protected ConfigurableApplicationContext applicationContext;

    @Resource
    public void setMqProperties(RocketMQProperties mqProperties) {
        Assert.notNull(mqProperties, "[rocketmq] must not be null");
        Assert.hasText(mqProperties.getNameServer(), "[rocketmq.nameServer] must not be null");
        Assert.hasText(mqProperties.getSerializer(), "[rocketmq.serialize] must not be null");

        this.mqProperties = mqProperties;
        init();
    }

    @SuppressWarnings("all")
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
    }

    synchronized protected void init() {
        // init MessageBuilder serializer type
        IObjectSerializer serializer = SerializerUtil.getSerializer(mqProperties.getSerializer());
        if (serializer == null) {
            throw new RocketmqParamException("[rocketmq.serialize] " + mqProperties.getSerializer() + " lawless");
        }

        MessageBuilder.setSerializer(serializer);
        log.info("init MessageBuilder serialize success, serialize type={}", mqProperties.getSerializer());

        // init MessageBuilder topicPrefix
        if (StringUtils.isNotBlank(mqProperties.getTopicPrefix())) {
            MessageBuilder.setTopicPrefix(mqProperties.getTopicPrefix().trim());
            log.info("init MessageBuilder topicPrefix success, topicPrefix={}", mqProperties.getTopicPrefix().trim());
        }

        // init MessageBuilder default properties
        try {
            MessageBuilder.getDefaultPropertyMap().putIfAbsent(MQDefaultProperties.mqFromIp, SystemUtil.localIp());
        } catch (UnknownHostException e) {
            throw new RocketmqParamException("unknown localhost ip!!!");
        }
    }

}
