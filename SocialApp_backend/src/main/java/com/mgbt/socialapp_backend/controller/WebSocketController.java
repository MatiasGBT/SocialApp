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
        try {
            boolean friendInChat = Boolean.parseBoolean(friendIsInChat);
            if (!friendInChat) {
                NotificationMessage notification = new NotificationMessage();
                notification.setUserTransmitter(message.getUserTransmitter());
                notification.setUserReceiver(message.getUserReceiver());
                notificationService.save(notification);
            }
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
        return message;
    }

    @MessageMapping("/chat/message/delete/{usernameTransmitter}/{usernameReceiver}")
    @SendTo({"/ws/chat/message/delete/{usernameReceiver}/{usernameTransmitter}"})
    public Message deleteMessage(Message message) {
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
        user.setStatus("Connected");
        userService.save(user);
        return "200";
    }

    @MessageMapping("/chat/disconnect/{username}")
    @SendTo({"/ws/chat/disconnect/{username}"})
    public String userDisconnect(@DestinationVariable String username) {
        UserApp user = userService.findByUsername(username);
        user.setStatus("Disconnected");
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

    @MessageMapping("/call/{usernameEmitter}/{usernameReceiver}")
    @SendTo({"/ws/call/{usernameReceiver}"})
    public UserApp callFriend(@DestinationVariable String usernameEmitter) {
        return this.userService.findByUsername(usernameEmitter);
    }

    @MessageMapping("/call/accept/{usernameTransmitter}/{usernameReceiver}")
    @SendTo({"/ws/call/accept/{usernameReceiver}/{usernameTransmitter}"})
    public String acceptCall(@DestinationVariable String usernameTransmitter,
                             @DestinationVariable String usernameReceiver) {
        UserApp userTransmitter = userService.findByUsername(usernameTransmitter);
        UserApp userReceiver = userService.findByUsername(usernameReceiver);
        userTransmitter.setStatus("On call");
        userReceiver.setStatus("On call");
        userService.save(userTransmitter);
        userService.save(userReceiver);
        return "200";
    }

    @MessageMapping("/call/ready/{usernameTransmitter}/{usernameReceiver}")
    @SendTo({"/ws/call/ready/{usernameReceiver}/{usernameTransmitter}"})
    public String userReceiverIsReadyToCall() {
        return "200";
    }

    @MessageMapping("/call/reject/{usernameTransmitter}/{usernameReceiver}")
    @SendTo({"/ws/call/reject/{usernameReceiver}/{usernameTransmitter}"})
    public String rejectCall() {
        return "200";
    }

    @MessageMapping("/call/end/{usernameTransmitter}/{usernameReceiver}")
    @SendTo({"/ws/call/end/{usernameReceiver}/{usernameTransmitter}"})
    public String endCall(@DestinationVariable String usernameTransmitter,
                          @DestinationVariable String usernameReceiver) {
        UserApp userTransmitter = userService.findByUsername(usernameTransmitter);
        UserApp userReceiver = userService.findByUsername(usernameReceiver);
        userTransmitter.setStatus("Connected");
        userReceiver.setStatus("Connected");
        userService.save(userTransmitter);
        userService.save(userReceiver);
        return "200";
    }

    @MessageMapping("/call/peerid/{username}")
    @SendTo({"/ws/call/peerid/{username}"})
    public String sendPeerId(String peerId) {
        return peerId;
    }

    @MessageMapping("/call/video-enabled/{username}")
    @SendTo({"/ws/call/video-enabled/{username}"})
    public String notifyCameraChange(String videoIsEnabled) {
        try {
            Boolean.parseBoolean(videoIsEnabled);
            return videoIsEnabled;
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return "500";
        }
    }
}