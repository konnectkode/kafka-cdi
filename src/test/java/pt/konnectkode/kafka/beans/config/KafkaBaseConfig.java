package pt.konnectkode.kafka.beans.config;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import pt.konnectkode.kafka.cdi.annotation.KafkaConfig;
import pt.konnectkode.kafka.config.KafkaConfigProperties;

import java.util.Properties;

@KafkaConfig
public class KafkaBaseConfig implements KafkaConfigProperties {

    public static final int KAFKA_BOOTSTRAP_PORT = 9098;
    public static final String SIMPLE_PRODUCER_TOPIC_NAME = "ServiceInjectionTest.SimpleProducer";
    public static final String EXTENDED_PRODUCER_TOPIC_NAME = "ServiceInjectionTest.ExtendedProducer";

    @Override
    public Properties commonClientConfigs() {
        Properties properties = new Properties();
        properties.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "localhost:" + KAFKA_BOOTSTRAP_PORT);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "group.id");

        return properties;
    }

    @Override
    public Properties producerConfig() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);

        return properties;
    }

}
