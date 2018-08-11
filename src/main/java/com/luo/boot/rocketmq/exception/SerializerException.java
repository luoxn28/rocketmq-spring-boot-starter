package com.luo.boot.rocketmq.exception;

/**
 * @author xiangnan
 * date 2018/8/6 11:06
 */
public class SerializerException extends RuntimeException {

    public SerializerException(String message) {
        super(message);
    }

    public SerializerException(String message, Throwable e) {
        super(message, e);
    }

}
