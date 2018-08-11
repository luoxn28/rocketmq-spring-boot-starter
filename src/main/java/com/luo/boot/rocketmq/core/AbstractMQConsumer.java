package com.luo.boot.rocketmq.core;

import com.luo.boot.rocketmq.serializer.IObjectSerializer;
import com.luo.boot.rocketmq.util.MessageBuilder;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author xiangnan
 * date 2018/8/6 14:03
 */
@Slf4j
public abstract class AbstractMQConsumer<T> {

    @Setter
    private Type type;

    /**
     * 自定义consumer的(反)序列化器，便于兼容多种数据类型
     */
    private List<IObjectSerializer> serializerList = new ArrayList<>();

    public void addSerializer(IObjectSerializer serializer) {
        serializerList.add(serializer);
    }

    protected T parseMessage(byte[] data) {
        if (Objects.isNull(data)) {
            return null;
        }

        for (int i = 0; i < serializerList.size(); i++) {
            try {
                return serializerList.get(i).deserialize(data, type);
            } catch (Exception e) {
                if (i == serializerList.size() - 1) {
                    throw e;
                }
                log.warn("parseMessage error, data={}, serializer={}",
                        serializerList.get(i).byteToString(data), serializerList.get(i).getClass().getSimpleName());
            }
        }

        return MessageBuilder.getSerializer().deserialize(data, type);
    }

}
