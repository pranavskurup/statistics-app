package bdd;

import app.statistics.n26.models.Transaction;
import cucumber.api.java.Before;
import cucumber.api.java.en.Then;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Pranav S Kurup on 3/29/2018.
 */
public class StepDoLoadTest {
    private WebTestClient client;

    @Before
    public void setup() {
        client = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + "5050")
                .build();
    }

    @Then("^Do load test$")
    public void do_load_test() throws InterruptedException {
        AtomicInteger atomicInteger = new AtomicInteger();
        ExecutorService executorService = Executors.newFixedThreadPool(25);
        int numberOfThreads = 20;
        Runnable producer = () -> {
            WebTestClient client = WebTestClient.bindToServer()
                    .baseUrl("http://localhost:" + "5050")
                    .build();
            IntStream
                    .rangeClosed(0, 10000000)
                    .forEach(index -> {
                        client.post().uri("/transactions")
                                .body(BodyInserters.fromObject(Transaction.builder().amount(12.50).timestamp(OffsetDateTime.now().toInstant().toEpochMilli()).build()))
                                .exchange()
                                .expectStatus().isEqualTo(HttpStatus.CREATED);
                        atomicInteger.getAndIncrement();
                    });
        };
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.execute(producer);
        }
        Thread.sleep(20000);
        EntityExchangeResult<HashMap> result = client.get().uri("/statistics").accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectStatus().isOk()
                .expectBody(HashMap.class).returnResult();

        Integer count = atomicInteger.get();
        HashMap statisticsResource = result.getResponseBody();
        assertThat((Integer) statisticsResource.get("count")).isBetween(count - 100, count);
    }
}
