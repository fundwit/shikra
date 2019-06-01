package com.fundwit.sys.shikra.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceType, String keyName, String keyValue) {
        super(ResourceNotFoundException.buildMessage(resourceType, keyName, keyValue));
    }

    public ResourceNotFoundException(String resourceType, String keyName, String keyValue, Throwable cause) {
        super(ResourceNotFoundException.buildMessage(resourceType, keyName, keyValue), cause);
    }

    public static String buildMessage(String resourceType, String keyName, String keyValue) {
        return String.format("can not find a resource [%s] with [%s] matches [%s]",
                resourceType, keyName, keyValue);
    }
}
