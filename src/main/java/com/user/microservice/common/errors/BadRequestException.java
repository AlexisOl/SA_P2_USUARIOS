package com.user.microservice.common.errors;

public class BadRequestException extends RuntimeException {

    public BadRequestException(String m) {
        super(m);
    }
}
