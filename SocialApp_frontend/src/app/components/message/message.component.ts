import { Component, ElementRef, Input, OnInit, Renderer2, ViewChild } from '@angular/core';
import { Message } from 'src/app/models/message';
import { User } from 'src/app/models/user';
import { MessageService } from 'src/app/services/message.service';
import { TranslateExtensionService } from 'src/app/services/translate-extension.service';
import { WebsocketService } from 'src/app/services/websocket.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'message-comp',
  templateUrl: './message.component.html',
  styleUrls: ['./message.component.css']
})
export class MessageComponent implements OnInit {
  @Input() public message: Message;
  @Input() public keycloakUser: User;
  private startTime: Date;
  private endTime: Date;
  @ViewChild("messageEl") selectedMessage: ElementRef;

  constructor(private renderer2: Renderer2, private messageService: MessageService,
    private translateExtensionService: TranslateExtensionService,
    private webSocketService: WebsocketService) { }

  ngOnInit(): void {
  }

  public deleteMessageMouseDown() {
    if (this.message.userTransmitter.idUser == this.keycloakUser.idUser) {
      this.startTime = new Date;
      this.renderer2.setStyle(this.selectedMessage.nativeElement, 'opacity', 0.75);
    }
  }

  public deleteMessageMouseUp() {
    if (this.message.userTransmitter.idUser == this.keycloakUser.idUser) {
      this.renderer2.setStyle(this.selectedMessage.nativeElement, 'opacity', 1);
      this.endTime = new Date;
      let timeDiff = this.endTime.getTime() - this.startTime.getTime();
      if (timeDiff >= 1000) {
        this.fireModal();
      }
    }
  }

  private fireModal() {
    Swal.fire({
      icon: 'warning', showCancelButton: true,
      title: this.translateExtensionService.getTranslatedStringByUrl('MESSAGE.DELETE_MODAL_TITLE'),
      text: this.translateExtensionService.getTranslatedStringByUrl('MESSAGE.DELETE_MODAL_TEXT'),
      confirmButtonText: this.translateExtensionService.getTranslatedStringByUrl('MESSAGE.DELETE_MODAL_BUTTON_DELETE'),
      cancelButtonText: this.translateExtensionService.getTranslatedStringByUrl('MESSAGE.DELETE_MODAL_BUTTON_CANCEL'),
      background: '#7f5af0', color: 'white', confirmButtonColor: '#d33', cancelButtonColor: '#2cb67d'
    }).then((result) => {
      if (result.isConfirmed) {
        this.deleteMessage();
      }
    });
  }

  private deleteMessage() {
    this.messageService.deleteMessage(this.message.idMessage).subscribe(response => {
      console.log(response.message);
      this.webSocketService.deleteMessage(this.message.userReceiver, this.message);
      this.message = null;
    })
  }
}