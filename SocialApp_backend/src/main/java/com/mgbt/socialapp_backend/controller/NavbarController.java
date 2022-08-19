package com.mgbt.socialapp_backend.controller;

import com.mgbt.socialapp_backend.model.entity.UserApp;
import com.mgbt.socialapp_backend.model.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/navbar/")
@PreAuthorize("isAuthenticated()")
public class NavbarController {

    @Autowired
    private UserService userService;

    @GetMapping("/autocomplete/{name}&{keycloakName}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> filter(@PathVariable String name, @PathVariable String keycloakName) {
        List<UserApp> users;
        Map<String, Object> response = new HashMap<>();
        try {
            users = userService.filter(name, keycloakName);
        } catch (DataAccessException e) {
            response.put("message", "Database error");
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<Map>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("users", users);
        return new ResponseEntity<Map>(response, HttpStatus.OK);
    }

    @GetMapping("/autocomplete/full/{name}&{keycloakName}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> filterWithoutLimit(@PathVariable String name, @PathVariable String keycloakName) {
        List<UserApp> users;
        Map<String, Object> response = new HashMap<>();
        try {
            users = userService.filterWithoutLimit(name, keycloakName);
        } catch (DataAccessException e) {
            response.put("message", "Database error");
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<Map>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("users", users);
        return new ResponseEntity<Map>(response, HttpStatus.OK);
    }
}
