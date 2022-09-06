package com.mgbt.socialapp_backend.controller;

import com.mgbt.socialapp_backend.model.entity.UserApp;
import com.mgbt.socialapp_backend.model.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    MessageSource messageSource;

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
}
