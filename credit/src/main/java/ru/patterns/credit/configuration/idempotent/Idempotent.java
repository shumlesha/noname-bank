package ru.patterns.credit.configuration.idempotent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Idempotent {
    Source source() default Source.HEADER;

    enum Source {
        HEADER,
        PARAM
    }
}
