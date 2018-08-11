package com.luo.boot.rocketmq.serializer;

import com.alibaba.fastjson.JSON;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * @author xiangnan
 * date 2018/8/3 17:54
 */
public class FastJsonSerializer implements IObjectSerializer {

    public static final String qualifier = "fastjson";

    @Override
    public <T> byte[] serialize(T obj) {
        return Objects.nonNull(obj) ? stringToByte(JSON.toJSONString(obj)) : null;
    }

    @Override
    public <T> T deserialize(byte[] data, Type type) {
        return Objects.nonNull(data) ? JSON.parseObject(byteToString(data), type) : null;
    }

}
