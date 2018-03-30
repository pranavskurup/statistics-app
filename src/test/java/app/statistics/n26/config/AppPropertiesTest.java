package app.statistics.n26.config;

import app.statistics.n26.config.AppProperties;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.reactive.context.AnnotationConfigReactiveWebApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.lang.invoke.MethodHandles;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

/**
 * Created by Pranav S Kurup on 3/30/2018.
 */
public class AppPropertiesTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static AppProperties appProperties;
    private static AnnotationConfigReactiveWebApplicationContext applicationContext;


    @BeforeClass
    public static void init() {
        applicationContext = new AnnotationConfigReactiveWebApplicationContext();
        applicationContext.register(SpringTestConfiguration.class);
    }

    private void reloadContext(String mode) throws InterruptedException {
        System.clearProperty("app.statistics.time.time-unit");
        System.clearProperty("app.statistics.time.limit");
        System.clearProperty("app.statistics.transaction.lower-limit");
        if (null != mode && !"no-config".equals(mode)) {
            YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
            Path resourceDirectory = Paths.get("src", "test", "resources", "yml", mode + "-application.yml");
            //File inputXmlFile = new File(this.getClass().getResource("/yml/test1-application.yml").getFile());
            yaml.setResources(new FileSystemResource(resourceDirectory.toFile()));

            yaml.getObject().forEach((key, value) -> {
                LOGGER.info("Key: {}, value: {} ", key, value);
                System.setProperty(key.toString(), value.toString());
            });
        }
        /*System.setProperty("test-prop","test1");*/
        applicationContext.refresh();
        appProperties = applicationContext.getBean(AppProperties.class);
    }

    @Test
    public void appPropertiesWithNoConfigFile() throws InterruptedException {
        reloadContext(null);
        Assert.assertNotNull(appProperties.getTime());
        Assert.assertEquals(TimeUnit.SECONDS, appProperties.getTime().getTimeUnit());
        Assert.assertEquals(60, appProperties.getTime().getLimit(), 0);
        Assert.assertEquals(60000, appProperties.getTime().getDuration(), 0);
        Assert.assertNotNull(appProperties.getTransaction());
        Assert.assertEquals(0, appProperties.getTransaction().getLowerLimit(), 0);
        Assert.assertEquals("AppProperties(time=AppProperties.TimeLimit(limit=60, timeUnit=SECONDS, duration=60000), transaction=AppProperties.TransactionLimit(lowerLimit=0.0))", appProperties.toString());
    }


    @Test
    public void appPropertiesWithTest1File() throws InterruptedException {
        reloadContext("test1");
        Assert.assertNotNull(appProperties.getTime());
        Assert.assertEquals(TimeUnit.HOURS, appProperties.getTime().getTimeUnit());
        Assert.assertEquals(1, appProperties.getTime().getLimit(), 0);
        Assert.assertEquals(3600000, appProperties.getTime().getDuration(), 0);
        Assert.assertNotNull(appProperties.getTransaction());
        Assert.assertEquals(-5, appProperties.getTransaction().getLowerLimit(), 0);
        Assert.assertEquals("AppProperties(time=AppProperties.TimeLimit(limit=1, timeUnit=HOURS, duration=3600000), transaction=AppProperties.TransactionLimit(lowerLimit=-5.0))", appProperties.toString());
    }

    @TestPropertySource("classpath*:yml/test1-application.yml")
    @ContextConfiguration
    @EnableConfigurationProperties({
            AppProperties.class
    })
    public static class SpringTestConfiguration {

        @Autowired
        Environment environment;

     /*   @Bean
        Properties appProps() {
            YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
            Path resourceDirectory = Paths.get("src", "test", "resources", "yml", environment.getProperty("test-prop") + "-application.yml");
            //File inputXmlFile = new File(this.getClass().getResource("/yml/test1-application.yml").getFile());
            yaml.setResources(new FileSystemResource(resourceDirectory.toFile()));
            return yaml.getObject();
        }*/
    }
}