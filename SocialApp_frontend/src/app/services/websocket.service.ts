import { EventEmitter, Injectable, Output } from '@angular/core';
import { Router } from '@angular/router';
import { Client, StompSubscription } from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';
import Swal from 'sweetalert2';
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
  //#region Chat properties
  private friendConnected: StompSubscription;
  private friendDisconnected: StompSubscription;
  private messageSubscription: StompSubscription;
  private deleteMessageSubscription: StompSubscription;
  private writingStatusSubscription: StompSubscription;
  private friendIsInChatSubscription: StompSubscription;
  private friendIsInChat: string = "false";
  //#endregion
  //#region Call properties
  private waitingForFriendToAcceptCall: StompSubscription;
  private friendIsReadyToCall: StompSubscription;
  private waitingForFriendToRejectCall: StompSubscription;
  private enterCall: StompSubscription;
  private receivePeerId: StompSubscription;
  private friendVideoStatus: StompSubscription;
  @Output() friendAcceptedCallEvent: EventEmitter<any> = new EventEmitter();
  @Output() friendIsReadyToStartCallEvent: EventEmitter<any> = new EventEmitter();
  @Output() friendRejectedCallEvent: EventEmitter<any> = new EventEmitter();
  @Output() receivePeerIdEvent: EventEmitter<any> = new EventEmitter();
  @Output() friendChangedCameraStatusEvent: EventEmitter<any> = new EventEmitter();
  public callCreatorUsername: string;
  //#endregion

  constructor(private notificationsService: NotificationsService, private authService: AuthService,
    private messageService: MessageService, private userService: UserService,
    private translateExtensionService: TranslateExtensionService, private router: Router) { }

  //This method is called when the application is started in App Component
  public startConnection() {
    this.stopmJsClient = new Client;
    this.stopmJsClient.webSocketFactory = () => {
      return new SockJS("http://localhost:8090/ws");
    }
    this.stopmJsClient.onConnect = (frame) => {
      this.subscribeToNotifications();
      this.subscribeToCalls();
      this.stopmJsClient.publish({ destination: `/ws-app/chat/connect/${this.authService.getUsername()}` });
    }
    this.stopmJsClient.activate();
  }

  //#region onConnect subscriptions
  private subscribeToNotifications() {
    this.stopmJsClient.subscribe(`/ws/notifications/${this.authService.getUsername()}`, e => {
      if (e.body == "200") {
        console.log(this.translateExtensionService.getTranslatedStringByUrl("WEBSOCKET.NEW_NOTIFICATION"));
        this.notificationsService.newNotificationEvent.emit();
      }
    });
  }

  private subscribeToCalls() {
    this.stopmJsClient.subscribe(`/ws/call/${this.authService.getUsername()}`, e => {
      const audio = new Audio('assets/call.mp3');
      audio.play();
      let user: User = JSON.parse(e.body) as User;
      Swal.fire({
        title: user.name + " " + user.surname + this.translateExtensionService.getTranslatedStringByUrl("CALL.IS_CALLING_YOU_TITLE"),
        text: this.translateExtensionService.getTranslatedStringByUrl("CALL.IS_CALLING_YOU_TEXT"),
        timer: 15000, background: '#7f5af0', color: 'white', showCancelButton: true,
        confirmButtonColor: '#2cb67d', cancelButtonColor: '#d33',
        confirmButtonText: this.translateExtensionService.getTranslatedStringByUrl("CALL.IS_CALLING_YOU_CONFIRM_BUTTON"),
        cancelButtonText: this.translateExtensionService.getTranslatedStringByUrl("CALL.IS_CALLING_YOU_REJECT_BUTTON")
      }).then((result) => {
        audio.pause();
        if (result.isConfirmed) {
          this.router.navigate(['/profile/call', user.idUser]);
          this.acceptCall(user);
        } else {
          this.rejectCall(user);
        }
      });
    });
  }

  private acceptCall(friend: User) {
    this.stopmJsClient.publish({ destination: `/ws-app/call/accept/${this.authService.getUsername()}/${friend.username}` });
  }

  private rejectCall(friend: User) {
    this.stopmJsClient.publish({ destination: `/ws-app/call/reject/${this.authService.getUsername()}/${friend.username}` });
  }
  //#endregion

  /*This method sends the backend a signal that the user is no longer connected to the application
  (which changes the status for all users who were chatting with him).
  It also disables the WebSocket connection.
  This method is called before closing the application.*/
  public endConnection() {
    this.stopmJsClient.publish({ destination: `/ws-app/chat/disconnect/${this.authService.getUsername()}` });
    this.stopmJsClient.deactivate();
  }

  public newNotification(userReceiver: User) {
    if (this.friendIsInChat !== 'true') {
      this.stopmJsClient.publish({ destination: `/ws-app/notifications/${userReceiver.username}`});
    }
  }

  //#region Chat
  public enterChat(userFriend: User) {
    this.unsubscribeFromChat();
    this.subscribeToChat(userFriend);
  }

  private unsubscribeFromChat() {
    this.friendConnected?.unsubscribe();
    this.friendDisconnected?.unsubscribe();
    this.messageSubscription?.unsubscribe();
    this.deleteMessageSubscription?.unsubscribe();
    this.writingStatusSubscription?.unsubscribe();
    this.friendIsInChatSubscription?.unsubscribe();
  }

  private subscribeToChat(userFriend: User) {
    this.subscribeToFriendIsConnected(userFriend);
    this.subscribeToFriendIsDisconnected(userFriend);
    this.subscribeToMessage(userFriend);
    this.subscribeToDeleteMessage(userFriend);
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

  private subscribeToDeleteMessage(userFriend: User) {
    this.deleteMessageSubscription = this.stopmJsClient.subscribe(`/ws/chat/message/delete/${this.authService.getUsername()}/${userFriend.username}`, e => {
      this.messageService.deleteMessageEvent.emit(JSON.parse(e.body) as Message);
    });
  }

  private subscribeToWritingStatus(userFriend: User) {
    this.writingStatusSubscription = this.stopmJsClient.subscribe(`/ws/chat/writing/${this.authService.getUsername()}/${userFriend.username}`, e => {
      if (e.body == "200") {
        userFriend.status.text = "Writing";
        setTimeout(() => { userFriend.status.text = "Connected" }, 2000);
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

  public sendMessage(userReceiver: User, message: Message) {
    this.stopmJsClient.publish({ destination: `/ws-app/chat/message/${this.authService.getUsername()}/${userReceiver.username}/${this.friendIsInChat}`, body: JSON.stringify(message)});
  }

  public deleteMessage(userReceiver: User, message: Message) {
    this.stopmJsClient.publish({ destination: `/ws-app/chat/message/delete/${this.authService.getUsername()}/${userReceiver.username}`, body: JSON.stringify(message)});
  }

  public userIsWriting(userReceiver: User) {
    this.stopmJsClient.publish({ destination: `/ws-app/chat/writing/${this.authService.getUsername()}/${userReceiver.username}` });
  }

  public quitChat(userReceiver: User) {
    this.stopmJsClient.publish({ destination: `/ws-app/chat/outChat/${this.authService.getUsername()}/${userReceiver.username}` });
  }
  //#endregion

  //#region Call
  public callFriend(userReceiver: User) {
    this.stopmJsClient.publish({ destination: `/ws-app/call/${this.authService.getUsername()}/${userReceiver.username}` });
    this.unsubscribeFromWaitingCall();
    this.subscribeToWaitingCall(userReceiver);
  }

  private unsubscribeFromWaitingCall() {
    this.waitingForFriendToAcceptCall?.unsubscribe();
    this.friendIsReadyToCall?.unsubscribe();
    this.waitingForFriendToRejectCall?.unsubscribe();
  }

  private subscribeToWaitingCall(userReceiver: User) {
    this.waitForFriendToAcceptCall(userReceiver);
    this.waitForFriendToBeReady(userReceiver);
    this.waitForFriendToRejectCall(userReceiver);
  }

  //#region Private subscriptions to waiting call
  private waitForFriendToAcceptCall(userReceiver: User) {
    this.waitingForFriendToAcceptCall = this.stopmJsClient.subscribe(`/ws/call/accept/${this.authService.getUsername()}/${userReceiver.username}`, e => {
      if (e.body == "200") {
        this.callCreatorUsername = this.authService.getUsername();
        this.friendAcceptedCallEvent.emit(userReceiver);
      }
    });
  }

  private waitForFriendToBeReady(userReceiver: User) {
    /*
    This subscription is necessary because, if the creator of the call connects to the call
    first and sends the peer ID to the other user, and the other user did not connect,
    the call does not work (as the peer ID is not sent again).
    Because of this, it is necessary for the creator of the call to wait for the other user
    to connect. When when the other user is available, he/she needs to notify the creator
    when he/she can join, so that the peer ID can be correctly sent.
    */
    this.friendIsReadyToCall = this.stopmJsClient.subscribe(`/ws/call/ready/${this.authService.getUsername()}/${userReceiver.username}`, e => {
      if (e.body == "200") {
        this.friendIsReadyToStartCallEvent.emit();
        this.unsubscribeFromWaitingCall();
      }
    });
  }

  private waitForFriendToRejectCall(userReceiver: User) {
    this.waitingForFriendToRejectCall = this.stopmJsClient.subscribe(`/ws/call/reject/${this.authService.getUsername()}/${userReceiver.username}`, e => {
      if (e.body == "200") {
        this.friendRejectedCallEvent.emit(userReceiver);
        this.unsubscribeFromWaitingCall();
      }
    });
  }
  //#endregion

  public sendFriendIsReady(userReceiver: User) {
    this.stopmJsClient.publish({ destination: `/ws-app/call/ready/${this.authService.getUsername()}/${userReceiver.username}`});
  }

  public subscribeToCall(userReceiver: User) {
    this.enterCall = this.stopmJsClient.subscribe(`/ws/call/end/${this.authService.getUsername()}/${userReceiver.username}`, e => {
      if (e.body == "200") {
        Swal.fire({
          title: this.translateExtensionService.getTranslatedStringByUrl("CALL.END_CALL"),
          showConfirmButton: false, timer: 1500, background: '#7f5af0', color: 'white'
        });
        this.router.navigate(['/profile', userReceiver.idUser]);
      }
    });
    this.receivePeerId = this.stopmJsClient.subscribe(`/ws/call/peerid/${this.authService.getUsername()}`, e => {
      this.receivePeerIdEvent.emit(e.body);
    });
    this.friendVideoStatus = this.stopmJsClient.subscribe(`/ws/call/video-enabled/${this.authService.getUsername()}`, e => {
      if (e.body != "500") {
        this.friendChangedCameraStatusEvent.emit(e.body);
      }
    });
  }

  private unsubcribeFromCall() {
    this.enterCall?.unsubscribe();
    this.receivePeerId?.unsubscribe();
    this.friendVideoStatus?.unsubscribe();
  }

  /*When both users join the call, the call creator (callCreatorUsername) sends his peer ID
  so that the other user can send and receive audio and video from the call (this is done by
  the system internally, the user does not have to set the peer ID manually).
  This method allows the call originator to notify the WebSocket,
  who sends the peer ID to the other user.*/
  public sendPeerId(userReceiver: User, peerId: string) {
    this.stopmJsClient.publish({ destination: `/ws-app/call/peerid/${userReceiver.username}`, body: peerId});
  }

  public notifyCameraChange(userReceiver: User, videoIsEnabled: boolean) {
    this.stopmJsClient.publish({ destination: `/ws-app/call/video-enabled/${userReceiver.username}`, body: videoIsEnabled.toString()});
  }

  public endCall(userReceiver: User) {
    this.callCreatorUsername = null;
    //When one user ends the call, the system informs the other user
    this.stopmJsClient.publish({ destination: `/ws-app/call/end/${this.authService.getUsername()}/${userReceiver.username}` });
    this.unsubscribeFromWaitingCall();
    this.unsubcribeFromCall();
    this.router.navigate(['/profile']);
  }
  //#endregion
}