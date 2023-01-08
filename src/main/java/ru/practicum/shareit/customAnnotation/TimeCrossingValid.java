package ru.practicum.shareit.customAnnotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = CheckDateValidator.class)
@Target(TYPE_USE)
@Retention(RUNTIME)
public @interface TimeCrossingValid {
    String message() default "Wrong timecodes.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
