package com.konnectkode.kafka.cdi.annotation;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.lang.annotation.*;
import java.time.Duration;


/**
 * Identifies the method to register as a KafkaConsumer and consume kafka cluster records.
 */
@Inherited
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Consumer {

    /**
     * <p>
     *     The list of topics to subscribe to
     * </p>
     */
    String[] topics();

    /**
     * <p>
     *     A unique string that identifies the consumer group this consumer belongs to.
     * </p>
     */
    String groupId() default "";

    /**
     * What to do when there is no initial offset in Kafka or if the current offset does not exist any more on the server (e.g. because that data has been deleted):
     *
     * <ul>
     *     <li>earliest: automatically reset the offset to the earliest offset</li>
     *     <li>latest: automatically reset the offset to the latest offset</li>
     *     <li>none: throw exception to the consumer if no previous offset is found for the consumer's group</li>
     *     <li>anything else: throw exception to the consumer.</li>
     * </ul>
     */
    String offset() default "latest";

    /**
     * Deserializer class for key that implements the <code>org.apache.kafka.common.serialization.Deserializer</code> interface.
     */
    Class<? extends Deserializer> keyDeserializer() default StringDeserializer.class;

    /**
     * Deserializer class for value that implements the <code>org.apache.kafka.common.serialization.Deserializer</code> interface.
     */
    Class<? extends Deserializer> valueDeserializer() default StringDeserializer.class;

    /**
     * <p>
     * Specifies {@linkplain org.apache.kafka.clients.consumer.KafkaConsumer#close(Duration)} timeout in milliseconds.
     * </p>
     * <p>
     * The default the value is the same value of the KafkaConsumer.
     * </p>
     */
    long closeTimeout() default 30 * 1000;

}
