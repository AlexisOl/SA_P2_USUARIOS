package com.user.microservice.common.errors;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String m) {
        super(m);
    }
}
