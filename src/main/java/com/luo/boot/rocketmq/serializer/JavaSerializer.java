package com.luo.boot.rocketmq.serializer;

import org.springframework.util.SerializationUtils;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * @author xiangnan
 * date 2018/8/6 10:34
 */
public class JavaSerializer implements IObjectSerializer {

    public static final String qualifier = "java";

    @Override
    public <T> byte[] serialize(T obj) {
        return Objects.nonNull(obj) ? SerializationUtils.serialize(obj) : null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialize(byte[] data, Type type) {
        return Objects.nonNull(data) ? (T) SerializationUtils.deserialize(data) : null;
    }
}
