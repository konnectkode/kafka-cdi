package pt.konnectkode.kafka.config;

import pt.konnectkode.kafka.cdi.annotation.Consumer;

import java.util.Properties;

public interface KafkaPropertiesResolver {

    Properties consumerProperties(Consumer consumerAnnotation);

    Properties producerProperties();

}
