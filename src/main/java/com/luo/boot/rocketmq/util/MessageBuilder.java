package com.luo.boot.rocketmq.util;

import com.luo.boot.rocketmq.enums.DelayTimeLevel;
import com.luo.boot.rocketmq.exception.RocketmqParamException;
import com.luo.boot.rocketmq.serializer.FastJsonSerializer;
import com.luo.boot.rocketmq.serializer.IObjectSerializer;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.common.message.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Rocketmq message builder
 *
 * @author xiangnan
 * date 2018/8/3 17:42
 */
@Data
@Slf4j
public class MessageBuilder {

    @Setter
    @Getter
    private static IObjectSerializer serializer = new FastJsonSerializer();

    @Setter
    @Getter
    private static String topicPrefix;

    @Getter
    private static Map<String, String> defaultPropertyMap = new HashMap<>();

    private String topic;
    private String tag;
    private String key;
    private Object data;
    private Integer delayTimeLevel;

    private Map<String, String> userPropertyMap = new HashMap<>();

    private MessageBuilder() {
    }

    public static MessageBuilder of(Object data) {
        MessageBuilder builder = new MessageBuilder();
        builder.setData(data);
        return builder;
    }

    public MessageBuilder topic(String topic) {
        this.topic = topic;
        return this;
    }

    public MessageBuilder tag(String tag) {
        this.tag = tag;
        return this;
    }

    public MessageBuilder key(String key) {
        this.key = key;
        return this;
    }

    public MessageBuilder delayTimeLevel(DelayTimeLevel delayTimeLevel) {
        this.delayTimeLevel = delayTimeLevel.getLevel();
        return this;
    }

    public MessageBuilder putUserProperty(String key, String value) {
        if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
            userPropertyMap.put(key, value);
        }
        return this;
    }

    public Message build() {
        if (StringUtils.isBlank(topic)) {
            throw new RocketmqParamException("no topic defined to send this message");
        }
        if (Objects.isNull(data)) {
            throw new RocketmqParamException("this message is null");
        }

        Message message = new Message(StringUtils.isBlank(topicPrefix) ? topic : topicPrefix + topic,
                serializer.serialize(data));
        if (StringUtils.isNotEmpty(tag)) {
            message.setTags(tag.trim());
        }
        if (StringUtils.isNotEmpty(key)) {
            message.setKeys(key);
        }
        if (Objects.nonNull(delayTimeLevel)) {
            message.setDelayTimeLevel(delayTimeLevel);
        }

        defaultPropertyMap.forEach((key, value) -> {
            if (userPropertyMap.put(key, value) != null) {
                log.warn("notice: you have put a rocketmq's property, key={}, value={}", key, value);
            }
        });

        userPropertyMap.forEach((key, value) -> {
            if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
                message.putUserProperty(key, value);
            }
        });

        return message;
    }

}
