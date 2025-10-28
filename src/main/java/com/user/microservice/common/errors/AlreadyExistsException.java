package com.user.microservice.common.errors;

public class AlreadyExistsException extends RuntimeException {

    public AlreadyExistsException(String m) {
        super(m);
    }
}


