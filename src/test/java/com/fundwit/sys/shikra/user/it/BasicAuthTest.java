package com.fundwit.sys.shikra.user.it;

import com.fundwit.sys.shikra.user.persistence.po.User;
import com.fundwit.sys.shikra.user.persistence.repository.IdentityRepository;
import com.fundwit.sys.shikra.user.persistence.repository.UserRepository;
import com.fundwit.sys.shikra.user.pojo.RegisterRequest;
import com.fundwit.sys.shikra.user.service.CaptchaService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.Base64Utils;

import java.io.UnsupportedEncodingException;
import java.time.Duration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BasicAuthTest {
    protected WebTestClient client;
    @LocalServerPort
    private int port;

    @Autowired
    private CaptchaService captchaService;

    @Autowired
    UserRepository userRepository;
    @Autowired
    IdentityRepository identityRepository;
    @Before
    public void setUp() {
        this.client = WebTestClient.bindToServer().baseUrl("http://localhost:" + this.port)
                .responseTimeout(Duration.ofMinutes(5))
                //.defaultHeader("")
                .build();
    }
    @After
    public void tearDown() {
        userRepository.deleteAll();
        userRepository.flush();
        identityRepository.deleteAll();
        identityRepository.flush();

    }

    private void registry(RegisterRequest registerRequest){
        registerRequest.setVerifyCode(captchaService.makeCaptcha("test2@test.com"));

        client.post().uri("/register").syncBody(registerRequest).exchange()
                .expectStatus().isCreated().expectBody(User.class).value(user -> {
            assertNotNull(user);
            assertNotNull(user.getId());
            assertEquals(registerRequest.getUsername(), user.getUsername());
            assertEquals(registerRequest.getNickname(), user.getNickname());
            assertEquals(registerRequest.getEmail(), user.getEmail());
        });
    }

    @Test
    public void testLoginBasicSuccess() throws UnsupportedEncodingException {
        RegisterRequest registerRequest = new RegisterRequest();

        registerRequest.setUsername("test2");
        registerRequest.setNickname("Test2");
        registerRequest.setEmail("test2@test.com");
        registerRequest.setPassword("abcd1234");
        registerRequest.setPasswordConfirm("abcd1234");

        this.registry(registerRequest);

        String header = Base64Utils.encodeToString((registerRequest.getUsername()+":"+registerRequest.getPassword()).getBytes("UTF-8"));

        client.get().uri("/users/self").header(HttpHeaders.AUTHORIZATION, "Basic "+header).exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("user", registerRequest.getUsername())
                .expectBody(User.class).value(user -> {
            assertNotNull(user);
            assertNotNull(user.getId());
            assertEquals(registerRequest.getUsername(), user.getUsername());
            assertEquals(registerRequest.getNickname(), user.getNickname());
            assertEquals(registerRequest.getEmail(), user.getEmail());
        });
    }

    @Test
    public void testLoginBasicFailedWithErrorPassword() throws UnsupportedEncodingException {
        RegisterRequest registerRequest = new RegisterRequest();

        registerRequest.setUsername("test2");
        registerRequest.setNickname("Test2");
        registerRequest.setEmail("test2@test.com");
        registerRequest.setPassword("abcd1234");
        registerRequest.setPasswordConfirm("abcd1234");

        this.registry(registerRequest);

        String header = Base64Utils.encodeToString((registerRequest.getUsername()+":ERROR_PASSWORD").getBytes("UTF-8"));

        // 401 Unauthorized
        // WWW-Authenticate: Basic realm="REALM_NAME"
        client.get().uri("/users/self").header(HttpHeaders.AUTHORIZATION, "Basic "+header).exchange()
                .expectStatus().isUnauthorized().expectHeader().valueEquals(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"Shikra\"");
    }

    @Test
    public void testAccessDeniedBeforeLogin() {
        // 401 Unauthorized
        // WWW-Authenticate: Basic realm="REALM_NAME"
        WebTestClient.ResponseSpec resp = client.get().uri("/users/self").exchange();

        resp.expectStatus().isUnauthorized().expectHeader().valueEquals(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"Shikra\"");
    }

//    @Test
//    public void testLoginRest() {
//        RegisterRequest registerRequest = new RegisterRequest();
//
//        registerRequest.setUsername("test2");
//        registerRequest.setEmail("test2@test.com");
//        registerRequest.setPassword("abcd1234");
//
//        client.post().uri("/auth/login").syncBody(registerRequest).exchange()
//                .expectStatus().isOk().expectBody(User.class).value(user -> {
//            assertNotNull(user);
//            assertNotNull(user.getId());
//            assertEquals(registerRequest.getUsername(), user.getUsername());
//            assertEquals(registerRequest.getNickname(), user.getNickname());
//            assertEquals(registerRequest.getEmail(), user.getEmail());
//        });
//    }

//    @Test
//    public void testLoginForm() {
//        RegisterRequest registerRequest = new RegisterRequest();
//
//        registerRequest.setUsername("test2");
//        registerRequest.setNickname("Test2");
//        registerRequest.setEmail("test2@test.com");
//        registerRequest.setPassword("abcd1234");
//        registerRequest.setPasswordConfirm("abcd1234");
//
//        this.registry(registerRequest);
//
//        client.post().uri("/auth/login").contentType(MediaType.APPLICATION_FORM_URLENCODED)
//                .syncBody("username="+URLEncoder.encode(registerRequest.getNickname(), Charset.forName("UTF-8"))
//                        +"&password="+URLEncoder.encode(registerRequest.getPassword(), Charset.forName("UTF-8")))
//                .exchange()
//                .expectStatus().isFound().expectHeader().valueEquals(HttpHeaders.LOCATION, "/");
//    }

//
//    @Test
//    public void testAccessSuccessAfterLogin(){
//        throw new RuntimeException("not implemented");
//    }
//
//    @Test
//    public void testAccessDeniedAfterLogout() {
//        throw new RuntimeException("not implemented");
//    }
}
