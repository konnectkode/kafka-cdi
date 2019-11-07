package pt.konnectkode.kafka.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import pt.konnectkode.kafka.cdi.annotation.Producer;
import pt.konnectkode.kafka.beans.ForTopic;
import pt.konnectkode.kafka.beans.config.KafkaBaseConfig;
import pt.konnectkode.kafka.beans.mock.MessageReceiver;
import pt.konnectkode.kafka.beans.mock.MockProvider;
import pt.konnectkode.kafka.tests.JavaArchiveUtils;
import pt.konnectkode.kafka.tests.rule.KafkaClusterRule;

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.verify;
import static pt.konnectkode.kafka.beans.config.KafkaBaseConfig.*;

@RunWith(Arquillian.class)
public class KafkaProducerTest {

    @Inject
    @Producer
    private KafkaProducer<Integer, String> kafkaProducer;

    @ClassRule
    public static KafkaClusterRule kafkaCluster = new KafkaClusterRule(KAFKA_BOOTSTRAP_PORT);

    @Deployment
    public static JavaArchive deployment() {

        return JavaArchiveUtils.createFrameworkDeployment()
                .addPackage(KafkaProducerProducer.class.getPackage())
                .addPackage(MockProvider.class.getPackage())
                .addClass(KafkaBaseConfig.class);
    }

    @Test
    public void produceSimpleMessage(@ForTopic(SIMPLE_PRODUCER_TOPIC_NAME) MessageReceiver messageReceiver) throws Exception {
        final String message = "This is only a test";

        Thread.sleep(1_000);

        kafkaProducer.send(new ProducerRecord<>(SIMPLE_PRODUCER_TOPIC_NAME, message));

        Thread.sleep(2_000);

        verify(messageReceiver).ack(message);
    }

    @Test
    public void produceMessageWithHeader(@ForTopic(EXTENDED_PRODUCER_TOPIC_NAME) MessageReceiver messageReceiver) throws Exception {
        final String message = "This is only a test";
        final int key = 1_001;
        final String headerOneKey = "Header1";
        final String headerOne = "HeaderOne";
        final String headerTwoKey = "Header2";
        final String headerTwo = "HeaderTwo";

        Thread.sleep(1_000);

        List<Header> headers = Arrays.asList(
                new RecordHeader(headerOneKey, headerOne.getBytes(StandardCharsets.UTF_8)),
                new RecordHeader(headerTwoKey, headerTwo.getBytes(StandardCharsets.UTF_8))
        );

        RecordHeaders recordHeaders = new RecordHeaders(headers);

        kafkaProducer.send(new ProducerRecord<>(EXTENDED_PRODUCER_TOPIC_NAME, null, key, message, recordHeaders));

        Thread.sleep(2_000);

        verify(messageReceiver).ack(key, message, recordHeaders);
    }

}
