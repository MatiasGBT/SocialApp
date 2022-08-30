package com.mgbt.socialapp_backend.controller;

import com.mgbt.socialapp_backend.model.entity.UserApp;
import com.mgbt.socialapp_backend.model.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("api/navbar/")
@PreAuthorize("isAuthenticated()")
public class NavbarController {

    @Autowired
    private UserService userService;

    @Autowired
    MessageSource messageSource;

    @GetMapping("/autocomplete/{name}&{keycloakName}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> filter(@PathVariable String name, @PathVariable String keycloakName, Locale locale) {
        try {
            List<UserApp> users = userService.filter(name, keycloakName);
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (DataAccessException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", messageSource.getMessage("error.database", null, locale));
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/autocomplete/full/{name}&{keycloakName}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> filterWithoutLimit(@PathVariable String name, @PathVariable String keycloakName,
                                                Locale locale) {
        try {
            List<UserApp> users = userService.filterWithoutLimit(name, keycloakName);
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (DataAccessException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", messageSource.getMessage("error.database", null, locale));
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
