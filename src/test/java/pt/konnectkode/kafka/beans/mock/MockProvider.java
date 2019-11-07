package pt.konnectkode.kafka.beans.mock;

import org.mockito.Mockito;
import pt.konnectkode.kafka.beans.ForTopic;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import static pt.konnectkode.kafka.beans.config.KafkaBaseConfig.EXTENDED_PRODUCER_TOPIC_NAME;
import static pt.konnectkode.kafka.beans.config.KafkaBaseConfig.SIMPLE_PRODUCER_TOPIC_NAME;

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
