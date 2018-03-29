package app.statistics.n26.storage;

import app.statistics.n26.config.AppProperties;
import app.statistics.n26.models.Statistics;
import app.statistics.n26.models.Transaction;
import app.statistics.n26.service.GenericService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import reactor.core.publisher.Mono;

import java.time.Clock;
import java.util.concurrent.TimeUnit;

/**
 * Created by Pranav S Kurup on 3/28/2018.
 */
public class StatisticStoreTest {
    Clock utcClock;
    private GenericService genericService;
    private Store store;

    @Before
    public void init() {
        utcClock = Clock.systemUTC();
        AppProperties appProps = new AppProperties();
        appProps.getTime().setTimeUnit(TimeUnit.SECONDS);
        appProps.getTime().setLimit(60l);
        appProps.init();
        genericService = new GenericService(appProps);
        store = new StatisticStore(genericService, utcClock);
    }

    @Test
    public void saveValidTransaction() throws Exception {
        long now = utcClock.millis();
        long timestamp = utcClock.millis() - 50;
        Transaction transaction = Transaction.builder().amount(1d).timestamp(timestamp).build();
        Assert.assertEquals(true, saveTransaction(transaction, now).block());
    }

    @Test
    public void saveInValidTransaction() throws Exception {
        long now = utcClock.millis();
        long timestamp = now - 90000;
        Transaction transaction = Transaction.builder().amount(1d).timestamp(timestamp).build();
        Assert.assertEquals(false, saveTransaction(transaction, now).block());
    }

    @Test
    public void saveValidTransactionFuture() throws Exception {
        long now = utcClock.millis();
        long timestamp = now + 5;
        Transaction transaction = Transaction.builder().amount(1d).timestamp(timestamp).build();
        Assert.assertEquals(true, saveTransaction(transaction, now).block());
    }


    @Test
    public void fetchStatisticsWhenNoTransactionPresent() throws Exception {
        long now = utcClock.millis();
        Statistics statistics = store.fetchStatistics(now).block();
        Assert.assertEquals(0, statistics.getAvg(), 0);
        Assert.assertEquals(0, statistics.getCount(), 0);
        Assert.assertEquals(0, statistics.getMax(), 0);
        Assert.assertEquals(0, statistics.getMin(), 0);
        Assert.assertEquals(0, statistics.getSum(), 0);
    }


    @Test
    public void fetchStatisticsWhenOneTransactionPresent() throws Exception {
        long now = utcClock.millis();
        Transaction transaction = Transaction.builder().amount(14d).timestamp(now - 1).build();
        Assert.assertEquals(true, saveTransaction(transaction, now).block());
        Statistics block = store.fetchStatistics(now).block();
        Assert.assertEquals(transaction.getAmount(), block.getAvg(), 0);
        Assert.assertEquals(1, block.getCount(), 0);
        Assert.assertEquals(transaction.getAmount(), block.getMin(), 0);
        Assert.assertEquals(transaction.getAmount(), block.getMax(), 0);
        Assert.assertEquals(transaction.getAmount(), block.getSum(), 0);
    }

    @Test
    public void cleanUpOldTransactions() throws Exception {
        long now = utcClock.millis() - 90000;
        Transaction transaction1 = Transaction.builder().amount(1d).timestamp(now).build();
        Transaction transaction2 = Transaction.builder().amount(14d).timestamp(now - 1).build();
        Transaction transaction3 = Transaction.builder().amount(15d).timestamp(now - 2).build();
        Assert.assertEquals(true, saveTransaction(transaction1, now).block());
        Assert.assertEquals(true, saveTransaction(transaction2, now).block());
        Assert.assertEquals(true, saveTransaction(transaction3, now).block());
        now = now + 90000;
        store.cleanUp();
        Statistics statistics = store.fetchStatistics(now).block();
        Assert.assertEquals(0, statistics.getAvg(), 0);
        Assert.assertEquals(0, statistics.getCount(), 0);
        Assert.assertEquals(0, statistics.getMax(), 0);
        Assert.assertEquals(0, statistics.getMin(), 0);
        Assert.assertEquals(0, statistics.getSum(), 0);
    }

    private Mono<Boolean> saveTransaction(Transaction transaction, long now) {
        Mono<Boolean> mono = store.save(Mono.just(transaction), now);
        return mono;
    }

    @Test
    public void cleanUpShouldNotRemoveNewTransactions() throws Exception {
        long now = utcClock.millis();
        Transaction transaction1 = Transaction.builder().amount(1d).timestamp(now).build();
        Transaction transaction2 = Transaction.builder().amount(14d).timestamp(now - 1).build();
        Transaction transaction3 = Transaction.builder().amount(15d).timestamp(now - 2).build();
        Assert.assertEquals(true, saveTransaction(transaction1, now).block());
        Assert.assertEquals(true, saveTransaction(transaction2, now).block());
        Assert.assertEquals(true, saveTransaction(transaction3, now).block());
        store.cleanUp();
        Statistics statistics = store.fetchStatistics(now).block();
        Assert.assertEquals((Double.sum(Double.sum(transaction1.getAmount(), transaction2.getAmount()), transaction3.getAmount())) / 3, statistics.getAvg(), 0);
        Assert.assertEquals(3, statistics.getCount(), 0);
        Assert.assertEquals(Double.min(Double.min(transaction1.getAmount(), transaction2.getAmount()), transaction3.getAmount()), statistics.getMin(), 0);
        Assert.assertEquals(Double.max(Double.max(transaction1.getAmount(), transaction2.getAmount()), transaction3.getAmount()), statistics.getMax(), 0);
        Assert.assertEquals(Double.sum(Double.sum(transaction1.getAmount(), transaction2.getAmount()), transaction3.getAmount()), statistics.getSum(), 0);
    }


    @TestConfiguration
    @ComponentScan("app.statistics.n26")
    static class EmployeeServiceImplTestContextConfiguration {


    }
}
