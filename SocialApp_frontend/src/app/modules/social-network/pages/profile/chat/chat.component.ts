import { AfterViewChecked, Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Friendship } from 'src/app/models/friendship';
import { Message } from 'src/app/models/message';
import { User } from 'src/app/models/user';
import { AuthService } from 'src/app/services/auth.service';
import { FriendshipService } from 'src/app/services/friendship.service';
import { MessageService } from 'src/app/services/message.service';
import { TranslateExtensionService } from 'src/app/services/translate-extension.service';
import { UserService } from 'src/app/services/user.service';
import { WebsocketService } from 'src/app/services/websocket.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.css']
})
export class ChatComponent implements OnInit, AfterViewChecked, OnDestroy {
  public keycloakUser: User;
  public friend: User;
  public message: Message = new Message;
  public file: File;
  public messages: Message[] = [];
  public friendStatus: string;
  @ViewChild('scrollChat') private scrollChat: ElementRef;
  private page: number = 0;
  public isLastPage: boolean;

  private scrollDisabled: boolean = false;

  constructor(private authService: AuthService, private activatedRoute: ActivatedRoute,
    private router: Router, private messageService: MessageService,
    private friendshipService: FriendshipService, private webSocketService: WebsocketService,
    private userService: UserService, private translateExtensionService: TranslateExtensionService) { }

  ngOnInit(): void {
    this.activatedRoute.params.subscribe(params => {
      let id = params['id'];
      if (id) {
        this.getFriendship(id);
      } else {
        this.router.navigate(['/index']);
      }
    });

    this.messageService.newMessageEvent.subscribe(message => {
      this.messages.push(message);
      this.scrollDisabled = false;
    });

    this.messageService.deleteMessageEvent.subscribe(message => {
      this.messages = this.messages.filter(msg => msg.idMessage != message.idMessage);
    });

    this.userService.userConnectEvent.subscribe(() => this.friend.status.text = "Connected");

    this.userService.userDisconnectEvent.subscribe(() => this.friend.status.text = "Disconnected");
  }

  ngAfterViewChecked(): void {
    this.scrollToBottom();
  }

  ngOnDestroy(): void {
    //It is necessary to make the check because, if the user enters through the url to a chat
    //of someone who does not have as a friend, the property this.friend will not be instantiated
    //and this will throw an error.
    if (this.friend) {
      this.webSocketService.quitChat(this.friend);
    }
  }

  private scrollToBottom() {
    if (this.messages.length !== 0 && !this.scrollDisabled) {
      this.scrollChat.nativeElement.scrollTop = this.scrollChat.nativeElement.scrollHeight;
      this.scrollDisabled = true;
    }
  }

  //#region Init methods
  private getFriendship(id: number) {
    this.friendshipService.getFriendship(id).subscribe(friendship => {
      if (friendship.status) {
        this.setUsers(friendship);
        this.initChat();
      } else {
        this.router.navigate(['/profile']);
      }
    });
  }

  private setUsers(friendship: Friendship) {
    if (friendship?.userTransmitter.username == this.authService.getUsername()) {
      this.keycloakUser = friendship.userTransmitter;
      this.friend = friendship.userReceiver;
    } else {
      this.keycloakUser = friendship.userReceiver;
      this.friend = friendship.userTransmitter;
    }
  }

  private initChat() {
    this.webSocketService.enterChat(this.friend);
    this.messageService.getMessagesByUsers(this.keycloakUser.idUser, this.friend.idUser, this.page).subscribe(response => {
      this.messages = response.messages.reverse();
      this.isLastPage = response.isLastPage;
    });
  }
  //#endregion

  public sendMessage() {
    if (this.message?.text || this.file) {
      this.createMessage();
    } else {
      Swal.fire({
        title: this.translateExtensionService.getTranslatedStringByUrl("CHAT.INVALID_MESSAGE_MODAL_TITLE"),
        text: this.translateExtensionService.getTranslatedStringByUrl("CHAT.INVALID_MESSAGE_MODAL_TEXT"),
        icon: 'error', showConfirmButton: false, timer: 1250, background: '#7f5af0', color: 'white'
      });
    }
  }

  private createMessage() {
    if (!this.message?.text) {
      this.message.text = "";
    }
    if (this.message.text?.length <= 50) {
      this.message.userTransmitter = this.keycloakUser;
      this.message.userReceiver = this.friend;
      this.message.idMessage = 0;
      this.messageService.createMessage(this.message, this.file).subscribe(response => {
        this.message.idMessage = response.messageId;
        this.message.text = response.messageText;
        this.message.photo = response.messagePhoto;
        this.message.date = response.messageDate;
        this.messages.push(this.message);
        this.webSocketService.newNotification(this.message.userReceiver);
        this.webSocketService.sendMessage(this.message.userReceiver, this.message);
        this.message = new Message;
        this.file = null;
        this.scrollDisabled = false;
      });
    }
  }

  public userIsWriting(event) {
    if ((event.key === 'Enter' || event.keyCode == 13)) {
      this.sendMessage();
    } else {
      this.webSocketService.userIsWriting(this.friend);
    }
  }

  public getMoreMessagesIfTheUserScrollToTop() {
    let fullScrollHeight = this.scrollChat.nativeElement.scrollHeight;
    if (this.scrollChat.nativeElement.scrollTop == 0 && !this.isLastPage) {
      this.getMoreMessages(fullScrollHeight);
    }
  }

  private getMoreMessages(fullScrollHeight: number) {
    this.page += 15;
    this.messageService.getMessagesByUsers(this.keycloakUser.idUser, this.friend.idUser, this.page).subscribe(response => {
      this.messages = response.messages.reverse().concat(this.messages);
      this.isLastPage = response.isLastPage;

      /*
      This is to make the scroll height the same as it was before you got the new messages
      (when you get new messages, this makes the scroll not go to the top or bottom).
      This method needs to be asynchronous to work because the total height of the HTML element
      is changed asynchronously, so it will not work if it is not executed this way.
      */
      setTimeout(() => {
        this.scrollChat.nativeElement.scrollTop = this.scrollChat.nativeElement.scrollHeight - fullScrollHeight;
      }, 0);
    });
  }

  public selectPhoto(event) {
    this.file = event.target.files[0];
    if (this.file.type.indexOf('image') < 0) {
      Swal.fire({
        title: this.translateExtensionService.getTranslatedStringByUrl("CHAT.INVALID_FORMAT"),
        icon: 'error', showConfirmButton: false,
        timer: 1500, background: '#7f5af0', color: 'white'
      });
      this.file = null;
    }
  }

  public unselectFile() {
    this.file = null;
  }
}