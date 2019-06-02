package com.fundwit.sys.shikra.user.it;

import com.fundwit.sys.shikra.user.persistence.po.Identity;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.time.Duration;

import static com.fundwit.sys.shikra.user.service.UserServiceImpl.LOCAL_CREDENTIAL;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RegisterControllerTest {
    protected WebTestClient client;
    @LocalServerPort
    private int port;
    @Autowired
    UserRepository userRepository;
    @Autowired
    IdentityRepository identityRepository;
    @Autowired
    private CaptchaService captchaService;

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
    }

    @Test
    public void testRegisterRest() throws IOException {
        RegisterRequest registerRequest = new RegisterRequest();

        registerRequest.setUsername("test2");
        registerRequest.setNickname("Test2");
        registerRequest.setEmail("test2@test.com");
        registerRequest.setPassword("abcd1234");
        registerRequest.setPasswordConfirm("abcd1234");
        registerRequest.setVerifyCode(captchaService.makeCaptcha("test2@test.com"));

        client.post().uri("/register").syncBody(registerRequest).exchange()
                .expectStatus().isCreated().expectBody(User.class).value(user -> {
            assertNotNull(user);
            assertNotNull(user.getId());
            assertEquals(registerRequest.getUsername(), user.getUsername());
            assertEquals(registerRequest.getNickname(), user.getNickname());
            assertEquals(registerRequest.getEmail(), user.getEmail());

            // verfify Identity
            Identity identity = identityRepository.findByUserIdAndType(user.getId(), LOCAL_CREDENTIAL);
            assertEquals(LOCAL_CREDENTIAL, identity.getType());
            assertEquals(user.getId(), identity.getUserId());
            assertNotNull(identity.getCreateAt());
            assertNotNull(identity.getCredential());
            assertNotNull(identity.getId());
            assertNotNull(identity.getLastUpdateAt());
            assertNull(identity.getExternalId());
            assertEquals(identity.getCreateAt(), identity.getLastUpdateAt());
        });
    }

    @Test
    public void testRegisterHtml() throws IOException {
        String verifyCode = captchaService.makeCaptcha("test1@test.com");
        client.post().uri("/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .syncBody("username=test1&email=test1@test.com&password=abcd1234&passwordConfirm=abcd1234&verifyCode="+verifyCode)
                .exchange()
                .expectStatus().isFound()
                .expectHeader().valueEquals(HttpHeaders.LOCATION, "/");
    }

    @Test
    public void testRegisterWithErrorVerifyCode() {
        String badVerifyCode = "xxxxxxx";
        RegisterRequest registerRequest = new RegisterRequest();

        registerRequest.setUsername("test2");
        registerRequest.setNickname("Test2");
        registerRequest.setEmail("test2@test.com");
        registerRequest.setPassword("abcd1234");
        registerRequest.setPasswordConfirm("abcd1234");
        registerRequest.setVerifyCode(badVerifyCode);

        client.post().uri("/register").syncBody(registerRequest).exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    public void testNameExists() {
        String userName = "name4nameCheck";
        String testEmail = userName+"@test.com";
        client.post().uri("/register/usernames?query="+userName).exchange()
                .expectStatus().isEqualTo(HttpStatus.NO_CONTENT);

        String verifyCode = captchaService.makeCaptcha(testEmail);
        client.post().uri("/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .syncBody("username="+userName+"&email="+testEmail+"&password=abcd1234&passwordConfirm=abcd1234&verifyCode="+verifyCode)
                .exchange()
                .expectStatus().isFound();

        client.post().uri("/register/usernames?query="+userName).exchange()
                .expectStatus().value(v-> assertEquals(HttpStatus.CONFLICT.value(), v.intValue()));
    }

    @Test
    public void testEmailExists() {
        String userName = "name4emailCheck";
        String testEmail = userName+"@test.com";

        client.post().uri("/register/emails?query="+testEmail).exchange()
                .expectStatus().isEqualTo(HttpStatus.NO_CONTENT);

        String verifyCode = captchaService.makeCaptcha(testEmail);
        client.post().uri("/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .syncBody("username="+userName+"&email="+testEmail+"&password=abcd1234&passwordConfirm=abcd1234&verifyCode="+verifyCode)
                .exchange()
                .expectStatus().isFound();

        client.post().uri("/register/emails?query="+testEmail).exchange()
                .expectStatus().value(v-> assertEquals(HttpStatus.CONFLICT.value(), v.intValue()));
    }
}