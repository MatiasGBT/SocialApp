package com.mgbt.socialapp_backend.exceptions;

public class EntityAlreadyExistsException extends Exception {
    public EntityAlreadyExistsException(String errorMessage) {
        super(errorMessage);
    }
}
