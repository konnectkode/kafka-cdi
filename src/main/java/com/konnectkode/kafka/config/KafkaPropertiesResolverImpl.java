package com.konnectkode.kafka.config;

import com.konnectkode.kafka.cdi.annotation.Consumer;
import com.konnectkode.kafka.cdi.annotation.KafkaConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Properties;

@ApplicationScoped
public class KafkaPropertiesResolverImpl implements KafkaPropertiesResolver {

    private KafkaConfigProperties kafkaConfigProperties;

    @Inject
    public KafkaPropertiesResolverImpl(@KafkaConfig KafkaConfigProperties kafkaConfigProperties) {
        this.kafkaConfigProperties = kafkaConfigProperties;
    }

    @Override
    public Properties consumerProperties(Consumer consumerAnnotation) {
        Properties newProperties = new Properties();
        newProperties.putAll(kafkaConfigProperties.commonClientConfigs());
        newProperties.putAll(kafkaConfigProperties.consumerConfig());

        newProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, consumerAnnotation.offset());
        newProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, consumerAnnotation.valueDeserializer());
        newProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, consumerAnnotation.keyDeserializer());

        if (!"".equals(consumerAnnotation.groupId())) {
            newProperties.put(ConsumerConfig.GROUP_ID_CONFIG, consumerAnnotation.groupId());
        }

        return newProperties;
    }

    @Override
    public Properties producerProperties() {
        Properties properties = new Properties();
        properties.putAll(kafkaConfigProperties.commonClientConfigs());
        properties.putAll(kafkaConfigProperties.producerConfig());

        return properties;
    }

}
