package com.mgbt.socialapp_backend.controller;

import com.mgbt.socialapp_backend.model.entity.*;
import com.mgbt.socialapp_backend.model.entity.notification.Notification;
import com.mgbt.socialapp_backend.model.entity.notification.NotificationFriend;
import com.mgbt.socialapp_backend.model.service.*;
import org.springframework.beans.factory.annotation.Autowired;
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
    private FriendshipService friendshipService;

    @GetMapping("/get/{username}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> getNotifications(@PathVariable String username) {
        UserApp user;
        List<Notification> notifications;
        Map<String, Object> response = new HashMap<>();
        try {
            user = userService.findByUsername(username);
            notifications = notificationService.findByUser(user);
        } catch (DataAccessException e) {
            response.put("message", "Database error");
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<Map>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (user == null) {
            response.put("message", "The user does not exist");
            return new ResponseEntity<Map>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Notification notifcation = notificationService.findById(id);
            response.put("id", notifcation.getIdNotification());
            notificationService.delete(notifcation);
            response.put("message", "Notification deleted");
            return new ResponseEntity<Map>(response, HttpStatus.OK);
        } catch (DataAccessException e) {
            response.put("message", "Database error");
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<Map>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete-all/{username}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> deleteAll(@PathVariable String username) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserApp user = userService.findByUsername(username);
            notificationService.deleteAllByUser(user.getIdUser());
            response.put("message", "Notification deleted");
            return new ResponseEntity<Map>(response, HttpStatus.OK);
        } catch (DataAccessException e) {
            response.put("message", "Database error");
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<Map>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/accept-request/{id}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> acceptFriendRequest(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Friendship friendship = friendshipService.findById(id);
            friendship.setStatus(true);
            friendship.setDate(new Date());
            friendship = friendshipService.save(friendship);
            NotificationFriend notificationFriend = new NotificationFriend();
            notificationFriend.setIsViewed(false);
            notificationFriend.setUserReceiver(friendship.getUserTransmitter());
            notificationFriend.setFriend(friendship.getUserReceiver());
            notificationService.save(notificationFriend);
            response.put("message", friendship.getUserTransmitter().getName() + " and you are friends now");
            return new ResponseEntity<Map>(response, HttpStatus.OK);
        } catch (DataAccessException e) {
            response.put("message", "Database error");
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<Map>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/view/{id}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> viewNotification(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Notification notification = notificationService.findById(id);
            notification.setIsViewed(true);
            notificationService.save(notification);
            response.put("message", "Notification viewed, going to the corresponding page");
            return new ResponseEntity<Map>(response, HttpStatus.OK);
        } catch (DataAccessException e) {
            response.put("message", "Database error");
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<Map>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
