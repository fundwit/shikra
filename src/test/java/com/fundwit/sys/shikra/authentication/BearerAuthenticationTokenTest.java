package com.fundwit.sys.shikra.authentication;

import org.junit.Test;

import static org.junit.Assert.*;

public class BearerAuthenticationTokenTest {
    @Test
    public void test() {
        String token = "test_token";
        BearerAuthenticationToken authenticationToken = new BearerAuthenticationToken(token);
        assertEquals(token, authenticationToken.getCredentials());
        assertNull(authenticationToken.getAuthorities());
        assertNull(authenticationToken.getName());
        assertNull(authenticationToken.getDetails());
        assertNull(authenticationToken.getPrincipal());
        assertEquals(false, authenticationToken.isAuthenticated());

        try {
            authenticationToken.setAuthenticated(true);
            assertTrue("unexpected status",false);
        }catch (IllegalArgumentException e){
        }
    }
}