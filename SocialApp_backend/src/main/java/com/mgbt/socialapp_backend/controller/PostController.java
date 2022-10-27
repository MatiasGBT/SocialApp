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

    private final static String FINAL_DIRECTORY = "/posts";
    private final static String INDEX_FEED_PAGE = "feed";
    private final static String INDEX_OLD_FEED_PAGE = "feedOld";
    private final static String PROFILE_PAGE = "profile";

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
            response.put("message", messageSource.getMessage("error.postNotExist", null, locale));
            response.put("error", messageSource.getMessage("error.postNotExist.redirect", null, locale));
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    @GetMapping("/get/posts/{idUser}&{from}&{page}")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> getFeedByUser(@PathVariable Long idUser, @PathVariable Integer from,
                                           @PathVariable String page, Locale locale) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Post> posts = new ArrayList<>();
            boolean isLastPage = true;
            switch (page) {
                case INDEX_FEED_PAGE -> {
                    posts = postService.findFeedByUserId(idUser, from);
                    Long lastIdPost = postService.findLastIdPostFromUserFeed(idUser);
                    isLastPage = postService.getIsLastPage(lastIdPost, posts);
                }
                case INDEX_OLD_FEED_PAGE -> {
                    posts = postService.findOldFeedByUserId(idUser, from);
                    Long lastIdPost = postService.findLastIdPostFromOldUserFeed(idUser);
                    isLastPage = postService.getIsLastPage(lastIdPost, posts);
                }
                case PROFILE_PAGE -> {
                    posts = postService.findByUserId(idUser, from);
                    Long lastIdPost = postService.findLastIdPostFromUserPosts(idUser);
                    isLastPage = postService.getIsLastPage(lastIdPost, posts);
                }
            }
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

    @GetMapping("/get/popular")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> getTheMostPopularPostFromToday(Locale locale) {
        try {
            Post popularPost = postService.findTheMostPopularPostsFromToday();
            return new ResponseEntity<>(popularPost, HttpStatus.OK);
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
            if (post.getUser().getIdUser() != user.getIdUser()) {
                NotificationPost notificationPost = new NotificationPost();
                notificationPost.setPost(post);
                notificationPost.setUserReceiver(post.getUser());
                this.notificationService.save(notificationPost);
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
            likeService.delete(like);
            response.put("message", messageSource.getMessage("postController.dislikePost", null, locale));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (DataAccessException e) {
            response.put("message", response.put("message", messageSource.getMessage("error.database", null, locale)));
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/post")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> createPost(@RequestParam(value = "file", required = false) MultipartFile file,
                                        @RequestParam("text") String text,
                                        @RequestParam("username") String username,
                                        Locale locale) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserApp user = userService.findByUsername(username);
            Post post = new Post();
            post.setText(text);
            post.setUser(user);
            if (file != null && !file.isEmpty()) {
                String fileName = uploadFileService.save(file, FINAL_DIRECTORY);
                post.setPhoto(fileName);
            }
            postService.save(post);
            response.put("message", messageSource.getMessage("postController.createPost", null, locale));
            response.put("idPost", post.getIdPost());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            response.put("message", messageSource.getMessage("error.databaseOrFile", null, locale));
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{idPost}")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> deletePost(@PathVariable Long idPost, Locale locale) {
        Map<String, Object> response = new HashMap<>();
        try {
            Post post = postService.findById(idPost);
            uploadFileService.delete(post.getPhoto(), FINAL_DIRECTORY);
            postService.delete(post);
            response.put("message", messageSource.getMessage("postController.deletePost", null, locale));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("message", response.put("message", messageSource.getMessage("error.databaseOrFile", null, locale)));
            response.put("error", e.getMessage() + ": " + e.getMessage());
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