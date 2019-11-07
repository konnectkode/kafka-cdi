package pt.konnectkode.kafka.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import pt.konnectkode.kafka.cdi.annotation.Producer;
import pt.konnectkode.kafka.beans.ForTopic;
import pt.konnectkode.kafka.beans.mock.MessageReceiver;
import pt.konnectkode.kafka.beans.mock.MockProvider;
import pt.konnectkode.kafka.beans.config.KafkaBaseConfig;
import pt.konnectkode.kafka.beans.config.TransactionalKafkaConfig;
import pt.konnectkode.kafka.tests.JavaArchiveUtils;
import pt.konnectkode.kafka.tests.rule.KafkaClusterRule;

import javax.inject.Inject;

@RunWith(Arquillian.class)
public class KafkaTransactionProducerTest {

    @ClassRule
    public static KafkaClusterRule kafkaCluster = new KafkaClusterRule(TransactionalKafkaConfig.KAFKA_BOOTSTRAP_PORT);

    @Deployment
    public static JavaArchive deployment() {
        return JavaArchiveUtils.createFrameworkDeployment()
                .addPackage(KafkaProducerProducer.class.getPackage())
                .addPackage(MockProvider.class.getPackage())
                .addClass(TransactionalKafkaConfig.class);
    }

    @Inject
    @Producer
    private KafkaProducer<Integer, String> producer;

    @Test
    public void producerWithTransaction(@ForTopic(KafkaBaseConfig.SIMPLE_PRODUCER_TOPIC_NAME) MessageReceiver messageReceiver) throws Exception {
        Thread.sleep(1_000);
        producer.beginTransaction();
        producer.send(new ProducerRecord<>(TransactionalKafkaConfig.SIMPLE_PRODUCER_TOPIC_NAME, "Message"));
        producer.commitTransaction();

        Thread.sleep(2_000);

        Mockito.verify(messageReceiver).ack("Message");
    }

    @Test
    public void producerWithTransactionsAbortTransaction(@ForTopic(KafkaBaseConfig.SIMPLE_PRODUCER_TOPIC_NAME) MessageReceiver messageReceiver) throws Exception {
        Thread.sleep(1_000);

        producer.beginTransaction();
        producer.send(new ProducerRecord<>(TransactionalKafkaConfig.SIMPLE_PRODUCER_TOPIC_NAME, "Message"));
        producer.abortTransaction();

        Thread.sleep(2_000);

        Mockito.verify(messageReceiver, Mockito.never()).ack("Message");
    }

}
