package com.fundwit.sys.shikra.actuator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.time.Duration;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "info.build.path=classpath:/testBuildInfo.properties"
})
public class BuildInfoControllerTest2 {
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
    public void testBuildInfo() throws IOException {
        String versionName = "test1111";
        client.get().uri("/build").exchange()
                .expectStatus().isOk()
                .expectBody(BuildInfoProperties.class)
                .value(p->
                        Assert.assertEquals(versionName, p.getVersion()));
    }
}