package com.fundwit.sys.shikra.authentication;

import com.fundwit.sys.shikra.util.UserHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebTestClientAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;

import static org.junit.Assert.assertEquals;


@RunWith(SpringRunner.class)
@TestPropertySource(properties = {
        "jwt.expiration=2d"
})
@ImportAutoConfiguration(WebTestClientAutoConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Import({UserHelper.class})
public class JwtServicePropertiesTest {
    @Autowired
    private JwtServiceProperties properties;

    @Test
    public void testProperties() {
        assertEquals(Duration.ofDays(2), properties.getExpiration());
    }
}