package com.fundwit.sys.shikra.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JwtServiceTest {
    @Test
    public void testBuildAndParse() {
        ObjectMapper objectMapper = new ObjectMapper();
        JwtService service = new JwtService(objectMapper, new JwtServiceProperties());

        long id = 123L;
        String nickname = "Tom";
        String username = "tom";
        String token = service.buildToken(this.buildApplicationAuthentication(id, username, nickname));
        LoginUser result = service.verifyToken(token);
        assertEquals(id, result.getId().longValue());
        assertEquals(nickname, result.getNickname());
        assertEquals(username, result.getUsername());

        assertNull(service.buildToken(new UsernamePasswordAuthenticationToken("test", "test", null)));
    }
    @Test(expected = TokenGenerateException.class)
    public void testTokenGenerateFailed() {
        JwtService service = new JwtService(null, new JwtServiceProperties());  // make a NPE
        service.buildToken(this.buildApplicationAuthentication(123L, "tom", "Tom"));
    }
    @Test(expected = TokenVerifyException.class)
    public void testParseFailed() throws IOException {
        JwtService service = new JwtService(new ObjectMapper(), new JwtServiceProperties());
        String token = service.buildToken(this.buildApplicationAuthentication(123L, "tom", "Tom"));

        ObjectMapper objectMapper = mock(ObjectMapper.class);
        when(objectMapper.readValue(isA(String.class), isA(Class.class))).thenThrow(new IOException("mock exception")); // make a NEP
        service = new JwtService(objectMapper, new JwtServiceProperties());
        service.verifyToken(token);
    }


    private ApplicationAuthentication buildApplicationAuthentication(long id, String username, String nickname) {
        LoginUser loginUser = new LoginUser();
        loginUser.setId(id);
        loginUser.setNickname(nickname);
        loginUser.setUsername(username);
        ApplicationAuthentication applicationAuthentication = new ApplicationAuthentication(loginUser, null);
        return applicationAuthentication;
    }
}