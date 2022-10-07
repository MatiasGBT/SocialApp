package com.mgbt.socialapp_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.stereotype.Controller;
import java.util.Locale;

@Controller
public class WebSocketController {

    @Autowired
    MessageSource messageSource;

    @MessageMapping("/notifications/receiving/{userReceiver}")
    @SendTo({"/ws/receiving/{userReceiver}"})
    public String newNotification(@DestinationVariable String userReceiver, String lang) {
        Locale locale = new Locale(lang);
        return messageSource.getMessage("websocketController.newNotification", null, locale);
    }
}