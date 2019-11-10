package com.konnectkode.kafka.beans;

import com.konnectkode.kafka.cdi.annotation.Consumer;
import com.konnectkode.kafka.cdi.annotation.Header;
import com.konnectkode.kafka.cdi.annotation.Key;
import com.konnectkode.kafka.cdi.annotation.Value;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.konnectkode.kafka.beans.mock.MessageReceiver;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import static com.konnectkode.kafka.beans.config.KafkaBaseConfig.EXTENDED_PRODUCER_TOPIC_NAME;
import static com.konnectkode.kafka.beans.config.KafkaBaseConfig.SIMPLE_PRODUCER_TOPIC_NAME;

public class KafkaMessageListener {

    private final Logger logger = LoggerFactory.getLogger(KafkaMessageListener.class);

    @Inject
    @ForTopic(SIMPLE_PRODUCER_TOPIC_NAME)
    private MessageReceiver simpleTopicReceiver;

    @Inject
    @ForTopic(EXTENDED_PRODUCER_TOPIC_NAME)
    private MessageReceiver extendedTopicReceiver;

    @PostConstruct
    public void setup() {
        logger.info("Bean is ready!");
    }

    @Consumer(topics = SIMPLE_PRODUCER_TOPIC_NAME, groupId = "groupId", closeTimeout = 1)
    public void onMessage(final @Value String simpleValue) {
        logger.info("Got message: {} ", simpleValue);
        simpleTopicReceiver.ack(simpleValue);
    }

    @Consumer(topics = EXTENDED_PRODUCER_TOPIC_NAME, groupId = "groupId", keyDeserializer = IntegerDeserializer.class, closeTimeout = 1)
    public void onMessage(final @Key Integer key, final @Value String simpleValue, final @Header org.apache.kafka.common.header.Headers headers) {
        logger.info("Got message: {}||{}||{} ",key, simpleValue, headers);
        extendedTopicReceiver.ack(key, simpleValue, headers);
    }

}
