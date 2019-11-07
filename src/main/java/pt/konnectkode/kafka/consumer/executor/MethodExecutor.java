package pt.konnectkode.kafka.consumer.executor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodExecutor {

    private final Object instance;

    private final Method method;

    public MethodExecutor(Object instance, Method method) {
        this.instance = instance;
        this.method = method;
    }

    public <T> void invoke(T consumerRecord, MethodParameterResolver<T> methodParameterResolver) throws InvocationTargetException, IllegalAccessException {
        method.invoke(instance, methodParameterResolver.resolve(method, consumerRecord));
    }

}
