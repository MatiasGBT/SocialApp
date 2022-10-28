package com.mgbt.socialapp_backend.controller;

import com.mgbt.socialapp_backend.model.entity.*;
import com.mgbt.socialapp_backend.model.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;

@RestController
@RequestMapping("api/user/")
public class UserController {

    private final static String FINAL_DIRECTORY = "/users";

    @Autowired
    private UserService userService;

    @Autowired
    private IUploadFileService uploadFileService;

    @Autowired
    MessageSource messageSource;

    @GetMapping("/get/{id}")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> getUser(@PathVariable Long id, Locale locale) {
        UserApp user;
        Map<String, Object> response = new HashMap<>();
        try {
            user = userService.findById(id);
        } catch (DataAccessException e) {
            response.put("message", messageSource.getMessage("error.database", null, locale));
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (user == null) {
            response.put("message", messageSource.getMessage("error.userNotExist", null, locale));
            response.put("error", messageSource.getMessage("error.userNotExist.redirect", null, locale));
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/get/keycloak/{username}")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> getKeycloakUser(@PathVariable String username, Locale locale) {
        try {
            UserApp user = userService.findByUsername(username);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (DataAccessException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", messageSource.getMessage("error.database", null, locale));
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get/friends/{idUser}")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> getFriends(@PathVariable Long idUser, Locale locale) {
        try {
            List<UserApp> friends = userService.getFriends(idUser);
            return new ResponseEntity<>(friends, HttpStatus.OK);
        } catch (DataAccessException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", messageSource.getMessage("error.database", null, locale));
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get/may-know/{idUser}&{idKeycloakUser}")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> getUsersYouMayKnow(@PathVariable Long idUser, @PathVariable Long idKeycloakUser,
                                                Locale locale) {
        try {
            List<UserApp> friends = userService.getUsersYouMayKnow(idUser, idKeycloakUser);
            return new ResponseEntity<>(friends, HttpStatus.OK);
        } catch (DataAccessException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", messageSource.getMessage("error.database", null, locale));
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
     If we ask the user to be authorised to make a request to this endpoint, the application will
     not work, so do not put @PreAuthorize("isAuthenticated()") in this controller.
     This endpoint needs to be Post and not Put because an image file has to be sent.
    */
    @PostMapping("/update")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> editProfileSendNewPhoto(@RequestParam(value = "file", required = false) MultipartFile file,
                                         @RequestParam(value = "description", required = false) String description,
                                         @RequestParam("username") String username,
                                         Locale locale) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserApp user = userService.findByUsername(username);
            if (file != null && !file.isEmpty()) {
                String fileName = uploadFileService.save(file, FINAL_DIRECTORY);
                String lastFileName = user.getPhoto();
                uploadFileService.delete(lastFileName, FINAL_DIRECTORY);
                user.setPhoto(fileName);
                response.put("photo", user.getPhoto());
            }
            if (description != null && !description.equals(user.getDescription())) {
                user.setDescription(description);
                response.put("description", user.getDescription());
            }
            userService.save(user);
            response.put("message", messageSource.getMessage("userController.editProfile", null, locale));
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            response.put("message", messageSource.getMessage("error.databaseOrFile", null, locale));
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get/autocomplete/{name}&{keycloakName}")
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

    @GetMapping("/get/search/{name}&{keycloakName}")
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

    @GetMapping("/img/{fileName:.+}")
    public ResponseEntity<Resource> viewPhoto(@PathVariable String fileName) {
        Resource resource = null;
        HttpHeaders header = null;
        try {
            resource = uploadFileService.charge(fileName, FINAL_DIRECTORY);
            header = new HttpHeaders();
            header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(resource, header, HttpStatus.OK);
    }
}
