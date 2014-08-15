package dataxu.intranet.test.integration;

import dataxu.intranet.Application;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class ApplicationIT {
    private static ConfigurableApplicationContext context;

    @BeforeClass
    public static void start() throws Exception {
        Callable<ConfigurableApplicationContext> callable = () -> SpringApplication.run(Application.class);
        Future<ConfigurableApplicationContext> future = Executors.newSingleThreadExecutor().submit(callable);
        context = future.get(60, TimeUnit.SECONDS);
    }

    @AfterClass
    public static void stop() {
        if (context != null) {
            context.close();
        }
    }

    @Test
    public void testExample() {
        ResponseEntity<String> entity = getRestTemplate().getForEntity(
                "http://localhost:8080/example", String.class);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK) ;
        String body = entity.getBody();
        assertThat(body).contains("<title>Thymleaf Example</title>") ;
        assertThat(body).contains("<p>Welcome to Thymleaf !</p>") ;
    }

    @Test
    public void testAnotherExample() {
        ResponseEntity<String> entity = getRestTemplate().getForEntity(
                "http://localhost:8080/another/example", String.class);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK) ;
        assertThat(entity.getBody()).contains("<title>Thymleaf Example-2</title>") ;
        assertThat(entity.getBody()).contains("<p>Another Thymleaf page...</p>") ;
    }

    @Test
    public void testNotFound() {
        ResponseEntity<String> entity = getRestTemplate().getForEntity(
                "http://localhost:8080/exampleDoesNotExists", String.class);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND) ;
    }

    private RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
            }
        });
        return restTemplate;

    }
}
