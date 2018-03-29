package app.statistics.n26.storage;

import app.statistics.n26.models.Statistics;
import app.statistics.n26.models.Transaction;
import org.springframework.scheduling.annotation.Scheduled;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by Pranav S Kurup on 3/28/2018.
 */
public interface Store extends ConcurrentMap<Long, Statistics>, Serializable {

    Mono<Boolean> save(Mono<Transaction> transactionPublish, long now);

    Mono<Statistics> fetchStatistics(long now);

    @Scheduled(fixedDelay = 120000)
    void cleanUp();
}
