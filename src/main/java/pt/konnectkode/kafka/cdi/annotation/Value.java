package pt.konnectkode.kafka.cdi.annotation;

import java.lang.annotation.*;

@Inherited
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Value {
}
