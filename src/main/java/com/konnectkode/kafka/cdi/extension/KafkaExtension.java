package com.konnectkode.kafka.cdi.extension;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.konnectkode.kafka.cdi.annotation.Consumer;
import com.konnectkode.kafka.consumer.DelegationKafkaConsumer;
import com.konnectkode.kafka.consumer.executor.MethodExecutor;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.*;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.newSetFromMap;

public class KafkaExtension implements Extension {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaExtension.class);

    private final Set<AnnotatedMethod<?>> listenerMethods = newSetFromMap(new ConcurrentHashMap<>());

    private UnmanagedInstanceStrategy cacheStrategy = new UnmanagedInstanceStrategy();

    private ThreadPoolExecutor executorService;

    <T> void registerConsumerListeners(@Observes @WithAnnotations(Consumer.class) ProcessAnnotatedType<T> pat) {
        LOGGER.debug("Scanning consumer type: {}", pat.getAnnotatedType().getJavaClass().getName());

        final AnnotatedType<T> annotatedType = pat.getAnnotatedType();

        for (AnnotatedMethod am : annotatedType.getMethods()) {

            if (am.isAnnotationPresent(Consumer.class)) {
                LOGGER.debug("Found annotated Consumer method, adding for further processing");

                listenerMethods.add(am);
            }
        }
    }

    void afterDeploymentValidation(@Observes AfterDeploymentValidation adv) {
        LOGGER.debug("Wiring annotated methods to internal Kafka Class");

        if (this.listenerMethods.size() > 0) {
            this.executorService = createExecutorService(listenerMethods.size());

            listenerMethods.forEach(consumerMethod -> {
                final DelegationKafkaConsumer frameworkConsumer = cacheStrategy.newInstance(DelegationKafkaConsumer.class);

                Class<?> consumerClass = consumerMethod.getJavaMember().getDeclaringClass();

                Object consumerInstance = cacheStrategy.cacheInstance(consumerClass);

                MethodExecutor methodExecutor = new MethodExecutor(consumerInstance, consumerMethod.getJavaMember());

                frameworkConsumer.initialize(consumerMethod.getAnnotation(Consumer.class), methodExecutor);

                executorService.execute(frameworkConsumer);
            });
        }
    }

    void beforeShutdown(@Observes final BeforeShutdown bs) {
        cacheStrategy.dispose();
    }

    private ThreadPoolExecutor createExecutorService(int poolSize) {
        return new ThreadPoolExecutor(poolSize, poolSize, 10, TimeUnit.SECONDS, new LinkedBlockingDeque<>());
    }

}
