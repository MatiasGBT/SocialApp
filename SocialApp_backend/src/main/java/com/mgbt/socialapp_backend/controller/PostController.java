package com.mgbt.socialapp_backend.controller;

import com.mgbt.socialapp_backend.model.entity.*;
import com.mgbt.socialapp_backend.model.entity.notification.NotificationPost;
import com.mgbt.socialapp_backend.model.service.IUploadFileService;
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
@RequestMapping("api/posts/")
public class PostController {

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    MessageSource messageSource;

    @Autowired
    private IUploadFileService uploadFileService;

    @GetMapping("/get-post/{idPost}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> getPost(@PathVariable Long idPost, Locale locale) {
        Post post;
        Map<String, Object> response = new HashMap<>();
        try {
            post = postService.findById(idPost);
        } catch (DataAccessException e) {
            response.put("message", messageSource.getMessage("error.database", null, locale));
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (post == null) {
            response.put("message", messageSource.getMessage("error.usernotexist", null, locale));
            response.put("error", messageSource.getMessage("error.usernotexist.redirect", null, locale));
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    @PostMapping("/like")
    public ResponseEntity<?> likePost(@RequestParam("idPost") Long idPost,
                                      @RequestParam("idUser") Long idUser, Locale locale) {
        Map<String, Object> response = new HashMap<>();
        try {
            Post post = postService.findById(idPost);
            UserApp user = userService.findById(idUser);
            Like like = new Like();
            like.setPost(post);
            like.setUser(user);
            this.likeService.save(like);
            NotificationPost notificationPost = new NotificationPost();
            notificationPost.setPost(post);
            notificationPost.setUserReceiver(post.getUser());
            this.notificationService.save(notificationPost);
            response.put("message", messageSource.getMessage("postcontroller.likePost", null, locale));
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (DataAccessException e) {
            response.put("message", response.put("message", messageSource.getMessage("error.database", null, locale)));
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/like/{idPost}&{idUser}")
    public ResponseEntity<?> dislikePost(@PathVariable Long idPost, @PathVariable Long idUser,
                                         Locale locale) {
        Map<String, Object> response = new HashMap<>();
        try {
            Like like = likeService.findByPostAndUser(idPost, idUser);
            likeService.delete(like);
            response.put("message", messageSource.getMessage("postcontroller.dislikePost", null, locale));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (DataAccessException e) {
            response.put("message", response.put("message", messageSource.getMessage("error.database", null, locale)));
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
