package com.mgbt.socialapp_backend.controller;

import com.mgbt.socialapp_backend.model.entity.*;
import com.mgbt.socialapp_backend.model.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.net.MalformedURLException;
import java.util.*;

@RestController
@RequestMapping("api/profile/")
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private IUploadFileService uploadFileService;

    @Autowired
    private FriendshipService friendshipService;

    @PostMapping("/send-photo")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> editProfile(@RequestParam("file") MultipartFile file,
                                         @RequestParam("username") String username) {
        Map<String, Object> response = new HashMap<>();
        if (!file.isEmpty()) {
            String fileName;
            try {
                fileName = uploadFileService.save(file);
                UserApp user = userService.findByUsername(username);
                String lastFileName = user.getPhoto();
                uploadFileService.delete(lastFileName);
                user.setPhoto(fileName);
                user = userService.save(user);
                response.put("user", user);
                response.put("message", "File uploaded correctly: " + fileName);
                return new ResponseEntity<Map>(response, HttpStatus.CREATED);
            } catch (Exception e) {
                response.put("message", "Database or File error");
                response.put("error", e.getMessage());
                return new ResponseEntity<Map>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            response.put("error", "The file is empty");
            return new ResponseEntity<Map>(response, HttpStatus.NO_CONTENT);
        }
    }

    @PutMapping("/edit")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> editProfile(@RequestBody UserApp userUpdated) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserApp user = userService.findByUsername(userUpdated.getUsername());
            user.setDescription(userUpdated.getDescription());
            user = userService.save(user);
            response.put("user", user);
            response.put("message", "Description changed correctly: " + user.getDescription());
            return new ResponseEntity<Map>(response, HttpStatus.CREATED);
        } catch (DataAccessException e) {
            response = new HashMap<>();
            response.put("message", "Database error");
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<Map>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/img/{fileName:.+}")
    public ResponseEntity<Resource> viewPhoto(@PathVariable String fileName) {
        Resource resource = null;
        try {
            resource = uploadFileService.charge(fileName);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"");
        return new ResponseEntity<>(resource, header, HttpStatus.OK);
    }

    @PostMapping("/add-friend")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> addFriend(@RequestParam("idReceiver") Long idReceiver,
                                       @RequestParam("usernameTransmitter") String usernameTransmitter) {
        Map<String, Object> response = new HashMap<>();
        UserApp userReceiver;
        UserApp userTransmitter;
        Friendship friendship;
        try {
            userTransmitter = userService.findByUsername(usernameTransmitter);
            userReceiver = userService.findById(idReceiver);
            friendship = friendshipService.findByUsers(userTransmitter, userReceiver);
        } catch (DataAccessException e) {
            response.put("message", "Database error");
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<Map>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (friendship == null) {
            friendship = new Friendship();
            friendship.setUserReceiver(userReceiver);
            friendship.setUserTransmitter(userTransmitter);
            friendship.setStatus(false);
            friendshipService.save(friendship);
            response.put("message", "The friend request was sent successfully");
            response.put("send", true);
        } else {
            response.put("message", "You have already sent a friend request to this user");
            response.put("send", false);
        }
        return new ResponseEntity<Map>(response, HttpStatus.CREATED);
    }

    @GetMapping("/get-friendship/{idReceiver}&{usernameTransmitter}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> getFriendship(@PathVariable Long idReceiver,
                                           @PathVariable String usernameTransmitter) {
        UserApp userReceiver;
        UserApp userTransmitter;
        Friendship friendship;
        try {
            userTransmitter = userService.findByUsername(usernameTransmitter);
            userReceiver = userService.findById(idReceiver);
            friendship = friendshipService.findByUsers(userTransmitter, userReceiver);
        } catch (DataAccessException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Database error");
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<Map>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (friendship == null) {
            /*This will prevent the add friends buttons from not being displayed
            on the frontend because there is no relationship with the status created.*/
            friendship = new Friendship();
            friendship.setStatus(false);
        }
        return new ResponseEntity<>(friendship, HttpStatus.OK);
    }
}
