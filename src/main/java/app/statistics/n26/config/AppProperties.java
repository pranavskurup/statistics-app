package app.statistics.n26.config;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.util.concurrent.TimeUnit;

/**
 * Created by Pranav S Kurup on 3/27/2018.
 * <p>
 * Application specific properties are loaded here
 */
@Data
@ToString
@ConfigurationProperties(prefix = "app.statistics")
public class AppProperties {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * {@link TimeLimit} specifies the duration for which transaction needs to be stored
     */
    @Getter(AccessLevel.NONE)
    private TimeLimit time;


    /**
     * {@link TransactionLimit} is used to specify minimum amount limit
     */
    @Getter(AccessLevel.NONE)
    private TransactionLimit transaction;

    public TransactionLimit getTransaction() {
        if (this.transaction == null) {
            this.transaction = new TransactionLimit();
        }
        return this.transaction;
    }

    public TimeLimit getTime() {
        if (this.time == null) {
            this.time = new TimeLimit();
        }
        return this.time;
    }

    @Data
    public class TimeLimit {

        /**
         * limit is used to specify the number of time units the transaction needs to be stored
         * default: 60l
         */
        private Long limit = 60l;

        /**
         * {@link TimeUnit} used to specify the unit type of limit
         */
        private TimeUnit timeUnit = TimeUnit.SECONDS;


        /**
         * duration specifies the number of milliseconds the transaction is stored calculated using limit and timeUnit
         */
        private Long duration;
    }

    @Data
    public class TransactionLimit {
        /**
         * lowerLimit is the minimum amount that can be added in a transaction
         */
        private Double lowerLimit = 0d;
    }


    /**
     * method calculates duration before application starts
     */
    @PostConstruct
    public void init() {
        this.getTime().setDuration(TimeUnit.MILLISECONDS.convert(this.getTime().getLimit(), this.getTime().timeUnit));
        LOGGER.debug("Application will keep transaction from last {} milliseconds", this.getTime().getDuration());
    }
}
