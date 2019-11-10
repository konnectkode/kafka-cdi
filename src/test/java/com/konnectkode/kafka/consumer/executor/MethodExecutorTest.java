package com.konnectkode.kafka.consumer.executor;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.junit.Before;
import org.junit.Test;
import com.konnectkode.kafka.consumer.ConsumerMethodException;

import java.lang.reflect.Method;

import static org.mockito.Mockito.*;

public class MethodExecutorTest {

    private static String CONSUMER_KEY = "consumer.key";

    private static String CONSUMER_VALUE = "consumer.value";

    private static Headers CONSUMER_HEADERS = new RecordHeaders();

    private ConsumerRecord consumerRecord;

    private Consumer consumer;

    @Before
    public void before() {
        consumer = mock(Consumer.class);
        consumerRecord = mock(ConsumerRecord.class);
        when(consumerRecord.key()).thenReturn(CONSUMER_KEY);
        when(consumerRecord.headers()).thenReturn(CONSUMER_HEADERS);
        when(consumerRecord.value()).thenReturn(CONSUMER_VALUE);
    }

    @Test
    public void methodOnlyValue() throws Exception {

        Method method = spy(Consumer.class.getMethod("consumerOnlyValue", String.class));
        MethodExecutor methodExecutor = new MethodExecutor(consumer, method);

        methodExecutor.invoke(consumerRecord, new KafkaConsumerMethodParameterResolver());

        verify(consumerRecord).value();
        verify(consumerRecord, never()).key();
        verify(consumerRecord, never()).headers();

        verify(method).invoke(any(Consumer.class), eq(CONSUMER_VALUE));
        verify(consumer).consumerOnlyValue(CONSUMER_VALUE);
    }

    @Test
    public void method3Parameters() throws Exception {

        Method method = spy(Consumer.class.getMethod("consumerAllParameters", String.class, String.class, Headers.class));

        MethodExecutor runner = new MethodExecutor(consumer, method);
        runner.invoke(consumerRecord, new KafkaConsumerMethodParameterResolver());

        verify(consumerRecord).value();
        verify(consumerRecord).key();
        verify(consumerRecord).headers();

        verify(method).invoke(any(Consumer.class), eq(CONSUMER_VALUE), eq(CONSUMER_KEY), eq(CONSUMER_HEADERS));
        verify(consumer).consumerAllParameters(CONSUMER_VALUE, CONSUMER_KEY, CONSUMER_HEADERS);
    }

    @Test(expected = ConsumerMethodException.class)
    public void moreThan3Parameters() throws Exception {

        Method method = spy(Consumer.class.getMethod("consumerMoreThan3Parameters", String.class, String.class, String.class, String.class));

        MethodExecutor runner = new MethodExecutor(consumer, method);
        runner.invoke(consumerRecord, new KafkaConsumerMethodParameterResolver());

        verify(method, never()).invoke(any(Consumer.class), any(Object[].class));
        verify(consumer, never()).consumerMoreThan3Parameters(any(), any(), any(), any());
    }

    @Test(expected = ConsumerMethodException.class)
    public void methodWithoutParameters() throws Exception {
        Method method = Consumer.class.getMethod("consumerWithoutParameter");

        MethodExecutor methodExecutor = new MethodExecutor(consumer, method);
        methodExecutor.invoke(consumerRecord, new KafkaConsumerMethodParameterResolver());
    }

}
