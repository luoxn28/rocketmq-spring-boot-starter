package com.luo.boot.rocketmq.core;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;
import java.util.Objects;

/**
 * @author xiangnan
 * date 2018/8/6 14:04
 */
@Slf4j
public abstract class AbstractMQPushConsumer<T> extends AbstractMQConsumer<T> {

    @Getter
    @Setter
    private DefaultMQPushConsumer consumer;

    public abstract boolean dealMessage(T message, MessageExt messageExt);

    /**
     * consume message
     */
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list,
                                                 ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        try {
            for (MessageExt messageExt : list) {
                // parse (deserializer) message
                T message = parseMessage(messageExt.getBody());

                if (Objects.nonNull(message) && !dealMessage(message, messageExt)) {
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
            }
        } catch (Exception e) {
            log.error("consumeMessage error, messageExtList=" + list, e);
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }

        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

    public ConsumeOrderlyStatus consumeMessage(List<MessageExt> list,
                                               ConsumeOrderlyContext consumeOrderlyContext) {
        for (MessageExt messageExt : list) {
            // parse (deserializer) message
            T message = parseMessage(messageExt.getBody());

            if (Objects.nonNull(message) && !dealMessage(message, messageExt)) {
                return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
            }
        }

        return ConsumeOrderlyStatus.SUCCESS;
    }

}
