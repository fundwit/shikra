/*
 Copyright (c) 2008-2019. Fundwit All Rights Reserved.
 */

package com.fundwit.sys.shikra.authentication;

import com.fundwit.sys.shikra.user.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.lang.Assert;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class ApplicationAuthenticationProvider implements AuthenticationProvider {
    private UserService userService;
    private JwtService jwtService;

    public ApplicationAuthenticationProvider(UserService userService, JwtService jwtService){
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        LoginUser user;
        if(authentication instanceof UsernamePasswordAuthenticationToken) {
            user = userService.authenticate(authentication.getPrincipal().toString(), authentication.getCredentials().toString()).block();
        }else if(authentication instanceof BearerAuthenticationToken) {
            try {
                user = jwtService.verifyToken((String) authentication.getCredentials());
                Assert.notNull(user);
            }catch (ExpiredJwtException e){
                throw new CredentialsExpiredException("jwt token expired", e);
            } catch (Exception e){
                throw new BadCredentialsException("failed to parse bearer token", e);
            }
        }else{
            throw new BadCredentialsException("unsupported authentication " + authentication);
        }

        // loadUserDetails
        // .....

        return new ApplicationAuthentication(user, null);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication)
                || BearerAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
