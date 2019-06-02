package com.fundwit.sys.shikra.database;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ImportAutoConfiguration(exclude = {FlywayAutoConfiguration.class})
@TestPropertySource(properties = {
        "application.test=false",
        "spring.datasource.url=jdbc:h2:mem:test;MODE=MySQL",
        "spring.datasource.username=test1",
        "spring.datasource.password=xxxx",
        "spring.datasource.name=sjfdskdf",
        "spring.datasource.schemaUsername=admin1",
        "spring.datasource.schemaPassword=adminpass1"
})
public class DatabaseConfigurationTest {
    protected WebTestClient client;
    @LocalServerPort
    private int port;
    @Autowired
    HikariDataSource dataSource;

    @Test
    public void testNonEmbeddedDataSource(){
        assertEquals("test1", dataSource.getUsername());
        assertEquals("xxxx", dataSource.getPassword());
        assertEquals("jdbc:h2:mem:test;MODE=MySQL", dataSource.getJdbcUrl());
        assertEquals("sjfdskdf", dataSource.getPoolName());
    }
}