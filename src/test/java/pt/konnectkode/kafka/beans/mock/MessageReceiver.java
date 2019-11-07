package pt.konnectkode.kafka.beans.mock;

import org.apache.kafka.common.header.Headers;
import pt.konnectkode.kafka.cdi.annotation.Header;
import pt.konnectkode.kafka.cdi.annotation.Key;
import pt.konnectkode.kafka.cdi.annotation.Value;

public interface MessageReceiver {

    void ack(@Value String message);

    void ack(@Key Integer key, @Value String value, @Header Headers expectedHeaders);

}
