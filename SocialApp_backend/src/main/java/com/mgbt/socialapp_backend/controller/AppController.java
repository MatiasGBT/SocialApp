package com.mgbt.socialapp_backend.controller;

import com.mgbt.socialapp_backend.model.entity.UserApp;
import com.mgbt.socialapp_backend.model.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
            response.put("user", userFound);
            return new ResponseEntity<Map>(response, HttpStatus.OK);
        } else {
            userFound = userService.save(user);
            response.put("message", "User created");
            response.put("user", userFound);
            response.put("status", HttpStatus.CREATED.value());
            return new ResponseEntity<Map>(response, HttpStatus.CREATED);
        }
    }
}
