package com.fundwit.sys.shikra.authentication;

import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerFormLoginAuthenticationConverter;
import org.springframework.security.web.server.authentication.ServerHttpBasicAuthenticationConverter;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DelegatingServerAuthenticationConverterTest {
    @Test
    public void testEmpty(){
        DelegatingServerAuthenticationConverter converter = new DelegatingServerAuthenticationConverter(null);
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/test1"));
        Authentication authentication = converter.convert(exchange).block();
        assertEquals(null, authentication);

        converter = new DelegatingServerAuthenticationConverter(new ArrayList<>());
        authentication = converter.convert(exchange).block();
        assertEquals(null, authentication);
    }

    @Test
    public void testFirstMatch() {
        ServerFormLoginAuthenticationConverter formLoginAuthenticationConverter = new ServerFormLoginAuthenticationConverter();
        ServerHttpBasicAuthenticationConverter  basicAuthenticationConverter = new ServerHttpBasicAuthenticationConverter();

        DelegatingServerAuthenticationConverter converter = new DelegatingServerAuthenticationConverter(
                Arrays.asList(formLoginAuthenticationConverter, basicAuthenticationConverter));
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/test1")
                .header(HttpHeaders.AUTHORIZATION, "Basic "+ Base64Utils.encodeToString("aaa:bbb".getBytes())));
        Authentication authentication = converter.convert(exchange).block();
        assertTrue(!StringUtils.hasText(authentication.getName()));

        converter = new DelegatingServerAuthenticationConverter(
                Arrays.asList(basicAuthenticationConverter, formLoginAuthenticationConverter));
        exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/test1")
                .header(HttpHeaders.AUTHORIZATION, "Basic "+ Base64Utils.encodeToString("aaa:bbb".getBytes())));
        authentication = converter.convert(exchange).block();
        assertEquals("aaa", authentication.getName());
    }

    @Test
    public void testLastMatch() {
        ServerHttpBasicAuthenticationConverter  basicAuthenticationConverter = new ServerHttpBasicAuthenticationConverter();

        DelegatingServerAuthenticationConverter converter = new DelegatingServerAuthenticationConverter(
                Arrays.asList(exchange -> Mono.empty(), basicAuthenticationConverter));
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/test1")
                .header(HttpHeaders.AUTHORIZATION, "Basic "+ Base64Utils.encodeToString("aaa:bbb".getBytes())));
        Authentication authentication = converter.convert(exchange).block();
        assertEquals("aaa", authentication.getName());
    }

    @Test
    public void testNoMatch() {
        ServerHttpBasicAuthenticationConverter  basicAuthenticationConverter = new ServerHttpBasicAuthenticationConverter();

        DelegatingServerAuthenticationConverter converter = new DelegatingServerAuthenticationConverter(Arrays.asList(exchange -> Mono.empty(), basicAuthenticationConverter));
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/test1"));
        Authentication authentication = converter.convert(exchange).block();
        assertEquals(null, authentication);
    }
}