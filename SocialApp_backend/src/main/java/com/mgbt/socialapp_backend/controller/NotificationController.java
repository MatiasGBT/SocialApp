package com.mgbt.socialapp_backend.controller;

import com.mgbt.socialapp_backend.model.entity.*;
import com.mgbt.socialapp_backend.model.entity.notification.*;
import com.mgbt.socialapp_backend.model.service.*;
import com.mgbt.socialapp_backend.utility_classes.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("api/notifications/")
@PreAuthorize("isAuthenticated()")
public class NotificationController {

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    MessageSource messageSource;

    @Operation(summary = "Gets all notifications from the user whose username is the one entered")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Array of notifications",
                    content = { @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Notification.class))) }),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = JsonMessage.class)) })
    })
    @GetMapping("/get/list/{username}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> getNotifications(@PathVariable String username, Locale locale) {
        UserApp user;
        List<Notification> notifications;
        Map<String, Object> response = new HashMap<>();
        try {
            user = userService.findByUsername(username);
            notifications = notificationService.findByUser(user);
        } catch (DataAccessException e) {
            response.put("message", messageSource.getMessage("error.database", null, locale));
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (user == null) {
            response.put("message", messageSource.getMessage("error.userNotExist", null, locale));
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    @Operation(summary = "Sets the isViewed property of a notification to true")
    @ApiResponse(responseCode = "200", description = "Notification updated correctly",
            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = JsonMessage.class)) })
    @PutMapping("/put/view/{idNotification}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> viewNotification(@PathVariable Long idNotification, Locale locale) {
        Map<String, Object> response = new HashMap<>();
        try {
            Notification notification = notificationService.findById(idNotification);
            notification.setIsViewed(true);
            notificationService.save(notification);
            response.put("message", messageSource.getMessage("notificationController.viewNotification", null, locale));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (DataAccessException e) {
            response.put("message", messageSource.getMessage("error.userNotExist", null, locale));
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Delete a notification")
    @ApiResponse(responseCode = "200", description = "Notification deleted correctly",
            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = JsonMessage.class)) })
    @DeleteMapping("/delete/{idNotification}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> delete(@PathVariable Long idNotification, Locale locale) {
        Map<String, Object> response = new HashMap<>();
        try {
            Notification notification = notificationService.findById(idNotification);
            response.put("id", idNotification);
            //It is possible for a user to unfollow another user before they remove a notification and,
            //as they are removed in cascade, the notification will not be found by the system.
            if (notification != null) {
                notificationService.delete(notification);
                response.put("message", messageSource.getMessage("notificationController.delete", null, locale));
            } else {
                response.put("message", messageSource.getMessage("notificationController.notFound", null, locale));
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (DataAccessException e) {
            response.put("message", messageSource.getMessage("error.userNotExist", null, locale));
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Deletes all notifications from the user whose username is the one entered")
    @ApiResponse(responseCode = "200", description = "Notifications deleted correctly",
            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = JsonMessage.class)) })
    @DeleteMapping("/delete-all/{username}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> deleteAll(@PathVariable String username, Locale locale) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserApp user = userService.findByUsername(username);
            notificationService.deleteAllByUser(user.getIdUser());
            response.put("message", messageSource.getMessage("notificationController.deleteAll", null, locale));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (DataAccessException e) {
            response.put("message", messageSource.getMessage("error.userNotExist", null, locale));
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}