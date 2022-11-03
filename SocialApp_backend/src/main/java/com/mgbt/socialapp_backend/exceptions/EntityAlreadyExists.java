package com.mgbt.socialapp_backend.exceptions;

public class EntityAlreadyExists extends Exception {
    public EntityAlreadyExists(String errorMessage) {
        super(errorMessage);
    }
}
