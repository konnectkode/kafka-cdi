package pt.konnectkode.kafka.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.LongDeserializer;
import static org.assertj.core.api.Assertions.*;
import org.junit.Test;
import static org.mockito.Mockito.*;
import pt.konnectkode.kafka.beans.config.KafkaBaseConfig;
import pt.konnectkode.kafka.cdi.annotation.Consumer;

import java.util.Properties;

public class KafkaPropertiesResolverTest {

    @Test
    public void consumerAnnotationProperties() {
        final String groupId = "KafkaConfigTest.groupId";
        final String offset = "SomeOffset";
        Class<? extends Deserializer> keyDeserializer = IntegerDeserializer.class;
        Class<? extends Deserializer> valueDeserializer = LongDeserializer.class;

        KafkaBaseConfig kafkaBaseConfig = new KafkaBaseConfig();
        KafkaPropertiesResolverImpl kafkaPropertiesResolver = new KafkaPropertiesResolverImpl(kafkaBaseConfig);

        Consumer consumer = mock(Consumer.class);

        when(consumer.groupId()).thenReturn(groupId);
        when(consumer.offset()).thenReturn(offset);
        doReturn(keyDeserializer).when(consumer).keyDeserializer();
        doReturn(valueDeserializer).when(consumer).valueDeserializer();

        Properties properties = kafkaPropertiesResolver.consumerProperties(consumer);

        assertThat(properties.get(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG)).isEqualTo(keyDeserializer);
        assertThat(properties.get(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG)).isEqualTo(valueDeserializer);
        assertThat(properties.get(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG)).isEqualTo(offset);
        assertThat(properties.get(ConsumerConfig.GROUP_ID_CONFIG)).isEqualTo(groupId);
    }

}
