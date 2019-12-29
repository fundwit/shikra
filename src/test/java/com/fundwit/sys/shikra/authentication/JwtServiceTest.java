package com.fundwit.sys.shikra.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JwtServiceTest {
    @Test
    public void testBuildAndParse() {
        ObjectMapper objectMapper = new ObjectMapper();
        JwtService service = new JwtService(objectMapper, new JwtServiceProperties());

        LoginUser loginUser = new LoginUser();
        loginUser.setId(123L);
        loginUser.setNickname("Tom");
        loginUser.setUsername("tom");
        ApplicationAuthentication applicationAuthentication = new ApplicationAuthentication(loginUser, null);
        String token = service.buildToken(applicationAuthentication);
        LoginUser result = service.verifyToken(token);
        assertEquals(loginUser.getId(), result.getId());
        assertEquals(loginUser.getNickname(), result.getNickname());
        assertEquals(loginUser.getUsername(), result.getUsername());
    }
}