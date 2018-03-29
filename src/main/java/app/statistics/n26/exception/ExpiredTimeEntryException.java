package app.statistics.n26.exception;

/**
 * Created by Pranav S Kurup on 3/27/2018.
 * {@link ExpiredTimeEntryException} will be thrown when a transction is
 * received at /transactions endpoint with expired timestamp
 */
public class ExpiredTimeEntryException extends RuntimeException {
    public ExpiredTimeEntryException() {
        super("Transaction expired");
    }
}
