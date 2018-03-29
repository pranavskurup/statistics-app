package app.statistics.n26.models;

import org.junit.Assert;

/**
 * Created by Pranav S Kurup on 3/28/2018.
 */
public class StatisticsTest {

    @org.junit.Test
    public void remap() throws Exception {
        Statistics statistics1 = Statistics.builder().min(5).max(3).count(2).sum(7).build();
        Statistics statistics2 = Statistics.builder().min(7).max(7).count(1).sum(7).build();
        Statistics statistics3 = statistics2.remap(statistics1);
        Assert.assertEquals(statistics3.getCount(), statistics1.getCount() + statistics2.getCount());
        Assert.assertEquals(statistics3.getMin(), statistics1.getMin(), 0);
        Assert.assertEquals(statistics3.getMax(), statistics2.getMax(), 0);
        Assert.assertEquals(statistics3.getSum(), statistics2.getSum() + statistics2.getSum(), 0);
        Assert.assertEquals(statistics3.getAvg(), (statistics3.getCount() != 0) ? statistics3.getSum() / statistics3.getCount() : 0, 0);
    }

}