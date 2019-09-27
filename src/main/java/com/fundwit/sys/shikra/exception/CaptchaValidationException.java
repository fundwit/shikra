/*
 Copyright (c) 2008-2019. Fundwit All Rights Reserved.
 */

package com.fundwit.sys.shikra.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CaptchaValidationException extends RuntimeException {
    public CaptchaValidationException(String message) {
        super(message);
    }
}
