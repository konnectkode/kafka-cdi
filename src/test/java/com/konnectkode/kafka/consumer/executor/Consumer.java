package com.konnectkode.kafka.consumer.executor;

import com.konnectkode.kafka.cdi.annotation.Header;
import com.konnectkode.kafka.cdi.annotation.Key;
import com.konnectkode.kafka.cdi.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class Consumer {

    private final Logger LOG = LoggerFactory.getLogger(Consumer.class);

    @PostConstruct
    private void postConstruct() {
        LOG.info("Post Construct Called!");
    }

    public void consumerOnlyValue(@Value String value) {

    }

    public void consumerWithoutParameter() {

    }

    public void consumerAllParameters(@Value String value, @Key String key, @Header org.apache.kafka.common.header.Headers headers) {

    }

    public void consumerMoreThan3Parameters(@Value String one, @Value String two, @Value String three, @Value String four) {

    }

    @PreDestroy
    private void preDestroy() {
        LOG.info("Pre Destroy Called!");
    }

}
