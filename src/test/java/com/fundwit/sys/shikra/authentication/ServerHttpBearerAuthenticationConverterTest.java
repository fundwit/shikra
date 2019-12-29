package com.fundwit.sys.shikra.authentication;

import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.core.Authentication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ServerHttpBearerAuthenticationConverterTest {
    @Test
    public void test(){
        ServerHttpBearerAuthenticationConverter converter = new ServerHttpBearerAuthenticationConverter();

        assertNull(converter.convert(MockServerWebExchange.builder(MockServerHttpRequest.get("/")).build()).block());
        assertNull(converter.convert(MockServerWebExchange.builder(MockServerHttpRequest.get("/").header(HttpHeaders.AUTHORIZATION, "test")).build()).block());

        assertEquals("", converter.convert(MockServerWebExchange.builder(MockServerHttpRequest.get("/")
                .header(HttpHeaders.AUTHORIZATION, ServerHttpBearerAuthenticationConverter.TOKEN_TYPE)).build()).block().getCredentials());

        String token = "test_token";
        Authentication authentication = converter.convert(MockServerWebExchange.builder(MockServerHttpRequest.get("/")
                .header(HttpHeaders.AUTHORIZATION, ServerHttpBearerAuthenticationConverter.TOKEN_TYPE + token)).build()).block();
        assertEquals(token, authentication.getCredentials());
    }
}