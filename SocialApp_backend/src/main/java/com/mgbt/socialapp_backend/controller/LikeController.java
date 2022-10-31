package com.mgbt.socialapp_backend.controller;

import com.mgbt.socialapp_backend.model.entity.*;
import com.mgbt.socialapp_backend.model.entity.notification.NotificationPost;
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
@RequestMapping("api/likes/")
public class LikeController {

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

    /*
    It is necessary to know whether a like exists when creating or deleting it because,
    if a publication appears as a featured publication on the home page and, in turn, appears
    in the user's feed, you can like it twice (you should not save it twice) and dislike it twice
    (the second time would throw an error if it is not checked because it does not exist).
    */
    @PostMapping("/like")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> likePost(@RequestParam("idPost") Long idPost,
                                      @RequestParam("idUser") Long idUser, Locale locale) {
        Map<String, Object> response = new HashMap<>();
        try {
            Post post = postService.findById(idPost);
            UserApp user = userService.findById(idUser);
            Like like = likeService.findByPostAndUser(post.getIdPost(), user.getIdUser());
            if (like == null) {
                like = new Like();
                like.setPost(post);
                like.setUser(user);
                likeService.save(like);
                if (!post.getUser().getIdUser().equals(user.getIdUser())) {
                    NotificationPost notificationPost = new NotificationPost();
                    notificationPost.setPost(post);
                    notificationPost.setUserReceiver(post.getUser());
                    notificationService.save(notificationPost);
                }
            }
            response.put("message", messageSource.getMessage("postController.likePost", null, locale));
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (DataAccessException e) {
            response.put("message", response.put("message", messageSource.getMessage("error.database", null, locale)));
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/dislike/{idPost}&{idUser}")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> dislikePost(@PathVariable Long idPost, @PathVariable Long idUser,
                                         Locale locale) {
        Map<String, Object> response = new HashMap<>();
        try {
            Like like = likeService.findByPostAndUser(idPost, idUser);
            if (like != null) {
                likeService.delete(like);
            }
            response.put("message", messageSource.getMessage("postController.dislikePost", null, locale));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (DataAccessException e) {
            response.put("message", response.put("message", messageSource.getMessage("error.database", null, locale)));
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
