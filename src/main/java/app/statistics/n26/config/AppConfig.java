package app.statistics.n26.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * Created by Pranav S Kurup on 3/27/2018.
 *
 * Loads {@link AppProperties}
 * Creates a bean refrence of {@link Clock}  for getting UTC time
 *
 */
@Configuration
@EnableConfigurationProperties({
        AppProperties.class
})
public class AppConfig {
    @Autowired
    private AppProperties appProperties;

    @Bean
    public Clock utcClock() {
        return Clock.systemUTC();
    }
}
