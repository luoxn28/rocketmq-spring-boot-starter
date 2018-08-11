package com.luo.boot.rocketmq.config;

import com.luo.boot.rocketmq.annotation.RocketMQConsumer;
import com.luo.boot.rocketmq.core.AbstractMQPushConsumer;
import com.luo.boot.rocketmq.exception.RocketmqParamException;
import com.luo.boot.rocketmq.serializer.IObjectSerializer;
import com.luo.boot.rocketmq.util.RocketMQConst;
import com.luo.boot.rocketmq.util.SerializerUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author xiangnan
 * date 2018/8/6 13:19
 */
@Slf4j
@Configuration
@ConditionalOnBean(value = BaseAutoConfiguration.class)
@AutoConfigureAfter(value = {BaseAutoConfiguration.class})
public class RocketMQConsumerAutoConfiguration extends BaseAutoConfiguration {

    @PostConstruct
    public void postConstruct() throws Exception {
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(RocketMQConsumer.class);
        if (CollectionUtils.isEmpty(beans)) {
            log.info("not found any RocketMQConsumer, will not init RocketMQConsumer");
            return;
        }

        // init & start consumer instance
        for (String name : beans.keySet()) {
            publishConsumer(name, beans.get(name));
        }
    }

    private void publishConsumer(String beanName, Object bean) throws Exception {
        RocketMQConsumer mqConsumer = applicationContext.findAnnotationOnBean(beanName, RocketMQConsumer.class);
        Assert.notNull(mqConsumer, "consumer must not null");

        if (!AbstractMQPushConsumer.class.isAssignableFrom(bean.getClass())) {
            throw new RocketmqParamException("bean " + bean + " isn't AbstractMQPushConsumer type");
        }
        AbstractMQPushConsumer pushBean = (AbstractMQPushConsumer) bean;

        Type type;
        try {
            // 获取泛型的实际类型
            type = ((ParameterizedType) bean.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            pushBean.setType(type);
            log.info("consumer instance name {} message event type {}", beanName, type);
        } catch (Exception e) {
            throw new RocketmqParamException("AbstractMQPushConsumer type must has real type", e);
        }

        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(mqConsumer.consumerGroup());
        consumer.setNamesrvAddr(mqProperties.getNameServer());
        consumer.setVipChannelEnabled(mqProperties.isVipChannelEnabled());
        consumer.setMessageModel(MessageModel.valueOf(mqConsumer.messageMode().toUpperCase()));
        consumer.subscribe(StringUtils.isBlank(mqProperties.getTopicPrefix()) ?
                        mqConsumer.topic() : mqProperties.getTopicPrefix() + mqConsumer.topic(),
                StringUtils.join(mqConsumer.tag(), "||"));

        // 如果一个JVM程序中有多个consumer实例，则consumer实例的InstanceName必须配置，默认InstanceName格式 ip@pid
        // 每个consumer实例的InstanceName必须不同，否则消息无法正常消费
        if (StringUtils.isNotBlank(mqConsumer.instanceName())) {
            consumer.setInstanceName(mqConsumer.instanceName());
        }

        if (StringUtils.equals(mqConsumer.consumeMode(), RocketMQConst.CONSUME_MODE_CONCURRENTLY)) {
            consumer.registerMessageListener((MessageListenerConcurrently) pushBean::consumeMessage);
        } else if (StringUtils.equals(mqConsumer.consumeMode(), RocketMQConst.CONSUME_MODE_ORDERLY)) {
            consumer.registerMessageListener((MessageListenerOrderly) pushBean::consumeMessage);
        } else {
            throw new RocketmqParamException("unknown consume mode! only support CONCURRENTLY and ORDERLY");
        }

        String[] serializers = mqConsumer.serializer();
        Assert.isTrue(serializers.length >= 1, "unknown consumer serializer");
        if (!StringUtils.equals(serializers[0], "")) {
            for (String decode : serializers) {
                IObjectSerializer serializer = SerializerUtil.getSerializer(decode);
                if (serializer == null) {
                    throw new RocketmqParamException("annotation [consumer.serialize] " +
                            mqProperties.getSerializer() + " lawless");
                }

                // consumer自定义(反)序列化器
                pushBean.addSerializer(serializer);
            }
        }

        // start
        pushBean.setConsumer(consumer);
        consumer.start();
        log.info("init & start consumer instance {} success, with annotation {}", bean, mqConsumer);
    }

    synchronized protected void init() {
    }

}
