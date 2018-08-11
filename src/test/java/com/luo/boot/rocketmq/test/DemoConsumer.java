package com.luo.boot.rocketmq.test;

import com.luo.boot.rocketmq.annotation.RocketMQConsumer;
import com.luo.boot.rocketmq.core.AbstractMQPushConsumer;
import com.luo.boot.rocketmq.util.MQDefaultProperties;
import org.apache.rocketmq.common.message.MessageExt;

/**
 * @author xiangnan
 * date 2018/8/6 13:40
 */
@RocketMQConsumer(consumerGroup = "201806-consumerGroup", topic = "test-topic", serializer = {"fastjson", "java"})
public class DemoConsumer extends AbstractMQPushConsumer<Message> {

    @Override
    public boolean dealMessage(Message message, MessageExt messageExt) {
        System.out.println("consumer: " + message + ", from:" + messageExt.getProperty(MQDefaultProperties.mqFromIp));
        return true;
    }

}
