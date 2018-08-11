## 简介

`rocketmq-spring-boot-starter`是一个rocketmq的工具包，目标是统一使用接口、简化配置，开箱即用。

### RELEASE NOTES

|版本|说明|
|---|---|
|1.0.0-SNAPSHOT|初始化|

### 环境要求

|环境|要求|
|---|---|
|JRE|8+|
|SPRING-BOOT|2.0.3.RELEASE+|


## 使用说明

### 引入依赖

```xml
<dependency>
    <groupId>com.luo.boot</groupId>
    <artifactId>rocketmq-spring-boot-starter</artifactId>
    <version>${last_version}</version>
</dependency>
```

### 配置

`application.yml`（推荐该方式）

```yaml
spring:
  rocketmq:
    # namesrv配置，多个nameserv使用';'分隔，比如 10.10.9.2:9876;172.21.10.111:9876
    name-server: 10.10.9.2:9876
    # 如果rocketmq版本低于4.0，这里必须设置为false，该参数用于rocketmq兼容用，默认为true
    vip-channel-enabled: false
    # rocketmq默认序列化器，默认为fastjson
    # 目前支持fastjson、java、string方式，string方式只作用开发测试使用（所有类型当做String来用），禁止在正式项目使用
    serializer: fastjson
    # topic-prefix为topic自定义前缀，只在开发/测试环境中作隔离使用，禁止在正式环境中配置
    # topic-prefix test-
    #producer:
      ## group-name: groupName  # 默认取spring.application.name，无需配置
```

### producer

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message implements Serializable {
    private int id;
    private String name;
}

public class Producer {
    @Resource
    private DefaultMQProducer mqProducer;
    
    @Test
    public void testBean() {
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
}
```

生成消息对象推荐使用`MessageBuilder`方式，可设置topic、tag、key、delayLevel、自定义property，并且`build`时会根据序列化器进行序列化工作。
```java
MessageBuilder.of(new Object())
        .topic("test-topic")
        .tag("test-tag")
        .key("test-key")
        .delayTimeLevel(DelayTimeLevel.SECOND_1)
        .putUserProperty("timeKey", "xxx")
        .build()
```

*消息build时，默认会加上生产者的IP信息，对应的property是("mqFromIp", "xx.xx.xx.xx)，消费端可直接使用以下代码获取生产者IP信息：*
```java
@RocketMQConsumer(consumerGroup = "201806-consumerGroup", topic = "test-topic")
public class Consumer extends AbstractMQPushConsumer<Message> {

    @Override
    public boolean dealMessage(Message message, MessageExt messageExt) {
        System.out.println("consumer: " + message);
        
        // 获取消息生产者IP
        System.out.println(messageExt.getProperty(MQDefaultProperties.mqFromIp));
        return true;
    }
}
```
*注意，消息默认的property禁止被覆盖，否则不生效且会打印warn日志。消息默认的property请查看`MQDefaultProperties`接口。*

#### 发送延时消息
```java
@Resource
private DefaultMQProducer mqProducer;

Message message = new Message(1, "hello world");
SendResult result = mqProducer.send(MessageBuilder.of(message)
        .topic("test-topic")
        // 延时级别
        .delayTimeLevel(DelayTimeLevel.SECOND_1)
        .build());
System.out.println(result);
```

延时级别枚举：
```java
public enum DelayTimeLevel {
    /**
     * 1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
     */
    SECOND_1(1),
    SECOND_5(2),
    SECOND_10(3),
    SECOND_30(4),
    MINUTE_1(5),
    MINUTE_2(6),
    MINUTE_3(7),
    MINUTE_4(8),
    MINUTE_5(9),
    MINUTE_6(10),
    MINUTE_7(11),
    MINUTE_8(12),
    MINUTE_9(13),
    MINUTE_10(14),
    MINUTE_20(15),
    MINUTE_30(16),
    HOUR_1(17),
    HOUR_2(18)
}
```

#### 发送顺序消息

```java
@Resource
private DefaultMQProducer mqProducer;

Message message = new Message(1, "hello world");
SendResult result = mqProducer.send(MessageBuilder.of(message)
        .topic("test-topic")
        .build(), new MessageQueueSelector() {
    @Override
    public MessageQueue select(List<MessageQueue> list, org.apache.rocketmq.common.message.Message message, Object o) {
        // o is 110
        // assert list.size >= 2
        // 选择0号queue或者1号queue发送消息
        return list.get(((Integer) o) == 110 ? 1 : 0);
    }
}, 110);
System.out.println(result);
```

### consumer

```java
@RocketMQConsumer(consumerGroup = "201806-consumerGroup", topic = "test-topic")
public class Consumer extends AbstractMQPushConsumer<Message> {

    @Override
    public boolean dealMessage(Message message, MessageExt messageExt) {
        System.out.println("consumer: " + message);
        
        System.out.println(messageExt.getUserProperty("timeKey"));
        return true;
    }
}
```

定义消息消费对象时必须继承`AbstractMQPushConsumer`、指定泛型类型、使用`@RocketMQConsumer`注解。
`@RocketMQConsumer`注解入参`consumerGroup`和`topic`必选。
```java
public @interface RocketMQConsumer {
    String consumerGroup();
    String topic();

    /**
     * 消息模式，可选值为: clustering or broadcasting
     * 默认集群模式，clustering
     */
    String messageMode() default "clustering";

    /**
     * 消费模式，可选值为: concurrently or orderly
     * 默认并发消费模式
     */
    String consumeMode() default "concurrently";

    String[] tag() default {"*"};

    /**
     * 当前consumerInstance实例的名字，如果一个JVM进程内有多个consumerInstance，名字必须不同
     */
    String instanceName() default "";

    /**
     * 默认使用luo.rocketmq.serialize对应的序列化器
     * 如果自定义了多个序列化器，则反序列化时按照顺序依次进行反序列化，只要有一个反序列化成功就返回
     * 这里只是为了保证消费端兼容多种消息数据格式用的
     */
    String[] serializer() default {""};

}
```