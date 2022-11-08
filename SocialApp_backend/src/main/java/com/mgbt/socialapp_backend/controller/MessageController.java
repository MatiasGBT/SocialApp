package com.mgbt.socialapp_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mgbt.socialapp_backend.exceptions.FileNameTooLongException;
import com.mgbt.socialapp_backend.model.entity.Message;
import com.mgbt.socialapp_backend.model.service.*;
import com.mgbt.socialapp_backend.utility_classes.*;
import io.swagger.v3.oas.annotations.*;
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
@RequestMapping("api/messages/")
public class MessageController {

    private final static String FINAL_DIRECTORY = "/messages";

    @Autowired
    private MessageService messageService;

    @Autowired
    MessageSource messageSource;

    @Autowired
    private UploadFileService uploadFileService;

    @Operation(summary = "Gets a paginated list of messages sent by entered users. It receives the page from which it is going to paginate, so the logic must be carried by the client.")
    @ApiResponse(responseCode = "200", description = "Array of messages",
            content = { @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Message.class))) })
    @GetMapping("/get/list/{idKeycloakUser}&{idFriend}&{page}")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
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

    @Operation(summary = "Creates a message with the request params")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Message created correctly",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = JsonMessageEntity.class)) }),
            @ApiResponse(responseCode = "400", description = "The file name is too long",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = InternalServerError.class)) })
    })
    @PostMapping("/post")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> createMessage(@RequestParam(value = "file", required = false) MultipartFile file,
                                           @Parameter(description = "A message entity converted to a string with JSON.stringify()",
                                                   content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class)) })
                                           @RequestParam("message") String message,
                                           Locale locale) {
        Map<String, Object> response = new HashMap<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Message messageConverted = objectMapper.readValue(message, Message.class);
            if (file != null && !file.isEmpty()) {
                String fileName = uploadFileService.save(file, FINAL_DIRECTORY);
                messageConverted.setPhoto(fileName);
            }
            messageConverted = messageService.save(messageConverted);
            response.put("messageId", messageConverted.getIdMessage());
            response.put("messageText", messageConverted.getText());
            response.put("messagePhoto", messageConverted.getPhoto());
            response.put("messageDate", messageConverted.getDate());
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

    @Operation(summary = "Delete a message")
    @ApiResponse(responseCode = "200", description = "Message deleted correctly",
            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = JsonMessage.class)) })
    @DeleteMapping("/delete/{idMessage}")
    @PreAuthorize("isAuthenticated() and hasRole('user')")
    public ResponseEntity<?> deleteMessage(@PathVariable Long idMessage, Locale locale) {
        Map<String, Object> response = new HashMap<>();
        try {
            Message message = messageService.findById(idMessage);
            uploadFileService.delete(message.getPhoto(), FINAL_DIRECTORY);
            messageService.delete(message);
            response.put("message", messageSource.getMessage("messageController.deleteMessage", null, locale));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("message", response.put("message", messageSource.getMessage("error.databaseOrFile", null, locale)));
            response.put("error", e.getMessage() + ": " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Gets file from messages directory by filename")
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