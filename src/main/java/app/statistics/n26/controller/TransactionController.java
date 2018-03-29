package app.statistics.n26.controller;

import app.statistics.n26.models.Transaction;
import app.statistics.n26.service.StatisticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.lang.invoke.MethodHandles;
import java.time.Clock;

/**
 * Created by Pranav S Kurup on 3/27/2018.

 * {@link StatisticController} exposes rest endpoint "/transactions"
 *
 */
@RestController
public class TransactionController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final StatisticService statisticService;
    private final Clock utcClock;

    /**
     * @param statisticService {@link StatisticService}
     * @param utcClock {@link Clock}
     */
    public TransactionController(StatisticService statisticService, Clock utcClock) {
        this.statisticService = statisticService;
        this.utcClock = utcClock;
    }

    /**
     * Used to publish transactions
     * @return responseEntity response entity
     */
    @PostMapping("/transactions")
    public Mono<ResponseEntity<Object>> registerTransactions(@Valid @RequestBody Transaction transaction) {
        long now = utcClock.millis();
        LOGGER.debug("Received Transaction request at {} epoch time", now);
        return Mono.just(transaction).publish(transactionMono -> statisticService.addTransaction(transactionMono, now))
                .map(created ->
                        created ? ResponseEntity.status(HttpStatus.CREATED).build() : ResponseEntity.status(HttpStatus.NO_CONTENT).build()
                ).defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
