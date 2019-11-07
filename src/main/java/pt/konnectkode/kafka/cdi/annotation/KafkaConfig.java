package pt.konnectkode.kafka.cdi.annotation;

import javax.inject.Qualifier;
import java.lang.annotation.*;

@Qualifier
@Inherited
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface KafkaConfig {

}
