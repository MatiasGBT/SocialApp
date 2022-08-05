package com.mgbt.socialapp_backend.controller;

import com.mgbt.socialapp_backend.model.entity.Message;
import com.mgbt.socialapp_backend.model.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("posts/")
@PreAuthorize("isAuthenticated()")
public class TestController {

    @Autowired
    PostService postService;

    @Autowired
    MessageService messageService;

    @GetMapping("/")
    @PreAuthorize("hasRole('admin')")
    public String findAllPosts() {
        return "hola";
        //postService.toList();
    }

    @GetMapping("/1")
    public List<Message> findAllMessages() {
        return messageService.toList();
    }
}
