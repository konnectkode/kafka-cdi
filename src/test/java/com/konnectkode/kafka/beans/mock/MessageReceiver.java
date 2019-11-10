package com.konnectkode.kafka.beans.mock;

import com.konnectkode.kafka.cdi.annotation.Header;
import com.konnectkode.kafka.cdi.annotation.Key;
import com.konnectkode.kafka.cdi.annotation.Value;
import org.apache.kafka.common.header.Headers;

public interface MessageReceiver {

    void ack(@Value String message);

    void ack(@Key Integer key, @Value String value, @Header Headers expectedHeaders);

}
