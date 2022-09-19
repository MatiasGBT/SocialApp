package com.mgbt.socialapp_backend.controller;

import com.mgbt.socialapp_backend.model.entity.*;
import com.mgbt.socialapp_backend.model.entity.notification.NotificationPost;
import com.mgbt.socialapp_backend.model.service.IUploadFileService;
import com.mgbt.socialapp_backend.model.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;

@RestController
@RequestMapping("api/posts/")
public class PostController {

    private final static String FINAL_DIRECTORY = "\\posts\\";

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

    @GetMapping("/get/{idPost}")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
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
            response.put("message", messageSource.getMessage("error.postnotexist", null, locale));
            response.put("error", messageSource.getMessage("error.postnotexist.redirect", null, locale));
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    @GetMapping("/get/feed/{idUser}&{limit}")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> getFeedByUser(@PathVariable Long idUser, @PathVariable Integer limit,
                                            Locale locale) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Post> posts = postService.findFeedByUserId(idUser, limit);
            Integer postQuantity = postService.countUserFeed(idUser);
            boolean isLastPage = postQuantity == posts.size();
            response.put("posts", posts);
            response.put("isLastPage", isLastPage);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (DataAccessException e) {
            response.put("message", messageSource.getMessage("error.database", null, locale));
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get/by-user/{idUser}&{limit}")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> getPostsByUser(@PathVariable Long idUser, @PathVariable Integer limit,
                                            Locale locale) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Post> posts = postService.findByUserId(idUser, limit);
            Integer postQuantity = postService.countUserPosts(idUser);
            boolean isLastPage = postQuantity == posts.size();
            response.put("posts", posts);
            response.put("isLastPage", isLastPage);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (DataAccessException e) {
            response.put("message", messageSource.getMessage("error.database", null, locale));
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get/count/feed/{idUser}")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> countFeedByUser(@PathVariable Long idUser, Locale locale) {
        try {
            Integer postQuantity = postService.countUserPosts(idUser);
            return new ResponseEntity<>(postQuantity, HttpStatus.OK);
        } catch (DataAccessException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", messageSource.getMessage("error.database", null, locale));
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get/count/posts/{idUser}")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> countPostsByUser(@PathVariable Long idUser, Locale locale) {
        try {
            Integer postQuantity = postService.countUserPosts(idUser);
            return new ResponseEntity<>(postQuantity, HttpStatus.OK);
        } catch (DataAccessException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", messageSource.getMessage("error.database", null, locale));
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/like")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
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

    @DeleteMapping("/dislike/{idPost}&{idUser}")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
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

    @PostMapping("/post")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> createPost(@RequestParam("file") MultipartFile file,
                                        @RequestParam("text") String text,
                                        @RequestParam("username") String username,
                                        Locale locale) {
        Map<String, Object> response = new HashMap<>();
        if (!file.isEmpty()) {
            try {
                UserApp user = userService.findByUsername(username);
                Post post = new Post();
                post.setText(text);
                post.setUser(user);
                String fileName = uploadFileService.save(file, FINAL_DIRECTORY);
                post.setPhoto(fileName);
                postService.save(post);
                response.put("message", messageSource.getMessage("postcontroller.createPostError", null, locale) + fileName);
                response.put("idPost", post.getIdPost());
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            } catch (Exception e) {
                response.put("message", messageSource.getMessage("error.databaseOrFile", null, locale));
                response.put("error", e.getMessage());
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            response.put("error", messageSource.getMessage("error.fileEmpty", null, locale));
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        }
    }

    @PostMapping("/post/text")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> createPostWithoutFile(@RequestParam("text") String text,
                                                   @RequestParam("username") String username,
                                                   Locale locale) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserApp user = userService.findByUsername(username);
            Post post = new Post();
            post.setText(text);
            post.setUser(user);
            postService.save(post);
            response.put("message", messageSource.getMessage("postcontroller.createPostError", null, locale));
            response.put("idPost", post.getIdPost());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            response.put("message", messageSource.getMessage("error.databaseOrFile", null, locale));
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/post/file")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> createPostWithoutText(@RequestParam("file") MultipartFile file,
                                                   @RequestParam("username") String username,
                                                   Locale locale) {
        Map<String, Object> response = new HashMap<>();
        if (!file.isEmpty()) {
            try {
                UserApp user = userService.findByUsername(username);
                Post post = new Post();
                post.setUser(user);
                String fileName = uploadFileService.save(file, FINAL_DIRECTORY);
                post.setPhoto(fileName);
                postService.save(post);
                response.put("message", messageSource.getMessage("postcontroller.createPostError", null, locale) + fileName);
                response.put("idPost", post.getIdPost());
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            } catch (Exception e) {
                response.put("message", messageSource.getMessage("error.databaseOrFile", null, locale));
                response.put("error", e.getMessage());
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            response.put("error", messageSource.getMessage("error.fileEmpty", null, locale));
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        }
    }

    @DeleteMapping("/delete/{idPost}")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> deletePost(@PathVariable Long idPost, Locale locale) {
        Map<String, Object> response = new HashMap<>();
        try {
            postService.delete(postService.findById(idPost));
            response.put("message", messageSource.getMessage("postcontroller.deletePost", null, locale));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (DataAccessException e) {
            response.put("message", response.put("message", messageSource.getMessage("error.database", null, locale)));
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/img/{fileName:.+}")
    public ResponseEntity<Resource> viewPhoto(@PathVariable String fileName) {
        Resource resource = null;
        HttpHeaders header = null;
        try {
            resource = uploadFileService.charge(fileName, FINAL_DIRECTORY);
            header = new HttpHeaders();
            header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(resource, header, HttpStatus.OK);
    }
}
