package com.luo.boot.rocketmq.util;

/**
 * @author xiangnan
 * date 2018/8/6 13:25
 */
public interface RocketMQConst {

    /**
     * message mode
     */
    String MESSAGE_MODE_CLUSTERING = "clustering";
    String MESSAGE_MODE_BROADCASTING = "broadcasting";

    /**
     * consume mode
     */
    String CONSUME_MODE_CONCURRENTLY = "concurrently";
    String CONSUME_MODE_ORDERLY = "orderly";
}
