package com.luo.boot.rocketmq.serializer;

import java.lang.reflect.Type;

/**
 * @author xiangnan
 * date 2018/8/6 14:59
 */
public class StringSerializer implements IObjectSerializer {

    public static final String qualifier = "string";

    @Override
    public <T> byte[] serialize(T obj) {
        return stringToByte(obj.toString());
    }

    @SuppressWarnings("all")
    @Override
    public <T> T deserialize(byte[] data, Type type) {
        return (T) byteToString(data);
    }
}
