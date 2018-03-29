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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Pranav S Kurup on 3/29/2018.
 */
public class StepStatisticsTest {
    private ResponseEntity<String> exchangeResponse;
    private HttpClientErrorException httpClientErrorException;
    private RestTemplate restTemplate;

    @Before
    public void init() {
        restTemplate = new RestTemplate();
    }

    @When("^Check statistics endpoint \"(.*)\"$")
    public void check_statistics_endpoint(String url) {
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

    @Then("Validate empty statistics response")
    public void validate_transaction_endpoint_response_for_single_transaction() {
        Assert.assertNotNull(exchangeResponse);
        Assert.assertEquals(HttpStatus.OK, exchangeResponse.getStatusCode());
        Assert.assertNotNull(exchangeResponse.getBody());
        try {
            JSONObject jsonObject = new JSONObject(exchangeResponse.getBody());

            Assert.assertEquals(0d, jsonObject.getDouble("min"), 0);
            Assert.assertEquals(0d, jsonObject.getDouble("max"), 0);
            Assert.assertEquals(0d, jsonObject.getDouble("sum"), 0);
            Assert.assertEquals(0d, jsonObject.getDouble("avg"), 0);
            Assert.assertEquals(0, jsonObject.getDouble("count"), 0);
        } catch (JSONException e) {
            e.printStackTrace();
            Assert.fail("error occurred while getting application statistic endpoint response");
        }
    }

    @Then("^Validate statistics endpoint response for transaction with amount \"(.*)\"$")
    public void validate_statistics_endpoint_response_for_transaction_with_future_timestamp_with_amount(String amount) {
        Assert.assertNotNull(exchangeResponse);
        Assert.assertEquals(HttpStatus.OK, exchangeResponse.getStatusCode());
        Assert.assertNotNull(exchangeResponse.getBody());
        try {
            JSONObject jsonObject = new JSONObject(exchangeResponse.getBody());

            Assert.assertEquals(Double.valueOf(amount), jsonObject.getDouble("min"), 0);
            Assert.assertEquals(Double.valueOf(amount), jsonObject.getDouble("max"), 0);
            Assert.assertEquals(Double.valueOf(amount), jsonObject.getDouble("sum"), 0);
            Assert.assertEquals(Double.valueOf(amount), jsonObject.getDouble("avg"), 0);
            Assert.assertEquals(1, jsonObject.getDouble("count"), 0);
        } catch (JSONException e) {
            e.printStackTrace();
            Assert.fail("error occurred while getting application statistic endpoint response");
        }
    }

    @Then("^Validate statistics endpoint response for multiple transaction with amounts \"(.*)\"$")
    public void validate_statistics_endpoint_response_for_transaction_with_multiple_tx_with_amount(String amount) {
        Assert.assertNotNull(exchangeResponse);
        Assert.assertEquals(HttpStatus.OK, exchangeResponse.getStatusCode());
        Assert.assertNotNull(exchangeResponse.getBody());
        List<Double> amountList = Stream.of(amount.split(",")).map(Double::valueOf).collect(Collectors.toList());
        double sum=amountList.stream().mapToDouble(d->d).sum();
        double min =amountList.stream().mapToDouble(d->d).min().getAsDouble();
        double max =amountList.stream().mapToDouble(d->d).max().getAsDouble();
        double avg = sum / amountList.size();
        try {
            JSONObject jsonObject = new JSONObject(exchangeResponse.getBody());
            Assert.assertEquals(min, jsonObject.getDouble("min"), 0);
            Assert.assertEquals(max, jsonObject.getDouble("max"), 0);
            Assert.assertEquals(sum, jsonObject.getDouble("sum"), 0);
            Assert.assertEquals(avg, jsonObject.getDouble("avg"), 0);
            Assert.assertEquals(amountList.size(), jsonObject.getDouble("count"), 0);
        } catch (JSONException e) {
            e.printStackTrace();
            Assert.fail("error occurred while getting application statistic endpoint response");
        }
    }

    private void reset() {
        exchangeResponse = null;
        httpClientErrorException = null;
    }
}
