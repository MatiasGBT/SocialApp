package com.mgbt.socialapp_backend.controller;

import com.mgbt.socialapp_backend.model.entity.Comment;
import com.mgbt.socialapp_backend.model.entity.notification.NotificationComment;
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
@RequestMapping("api/comments/")
@PreAuthorize("isAuthenticated()")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    MessageSource messageSource;

    @Operation(summary = "Gets a list of comments from a post")
    @ApiResponse(responseCode = "200", description = "Array of comments",
            content = { @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Comment.class))) })
    @GetMapping("/get/list/by-post/{idPost}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> getComments(@PathVariable Long idPost, Locale locale) {
        try {
            List<Comment> comments = commentService.toList(idPost);
            return new ResponseEntity<>(comments, HttpStatus.OK);
        } catch (DataAccessException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", messageSource.getMessage("error.database", null, locale));
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Gets a comment by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment entity",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Comment.class)) }),
            @ApiResponse(responseCode = "404", description = "Comment not found",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = InternalServerError.class)) })
    })
    @GetMapping("/get/{idComment}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> getComment(@PathVariable Long idComment, Locale locale) {
        Map<String, Object> response = new HashMap<>();
        Comment comment;
        try {
            comment = commentService.findById(idComment);
        } catch (DataAccessException e) {
            response.put("message", messageSource.getMessage("error.database", null, locale));
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (comment == null) {
            response.put("message", messageSource.getMessage("error.commentNotExist", null, locale));
            response.put("error", messageSource.getMessage("error.commentNotExist.redirect", null, locale));
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }

    @Operation(summary = "Gets a list of replies from a comment")
    @ApiResponse(responseCode = "200", description = "Array of comments",
            content = { @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Comment.class))) })
    @GetMapping("/get/list/replies/{idComment}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> getReplies(@PathVariable Long idComment, Locale locale) {
        try {
            Comment comment = commentService.findById(idComment);
            return new ResponseEntity<>(comment.getReplies(), HttpStatus.OK);
        } catch (DataAccessException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", messageSource.getMessage("error.database", null, locale));
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Creates a comment with the request body")
    @ApiResponse(responseCode = "201", description = "Message created correctly",
            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = JsonCommentMessage.class)) })
    @PostMapping("/post/{idSourceComment}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> createComment(@RequestBody Comment comment,
                                           @Parameter(description = "A comment can be a reply to another comment, so you need to pass the id of the original comment to the one you are replying to. If it is not a reply to another, you must send 0")
                                           @PathVariable Long idSourceComment,
                                           Locale locale) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long idComment = commentService.save(comment).getIdComment();
            //If the idSourceComment is not 0, the comment sent from the frontend is a reply from another comment.
            if (idSourceComment != 0) {
                Comment sourceComment = commentService.findById(idSourceComment);
                sourceComment.getReplies().add(comment);
                commentService.save(sourceComment);
                NotificationComment notificationComment = new NotificationComment(sourceComment.getUser(), sourceComment);
                notificationService.save(notificationComment);
            }
            response.put("message", messageSource.getMessage("commentController.createComment", null, locale));
            response.put("idComment", idComment);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            response.put("message", messageSource.getMessage("error.database", null, locale));
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}