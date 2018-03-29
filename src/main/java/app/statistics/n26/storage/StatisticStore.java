package app.statistics.n26.storage;

import app.statistics.n26.models.Statistics;
import app.statistics.n26.models.Transaction;
import app.statistics.n26.service.GenericService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.lang.invoke.MethodHandles;
import java.time.Clock;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Pranav S Kurup on 3/28/2018.
 * In memory store
 */
@Component
public class StatisticStore extends ConcurrentHashMap<Long, Statistics> implements Store {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final transient GenericService genericService;
    private final transient Clock utcClock;

    public StatisticStore(GenericService genericService, Clock utcClock) {
        this.genericService = genericService;
        this.utcClock = utcClock;
    }

    /**
     * Saves transaction to in memory
     *
     * @param transactionPublish
     * @param now
     * @return
     */
    @Override
    public Mono<Boolean> save(Mono<Transaction> transactionPublish, long now) {
        LOGGER.debug("Saving transaction if valid  before {}",now);
        return transactionPublish.map(transaction ->
                {
                    Long timestamp = transaction.getTimestamp();
                    if (!genericService.tooOld(timestamp, now)) {
                        merge(timestamp, genericService.getStatisticsFromTransaction(transaction), Statistics::remap);
                        LOGGER.debug("Transaction {} is valid", transaction);
                        return true;
                    } else {
                        LOGGER.debug("Transaction {} is invalid", transaction);
                    }
                    return false;
                }
        ).defaultIfEmpty(false);
    }

    /**
     * Fethces valid statistic details by comparing data for the timeperiod
     *
     * @param now
     * @return
     */
    @Override
    public Mono<Statistics> fetchStatistics(long now) {
        LOGGER.debug("Fetch statistic record from store, store size  {}", size());
        return Mono.just(entrySet().stream()
                .filter(entry -> !genericService.tooOld(entry.getKey(), now) && entry.getKey() <= now)
                .flatMap(entry -> Stream.of(entry.getValue()))
                .reduce(Statistics::remap).orElse(new Statistics(0, 0, 0, 0)));
    }

    @Scheduled(fixedDelay = 120000)
    @Override
    public void cleanUp() {
        LOGGER.debug("Cleaning old statistics details every  {}", "120s");
        long now = utcClock.millis();
        Set<Long> old = keySet().stream().filter(key -> genericService.tooOld(key, now)).collect(Collectors.toSet());
        old.forEach(this::remove);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
