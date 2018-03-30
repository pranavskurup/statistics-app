package app.statistics.n26.models;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Pranav S Kurup on 3/30/2018.
 */
public class ErrorTest {
    @Test
    public void toStringTest() throws Exception {

        Error error=Error.builder()
                .errorCode("123")
                .message("test")
                .path("/test")
                .message("test")
                .build();
        Assert.assertEquals("Error(errorCode=123, message=test, path=/test)", error.toString());
    }

}