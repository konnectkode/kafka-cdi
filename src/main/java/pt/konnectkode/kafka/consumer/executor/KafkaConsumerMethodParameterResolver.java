package pt.konnectkode.kafka.consumer.executor;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import pt.konnectkode.kafka.cdi.annotation.Header;
import pt.konnectkode.kafka.cdi.annotation.Key;
import pt.konnectkode.kafka.cdi.annotation.Value;
import pt.konnectkode.kafka.consumer.ConsumerMethodException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedHashMap;

public class KafkaConsumerMethodParameterResolver implements MethodParameterResolver<ConsumerRecord> {

    @Override
    public Object[] resolve(Method method, ConsumerRecord parameterValues) {
        final LinkedHashMap<Class<? extends Annotation>, Object> parameters = new LinkedHashMap<>();

        final int parameterCount = method.getParameterCount();
        validateParameters(parameterCount, method);

        for (Parameter parameter : method.getParameters()) {
            if (parameter.isAnnotationPresent(Key.class)) {
                parameters.put(Key.class, parameterValues.key());
            }
            else if (parameter.isAnnotationPresent(Header.class)) {
                parameters.put(Header.class, parameterValues.headers());
            }
            else if (parameter.isAnnotationPresent(Value.class)) {
                parameters.put(Value.class, parameterValues.value());
            }
        }

        return parameters.values().toArray();
    }

    private void validateParameters(int parameterCount, Method method) {

        if (parameterCount == 0) {
            throw new ConsumerMethodException(String.format("Invalid consumer parameter configuration: %s.%s",
                    method.getDeclaringClass().getName(), method.getName()));
        }
        else if (parameterCount > 3) {
            throw new ConsumerMethodException(String.format("Invalid consumer parameter configuration: %s.%s",
                    method.getDeclaringClass().getName(), method.getName()));
        }
    }

}
