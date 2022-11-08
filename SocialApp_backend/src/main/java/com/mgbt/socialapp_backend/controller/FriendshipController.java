package com.mgbt.socialapp_backend.controller;

import com.mgbt.socialapp_backend.exceptions.*;
import com.mgbt.socialapp_backend.model.entity.*;
import com.mgbt.socialapp_backend.model.entity.notification.*;
import com.mgbt.socialapp_backend.model.service.*;
import com.mgbt.socialapp_backend.utility_classes.*;
import io.swagger.v3.oas.annotations.*;
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
@RequestMapping("api/friendships/")
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

    @Operation(summary = "Gets a Friendship entity whose users (both transmitter and receiver) are the ones entered. If the entity does not exist, returns a Friendship entity whose status is false")
    @ApiResponse(responseCode = "200", description = "Friendship entity",
            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Friendship.class)) })
    @GetMapping("/get/{idFriend}&{keycloakUsername}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> getFriendship(@Parameter(description = "Username of the user making the request") @PathVariable String keycloakUsername,
                                           @Parameter(description = "ID of the potential friend of the user who made the request (is a user ID)") @PathVariable Long idFriend,
                                           Locale locale) {
        Friendship friendship = null;
        try {
            UserApp userTransmitter = userService.findByUsername(keycloakUsername);
            UserApp userReceiver = userService.findById(idFriend);
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

    @Operation(summary = "Creates a friendship with the entered users (status = false)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Friendship already exists (response.send = false)",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = JsonFriendshipMessage.class)) }),
            @ApiResponse(responseCode = "201", description = "Friendship created correctly (response.send = true)",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = JsonFriendshipMessage.class)) }),
            @ApiResponse(responseCode = "404", description = "A checked user cannot have friends, only followers",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = InternalServerError.class)) })
    })
    @PostMapping("/post/send-request")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> sendFriendRequest(@RequestParam("usernameTransmitter") String usernameTransmitter,
                                               @RequestParam("idReceiver") Long idReceiver,
                                               Locale locale) {
        Map<String, Object> response = new HashMap<>();
        Friendship friendship = null;
        UserApp userTransmitter = null;
        try {
            UserApp userReceiver = userService.findById(idReceiver);
            userTransmitter = userService.findByUsername(usernameTransmitter);
            if (userReceiver.getIsChecked() || userTransmitter.getIsChecked()) {
                throw new friendsAndFollowersException("A checked user cannot have friends, only followers");
            }
            friendship = friendshipService.findByUsers(userTransmitter, userReceiver);
            if (friendship != null) {
                throw new EntityAlreadyExistsException("The friend request already exists");
            }
            friendship = new Friendship(userReceiver, userTransmitter);
            friendship = friendshipService.save(friendship);
            NotificationFriendship notificationFriendship = new NotificationFriendship(userReceiver, friendship);
            notificationService.save(notificationFriendship);
            response.put("message", messageSource.getMessage("friendshipController.addFriend.notSent", null, locale));
            response.put("send", true);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (friendsAndFollowersException e) {
            response.put("message", messageSource.getMessage("friendshipController.addFriend.userIsChecked", null, locale));
            response.put("send", false);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (EntityAlreadyExistsException e) {
            if (friendship.getUserTransmitter().getIdUser().equals(userTransmitter.getIdUser())) {
                response.put("message", messageSource.getMessage("friendshipController.addFriend.alreadySent", null, locale));
            } else {
                response.put("message", messageSource.getMessage("friendshipController.addFriend.alreadySentByTheOtherUser", null, locale));
            }
            response.put("send", false);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (DataAccessException e) {
            response.put("message", messageSource.getMessage("error.database", null, locale));
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Sets the status property of a friendship to true")
    @ApiResponse(responseCode = "200", description = "Friendship updated correctly",
            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = JsonMessage.class)) })
    @PutMapping("/put/accept-request/{idFriendship}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> acceptFriendRequest(@PathVariable Long idFriendship, Locale locale) {
        Map<String, Object> response = new HashMap<>();
        try {
            Friendship friendship = friendshipService.findById(idFriendship);
            friendship.setStatus(true);
            friendship.setDate(new Date());
            friendship = friendshipService.save(friendship);
            NotificationFriend notificationFriend = new NotificationFriend(friendship.getUserTransmitter(), friendship.getUserReceiver());
            notificationService.save(notificationFriend);
            response.put("message", friendship.getUserTransmitter().getName() + " " +
                    messageSource.getMessage("friendshipController.acceptFriendRequest", null, locale));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (DataAccessException e) {
            response.put("message", messageSource.getMessage("error.userNotExist", null, locale));
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //This method can be used to either delete a friend or reject a friend request
    @Operation(summary = "Delete a friendship")
    @ApiResponse(responseCode = "200", description = "Friendship deleted correctly",
            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = JsonMessage.class)) })
    @DeleteMapping("/delete/{idFriendship}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> deleteFriendship(@PathVariable Long idFriendship, Locale locale) {
        Map<String, Object> response = new HashMap<>();
        try {
            Friendship friendship = friendshipService.findById(idFriendship);
            this.friendshipService.delete(friendship);
            //If the status of the request is true it means that the user is deleting a friend and not rejecting a friend request
            if (friendship.getStatus()) {
                response.put("message", messageSource.getMessage("friendshipController.deleteFriendship", null, locale));
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (DataAccessException e) {
            response.put("message", messageSource.getMessage("error.database", null, locale));
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Get the number of friends a user has")
    @ApiResponse(responseCode = "200", description = "Friends quantity",
            content = { @Content(mediaType = "text/plain", schema = @Schema(type = "integer")) })
    @GetMapping("/get/friends-count/{idUser}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> getFriendsCount(@PathVariable Long idUser, Locale locale) {
        try {
            UserApp user = userService.findById(idUser);
            return new ResponseEntity<>(friendshipService.getFriendsQuantity(user), HttpStatus.OK);
        } catch (DataAccessException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", messageSource.getMessage("error.database", null, locale));
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}