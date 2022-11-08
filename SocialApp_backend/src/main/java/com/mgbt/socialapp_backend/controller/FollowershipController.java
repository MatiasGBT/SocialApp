package com.mgbt.socialapp_backend.controller;

import com.mgbt.socialapp_backend.exceptions.friendsAndFollowersException;
import com.mgbt.socialapp_backend.model.entity.*;
import com.mgbt.socialapp_backend.model.entity.notification.NotificationFollowership;
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
@RequestMapping("api/followerships/")
@PreAuthorize("isAuthenticated()")
public class FollowershipController {

    @Autowired
    private FollowershipService followershipService;

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    MessageSource messageSource;

    @Operation(summary = "Gets a Followership entity whose users (both checked and follower) are the ones entered.")
    @ApiResponse(responseCode = "200", description = "Followership entity",
            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Followership.class)) })
    @GetMapping("/get/{idUserChecked}&{keycloakUsername}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> getFollowership(@Parameter(description = "ID of the user whose account is followed or not by the user making the request") @PathVariable Long idUserChecked,
                                             @Parameter(description = "Username of the user making the request") @PathVariable String keycloakUsername,
                                             Locale locale) {
        try {
            Followership followership = null;
            UserApp userFollower = userService.findByUsername(keycloakUsername);
            UserApp userChecked = userService.findById(idUserChecked);
            if (userChecked != null && userFollower != null) {
                followership = followershipService.findByUsers(userChecked, userFollower);
            }
            if (followership == null) {
                return new ResponseEntity<>(null, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(followership, HttpStatus.OK);
            }
        } catch (DataAccessException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", messageSource.getMessage("error.database", null, locale));
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Creates a followership with the entered users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Followership created correctly",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Followership.class)) }),
            @ApiResponse(responseCode = "404", description = "An unchecked user cannot have followers, only friends",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = InternalServerError.class)) })
    })
    @PostMapping("/post")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> createFollowership(@RequestParam("usernameKeycloak") String usernameKeycloak,
                                                @RequestParam("idUserChecked") Long idUserChecked,
                                               Locale locale) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserApp userChecked = userService.findById(idUserChecked);
            if (!userChecked.getIsChecked()) {
                throw new friendsAndFollowersException("An unchecked user cannot have followers, only friends");
            }
            UserApp userFollower = userService.findByUsername(usernameKeycloak);
            Followership followership = new Followership(userChecked, userFollower);
            followership = followershipService.save(followership);
            NotificationFollowership notificationFollowership = new NotificationFollowership(userChecked, followership);
            notificationService.save(notificationFollowership);
            return new ResponseEntity<>(followership, HttpStatus.CREATED);
        } catch (friendsAndFollowersException e) {
            response.put("message", messageSource.getMessage("friendshipController.addFriend.userIsNotChecked", null, locale));
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (DataAccessException e) {
            response.put("message", messageSource.getMessage("error.database", null, locale));
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Delete a followership")
    @ApiResponse(responseCode = "200", description = "Followership deleted correctly",
            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = JsonMessage.class)) })
    @DeleteMapping("/delete/{idFollowership}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> deleteFollowership(@PathVariable Long idFollowership, Locale locale) {
        Map<String, Object> response = new HashMap<>();
        try {
            Followership followership = followershipService.findById(idFollowership);
            this.followershipService.delete(followership);
            response.put("message", messageSource.getMessage("followershipController.deleteFollowership", null, locale));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (DataAccessException e) {
            response.put("message", messageSource.getMessage("error.database", null, locale));
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Get the number of follower a user has")
    @ApiResponse(responseCode = "200", description = "Followers quantity",
            content = { @Content(mediaType = "text/plain", schema = @Schema(type = "integer")) })
    @GetMapping("/get/followers-count/{idUser}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> getFollowersCount(@PathVariable Long idUser, Locale locale) {
        try {
            return new ResponseEntity<>(followershipService.findFollowersQuantity(idUser), HttpStatus.OK);
        } catch (DataAccessException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", messageSource.getMessage("error.database", null, locale));
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Gets the number of accounts a user follows")
    @ApiResponse(responseCode = "200", description = "Following quantity",
            content = { @Content(mediaType = "text/plain", schema = @Schema(type = "integer")) })
    @GetMapping("/get/following-count/{idUser}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> getFollowingCount(@PathVariable Long idUser, Locale locale) {
        try {
            return new ResponseEntity<>(followershipService.findFollowingQuantity(idUser), HttpStatus.OK);
        } catch (DataAccessException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", messageSource.getMessage("error.database", null, locale));
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
