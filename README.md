# Kafka-CDI - A extension for Apache Kafka

[![CircleCI](https://img.shields.io/circleci/build/github/konnectkode/kafka-cdi?style=for-the-badge)](https://circleci.com/gh/konnectkode/kafka-cdi)
[![Codecov](https://img.shields.io/codecov/c/github/konnectkode/kafka-cdi?style=for-the-badge)](https://codecov.io/gh/konnectkode/kafka-cdi)
![Maven Central](https://img.shields.io/maven-central/v/com.konnectkode/kafka-cdi?style=for-the-badge)
[![GitHub](https://img.shields.io/github/license/konnectkode/kafka-cdi?style=for-the-badge)]((https://www.apache.org/licenses/LICENSE-2.0))

## Getting started

The config is done by implementing the interface KafkaConfigProperties with qualifier @KafkaConfig. 
The interface is composed by three methods `commonClientConfigs()`, `producerConfigs()` and `consumerConfigs()`, 
each methods should return only the specific Kafka configuration `org.apache.kafka.clients.CommonClientConfigs`, 
`org.apache.kafka.clients.producer.ProducerConfig` and `org.apache.kafka.clients.consumer.ConsumerConfig` respectively.

```java
@KafkaConfig
public class KafkaConfigPropertiesImpl implements KafkaConfigProperties {
    
    @Override
    public Properties commonClientConfigs() {
        Properties properties = new Properties();
        properties.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(CommonClientConfigs.CLIENT_ID_CONFIG, "client.id");

        return properties;
    }
    
    
    @Override
    public Properties producerConfig() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "transactional.id");
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        properties.put(ProducerConfig.TRANSACTION_TIMEOUT_CONFIG, Long.MAX_VALUE);
        
        return properties;
    }

    @Override
    public Properties consumerProperties() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "group.id");
        properties.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
    
        return properties;
    }

}
```

## Creating a Kafka Consumer

The API provides the `@Consumer` annotation to register Kafka Consumers which internally will be used to create a vanilla `KafkaConsumer`.

```java
public class MyConsumer {

    private final Logger LOG = LoggerFactory.getLogger(MyConsumer.class);

    @Consumer(topics = {"topic.one", "topic.two"}, groupId = "my-consumer-group")
    public void consume(@Value String payload) {
        LOG.info("Received message: {}", payload);
    }
}
```

Receive additional informations is also possible using `@Key` and `@Header` annotations to inject the record key and record header.

```java
import org.apache.kafka.common.header.Headers;

public class MyConsumer {

    private final Logger LOG = LoggerFactory.getLogger(MyConsumer.class);

    @Consumer(topics = {"topic.one", "topic.two"}, groupId = "my-consumer-group")
    public void consume(@Value String payload, @Key String key, @Headers Headers headers) {
        LOG.info("Received message: {}, key: {}, header: {}", payload, key, headers);
    }
}
```

The `groupId` is optional if the `KafkaConfigProperties.consumerConfig()` method returns the `ConsumerConfig.GROUP_ID_CONFIG` property. 
If both are present, `@Consumer(groupId = "any")` will be used.

#### Deserializer

The Consumer default deserializer is `org.apache.kafka.common.serialization.StringDeserializer` for the key and value, 
both can be changed in the `@Consumer` annotation by any class that implements the `org.apache.kafka.common.serialization.Deserializer` interface.

```java
public class MyConsumer {

    private final Logger LOG = LoggerFactory.getLogger(MyConsumer.class);

    @Consumer(topics = {"topic.one", "topic.two"}, groupId = "my-consumer-group", keyDeserializer = IntegerDeserializer.class)
    public void consume(@Value String payload, @Key Integer key) {
        LOG.info("Received message: {}", payload);
    }
}
```

## Injecting a KafkaProducer

For better flexibility and compatibility, the API provides a properly configured vanilla KafkaProducer, without wrappers or decorators. 
However, if the `ProducerConfig.TRANSACTIONAL_ID_CONFIG` property is returned by the `KafkaConfigProperties.producerConfig()` method, 
the API will be responsible for calling the `KafkaProducer.initTransactions()` method before any other method.

```java
public class MyProducer {

    @Inject
    @Producer
    private KafkaProducer<Integer, String> producer;

    public void myProducerMethod(String message) {
        producer.send(new ProducerRecord<>("kafka.topic", message));
    }
}
```

KafkaProducer is thread safe, so the producer is declared as `@ApplicationScoped`.