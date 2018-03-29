package app.statistics.n26.service;

import app.statistics.n26.config.AppProperties;
import app.statistics.n26.models.Statistics;
import app.statistics.n26.models.Transaction;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Clock;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

/**
 * Created by Pranav S Kurup on 3/29/2018.
 */
public class GenericServiceTest {
    private GenericService genericService;
    private Clock clock;
    private static final long LIMIT = 60l;

    @Before
    public void init() {
        clock = Clock.systemUTC();
        AppProperties appProperties = new AppProperties();
        appProperties.getTime().setLimit(LIMIT);
        appProperties.getTime().setTimeUnit(TimeUnit.SECONDS);
        appProperties.init();
        genericService = new GenericService(appProperties);
    }

    @Test
    public void tooOldTimestampIsInLimit() throws Exception {
        long now = clock.millis();
        long timestamp = now + LIMIT - 5;
        assertEquals(false, genericService.tooOld(timestamp, now));
    }

    @Test
    public void tooOldTimestampIsNotInLimit() throws Exception {
        long now = clock.millis();
        long timestamp = now + LIMIT + 5;
        assertEquals(false, genericService.tooOld(timestamp, now));
    }

    @Test
    public void getStatisticsFromTransaction() throws Exception {
        Transaction transaction = Transaction.builder().amount(2d).timestamp(clock.millis()).build();
        Statistics statistics = genericService.getStatisticsFromTransaction(transaction);
        Assert.assertEquals(transaction.getAmount(), statistics.getAvg(), 0);
        Assert.assertEquals(1, statistics.getCount(), 0);
        Assert.assertEquals(transaction.getAmount(), statistics.getMin(), 0);
        Assert.assertEquals(transaction.getAmount(), statistics.getMax(), 0);
        Assert.assertEquals(transaction.getAmount(), statistics.getSum(), 0);
    }

}