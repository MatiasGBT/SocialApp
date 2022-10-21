import { AfterViewChecked, Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Friendship } from 'src/app/models/friendship';
import { Message } from 'src/app/models/message';
import { User } from 'src/app/models/user';
import { AuthService } from 'src/app/services/auth.service';
import { FriendshipService } from 'src/app/services/friendship.service';
import { MessageService } from 'src/app/services/message.service';
import { UserService } from 'src/app/services/user.service';
import { WebsocketService } from 'src/app/services/websocket.service';

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.css']
})
export class ChatComponent implements OnInit, AfterViewChecked, OnDestroy {
  public keycloakUser: User;
  public friend: User;
  public message: Message = new Message;
  public messages: Message[] = [];
  public friendStatus: string;
  @ViewChild('scrollChat') private scrollChat: ElementRef;
  private page: number = 0;
  public isLastPage: boolean;

  private scrollDisabled: boolean = false;

  constructor(private authService: AuthService, private activatedRoute: ActivatedRoute,
    private router: Router, private messageService: MessageService,
    private friendshipService: FriendshipService, private webSocketService: WebsocketService,
    private userService: UserService) { }

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

    this.userService.userConnectEvent.subscribe(() => this.friend.status = "Connected");

    this.userService.userDisconnectEvent.subscribe(() => this.friend.status = "Disconnected");
  }

  ngAfterViewChecked(): void {
    this.scrollToBottom();
  }

  ngOnDestroy(): void {
      this.webSocketService.quitChat(this.friend);
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
    this.initChat();
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
    if (this.message.text != "" && this.message.text.length <= 50) {
      this.message.userTransmitter = this.keycloakUser;
      this.message.userReceiver = this.friend;
      this.message.date = new Date;
      this.messages.push(this.message);
      this.webSocketService.newNotification(this.message.userReceiver);
      this.webSocketService.sendMessage(this.message.userReceiver, this.message);
      this.message = new Message;
      this.scrollDisabled = false;
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
}