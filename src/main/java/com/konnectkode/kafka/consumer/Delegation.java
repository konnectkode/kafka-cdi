package com.konnectkode.kafka.consumer;

import com.konnectkode.kafka.cdi.annotation.Consumer;
import com.konnectkode.kafka.consumer.executor.MethodExecutor;

public interface Delegation extends Runnable {

    void initialize(Consumer consumerAnnotation, MethodExecutor methodExecutor);

}
