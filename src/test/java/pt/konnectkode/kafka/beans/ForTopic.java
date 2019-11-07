package pt.konnectkode.kafka.beans;

import javax.inject.Qualifier;
import java.lang.annotation.*;

@Qualifier
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD})
public @interface ForTopic {

    String value();

}
