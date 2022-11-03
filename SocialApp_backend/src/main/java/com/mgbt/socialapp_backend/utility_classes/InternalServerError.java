package com.mgbt.socialapp_backend.utility_classes;

public class InternalServerError {
    private String message;
    private String error;

    public InternalServerError(String message, String error) {
        this.message = message;
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public String getError() {
        return error;
    }
}
