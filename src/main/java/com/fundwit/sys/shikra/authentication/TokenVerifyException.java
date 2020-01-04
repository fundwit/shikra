package com.fundwit.sys.shikra.authentication;

public class TokenVerifyException extends RuntimeException {
    public TokenVerifyException(String message, Throwable cause) {
        super(message, cause);
    }
}
