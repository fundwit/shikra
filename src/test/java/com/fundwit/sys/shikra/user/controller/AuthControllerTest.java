package com.fundwit.sys.shikra.user.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebTestClientAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.fundwit.sys.shikra.user.controller.AuthController.AUTH_LOGIN_ENDPOINT_PATH;

@RunWith(SpringRunner.class)
@TestPropertySource(properties = {
        "auth.form.endpoint=http://localhost:8081"
})
@ImportAutoConfiguration(WebTestClientAutoConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class AuthControllerTest {
    @Autowired
    private WebTestClient client;

    @Test
    public void testLoginUrl() {
        client.get().uri(AUTH_LOGIN_ENDPOINT_PATH)
                .exchange()
                .expectStatus().isFound()
                .expectHeader().valueEquals(HttpHeaders.LOCATION, "http://localhost:8081");
    }
}