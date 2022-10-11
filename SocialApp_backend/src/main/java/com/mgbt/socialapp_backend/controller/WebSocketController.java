package com.mgbt.socialapp_backend.controller;

import com.mgbt.socialapp_backend.model.entity.*;
import com.mgbt.socialapp_backend.model.entity.notification.NotificationMessage;
import com.mgbt.socialapp_backend.model.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @MessageMapping("/notifications/{userReceiver}")
    @SendTo({"/ws/notifications/{userReceiver}"})
    public String newNotification() {
        return "200";
    }

    @MessageMapping("/chat/message/{usernameTransmitter}/{usernameReceiver}/{friendIsInChat}")
    @SendTo({"/ws/chat/message/{usernameReceiver}/{usernameTransmitter}"})
    public Message sendMessage(@DestinationVariable String friendIsInChat, Message message) {
        messageService.save(message);
        if (!Boolean.parseBoolean(friendIsInChat)) {
            NotificationMessage notification = new NotificationMessage();
            notification.setUserTransmitter(message.getUserTransmitter());
            notification.setUserReceiver(message.getUserReceiver());
            notificationService.save(notification);
        }
        return message;
    }

    @MessageMapping("/chat/writing/{usernameTransmitter}/{usernameReceiver}")
    @SendTo({"/ws/chat/writing/{usernameReceiver}/{usernameTransmitter}"})
    public String userIsWriting() {
        return "200";
    }

    @MessageMapping("/chat/connect/{username}")
    @SendTo({"/ws/chat/connect/{username}"})
    public String userConnected(@DestinationVariable String username) {
        UserApp user = userService.findByUsername(username);
        user.setIsConnected(true);
        userService.save(user);
        return "200";
    }

    @MessageMapping("/chat/disconnect/{username}")
    @SendTo({"/ws/chat/disconnect/{username}"})
    public String userDisconnect(@DestinationVariable String username) {
        UserApp user = userService.findByUsername(username);
        user.setIsConnected(false);
        userService.save(user);
        return "200";
    }

    @MessageMapping("/chat/inChat/{usernameTransmitter}/{usernameReceiver}")
    @SendTo({"/ws/chat/inChat/{usernameReceiver}/{usernameTransmitter}"})
    public String userIsInChat() {
        return "true";
    }

    @MessageMapping("/chat/outChat/{usernameTransmitter}/{usernameReceiver}")
    @SendTo({"/ws/chat/inChat/{usernameReceiver}/{usernameTransmitter}"})
    public String userIsNotInChat() {
        return "false";
    }
}