package bdd;

import cucumber.api.java.Before;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Clock;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Created by Pranav S Kurup on 3/29/2018.
 */
public class StepTransactionTest {
    private ResponseEntity<String> exchangeResponse;
    private HttpClientErrorException httpClientErrorException;
    private RestTemplate restTemplate;
    public static final Double[] TRANSACTION_AMOUNTS = new Double[]{4d, 8d, 213d, 32d};

    @Before
    public void init() {
        restTemplate = new RestTemplate();
    }

    @When("^Post empty JSON request body to \"(.*)\"$")
    public void post_empty_JSON_request_body_transaction_endpoint(String url) {
        reset();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<?> entity = new HttpEntity<>("{}", headers);
        try {
            exchangeResponse = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        } catch (HttpClientErrorException ex) {
            this.httpClientErrorException = ex;
        }
    }

    @Then("Validate transaction endpoint response for empty JSON request body")
    public void validate_transaction_endpoint_response_for_empty_JSON_request_body() {
        Assert.assertNotNull(httpClientErrorException);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, httpClientErrorException.getStatusCode());
        try {
            JSONObject jsonObject = new JSONObject(httpClientErrorException.getResponseBodyAsString());
            Assert.assertEquals("100-" + HttpStatus.BAD_REQUEST.value(), jsonObject.get("errorCode"));
        } catch (JSONException e) {
            e.printStackTrace();
            Assert.fail("error occurred while parsing JSON");
        }
    }


    @When("^Post transaction with amount negative to \"(.*)\"$")
    public void post_transaction_with_amount_negative_transaction_endpoint(String url) {
        reset();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> body = new HashMap<>();
        body.put("amount", -50);
        body.put("timestamp", Clock.systemUTC().millis());
        HttpEntity<?> entity = new HttpEntity<>(body, headers);
        try {
            exchangeResponse = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        } catch (HttpClientErrorException ex) {
            this.httpClientErrorException = ex;
        }
    }

    @Then("Validate transaction endpoint response for transaction with amount negative")
    public void validate_transaction_endpoint_response_for_transaction_with_amount_negative() {
        Assert.assertNotNull(httpClientErrorException);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, httpClientErrorException.getStatusCode());
        try {
            JSONObject jsonObject = new JSONObject(httpClientErrorException.getResponseBodyAsString());
            Assert.assertEquals("100-" + HttpStatus.BAD_REQUEST.value(), jsonObject.get("errorCode"));
        } catch (JSONException e) {
            e.printStackTrace();
            Assert.fail("error occurred while parsing JSON");
        }
    }


    @When("^Post transaction with old timestamp to \"(.*)\"$")
    public void post_transaction_with_old_timestamp_transaction_endpoint(String url) {
        reset();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> body = new HashMap<>();
        body.put("amount", 50);
        body.put("timestamp", Clock.systemUTC().millis() - 90000);
        HttpEntity<?> entity = new HttpEntity<>(body, headers);
        try {
            exchangeResponse = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        } catch (HttpClientErrorException ex) {
            this.httpClientErrorException = ex;
        }
    }

    @Then("Validate transaction endpoint response for transaction with old timestamp")
    public void validate_transaction_endpoint_response_for_transaction_with_old_timestamp() {
        Assert.assertNotNull(exchangeResponse);
        Assert.assertEquals(HttpStatus.NO_CONTENT, exchangeResponse.getStatusCode());
        Assert.assertEquals(null, exchangeResponse.getBody());
    }

    @When("^Post transaction with future timestamp with amount \"(.*)\" to \"(.*)\"$")
    public void post_transaction_with_future_timestamp_transaction_endpoint(String amount, String url) {
        reset();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> body = new HashMap<>();
        body.put("amount", Double.valueOf(amount));
        body.put("timestamp", Clock.systemUTC().millis() + 90000);
        HttpEntity<?> entity = new HttpEntity<>(body, headers);
        try {
            exchangeResponse = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        } catch (HttpClientErrorException ex) {
            this.httpClientErrorException = ex;
        }
    }

    @Then("Validate transaction endpoint response for single transaction")
    public void validate_transaction_endpoint_response_for_single_transaction() {
        Assert.assertNotNull(exchangeResponse);
        Assert.assertEquals(HttpStatus.CREATED, exchangeResponse.getStatusCode());
        Assert.assertEquals(null, exchangeResponse.getBody());
    }

    @When("^Post single transaction with valid time and with amount \"(.*)\" \"(.*)\"$")
    public void post_single_transaction_with_valid_time_transaction_endpoint(String amount, String url) {
        reset();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> body = new HashMap<>();
        body.put("amount", Double.valueOf(amount));
        body.put("timestamp", Clock.systemUTC().millis());
        HttpEntity<?> entity = new HttpEntity<>(body, headers);
        try {
            exchangeResponse = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        } catch (HttpClientErrorException ex) {
            this.httpClientErrorException = ex;
        }
    }

    @When("^Post single transaction with amount \"(.*)\" \"(.*)\"$")
    public void post_multiple_transaction_with_valid_time_transaction_endpoint(String amounts,String url) {
        reset();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> body = new HashMap<>();
        HttpEntity<?> entity = new HttpEntity<>(body, headers);
        Stream.of(amounts.split(",")).parallel().forEach(amt -> {
            try {
                body.put("amount", Double.valueOf(amt));
                body.put("timestamp", Clock.systemUTC().millis());
                exchangeResponse = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
                Assert.assertEquals(HttpStatus.CREATED, exchangeResponse.getStatusCode());
                Assert.assertEquals(null, exchangeResponse.getBody());
            } catch (HttpClientErrorException ex) {
                this.httpClientErrorException = ex;
            }
        });

    }

    private void reset() {
        exchangeResponse = null;
        httpClientErrorException = null;
    }
}
