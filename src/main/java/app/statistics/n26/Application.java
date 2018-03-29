package app.statistics.n26;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.lang.invoke.MethodHandles;

/**
 * Created by Pranav S Kurup on 3/27/2018.
 * <p>
 * Bootstraping spring boot application
 * <p>
 * Configurations are loaded from application.yml
 */
@SpringBootApplication
public class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * To start Spring Boot Application please run this class
     * @param args
     */
    public static void main(String[] args) {
        LOGGER.debug("Starting Statistics application {}");
        SpringApplication.run(Application.class, args);
    }

}
