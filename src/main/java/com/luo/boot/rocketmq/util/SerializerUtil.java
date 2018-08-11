package com.luo.boot.rocketmq.util;

import com.luo.boot.rocketmq.serializer.FastJsonSerializer;
import com.luo.boot.rocketmq.serializer.IObjectSerializer;
import com.luo.boot.rocketmq.serializer.JavaSerializer;
import com.luo.boot.rocketmq.serializer.StringSerializer;
import org.apache.commons.lang3.StringUtils;

/**
 * @author xiangnan
 * date 2018/8/6 17:08
 */
public class SerializerUtil {

    public static IObjectSerializer getSerializer(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }

        IObjectSerializer serializer = null;
        switch (name) {
            case FastJsonSerializer.qualifier: {
                serializer = new FastJsonSerializer();
                break;
            }
            case JavaSerializer.qualifier: {
                serializer = new JavaSerializer();
                break;
            }
            case StringSerializer.qualifier: {
                serializer = new StringSerializer();
                break;
            }
            default: {
                break;
            }
        }

        return serializer;
    }

}
