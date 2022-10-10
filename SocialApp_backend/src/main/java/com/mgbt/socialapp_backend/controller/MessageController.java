package com.mgbt.socialapp_backend.controller;

import com.mgbt.socialapp_backend.model.entity.Message;
import com.mgbt.socialapp_backend.model.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("api/messages/")
@PreAuthorize("isAuthenticated()")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    MessageSource messageSource;

    @GetMapping("/get/by-users/{idKeycloakUser}&{idFriend}&{page}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> getMessagesFromUsers(@PathVariable Long idKeycloakUser,
                                                  @PathVariable Long idFriend,
                                                  @PathVariable Integer page,
                                                  Locale locale) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Message> messages = messageService.findByUsers(idKeycloakUser, idFriend, page);
            Long lastIdMessage = messageService.findLastIdMessageFromUsers(idKeycloakUser, idFriend);
            boolean isLastPage = messageService.getIsLastPage(lastIdMessage, messages);
            response.put("messages", messages);
            response.put("isLastPage", isLastPage);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (DataAccessException e) {
            response.put("message", messageSource.getMessage("error.database", null, locale));
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}