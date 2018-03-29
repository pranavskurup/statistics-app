package app.statistics.n26.models;

import lombok.*;

import java.io.Serializable;

/**
 * Created by Pranav S Kurup on 3/27/2018.
 *
 * when statistic endpoint is called the response will be send with {@link Statistics} type
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
public class Statistics implements Serializable {

    /**
     *  used to represent number of transaction in given time  period
     */
    private long count;

    /**
     *  used to represent total amount in given time  period
     */
    private double sum;

    /**
     *  used to represent minimum amount in given time  period
     */
    private double min;

    /**
     *  used to represent maximum amount in given time  period
     */
    private double max;

    public Statistics remap(Statistics statistics) {
        return Statistics.builder().
                count(this.count + statistics.count).
                sum(this.sum + statistics.sum).
                min(Double.min(this.min, statistics.min)).
                max(Double.max(this.max, statistics.max)).
                build();
    }


    /**
     *  used to represent average amount in given time  period
     */
    public double getAvg() {
        if (count == 0) {
            return 0d;
        }
        return sum / count;
    }
}
