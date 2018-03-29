package app.statistics.n26.annotations;

import app.statistics.n26.constraint.ValidTimestampConstraint;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Created by Pranav S Kurup on 3/27/2018.
 *
 * Constraint annotation used to validate transaction timestamp
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidTimestampConstraint.class)
@Documented
public @interface ValidTimestamp {
    String message() default "timestamp is empty";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
