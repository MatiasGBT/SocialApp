package com.mgbt.socialapp_backend.exceptions;

public class FileNameTooLong extends RuntimeException {
    public FileNameTooLong(String errorMessage) {
        super(errorMessage);
    }
}
