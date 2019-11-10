package com.konnectkode.kafka.beans.mock;

import com.konnectkode.kafka.beans.ForTopic;
import org.mockito.Mockito;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import static com.konnectkode.kafka.beans.config.KafkaBaseConfig.EXTENDED_PRODUCER_TOPIC_NAME;
import static com.konnectkode.kafka.beans.config.KafkaBaseConfig.SIMPLE_PRODUCER_TOPIC_NAME;

@ApplicationScoped
public class MockProvider {

    private MessageReceiver simpleTopicReceiver = Mockito.mock(MessageReceiver.class);
    private MessageReceiver extendedTopicReceiver = Mockito.mock(MessageReceiver.class);

    @Produces
    @ForTopic(SIMPLE_PRODUCER_TOPIC_NAME)
    public MessageReceiver simpleReceiver() {
        return simpleTopicReceiver;
    }

    @Produces
    @ForTopic(EXTENDED_PRODUCER_TOPIC_NAME)
    public MessageReceiver extendedReceiver() {
        return extendedTopicReceiver;
    }

}
