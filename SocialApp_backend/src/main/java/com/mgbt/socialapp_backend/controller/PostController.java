package com.mgbt.socialapp_backend.controller;

import com.mgbt.socialapp_backend.exceptions.FileNameTooLongException;
import com.mgbt.socialapp_backend.model.entity.*;
import com.mgbt.socialapp_backend.model.service.IUploadFileService;
import com.mgbt.socialapp_backend.model.service.*;
import com.mgbt.socialapp_backend.utility_classes.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
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
    private final static String INDEX_FRIENDS_FEED_PAGE = "friendsFeed";
    private final static String INDEX_OLD_FRIENDS_FEED_PAGE = "friendsFeedOld";
    private final static String INDEX_FOLLOWING_FEED_PAGE = "followingFeed";
    private final static String INDEX_OLD_FOLLOWING_FEED_PAGE = "followingFeedOld";
    private final static String INDEX_TREND_FEED_PAGE = "trendFeed";
    private final static String PROFILE_PAGE = "profile";
    private final static String LIKED_POSTS_PAGE = "likedPosts";

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    MessageSource messageSource;

    @Autowired
    private IUploadFileService uploadFileService;

    @Operation(summary = "Gets a post by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post entity",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Post.class)) }),
            @ApiResponse(responseCode = "404", description = "Post not found",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = InternalServerError.class)) })
    })
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

    @Operation(summary = "Gets a paginated list of posts. It receives from where it is going to continue with pagination, so the client will have to own the pagination logic. It also gets the frontend page where it is so it knows which list to get")
    @ApiResponse(responseCode = "200", description = "Array of posts",
            content = { @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Post.class))) })
    @GetMapping("/get/list/{idUser}&{from}&{frontendPage}")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> getFeedByUser(@PathVariable Long idUser, @PathVariable Integer from,
                                           @PathVariable String frontendPage, Locale locale) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Post> posts = new ArrayList<>();
            Long lastIdPost = null;
            boolean isLastPage;
            switch (frontendPage) {
                case INDEX_FRIENDS_FEED_PAGE -> {
                    posts = postService.findFriendsFeedByUserId(idUser, from);
                    lastIdPost = postService.findLastIdPostFromFriendsFeedByUserId(idUser);
                }
                case INDEX_OLD_FRIENDS_FEED_PAGE -> {
                    posts = postService.findOldFriendsFeedByUserId(idUser, from);
                    lastIdPost = postService.findLastIdPostFromOldFriendsFeedByUserId(idUser);
                }
                case INDEX_FOLLOWING_FEED_PAGE -> {
                    posts = postService.findFollowingFeedByUserId(idUser, from);
                    lastIdPost = postService.findLastIdPostFromFollowingFeedByUserId(idUser);
                }
                case INDEX_OLD_FOLLOWING_FEED_PAGE -> {
                    posts = postService.findOldFollowingFeedByUserId(idUser, from);
                    lastIdPost = postService.findLastIdPostFromOldFollowingFeedByUserId(idUser);
                }
                case INDEX_TREND_FEED_PAGE -> {
                    posts = postService.findTrendFeed(from);
                    lastIdPost = postService.findLastIdPostFromTrendFeed(idUser);
                }
                case PROFILE_PAGE -> {
                    posts = postService.findPostsByUserId(idUser, from);
                    lastIdPost = postService.findLastIdPostFromUserPosts(idUser);
                }
                case LIKED_POSTS_PAGE -> {
                    posts = postService.findLikedPostsByUserId(idUser, from);
                    lastIdPost = postService.findLastIdPostFromLikedPostsUser(idUser);
                }
            }
            isLastPage = postService.getIsLastPage(lastIdPost, posts);
            response.put("posts", posts);
            response.put("isLastPage", isLastPage);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (DataAccessException e) {
            response.put("message", messageSource.getMessage("error.database", null, locale));
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Get today's featured post")
    @ApiResponse(responseCode = "200", description = "Post entity",
            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Post.class)) })
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

    @Operation(summary = "Get the number of posts a user has")
    @ApiResponse(responseCode = "200", description = "Posts quantity",
            content = { @Content(mediaType = "text/plain", schema = @Schema(type = "integer")) })
    @GetMapping("/get/count/{idUser}")
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

    @Operation(summary = "Creates a post with the request params")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Post created correctly",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = JsonPostMessage.class)) }),
            @ApiResponse(responseCode = "400", description = "The file name is too long",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = InternalServerError.class)) })
    })
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
        } catch (FileNameTooLongException e) {
            response.put("message", messageSource.getMessage("error.nameTooLong", null, locale));
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            response.put("message", messageSource.getMessage("error.databaseOrFile", null, locale));
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Delete a post")
    @ApiResponse(responseCode = "200", description = "Post deleted correctly",
            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = JsonMessage.class)) })
    @DeleteMapping("/delete/{idPost}")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> deletePost(@PathVariable Long idPost, Locale locale) {
        Map<String, Object> response = new HashMap<>();
        try {
            Post post = postService.findById(idPost);
            if (post != null) {
                uploadFileService.delete(post.getPhoto(), FINAL_DIRECTORY);
                postService.delete(post);
            }
            response.put("message", messageSource.getMessage("postController.deletePost", null, locale));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("message", response.put("message", messageSource.getMessage("error.databaseOrFile", null, locale)));
            response.put("error", e.getMessage() + ": " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //#region Highlight Post
    @Operation(summary = "Gets the pinned post by its user ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post entity",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Post.class)) }),
            @ApiResponse(responseCode = "404", description = "Post not found",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = InternalServerError.class)) })
    })
    @GetMapping("/get/pinned/{idUser}")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> getPinnedPost(@PathVariable Long idUser, Locale locale) {
        try {
            UserApp user = userService.findById(idUser);
            Post post = postService.findByUserAndIsPinned(user);
            return new ResponseEntity<>(post, HttpStatus.OK);
        } catch (DataAccessException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", messageSource.getMessage("error.database", null, locale));
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Sets the isPinned property of a post to true")
    @ApiResponse(responseCode = "200", description = "Post updated correctly",
            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = JsonMessage.class)) })
    @PutMapping("/put/pin/{idPost}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> pinPost(@PathVariable Long idPost, Locale locale) {
        Map<String, Object> response = new HashMap<>();
        try {
            Post post = postService.findById(idPost);
            Post featuredPost = postService.findByUserAndIsPinned(post.getUser());
            if (featuredPost != null) {
                featuredPost.setIsPinned(false);
                postService.save(featuredPost);
            }
            post.setIsPinned(true);
            postService.save(post);
            response.put("message", messageSource.getMessage("postController.highlightPost", null, locale));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (DataAccessException e) {
            response.put("message", messageSource.getMessage("error.database", null, locale));
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Sets the isPinned property of a post to false")
    @ApiResponse(responseCode = "200", description = "Post updated correctly",
            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = JsonMessage.class)) })
    @PutMapping("/put/unpin/{idPost}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> unpinPost(@PathVariable Long idPost, Locale locale) {
        Map<String, Object> response = new HashMap<>();
        try {
            Post post = postService.findById(idPost);
            post.setIsPinned(false);
            postService.save(post);
            response.put("message", messageSource.getMessage("postController.highlightPost", null, locale));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (DataAccessException e) {
            response.put("message", messageSource.getMessage("error.database", null, locale));
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    //#endregion

    @Operation(summary = "Gets file from posts directory by filename")
    @ApiResponse(description = "Image file", content = { @Content(mediaType = "multipart/form-data") })
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