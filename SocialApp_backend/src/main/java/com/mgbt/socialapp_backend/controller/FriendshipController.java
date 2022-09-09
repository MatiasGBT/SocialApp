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
@RequestMapping("api/friend/")
@PreAuthorize("isAuthenticated()")
public class FriendshipController {

    @Autowired
    private UserService userService;

    @Autowired
    private FriendshipService friendshipService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    MessageSource messageSource;

    @GetMapping("/get/{idReceiver}&{usernameTransmitter}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> getFriendship(@PathVariable Long idReceiver,
                                           @PathVariable String usernameTransmitter,
                                           Locale locale) {
        Friendship friendship = null;
        try {
            UserApp userTransmitter = userService.findByUsername(usernameTransmitter);
            UserApp userReceiver = userService.findById(idReceiver);
            if (userTransmitter != null && userReceiver != null) {
                friendship = friendshipService.findByUsers(userTransmitter, userReceiver);
            }
        } catch (DataAccessException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", messageSource.getMessage("error.database", null, locale));
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (friendship == null) {
            /*This will prevent the add friends buttons from not being displayed
            on the frontend because there is no relationship with the status created.*/
            friendship = new Friendship();
            friendship.setStatus(false);
        }
        return new ResponseEntity<>(friendship, HttpStatus.OK);
    }

    @PostMapping("/add-friend")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> addFriend(@RequestParam("idReceiver") Long idReceiver,
                                       @RequestParam("usernameTransmitter") String usernameTransmitter,
                                       Locale locale) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserApp userTransmitter = userService.findByUsername(usernameTransmitter);
            UserApp userReceiver = userService.findById(idReceiver);
            Friendship friendship = friendshipService.findByUsers(userTransmitter, userReceiver);
            if (friendship == null) {
                friendship = new Friendship();
                friendship.setUserReceiver(userReceiver);
                friendship.setUserTransmitter(userTransmitter);
                friendship.setStatus(false);
                friendship = friendshipService.save(friendship);
                NotificationFriendship notificationFriendship = new NotificationFriendship();
                notificationFriendship.setUserReceiver(friendship.getUserReceiver());
                notificationFriendship.setIsViewed(false);
                notificationFriendship.setFriendship(friendship);
                notificationService.save(notificationFriendship);
                response.put("message", messageSource.getMessage("friendshipcontroller.addFriend.notSent", null, locale));
                response.put("send", true);
            } else {
                response.put("message", messageSource.getMessage("friendshipcontroller.addFriend.alreadySent", null, locale));
                response.put("send", false);
            }
        } catch (DataAccessException e) {
            response.put("message", messageSource.getMessage("error.database", null, locale));
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
        }
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/accept-request/{id}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> acceptFriendRequest(@PathVariable Long id, Locale locale) {
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
            response.put("message", friendship.getUserTransmitter().getName() + " " +
                    messageSource.getMessage("friendshipcontroller.acceptFriendRequest", null, locale));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (DataAccessException e) {
            response.put("message", messageSource.getMessage("error.usernotexist", null, locale));
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/reject-request/{idFriendship}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> deleteFriendship(@PathVariable Long idFriendship, Locale locale) {
        Map<String, Object> response = new HashMap<>();
        try {
            this.friendshipService.delete(friendshipService.findById(idFriendship));
            response.put("message", messageSource.getMessage("friendshipcontroller.deleteFriendship", null, locale));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (DataAccessException e) {
            response.put("message", messageSource.getMessage("error.database", null, locale));
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{idUserFriend}&{keycloakUsername}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> deleteFriendship(@PathVariable Long idUserFriend,
                                              @PathVariable String keycloakUsername,
                                              Locale locale) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserApp userFriend = userService.findById(idUserFriend);
            UserApp userKeycloak = userService.findByUsername(keycloakUsername);
            friendshipService.delete(friendshipService.findByUsers(userFriend, userKeycloak));
            response.put("message", messageSource.getMessage("friendshipcontroller.deleteFriendship", null, locale));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (DataAccessException e) {
            response.put("message", messageSource.getMessage("error.database", null, locale));
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get/friends/quantity/{idUser}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> getFriendsQuantity(@PathVariable Long idUser, Locale locale) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserApp user = userService.findById(idUser);
            if (user != null) {
                Integer friendsQuantity = friendshipService.friendsQuantity(user);
                return new ResponseEntity<>(friendsQuantity, HttpStatus.OK);
            } else {
                response.put("message", messageSource.getMessage("error.usernotexist", null, locale));
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (DataAccessException e) {
            response.put("message", messageSource.getMessage("error.database", null, locale));
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
