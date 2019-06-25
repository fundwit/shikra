package com.fundwit.sys.shikra.authentication;

import com.fundwit.sys.shikra.util.IdWorker;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ApplicationAuthenticationTest {
    @Test
    public void test(){
        String username = "test-user";
        String nickname = "TestUser";
        String email = "test-user@test.com";
        long id = new IdWorker(0,0).nextId();
        List<GrantedAuthority> grantedAuthorityList = Arrays.asList(new SimpleGrantedAuthority("admin"));

        LoginUser loginUser = new LoginUser();
        loginUser.setUsername(username);
        loginUser.setNickname(nickname);
        loginUser.setEmail(email);
        loginUser.setId(id);

        ApplicationAuthentication applicationAuthentication = new ApplicationAuthentication(loginUser, grantedAuthorityList);
        assertTrue(applicationAuthentication.isAuthenticated());
        assertEquals(loginUser.getUsername(), applicationAuthentication.getName());
        assertEquals(loginUser, applicationAuthentication.getPrincipal());
        assertEquals(loginUser, applicationAuthentication.getDetails());
        assertEquals(grantedAuthorityList, applicationAuthentication.getAuthorities());
        assertEquals(null, applicationAuthentication.getCredentials());
        assertEquals(email, applicationAuthentication.getPrincipal().getEmail());
        assertEquals(nickname, applicationAuthentication.getPrincipal().getNickname());

        try{
            applicationAuthentication.setAuthenticated(true);
            assertTrue("IllegalArgumentException expected", false);
        }catch (IllegalArgumentException e){
            assertEquals("Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead", e.getMessage());
        }

        applicationAuthentication.setAuthenticated(false);
        assertEquals(false, applicationAuthentication.isAuthenticated());
    }
}