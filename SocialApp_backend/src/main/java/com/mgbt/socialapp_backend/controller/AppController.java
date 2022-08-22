package com.mgbt.socialapp_backend.controller;

import com.mgbt.socialapp_backend.model.entity.UserApp;
import com.mgbt.socialapp_backend.model.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("api/app/")
@PreAuthorize("isAuthenticated()")
public class AppController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> login(@RequestBody UserApp user) {
        UserApp userFound = userService.findByUsername(user.getUsername());
        Map<String, Object> response = new HashMap<>();
        if (userFound != null) {
            userService.checkIfUserIsPersisted(userFound, user); //Needed if the user updates their first and last name from Keycloak
            response.put("message", "User found");
            return new ResponseEntity<Map>(response, HttpStatus.OK);
        } else {
            userService.save(user);
            response.put("message", "User created");
            response.put("status", HttpStatus.CREATED.value());
            return new ResponseEntity<Map>(response, HttpStatus.CREATED);
        }
    }

    @GetMapping("/get-user/keycloak/{username}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> getKeycloakUser(@PathVariable String username) {
        UserApp user;
        Map<String, Object> response = new HashMap<>();
        try {
            user = userService.findByUsername(username);
        } catch (DataAccessException e) {
            response.put("message", "Database error");
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<Map>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (user == null) {
            response.put("message", "The user does not exist");
            return new ResponseEntity<Map>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/get-user/{id}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        UserApp user;
        Map<String, Object> response = new HashMap<>();
        try {
            user = userService.findById(id);
        } catch (DataAccessException e) {
            response.put("message", "Database error");
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<Map>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (user == null) {
            response.put("message", "The user does not exist");
            return new ResponseEntity<Map>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
