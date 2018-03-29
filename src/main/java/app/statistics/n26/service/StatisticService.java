package app.statistics.n26.service;

import app.statistics.n26.models.Statistics;
import app.statistics.n26.models.Transaction;
import app.statistics.n26.storage.Store;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.lang.invoke.MethodHandles;

/**
 * Created by Pranav S Kurup on 3/28/2018.
 *
 * {@link StatisticService} is a service layer between {@link org.springframework.web.bind.annotation.RestController} and {@link Store}
 *
 */
@Service
public class StatisticService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Store store;

    public StatisticService(Store store) {
        this.store = store;
    }

    /**
     *
     * Publishes transaction into storage
     *
     * @param transactionPublisher
     * @param now
     * @return
     */
    public Mono<Boolean> addTransaction(Publisher<Transaction> transactionPublisher, long now) {
        LOGGER.debug("Add transaction to statistics store if valid  {}");
        return store.save(Mono.from(transactionPublisher), now);
    }

    /**
     * Retrieves statistics data for the period
     * @param now
     * @return
     */
    public Mono<Statistics> getStatistics(long now) {
        LOGGER.debug("Get statistcs of transaction with respect to system time {}", now);
        return store.fetchStatistics(now);
    }
}
