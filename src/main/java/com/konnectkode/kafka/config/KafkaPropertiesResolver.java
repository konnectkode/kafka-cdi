package com.konnectkode.kafka.config;

import com.konnectkode.kafka.cdi.annotation.Consumer;

import java.util.Properties;

public interface KafkaPropertiesResolver {

    Properties consumerProperties(Consumer consumerAnnotation);

    Properties producerProperties();

}
