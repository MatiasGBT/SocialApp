import { Injectable } from '@angular/core';
import { Client, StompSubscription } from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';
import { Message } from '../models/message';
import { User } from '../models/user';
import { AuthService } from './auth.service';
import { MessageService } from './message.service';
import { NotificationsService } from './notifications.service';
import { TranslateExtensionService } from './translate-extension.service';
import { UserService } from './user.service';

@Injectable({
  providedIn: 'root'
})
export class WebsocketService {
  private stopmJsClient: Client;
  private friendConnected: StompSubscription;
  private friendDisconnected: StompSubscription;
  private messageSubscription: StompSubscription;
  private writingStatusSubscription: StompSubscription;
  private friendIsInChatSubscription: StompSubscription;
  private friendIsInChat: string = "false";

  constructor(private notificationsService: NotificationsService, private authService: AuthService,
    private messageService: MessageService, private userService: UserService,
    private translateExtensionService: TranslateExtensionService) { }

  public startConnection() {
    this.stopmJsClient = new Client;
    this.stopmJsClient.webSocketFactory = () => {
      return new SockJS("http://localhost:8090/ws");
    }
    this.stopmJsClient.onConnect = (frame) => {
      this.stopmJsClient.subscribe(`/ws/notifications/${this.authService.getUsername()}`, e => {
        if (e.body == "200") {
          console.log(this.translateExtensionService.getTranslatedStringByUrl("WEBSOCKET.NEW_NOTIFICATION"));
          this.notificationsService.newNotification.emit();
        }
      });
      this.stopmJsClient.publish({ destination: `/ws-app/chat/connect/${this.authService.getUsername()}` });
    }
    this.stopmJsClient.activate();
  }

  public endConnection() {
    this.stopmJsClient.publish({ destination: `/ws-app/chat/disconnect/${this.authService.getUsername()}` });
  }

  public newNotification(userReceiver: User) {
    if (this.friendIsInChat !== 'true') {
      this.stopmJsClient.publish({ destination: `/ws-app/notifications/${userReceiver.username}`});
    }
  }

  //#region Subscribe to a chat
  public openChat(userFriend: User) {
    this.unsubscribeFromChat();
    this.subscribeToChat(userFriend);
  }

  private unsubscribeFromChat() {
    this.friendConnected?.unsubscribe();
    this.friendDisconnected?.unsubscribe();
    this.messageSubscription?.unsubscribe();
    this.writingStatusSubscription?.unsubscribe();
    this.friendIsInChatSubscription?.unsubscribe();
  }

  private subscribeToChat(userFriend: User) {
    this.subscribeToFriendIsConnected(userFriend);
    this.subscribeToFriendIsDisconnected(userFriend);
    this.subscribeToMessage(userFriend);
    this.subscribeToWritingStatus(userFriend);
    this.subscribeAndPublishIsInChatStatus(userFriend);
  }

  //#region Private subscriptions
  private subscribeToFriendIsConnected(userFriend: User) {
    this.friendConnected = this.stopmJsClient.subscribe(`/ws/chat/connect/${userFriend.username}`, e => {
      if (e.body == "200") {
        this.userService.userConnectEvent.emit();
      }
    });
  }

  private subscribeToFriendIsDisconnected(userFriend: User) {
    this.friendDisconnected = this.stopmJsClient.subscribe(`/ws/chat/disconnect/${userFriend.username}`, e => {
      if (e.body == "200") {
        this.userService.userDisconnectEvent.emit();
      }
    });
  }

  private subscribeToMessage(userFriend: User) {
    this.messageSubscription = this.stopmJsClient.subscribe(`/ws/chat/message/${this.authService.getUsername()}/${userFriend.username}`, e => {
      this.messageService.newMessageEvent.emit(JSON.parse(e.body) as Message);
    });
  }

  private subscribeToWritingStatus(userFriend: User) {
    this.writingStatusSubscription = this.stopmJsClient.subscribe(`/ws/chat/writing/${this.authService.getUsername()}/${userFriend.username}`, e => {
      if (e.body == "200") {
        userFriend.isConnected = null;
        setTimeout(() => { userFriend.isConnected = true }, 3000);
      }
    });
  }

  private subscribeAndPublishIsInChatStatus(userFriend: User) {
    this.friendIsInChatSubscription = this.stopmJsClient.subscribe(`/ws/chat/inChat/${this.authService.getUsername()}/${userFriend.username}`, e => {
      this.friendIsInChat = e.body;
    });

    this.stopmJsClient.publish({ destination: `/ws-app/chat/inChat/${this.authService.getUsername()}/${userFriend.username}` });
  }
  //#endregion
  //#endregion

  public sendMessage(userReceiver: User, message: Message) {
    this.stopmJsClient.publish({ destination: `/ws-app/chat/message/${this.authService.getUsername()}/${userReceiver.username}/${this.friendIsInChat}`, body: JSON.stringify(message)});
  }

  public userIsWriting(userReceiver: User) {
    this.stopmJsClient.publish({ destination: `/ws-app/chat/writing/${this.authService.getUsername()}/${userReceiver.username}` });
  }

  public quitChat(userReceiver: User) {
    this.stopmJsClient.publish({ destination: `/ws-app/chat/outChat/${this.authService.getUsername()}/${userReceiver.username}` });
  }
}