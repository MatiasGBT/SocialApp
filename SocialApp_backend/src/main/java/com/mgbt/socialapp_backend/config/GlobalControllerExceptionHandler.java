package com.mgbt.socialapp_backend.config;

import com.mgbt.socialapp_backend.utility_classes.InternalServerError;
import org.springframework.dao.DataAccessException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalControllerExceptionHandler {

    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<InternalServerError> handleInternalServerError(DataAccessException ex) {
        return new ResponseEntity<>(new InternalServerError("Database error", ex.getMessage() + ": " + ex.getMostSpecificCause().getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
