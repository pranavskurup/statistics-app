package app.statistics.n26.controller;

import app.statistics.n26.exception.ExpiredTimeEntryException;
import app.statistics.n26.models.BindError;
import app.statistics.n26.models.Error;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;

import javax.validation.ValidationException;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Pranav S Kurup on 3/27/2018.
 *
 * Generic class to handle exception and respond with a generic error message
 *
 */
@ControllerAdvice
public class CommonControllerAdvices {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String COMMON_ERROR_LOG_MSG="Error occurred while processing request: {}";

    /**
     * All {@link Exception} will be handled by this method
     * @param exchange
     * @param ex
     * @return
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity onError(ServerWebExchange exchange, Exception ex) {
        String path = exchange.getRequest().getPath().value();
        LOGGER.error(COMMON_ERROR_LOG_MSG, path, ex);
        exchange.getRequest().getPath();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Error.builder().path(path).errorCode("100-00").message("Internal error occurred please refer log").build());
    }

    /**
     * {@link Exception} of type {@link ValidationException} will be handled by this method
     * @param exchange
     * @param ex
     * @return
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity onError(ServerWebExchange exchange, ValidationException ex) {
        if (ex.getCause() instanceof ExpiredTimeEntryException) {
            String path = exchange.getRequest().getPath().value();
            LOGGER.error(COMMON_ERROR_LOG_MSG, path, ex);
            exchange.getRequest().getPath();
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return onError(exchange, (Exception) ex);
        }
    }

    /**
     * {@link Exception} of type {@link WebExchangeBindException} will be handled by this method
     * @param exchange
     * @param ex
     * @return
     */
    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity onError(ServerWebExchange exchange, WebExchangeBindException ex) {
        String path = exchange.getRequest().getPath().value();
        LOGGER.error(COMMON_ERROR_LOG_MSG, path, ex);
        List<BindError.FieldError> errorList = Stream.of(
                ex.getAllErrors()).flatMap(
                objectErrors ->
                        objectErrors.parallelStream().map(
                                objectError ->
                                        BindError.FieldError.builder().
                                                field(((DefaultMessageSourceResolvable) objectError.getArguments()[0]).getDefaultMessage()).
                                                errorMessage(objectError.getDefaultMessage()).
                                                build()
                        )
        ).collect(Collectors.toList());
        BindError.BindErrorBuilder builder = BindError.builder();
        if (!errorList.isEmpty()) {
            builder.errorFields(errorList);
        }
        builder.errorCode("100-" + ex.getStatus());
        builder.message(ex.getReason());
        builder.path(path);
        exchange.getRequest().getPath();
        return ResponseEntity.status(ex.getStatus()).body(builder.build());
    }
}
