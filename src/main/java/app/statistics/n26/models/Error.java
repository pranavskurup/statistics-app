package app.statistics.n26.models;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

/**
 * Created by Pranav S Kurup on 3/27/2018.
 * When any error occurs , then endpoint response will be of type {@link BindError}
 */
@Getter
@Builder
@ToString
public class Error implements Serializable {
    private String errorCode;
    private String message;
    private String path;
}
