package com.fundwit.sys.shikra.user.mock;

import com.fundwit.sys.shikra.authentication.ApplicationAuthentication;
import com.fundwit.sys.shikra.authentication.LoginUser;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class MockBasicAuthenticationProvider implements AuthenticationProvider {
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        LoginUser user = new LoginUser();
        user.setId(0L);
        user.setEmail("mockUser@test.com");
        user.setUsername("mockUser");
        user.setNickname("Mock User");
        return new ApplicationAuthentication(user, null);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
