package com.fundwit.sys.shikra.actuator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Duration;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HealthCheckControllerTest {
    protected WebTestClient client;
    @LocalServerPort
    private int port;

    @Before
    public void setUp() {
        this.client = WebTestClient.bindToServer().baseUrl("http://localhost:" + this.port)
                .responseTimeout(Duration.ofMinutes(5))
                //.defaultHeader("")
                .build();
    }

    @Test
    public void testIsAlive() {
        client.get().uri("/health/liveness").exchange()
                .expectStatus().isOk();
    }
    @Test
    public void testIsReady() {
        client.get().uri("/health/readiness").exchange()
                .expectStatus().isOk();
    }
}