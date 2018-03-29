package app.statistics.n26.models;

import app.statistics.n26.annotations.ValidAmount;
import app.statistics.n26.annotations.ValidTimestamp;
import lombok.*;

import java.io.Serializable;

/**
 * Created by- Pranav S Kurup on 3/27/2018.
 * This model will be used by transaction endpoint to recieve transactions
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
public class Transaction implements Serializable{

    /**
     * transaction amount
     */
    @ValidAmount
    private Double amount;

    /**
     * Transaction timestamp
     */
    @ValidTimestamp
    private Long timestamp;
}
