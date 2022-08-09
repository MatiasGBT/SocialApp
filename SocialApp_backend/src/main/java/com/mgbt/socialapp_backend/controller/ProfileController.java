package com.mgbt.socialapp_backend.controller;

import com.mgbt.socialapp_backend.model.entity.UserApp;
import com.mgbt.socialapp_backend.model.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;

@RestController
@RequestMapping("api/profile/")
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private IUploadFileService uploadFileService;

    @PostMapping("/upload")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> editProfile(@RequestParam("file") MultipartFile file,
                                         @RequestParam("username") String username,
                                         @RequestParam("description") String description) {
        Map<String, Object> response = new HashMap<>();
        if (!file.isEmpty()) {
            String fileName;
            try {
                fileName = uploadFileService.save(file);
            } catch (IOException e) {
                response.put("message", "Server error");
                response.put("error", e.getMessage());
                return new ResponseEntity<Map>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            UserApp user = userService.findByUsername(username);
            String lastFileName = user.getPhoto();
            uploadFileService.delete(lastFileName);
            user.setPhoto(fileName);
            user.setDescription(description);
            user = userService.save(user);
            response.put("user", user);
            response.put("message", "File uploaded correctly: " + fileName);
        }
        return new ResponseEntity<Map>(response, HttpStatus.CREATED);
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
}
