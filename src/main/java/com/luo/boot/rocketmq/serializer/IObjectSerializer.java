package com.luo.boot.rocketmq.serializer;

import java.lang.reflect.Type;
import java.nio.charset.Charset;

/**
 * @author xiangnan
 * date 2018/8/3 17:49
 */
public interface IObjectSerializer {

    Charset defaultCharset = Charset.forName("utf-8");

    <T> byte[] serialize(T obj);

    <T> T deserialize(byte[] data, Type type);

    default byte[] stringToByte(String str) {
        return str.getBytes(defaultCharset);
    }

    default String byteToString(byte[] data) {
        return new String(data, defaultCharset);
    }

}
