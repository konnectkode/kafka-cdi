package com.konnectkode.kafka.cdi.annotation;

import javax.inject.Qualifier;
import java.lang.annotation.*;

@Qualifier
@Inherited
@Documented
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Producer {

}
