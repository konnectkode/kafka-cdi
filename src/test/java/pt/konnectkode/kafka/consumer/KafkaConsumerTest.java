package pt.konnectkode.kafka.consumer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.apache.kafka.common.serialization.Serdes;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.konnectkode.kafka.beans.ForTopic;
import pt.konnectkode.kafka.beans.config.KafkaBaseConfig;
import pt.konnectkode.kafka.beans.mock.MessageReceiver;
import pt.konnectkode.kafka.beans.mock.MockProvider;
import pt.konnectkode.kafka.tests.JavaArchiveUtils;
import pt.konnectkode.kafka.tests.rule.KafkaClusterRule;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import static pt.konnectkode.kafka.beans.config.KafkaBaseConfig.*;

@RunWith(Arquillian.class)
public class KafkaConsumerTest {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaConsumerTest.class);

    private static final byte[] EXTENDED_PRODUCER_HEADER_VALUE = "header.value".getBytes(StandardCharsets.UTF_8);
    private static final String EXTENDED_PRODUCER_HEADER_KEY = "header.key";
    private static final String EXTENDED_PRODUCER_VALUE = "This is only a second test";
    private static final int EXTENDED_PRODUCER_KEY = 42;

    @ClassRule
    public static KafkaClusterRule kafkaCluster = new KafkaClusterRule(KAFKA_BOOTSTRAP_PORT);

    @Deployment
    public static JavaArchive createDeployment() {
        return JavaArchiveUtils.createFrameworkDeployment()
                .addPackage(MockProvider.class.getPackage())
                .addClass(KafkaBaseConfig.class);
    }

    @Test
    public void consumeMessage(@ForTopic(SIMPLE_PRODUCER_TOPIC_NAME) MessageReceiver receiver) throws Exception {
        final String message = "Some Value";

        Thread.sleep(1_000);

        Properties properties = new Properties();

        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, new Serdes.StringSerde().serializer().getClass());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, new Serdes.StringSerde().serializer().getClass());

        KafkaProducer<String, String> producer = kafkaCluster.createKafkaProducer(properties);

        producer.send(new ProducerRecord<>(SIMPLE_PRODUCER_TOPIC_NAME, message));

        Thread.sleep(2_000);

        Mockito.verify(receiver).ack(message);
    }

    @Test
    public void consumeMessageWithHeader(@ForTopic(EXTENDED_PRODUCER_TOPIC_NAME) MessageReceiver receiver) throws Exception {
        Thread.sleep(1_000);

        Properties properties = new Properties();

        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, new Serdes.IntegerSerde().serializer().getClass());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, new Serdes.StringSerde().serializer().getClass());

        KafkaProducer<Integer, String> producer = kafkaCluster.createKafkaProducer(properties);

        List<Header> headersList = Collections.singletonList(new RecordHeader(EXTENDED_PRODUCER_HEADER_KEY, EXTENDED_PRODUCER_HEADER_VALUE));
        RecordHeaders expectedHeaders = new RecordHeaders(headersList);

        producer.send(new ProducerRecord<>(EXTENDED_PRODUCER_TOPIC_NAME,null, null, EXTENDED_PRODUCER_KEY,
                EXTENDED_PRODUCER_VALUE, expectedHeaders));

        Thread.sleep(1_000);

        Mockito.verify(receiver).ack(EXTENDED_PRODUCER_KEY, EXTENDED_PRODUCER_VALUE, expectedHeaders);
    }

}
