package com.luo.boot.rocketmq.test;

import com.luo.boot.rocketmq.serializer.FastJsonSerializer;
import com.luo.boot.rocketmq.serializer.IObjectSerializer;
import com.luo.boot.rocketmq.serializer.JavaSerializer;
import com.luo.boot.rocketmq.util.MessageBuilder;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author xiangnan
 * date 2018/8/3 16:55
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class ProducerTest {

    @Resource
    private DefaultMQProducer mqProducer;

    @Test
    public void testBean()  {
        assertNotNull(mqProducer);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    Message message = new Message(1, "hello world");
                    SendResult result = mqProducer.send(MessageBuilder.of(message).topic("test-topic").build());
                    assertTrue(result.getSendStatus() == SendStatus.SEND_OK);
                    System.out.println("producer send ok");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, 0, 5000);

        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(28));
    }

    @Test
    public void testBasicType() {
        testBasicType(new FastJsonSerializer());

        testBasicType(new JavaSerializer());
    }

    private void testBasicType(IObjectSerializer serializer) {

        byte b = 1;
        short s = 1;
        int i = 1;
        long l = 1L;
        assertTrue(b == (byte) serializer.deserialize(serializer.serialize(b), byte.class));
        assertTrue(s == (short) serializer.deserialize(serializer.serialize(s), short.class));
        assertTrue(i == (int) serializer.deserialize(serializer.serialize(i), int.class));
        assertTrue(l == (long) serializer.deserialize(serializer.serialize(l), long.class));

        float f = 1.1f;
        double d = 1.2;
        assertTrue(f == (float) serializer.deserialize(serializer.serialize(f), float.class));
        assertTrue(d == (double) serializer.deserialize(serializer.serialize(d), double.class));

        boolean bo = true;
        char c = 'a';
        assertTrue(bo == (boolean) serializer.deserialize(serializer.serialize(bo), boolean.class));
        assertTrue(c == (char) serializer.deserialize(serializer.serialize(c), char.class));

        String ss = "hello world";
        assertTrue(ss.equals(serializer.deserialize(serializer.serialize(ss), String.class)));
    }

}
