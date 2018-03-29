package app.statistics.n26.constraint;

import app.statistics.n26.annotations.ValidAmount;
import app.statistics.n26.config.AppProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.invoke.MethodHandles;

/**
 * Created by Pranav S Kurup on 3/27/2018.
 * {@link ValidAmount} uses this class to validate amount
 */
@Component
public class ValidAmountConstraint implements ConstraintValidator<ValidAmount, Double> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * lowerLimit will be minimum amount which is allowed for a transaction
     */
    private Double lowerLimit;

    /**
     * @param appProperties application specific configuration properties
     */
    public ValidAmountConstraint(AppProperties appProperties) {
        this.lowerLimit = appProperties.getTransaction().getLowerLimit();
    }

    /**
     * @param constraint {@link ValidAmount}
     */
    @Override
    public void initialize(ValidAmount constraint) {
        // Do nothing because nothing to initialize
    }

    /**
     * Constraint validation is done here for amount field
     *
     * @param amount  {@link Double}
     * @param context {@link ConstraintValidatorContext}
     * @return
     */
    @Override
    public boolean isValid(Double amount, ConstraintValidatorContext context) {
        boolean flag = false;
        context.disableDefaultConstraintViolation();
        if (null == amount) {
            LOGGER.debug("amount passed is {} will skip this transaction ", "null");
            context.buildConstraintViolationWithTemplate("Amount is null or empty").addConstraintViolation();
        } else if (amount < lowerLimit) {
            LOGGER.debug("amount passed {} is less than limit {}", amount, lowerLimit);
            context.buildConstraintViolationWithTemplate("Amount is less than " + lowerLimit).addConstraintViolation();
        } else {
            flag = true;
        }
        return flag;
    }
}
