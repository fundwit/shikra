package com.fundwit.sys.shikra.user.controller;

import com.fundwit.sys.shikra.authentication.JwtServiceProperties;
import com.fundwit.sys.shikra.user.persistence.po.User;
import com.fundwit.sys.shikra.util.UserHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebTestClientAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.Base64Utils;

import java.time.Duration;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
@TestPropertySource(properties = {
        "auth.form.endpoint=http://localhost:8081"
})
@ImportAutoConfiguration(WebTestClientAutoConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Import({UserHelper.class})
public class AuthControllerTest {
    @Autowired
    private WebTestClient client;
    @Autowired
    private UserHelper userHelper;

    @Value("${auth.login.path}")
    public String authLoginPath;

    @MockBean
    private JwtServiceProperties properties;

    @Before
    public void setUp(){
        when(properties.getExpiration()).thenReturn(Duration.ofMinutes(30));
    }

    @Test
    public void testLoginSuccessJson() {
        // register user
        String username = "testUserSuccessJson";
        String password = "testPassword";

        User user = userHelper.registerUser(username, password).block();

        // login in
        client.post().uri(authLoginPath)
                .header(HttpHeaders.AUTHORIZATION, "basic "+ Base64Utils.encodeToString((username+":"+password).getBytes()))
                .header(HttpHeaders.ACCEPT, MediaType.TEXT_HTML_VALUE)
                .exchange()
                .expectStatus().isFound()
                .expectHeader().valueEquals(HttpHeaders.LOCATION, "http://localhost:8081")
                .expectHeader().value(HttpHeaders.SET_COOKIE, v-> assertTrue(v.startsWith("auth_token=")));
    }

    @Test
    public void testLoginSuccessHtml() {
        // register user
        String username = "testUserSuccessHtml";
        String password = "testPassword";

        User user = userHelper.registerUser(username, password).block();

        // login in
        client.post().uri(authLoginPath)
                .header(HttpHeaders.AUTHORIZATION, "basic "+ Base64Utils.encodeToString((username+":"+password).getBytes()))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().value(HttpHeaders.SET_COOKIE, v-> assertTrue(v.startsWith("auth_token=")));
    }

    @Test
    public void testLoginFailedWithNonExistUser() {
        // login in
        client.post().uri(authLoginPath)
                .header(HttpHeaders.AUTHORIZATION, "basic "+ Base64Utils.encodeToString("not-exist:not-exist".getBytes()))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    public void testLoginFailedWithWrongPassword() {
        // register user
        String username = "testUserWWP";
        String password = "testPasswordWWP";
        User user = userHelper.registerUser(username, password).block();

        // login in
        client.post().uri(authLoginPath)
                .header(HttpHeaders.AUTHORIZATION, "basic "+ Base64Utils.encodeToString((username+":badPasswordWWP").getBytes()))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    public void testSuccessAccessWithToken() {
        // register user
        String username = "testSuccessAccessWithToken";
        String password = "testPassword";
        userHelper.registerUser(username, password).block();

        String tokenCookieName = "auth_token=";
        // login in
        client.post().uri(authLoginPath)
                .header(HttpHeaders.AUTHORIZATION, "basic "+ Base64Utils.encodeToString((username+":"+password).getBytes()))
                .header(HttpHeaders.ACCEPT, MediaType.TEXT_HTML_VALUE)
                .exchange()
                .expectStatus().isFound()
                .expectHeader().valueEquals(HttpHeaders.LOCATION, "http://localhost:8081")
                .expectHeader().value(HttpHeaders.SET_COOKIE, v-> {
                    assertTrue(v.startsWith(tokenCookieName));
                    int startIndex = v.indexOf(tokenCookieName)+tokenCookieName.length();
                    int endIndex = v.indexOf(";", startIndex);
                    String token = endIndex > 0 ? v.substring(startIndex, endIndex) : v.substring(startIndex);

                    client.get().uri("/users/self")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer "+ token)
                            .exchange()
                            .expectStatus().isOk();
                });

    }
    @Test
    public void testFailedAccessWithErrorToken() {
        String username = "testFailedAccessWithErrorToken";
        String password = "testPassword";
        userHelper.registerUser(username, password).block();

        String tokenCookieName = "auth_token=";
        // login in
        client.post().uri(authLoginPath)
                .header(HttpHeaders.AUTHORIZATION, "basic "+ Base64Utils.encodeToString((username+":"+password).getBytes()))
                .header(HttpHeaders.ACCEPT, MediaType.TEXT_HTML_VALUE)
                .exchange()
                .expectStatus().isFound()
                .expectHeader().valueEquals(HttpHeaders.LOCATION, "http://localhost:8081")
                .expectHeader().value(HttpHeaders.SET_COOKIE, v-> {
            assertTrue(v.startsWith(tokenCookieName));

            client.get().uri("/users/self")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer bad_bearer_token")
                    .exchange()
                    .expectStatus().isUnauthorized();
        });
    }

    @Test
    public void testFailedAccessWithExpiredToken() {
        when(properties.getExpiration()).thenReturn(Duration.ofSeconds(1));

        String username = "testFailedAccessWithExpiredToken";
        String password = "testPassword";
        userHelper.registerUser(username, password).block();

        String tokenCookieName = "auth_token=";
        // login in
        client.post().uri(authLoginPath)
                .header(HttpHeaders.AUTHORIZATION, "basic "+ Base64Utils.encodeToString((username+":"+password).getBytes()))
                .header(HttpHeaders.ACCEPT, MediaType.TEXT_HTML_VALUE)
                .exchange()
                .expectStatus().isFound()
                .expectHeader().valueEquals(HttpHeaders.LOCATION, "http://localhost:8081")
                .expectHeader().value(HttpHeaders.SET_COOKIE, v-> {
            assertTrue(v.startsWith(tokenCookieName));
            int startIndex = v.indexOf(tokenCookieName)+tokenCookieName.length();
            int endIndex = v.indexOf(";", startIndex);
            String token = endIndex > 0 ? v.substring(startIndex, endIndex) : v.substring(startIndex);

            client.get().uri("/users/self")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer "+token)
                    .exchange()
                    .expectStatus().isOk();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            client.get().uri("/users/self")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer "+token)
                    .exchange()
                    .expectStatus().isUnauthorized();
        });
    }
}