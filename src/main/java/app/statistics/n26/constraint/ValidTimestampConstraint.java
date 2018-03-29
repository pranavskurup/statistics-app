package app.statistics.n26.constraint;

import app.statistics.n26.annotations.ValidTimestamp;
import app.statistics.n26.exception.ExpiredTimeEntryException;
import app.statistics.n26.service.GenericService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.invoke.MethodHandles;
import java.time.Clock;

/**
 * Created by Pranav S Kurup on 3/27/2018.
 * {@link ValidTimestamp} uses this class to validate timestamp
 */
@Component
public class ValidTimestampConstraint implements ConstraintValidator<ValidTimestamp, Long> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final GenericService genericService;
    private final Clock utcClock;

    /**
     * @param genericService {@link GenericService} provides methods to validate timestamp validity
     * @param utcClock       {@link Clock} used to retrieve UTC time in milliseconds
     */
    public ValidTimestampConstraint(GenericService genericService, Clock utcClock) {
        this.genericService = genericService;
        this.utcClock = utcClock;
    }

    /**
     * @param constraint {@link ValidTimestamp}
     */
    @Override
    public void initialize(ValidTimestamp constraint) {
        // Do nothing because nothing to initialize
    }

    /**
     * Method validate whether transaction timestamp is valid or not
     * @param timestamp  {@link Long}
     * @param context   {@link ConstraintValidatorContext}
     * @return
     */
    @Override
    public boolean isValid(Long timestamp, ConstraintValidatorContext context) {
        boolean flag = false;
        context.disableDefaultConstraintViolation();
        if (null == timestamp) {
            LOGGER.debug("Timestamp passed is null will skip this transaction");
            context.buildConstraintViolationWithTemplate("Timestamp is null or empty").addConstraintViolation();
        } else if (genericService.tooOld(timestamp, utcClock.millis())) {
            LOGGER.debug("Timestamp passed is {} expired , current timestamp is {}", timestamp, utcClock.millis());
            throw new ExpiredTimeEntryException();
        } else {
            flag = true;
        }
        return flag;
    }
}
