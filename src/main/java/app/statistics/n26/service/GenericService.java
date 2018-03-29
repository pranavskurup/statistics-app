package app.statistics.n26.service;

import app.statistics.n26.config.AppProperties;
import app.statistics.n26.models.Statistics;
import app.statistics.n26.models.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

/**
 * Created by Pranav S Kurup on 3/28/2018.
 *
 * {@link GenericService} provide methods to calculate transaction validity
 * and generate statistics from an individual transaction
 *
 */
@Component
public class GenericService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final long validDuration;

    public GenericService(AppProperties appProperties) {
        this.validDuration = appProperties.getTime().getDuration();
    }

    /**
     * Compares timeStamp with now to determine whether transaction is expired or not
     *
     * @param timeStamp long transaction timestamp
     * @param now  long currrent time
     * @return status boolean
     */
    public boolean tooOld(final long timeStamp, final long now) {
        LOGGER.debug("Timestamp comparision between transaction time {} and system time {} in epoch UTC time", timeStamp, now);
        long diff = now - timeStamp;
        return diff > validDuration;
    }

    /**
     * Converts indivifual transaction into statistics
     * @param transaction {@link Transaction}
     * @return statistics {@link Statistics}
     */
    public Statistics getStatisticsFromTransaction(Transaction transaction) {
        LOGGER.debug("Generate statistic data for individual transaction");
        return Statistics.builder().count(1).sum(transaction.getAmount()).max(transaction.getAmount()).min(transaction.getAmount()).build();
    }
}
