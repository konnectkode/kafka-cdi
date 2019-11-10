package com.konnectkode.kafka.beans.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import com.konnectkode.kafka.cdi.annotation.KafkaConfig;

import java.util.Properties;

@KafkaConfig
public class TransactionalKafkaConfig extends KafkaBaseConfig {

    @Override
    public Properties producerConfig() {
        Properties properties = super.producerConfig();
        properties.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "transactional.id");

        return properties;
    }

    @Override
    public Properties consumerConfig() {
        Properties properties = super.consumerConfig();
        properties.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");

        return properties;
    }

}
