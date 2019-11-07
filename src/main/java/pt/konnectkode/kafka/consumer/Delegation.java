package pt.konnectkode.kafka.consumer;

import pt.konnectkode.kafka.cdi.annotation.Consumer;
import pt.konnectkode.kafka.consumer.executor.MethodExecutor;

public interface Delegation extends Runnable {

    void initialize(Consumer consumerAnnotation, MethodExecutor methodExecutor);

}
