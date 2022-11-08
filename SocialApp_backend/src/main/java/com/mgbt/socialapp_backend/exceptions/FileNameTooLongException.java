package com.mgbt.socialapp_backend.exceptions;

public class FileNameTooLongException extends RuntimeException {
    public FileNameTooLongException(String errorMessage) {
        super(errorMessage);
    }
}
