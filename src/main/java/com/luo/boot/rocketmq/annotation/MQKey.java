package com.luo.boot.rocketmq.annotation;

import java.lang.annotation.*;

/**
 * message key, format: "prefix + field"
 *
 * @author xiangnan
 * date 2018/8/3 17:44
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MQKey {

    String prefix();

}

