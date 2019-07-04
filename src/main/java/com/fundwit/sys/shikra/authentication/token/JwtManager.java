package com.fundwit.sys.shikra.authentication.token;

public interface JwtManager {
    String createToken();
    boolean verifyToken();
}
