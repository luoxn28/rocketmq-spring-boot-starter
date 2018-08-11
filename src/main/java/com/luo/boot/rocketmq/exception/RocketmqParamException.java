package com.luo.boot.rocketmq.exception;

/**
 * @author xiangnan
 * date 2018/8/3 18:19
 */
public class RocketmqParamException extends RuntimeException {

    public RocketmqParamException(String message) {
        super(message);
    }

    public RocketmqParamException(String message, Throwable e) {
        super(message, e);
    }

}
