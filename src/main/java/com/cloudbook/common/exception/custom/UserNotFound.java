package com.cloudbook.common.exception.custom;

public class UserNotFound extends RuntimeException {

    public UserNotFound() {
        super();
    }

    public UserNotFound(final String message) {
        super(message);
    }

}
