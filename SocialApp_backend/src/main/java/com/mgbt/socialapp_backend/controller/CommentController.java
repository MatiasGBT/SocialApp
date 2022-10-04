package com.mgbt.socialapp_backend.controller;

import com.mgbt.socialapp_backend.model.entity.Comment;
import com.mgbt.socialapp_backend.model.service.CommentService;
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
    MessageSource messageSource;

    @GetMapping("/get/by-post/{idPost}")
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

    @GetMapping("/get/{idComment}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> getComment(@PathVariable Long idComment, Locale locale) {
        try {
            Comment comment = commentService.findById(idComment);
            return new ResponseEntity<>(comment, HttpStatus.OK);
        } catch (DataAccessException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", messageSource.getMessage("error.database", null, locale));
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get/answers/{idComment}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> getAnswers(@PathVariable Long idComment, Locale locale) {
        try {
            Comment comment = commentService.findById(idComment);
            return new ResponseEntity<>(comment.getAnswers(), HttpStatus.OK);
        } catch (DataAccessException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", messageSource.getMessage("error.database", null, locale));
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/post/{idSourceComment}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> createComment(@RequestBody Comment comment,
                                           @PathVariable Long idSourceComment,
                                           Locale locale) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long idComment = commentService.save(comment).getIdComment();
            //If the idSourceComment is not 0, the comment sent from the frontend is a reply from another comment.
            if (idSourceComment != 0) {
                Comment sourceComment = commentService.findById(idSourceComment);
                sourceComment.getAnswers().add(comment);
                commentService.save(sourceComment);
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
