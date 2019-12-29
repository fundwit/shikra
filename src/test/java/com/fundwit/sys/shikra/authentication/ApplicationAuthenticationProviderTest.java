package com.fundwit.sys.shikra.authentication;

import org.junit.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.TestingAuthenticationToken;

import static org.junit.Assert.assertEquals;

public class ApplicationAuthenticationProviderTest {
    @Test(expected = BadCredentialsException.class)
    public void test() {
        ApplicationAuthenticationProvider provider = new ApplicationAuthenticationProvider(null, null);
        TestingAuthenticationToken token = new TestingAuthenticationToken(null, null);
        assertEquals(false, provider.supports(TestingAuthenticationToken.class));

        provider.authenticate(token);
    }
}