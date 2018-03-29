package app.statistics.n26.controller;

import app.statistics.n26.models.Statistics;
import app.statistics.n26.service.StatisticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.lang.invoke.MethodHandles;
import java.time.Clock;

/**
 * Created by Pranav S Kurup on 3/27/2018.
 *
 * {@link StatisticController} exposes rest endpoint "/statistics"
 *
 */
@RestController
public class StatisticController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final StatisticService statisticService;
    private final Clock utcClock;

    /**
     * @param statisticService
     * @param utcClock
     */
    public StatisticController(StatisticService statisticService, Clock utcClock) {
        this.statisticService = statisticService;
        this.utcClock = utcClock;
    }

    /**
     * exposes statistics data
     * @return responseEntity response entity
     */
    @GetMapping("/statistics")
    public Mono<ResponseEntity<Statistics>> registerTransactions() {
        long now = utcClock.millis();
        LOGGER.debug("Received statistics request at {} epoch time", now);
        return statisticService.getStatistics(now).
                map(
                        statistics -> ResponseEntity.status(HttpStatus.OK).body(statistics)
                ).defaultIfEmpty(
                ResponseEntity.status(HttpStatus.OK).body(
                        Statistics.builder().
                                count(0).
                                max(0).
                                min(0).
                                sum(0).
                                build())
        );
    }
}
