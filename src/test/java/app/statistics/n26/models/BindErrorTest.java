package app.statistics.n26.models;

import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Pranav S Kurup on 3/30/2018.
 */
public class BindErrorTest {
    @Test
    public void toStringTest() throws Exception {

        List<BindError.FieldError> errors = new LinkedList<>();
        errors.add(BindError.FieldError.builder().field("timestamp").errorMessage("expired").build());
        BindError error = BindError.builder()
                .errorCode("123")
                .message("test")
                .path("/test")
                .message("test")
                .errorFields(errors)
                .build();
        Assert.assertEquals("BindError(errorCode=123, message=test, path=/test, errorFields=[BindError.FieldError(field=timestamp, errorMessage=expired)])", error.toString());
    }

}