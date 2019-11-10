package com.konnectkode.kafka.config;

import java.util.Properties;

public interface KafkaConfigProperties {

    Properties commonClientConfigs();

    default Properties producerConfig() {
        return new Properties();
    }

    default Properties consumerConfig() {
        return new Properties();
    }

}
