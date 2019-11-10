/*
 * Copyright 2017 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.konnectkode.kafka.consumer;

import com.konnectkode.kafka.cdi.annotation.Consumer;
import com.konnectkode.kafka.consumer.executor.KafkaConsumerMethodParameterResolver;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.konnectkode.kafka.config.KafkaPropertiesResolver;
import com.konnectkode.kafka.consumer.executor.MethodExecutor;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class DelegationKafkaConsumer implements Delegation {

    private static final Logger LOGGER = LoggerFactory.getLogger(DelegationKafkaConsumer.class);

    private final AtomicBoolean running;

    private final CountDownLatch shutdownLatch;

    private KafkaConsumer<?, ?> consumer;

    private List<String> topics;

    private MethodExecutor methodExecutor;

    private Consumer consumerAnnotation;

    @Inject
    private KafkaPropertiesResolver kafkaPropertiesResolver;

    public DelegationKafkaConsumer() {
        this.running = new AtomicBoolean(Boolean.TRUE);
        this.shutdownLatch = new CountDownLatch(1);
    }

    @Override
    public void run() {
        try {
            consumer.subscribe(topics);
            LOGGER.trace("Subscribed to {}", topics);

            KafkaConsumerMethodParameterResolver parameterResolver = new KafkaConsumerMethodParameterResolver();

            while (running.get()) {
                ConsumerRecords<?, ?> records = consumer.poll(Duration.ofMillis(100));

                try {
                    for (ConsumerRecord<?, ?> consumerRecord : records) {
                        methodExecutor.invoke(consumerRecord, parameterResolver);
                    }
                } catch (IllegalAccessException e) {
                    throw new ConsumerMethodException("Can't run consumer method!");
                } catch (InvocationTargetException e) {
                    LOGGER.error("Consumer Exception: ", e);
                }
            }
        } finally {
            consumer.close(Duration.ofMillis(consumerAnnotation.closeTimeout()));
            shutdownLatch.countDown();
        }
    }

    @Override
    public void initialize(Consumer consumerAnnotation, MethodExecutor methodExecutor) {
        Objects.requireNonNull(consumerAnnotation);
        Objects.requireNonNull(methodExecutor);

        this.topics = Arrays.asList(consumerAnnotation.topics());
        this.methodExecutor = methodExecutor;
        this.consumerAnnotation = consumerAnnotation;

        Properties properties = kafkaPropertiesResolver.consumerProperties(consumerAnnotation);

        this.consumer = new KafkaConsumer<>(properties);
    }

    @PreDestroy
    public void shutdown() {
        running.set(Boolean.FALSE);
        consumer.wakeup();
        try {
            shutdownLatch.await();
        } catch (Exception e) {
            LOGGER.trace("Exception closing consumer", e);
        }
    }

}
