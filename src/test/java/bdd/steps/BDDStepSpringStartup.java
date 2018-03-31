package bdd.steps;

import app.statistics.n26.Application;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import cucumber.runtime.java.StepDefAnnotation;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.reactive.context.AnnotationConfigReactiveWebApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CountDownLatch;

/**
 * Created by Pranav S Kurup on 3/29/2018.
 */
public class BDDStepSpringStartup {
    ConfigurableApplicationContext applicationContext = null;
    private ResponseEntity<String> exchangeResponse;
    private HttpClientErrorException httpClientErrorException;
    private RestTemplate restTemplate;

    @Before
    public void init() {
        restTemplate = new RestTemplate();
    }

    @Given("^Start statistics application$")
    public void start_statistics_application() throws Throwable {
        SpringApplicationBuilder appBuilder =
                new
                        SpringApplicationBuilder()
                        .sources(Application.class, SpringTestConfig.class);
        applicationContext = appBuilder.run();
        applicationContext.start();
       /* applicationContext=new AnnotationConfigReactiveWebApplicationContext();
        applicationContext.register(Application.class, SpringTestConfig.class);
        applicationContext.refresh();*/
        System.out.println();
      CountDownLatch countDownLatch = applicationContext.getBean(CountDownLatch.class);
      countDownLatch.await();
    }


    @When("^Check health endpoint \"(.*)\"$")
    public void check_health_endpoint(String url) {
        reset();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            exchangeResponse = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        } catch (HttpClientErrorException ex) {
            this.httpClientErrorException = ex;
        }
    }

    @Then("^Validate health response$")
    public void validate_health_response() {
        Assert.assertNotNull(exchangeResponse);
        Assert.assertEquals(HttpStatus.OK, exchangeResponse.getStatusCode());
        try {
            JSONObject jsonObject = new JSONObject(exchangeResponse.getBody());
            Assert.assertEquals("UP", jsonObject.get("status"));
        } catch (JSONException e) {
            e.printStackTrace();
            Assert.fail("error occurred while getting application health");
        }
    }

    private void reset() {
        exchangeResponse = null;
        httpClientErrorException = null;
    }

    @After
    public void close() {
        if (null != applicationContext) {
            applicationContext.close();
            applicationContext = null;
        }
    }

    @TestConfiguration
    @SpringBootTest
    public static class SpringTestConfig {
        @Bean
        CountDownLatch countDownLatch() {
            return new CountDownLatch(1);
        }

        @Bean
        ApplicationListener<ContextStartedEvent> springApplicationRunListener() {
            return new ApplicationStartedListener(countDownLatch());
        }

        public static class ApplicationStartedListener implements ApplicationListener<ContextStartedEvent> {
            private final CountDownLatch countDownLatch;

            public ApplicationStartedListener(CountDownLatch countDownLatch) {
                this.countDownLatch = countDownLatch;
            }

            @Override
            public void onApplicationEvent(ContextStartedEvent contextStartedEvent) {
                countDownLatch.countDown();
            }
        }
    }
}
