package com.fundwit.sys.shikra.user.controller;

import com.fundwit.sys.shikra.email.RawEmailMessage;
import com.fundwit.sys.shikra.email.SmtpServerRule;
import com.fundwit.sys.shikra.user.persistence.repository.IdentityRepository;
import com.fundwit.sys.shikra.user.persistence.repository.UserRepository;
import com.fundwit.sys.shikra.user.pojo.VerifyCodeRequest;
import com.fundwit.sys.shikra.user.service.CaptchaService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import javax.mail.MessagingException;
import java.io.IOException;
import java.time.Duration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.mail.host=localhost",
        "spring.mail.sender=testSender@qq.com",
        "spring.mail.properties.mail.smtp.auth=false",
        "spring.mail.properties.mail.smtp.starttls.enable=false",
        "spring.mail.properties.mail.smtp.starttls.required=false"
})
public class VerifierControllerTest {
    @Rule
    public SmtpServerRule smtpServerRule = new SmtpServerRule();

    protected WebTestClient client;
    @LocalServerPort
    private int port;
    @Autowired
    UserRepository userRepository;
    @Autowired
    IdentityRepository identityRepository;
    @Autowired
    private CaptchaService captchaService;
    @Autowired
    private JavaMailSenderImpl javaMailSender;

    @Before
    public void setUp() {
        this.javaMailSender.setPort(smtpServerRule.getPort());
        this.client = WebTestClient.bindToServer().baseUrl("http://localhost:" + this.port)
                .responseTimeout(Duration.ofMinutes(5))
                //.defaultHeader("")
                .build();
    }

    @Test
    public void testVerifier() throws IOException, MessagingException {
        VerifyCodeRequest request = new VerifyCodeRequest();
        request.setEmail("xxx@test.com");
        client.post().uri("/verifier/email")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .syncBody(request).exchange()
                .expectStatus().isNoContent();

        assertEquals(1, smtpServerRule.getReceivedMails().size());
        RawEmailMessage message = smtpServerRule.getReceivedMails().get(0);
        assertEquals("xxx@test.com", message.getRecipient());
        assertEquals("testSender@qq.com", message.getFrom());
        assertEquals("[Shikra] 验证码", message.getSubject());
        assertTrue(message.getTextContent().contains("您的验证码为："));
    }
}