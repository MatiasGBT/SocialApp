package com.mgbt.socialapp_backend.controller;

import com.mgbt.socialapp_backend.exceptions.FileNameTooLongException;
import com.mgbt.socialapp_backend.model.entity.*;
import com.mgbt.socialapp_backend.model.entity.notification.NotificationDeleteAccount;
import com.mgbt.socialapp_backend.model.service.*;
import com.mgbt.socialapp_backend.utility_classes.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;

@RestController
@RequestMapping("api/users/")
public class UserController {

    private final static String FINAL_DIRECTORY = "/users";

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private IUploadFileService uploadFileService;

    @Autowired
    MessageSource messageSource;

    @Operation(summary = "Gets a user by his/her ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User entity",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = UserApp.class)) }),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = InternalServerError.class)) })
    })
    @GetMapping("/get/{idUser}")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> getUser(@PathVariable Long idUser, Locale locale) {
        UserApp user;
        Map<String, Object> response = new HashMap<>();
        try {
            user = userService.findById(idUser);
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

    @Operation(summary = "Gets the user making the request by his/her username")
    @ApiResponse(responseCode = "200", description = "User entity",
            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = UserApp.class)) })
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

    @Operation(summary = "Gets all the friends of the user entered")
    @ApiResponse(responseCode = "200", description = "Array of users",
            content = { @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserApp.class))) })
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

    @Operation(summary = "Gets all the followers of the user entered")
    @ApiResponse(responseCode = "200", description = "Array of users",
            content = { @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserApp.class))) })
    @GetMapping("/get/followers/{idUser}")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> getFollowers(@PathVariable Long idUser, Locale locale) {
        try {
            List<UserApp> followers = userService.getFollowers(idUser);
            return new ResponseEntity<>(followers, HttpStatus.OK);
        } catch (DataAccessException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", messageSource.getMessage("error.database", null, locale));
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Gets all the followed users of the user entered")
    @ApiResponse(responseCode = "200", description = "Array of users",
            content = { @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserApp.class))) })
    @GetMapping("/get/following/{idUser}")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> getFollowing(@PathVariable Long idUser, Locale locale) {
        try {
            List<UserApp> following = userService.getFollowing(idUser);
            return new ResponseEntity<>(following, HttpStatus.OK);
        } catch (DataAccessException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", messageSource.getMessage("error.database", null, locale));
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Gets 5 friends of the user entered excluding the user making the request")
    @ApiResponse(responseCode = "200", description = "Array of users",
            content = { @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserApp.class))) })
    @GetMapping("/get/may-know/{idUser}&{idKeycloakUser}")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> getUsersYouMayKnow(@PathVariable Long idUser, @PathVariable Long idKeycloakUser,
                                                Locale locale) {
        try {
            List<UserApp> usersYouMayKnow = userService.getUsersYouMayKnow(idUser, idKeycloakUser);
            return new ResponseEntity<>(usersYouMayKnow, HttpStatus.OK);
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
    @Operation(summary = "Updates a user by changing his/her description and/or photo. Requires the username of the user to be updated")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated correctly",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = JsonMessage.class)) }),
            @ApiResponse(responseCode = "400", description = "The file name is too long",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = InternalServerError.class)) })
    })
    @PostMapping("/update")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> editProfile(@RequestParam(value = "file", required = false) MultipartFile file,
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
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (FileNameTooLongException e) {
            response.put("message", messageSource.getMessage("error.nameTooLong", null, locale));
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            response.put("message", messageSource.getMessage("error.databaseOrFile", null, locale));
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Gets a list of users whose name or surname is the name entered. The user making the request is not included. Only has a maximum of 5 users")
    @ApiResponse(responseCode = "200", description = "Array of users",
            content = { @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserApp.class))) })
    @GetMapping("/get/autocomplete/{name}&{keycloakUsername}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> filter(@PathVariable String name, @PathVariable String keycloakUsername, Locale locale) {
        try {
            List<UserApp> users = userService.filter(name, keycloakUsername);
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (DataAccessException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", messageSource.getMessage("error.database", null, locale));
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Gets a list of users whose name or surname is the name entered. The user making the request is not included. No maximum number of users")
    @ApiResponse(responseCode = "200", description = "Array of users",
            content = { @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserApp.class))) })
    @GetMapping("/get/search/{name}&{keycloakUsername}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> filterWithoutLimit(@PathVariable String name, @PathVariable String keycloakUsername,
                                                Locale locale) {
        try {
            List<UserApp> users = userService.filterWithoutLimit(name, keycloakUsername);
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (DataAccessException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", messageSource.getMessage("error.database", null, locale));
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Gets file from users directory by filename")
    @ApiResponse(description = "Image file", content = { @Content(mediaType = "multipart/form-data") })
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

    @Operation(summary = "Gets all users whose name, surname or username is the one entered")
    @ApiResponse(responseCode = "200", description = "Paginator object (paginator.content = array of users)",
            content = { @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Page.class))) })
    @GetMapping("/get/list/by-name/{name}&{page}")
    @PreAuthorize("isAuthenticated() and hasRole('admin')")
    public ResponseEntity<?> getUsersByNameOrSurnameOrUsername(@PathVariable String name,
                                                               @PathVariable Integer page,
                                                               Locale locale) {
        try {
            Pageable pageable = PageRequest.of(page, 5);
            Page<UserApp> users = userService.getByNameOrSurnameOrUsername(name, pageable);
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (DataAccessException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", messageSource.getMessage("error.database", null, locale));
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Updates a user with the object passed in the Request Body")
    @ApiResponse(responseCode = "200", description = "User updated correctly",
            content = { @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = JsonMessage.class))) })
    @PutMapping("/put")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> update(@RequestBody UserApp user,
                                    Locale locale) {
        Map<String, Object> response = new HashMap<>();
        try {
            //If the update occurs because the user or an administrator initiates the account
            //deletion process, create a notification so that the user is aware of it.
            UserApp originalUser = userService.findById(user.getIdUser());
            if (originalUser.getDeletionDate() == null && user.getDeletionDate() != null) {
                NotificationDeleteAccount notification = new NotificationDeleteAccount(user);
                notificationService.save(notification);
            }
            userService.save(user);
            response.put("message", messageSource.getMessage("userController.userUpdated", null, locale));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (DataAccessException e) {
            response.put("message", messageSource.getMessage("error.database", null, locale));
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Deletes all users who have started the account deletion process 14 days ago")
    @ApiResponse(responseCode = "200", description = "Users deleted correctly",
            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = JsonMessage.class)) })
    @DeleteMapping("/delete")
    @PreAuthorize("isAuthenticated() and hasRole('admin')")
    public ResponseEntity<?> deleteUsers(Locale locale) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<UserApp> users = userService.getUsersWhoseDeletionDateIsNotNull();
            if (!users.isEmpty()) {
                users.forEach(u -> userService.delete(u));
            }
            response.put("message", users.size() + " " + messageSource.getMessage("userController.deletionProcess", null, locale));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("message", response.put("message", messageSource.getMessage("error.databaseOrFile", null, locale)));
            response.put("error", e.getMessage() + ": " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}