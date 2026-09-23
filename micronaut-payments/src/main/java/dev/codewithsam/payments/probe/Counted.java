package dev.codewithsam.payments.probe;

import io.micronaut.aop.Around;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Around
public @interface Counted {
}
