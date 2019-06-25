/*
 Copyright (c) 2008-2019. Fundwit All Rights Reserved.
 */

package com.fundwit.sys.shikra.authentication;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class ApplicationAuthentication implements Authentication {
    private LoginUser principal;
    private boolean authenticated;
    private Collection<? extends GrantedAuthority> authorities;

    public ApplicationAuthentication(LoginUser principal, Collection<? extends  GrantedAuthority> authorities){
        this.principal = principal;
        this.authorities = authorities;
        this.authenticated = true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return principal;
    }

    @Override
    public LoginUser getPrincipal() {
        return principal;
    }

    @Override
    public boolean isAuthenticated() {
        return this.authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (isAuthenticated) {
            throw new IllegalArgumentException(
                    "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        }

        this.authenticated = false;
    }

    @Override
    public String getName() {
        return principal.getUsername();
    }
}
