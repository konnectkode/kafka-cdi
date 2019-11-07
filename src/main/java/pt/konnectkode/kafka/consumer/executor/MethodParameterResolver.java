package pt.konnectkode.kafka.consumer.executor;

import java.lang.reflect.Method;

public interface MethodParameterResolver<T> {

    Object[] resolve(Method method, T parameterValues);

}
