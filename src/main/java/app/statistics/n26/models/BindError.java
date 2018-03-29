package app.statistics.n26.models;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Pranav S Kurup on 3/27/2018.
 *
 * When any constraint validation error occurs , then endpoint response will be of type {@link BindError}
 *
 */

@Getter
@ToString
@Builder
public class BindError implements Serializable {

    private String errorCode;

    private String message;

    private String path;

    private List<FieldError> errorFields;

    @Getter
    @Builder
    @ToString
    public static class FieldError implements Serializable {
        private String field;
        private String errorMessage;
    }
}
