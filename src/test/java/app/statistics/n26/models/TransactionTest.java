package app.statistics.n26.models;

import org.junit.Assert;
import org.junit.Test;

import java.time.Clock;
import java.util.Formatter;

/**
 * Created by Pranav S Kurup on 3/30/2018.
 */
public class TransactionTest {
    @Test
    public void equalsTest() throws Exception {

        long now= Clock.systemUTC().millis();
        Transaction transaction1 = Transaction.builder().amount(2d).timestamp(now).build();
        Transaction transaction2 = Transaction.builder().amount(2d).timestamp(now).build();
        Assert.assertTrue(transaction1.equals(transaction2));
    }

    @Test
    public void hashCodeTest() throws Exception {
        long now= Clock.systemUTC().millis();
        Transaction transaction1 = Transaction.builder().amount(2d).timestamp(now).build();
        Transaction transaction2 = Transaction.builder().amount(2d).timestamp(now).build();
        Assert.assertEquals(transaction1.hashCode(), transaction2.hashCode(), 0);
    }

    @Test
    public void toStringTest() throws Exception {
        long now= Clock.systemUTC().millis();
        Transaction transaction = Transaction.builder().amount(2d).timestamp(now).build();
        Assert.assertEquals(String.format("Transaction(amount=2.0, timestamp=%tQ)",now), transaction.toString());
    }

}