package pt.konnectkode.kafka.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import pt.konnectkode.kafka.cdi.annotation.Producer;
import pt.konnectkode.kafka.config.KafkaPropertiesResolver;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.time.Duration;
import java.util.Properties;

@ApplicationScoped
class KafkaProducerProducer {

    @Inject
    private KafkaPropertiesResolver kafkaPropertiesResolver;

    private KafkaProducer kafkaProducer;

    @Produces
    @Producer
    @SuppressWarnings("unchecked")
    public <K, V> KafkaProducer<K, V> kafkaProducer() {
        Properties properties = kafkaPropertiesResolver.producerProperties();

        this.kafkaProducer = new KafkaProducer<>(properties);

        if (properties.contains(ProducerConfig.TRANSACTIONAL_ID_CONFIG)) {
            kafkaProducer.initTransactions();
        }

        return this.kafkaProducer;
    }

    @PreDestroy
    public void beforeDestroy() {
        if (this.kafkaProducer != null) {
            this.kafkaProducer.close(Duration.ofSeconds(2));
        }
    }

}
