package com.mgbt.socialapp_backend.controller;

import com.mgbt.socialapp_backend.model.entity.UserApp;
import com.mgbt.socialapp_backend.model.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("api/app/")
public class AppController {

    @Autowired
    private UserService userService;

    @Autowired
    MessageSource messageSource;

    @PostMapping("/login")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> login(@RequestBody UserApp user, Locale locale) {
        UserApp userFound = userService.findByUsername(user.getUsername());
        Map<String, Object> response = new HashMap<>();
        if (userFound != null) {
            userService.checkIfUserIsPersisted(userFound, user); //Needed if the user updates their first and last name from Keycloak
            response.put("message", messageSource.getMessage("appController.login.userfound", null, locale));
            response.put("user", userFound);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            userService.save(user);
            response.put("message", messageSource.getMessage("appController.login.usercreated", null, locale));
            response.put("status", HttpStatus.CREATED.value());
            response.put("user", user);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
    }
}
