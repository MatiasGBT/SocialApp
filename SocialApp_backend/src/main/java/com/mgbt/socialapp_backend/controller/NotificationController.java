package com.mgbt.socialapp_backend.controller;

import com.mgbt.socialapp_backend.model.entity.*;
import com.mgbt.socialapp_backend.model.entity.notification.*;
import com.mgbt.socialapp_backend.model.service.*;
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

    @GetMapping("/get/{username}")
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
            response.put("message", messageSource.getMessage("error.usernotexist", null, locale));
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> delete(@PathVariable Long id, Locale locale) {
        Map<String, Object> response = new HashMap<>();
        try {
            Notification notification = notificationService.findById(id);
            response.put("id", notification.getIdNotification());
            notificationService.delete(notification);
            response.put("message", messageSource.getMessage("notificationcontroller.delete", null, locale));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (DataAccessException e) {
            response.put("message", messageSource.getMessage("error.usernotexist", null, locale));
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete-all/{username}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> deleteAll(@PathVariable String username, Locale locale) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserApp user = userService.findByUsername(username);
            notificationService.deleteAllByUser(user.getIdUser());
            response.put("message", messageSource.getMessage("notificationcontroller.deleteAll", null, locale));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (DataAccessException e) {
            response.put("message", messageSource.getMessage("error.usernotexist", null, locale));
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/view/{id}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> viewNotification(@PathVariable Long id, Locale locale) {
        Map<String, Object> response = new HashMap<>();
        try {
            Notification notification = notificationService.findById(id);
            notification.setIsViewed(true);
            notificationService.save(notification);
            response.put("message", messageSource.getMessage("notificationcontroller.viewNotification", null, locale));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (DataAccessException e) {
            response.put("message", messageSource.getMessage("error.usernotexist", null, locale));
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
