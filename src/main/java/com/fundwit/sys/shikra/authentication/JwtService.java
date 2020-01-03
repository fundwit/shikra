package com.fundwit.sys.shikra.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;

@Service
public class JwtService {
    private final static String JWT_SIGN_SECRET = "MyJwtSecret";

    private JwtServiceProperties properties;
    private ObjectMapper objectMapper;
    private final LoginUserJwtHandler jwtHandler;

    public JwtService(ObjectMapper objectMapper, JwtServiceProperties properties) {
        this.objectMapper = objectMapper;
        this.properties = properties;
        jwtHandler = new LoginUserJwtHandler(objectMapper);
    }

    public String buildToken(Authentication authentication) {
        if(authentication instanceof ApplicationAuthentication) {
            ApplicationAuthentication applicationAuthentication = (ApplicationAuthentication)authentication;
            try {
                return Jwts.builder()
                        .setSubject(objectMapper.writeValueAsString(applicationAuthentication.getPrincipal()))
                        .setExpiration(new Date(System.currentTimeMillis() + properties.getExpiration().toMillis()))
                        .signWith(SignatureAlgorithm.HS512, JWT_SIGN_SECRET)
                        .compact();
            } catch (Exception e) {
                throw new TokenGenerateException(e);
            }
        }else{
            return null;
        }
    }

    public LoginUser verifyToken(String token) {
        try {
            return Jwts.parser().setSigningKey(JWT_SIGN_SECRET).parse(token, jwtHandler);
        }catch (Exception e){
            throw new TokenVerifyException("failed to parse jws: "+token, e);
        }
    }

    public static class LoginUserJwtHandler extends JwtHandlerAdapter<LoginUser> {
        public ObjectMapper objectMapper;
        public LoginUserJwtHandler(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }
        @Override
        public LoginUser onClaimsJws(Jws jws) {
            try {
                Claims claims = (Claims) jws.getBody();
                return objectMapper.readValue(claims.getSubject(), LoginUser.class);
            } catch (IOException e) {
                throw new RuntimeException("failed to parse jws body: " + jws.getBody(), e);
            }
        }
    }
}
