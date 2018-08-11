package com.luo.boot.rocketmq.annotation;

import com.luo.boot.rocketmq.util.RocketMQConst;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * consumer instance
 *
 * @author xiangnan
 * date 2018/8/6 13:24
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface RocketMQConsumer {

    String consumerGroup();

    String topic();

    /**
     * message mode : CLUSTERING or BROADCASTING
     */
    String messageMode() default RocketMQConst.MESSAGE_MODE_CLUSTERING;

    /**
     * consume mode : CONCURRENTLY or CONSUME_MODE_ORDERLY
     */
    String consumeMode() default RocketMQConst.CONSUME_MODE_CONCURRENTLY;

    String[] tag() default {"*"};

    /**
     * if has many consumer, instanceName must not same
     */
    String instanceName() default "";

    String[] serializer() default {""};

}
