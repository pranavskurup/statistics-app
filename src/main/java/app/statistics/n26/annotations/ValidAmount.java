package app.statistics.n26.annotations;

import app.statistics.n26.constraint.ValidAmountConstraint;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Created by Pranav S Kurup on 3/27/2018.
 *
 * Constraint used to reject transaction if amount empty, null or less than 0
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidAmountConstraint.class)
@Documented
public @interface ValidAmount {
    String message() default "amount is empty";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
