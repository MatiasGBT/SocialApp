import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import Swal from 'sweetalert2';
import { User } from '../models/user';
import { TranslateExtensionService } from './translate-extension.service';
import { WebsocketService } from './websocket.service';

@Injectable({
  providedIn: 'root'
})
export class CallService {
  private friendAcceptedSubscription: Subscription;
  private friendIsReadySubscription: Subscription;
  private friendRejectedSubscription: Subscription;

  constructor(private router: Router, private webSocketService: WebsocketService,
    private translateExtensionService: TranslateExtensionService) { }

    public callFriend(friend: User) {
      if (friend.status.text == 'Disconnected') {
        Swal.fire({
          title: this.translateExtensionService.getTranslatedStringByUrl("CALL.USER_DISCONNECTED"),
          icon: 'error', showConfirmButton: false,
          timer: 1500, background: '#7f5af0', color: 'white'
        });
      }
      if (friend.status.text == 'On call') {
        Swal.fire({
          title: this.translateExtensionService.getTranslatedStringByUrl("CALL.USER_ON_CALL"),
          icon: 'error', showConfirmButton: false,
          timer: 1500, background: '#7f5af0', color: 'white'
        });
      }
      if (friend.status.text == 'Connected') {
        this.webSocketService.callFriend(friend);
        Swal.fire({
          title: this.translateExtensionService.getTranslatedStringByUrl("CALL.CALLING_TITLE") + friend.name,
          text: this.translateExtensionService.getTranslatedStringByUrl("CALL.CALLING_TEXT"),
          showConfirmButton: false, timer: 15000, background: '#7f5af0', color: 'white'
        });
      }
    }

  public subscribeToEvents() {
    this.friendAcceptedSubscription = this.webSocketService.friendAcceptedCallEvent.subscribe(user => {
      Swal.close();
      Swal.fire({
        title: this.translateExtensionService.getTranslatedStringByUrl('CALL.JOINING_CALL'),
        showConfirmButton: false, background: '#7f5af0', color: 'white'
      });
      this.friendIsReadySubscription = this.webSocketService.friendIsReadyToStartCallEvent.subscribe(() => { //desub
        Swal.close();
        this.router.navigate(['/profile/call', user.idUser]);
        this.unsubscribeFromEvents();
      });
    });

    this.friendRejectedSubscription = this.webSocketService.friendRejectedCallEvent.subscribe(() => {
      Swal.close();
      Swal.fire({
        title: this.translateExtensionService.getTranslatedStringByUrl("CALL.FRIEND_REJECTED"),
        icon: 'error', showConfirmButton: false,
        timer: 1500, background: '#7f5af0', color: 'white'
      });
    });
  }

  public unsubscribeFromEvents() {
    this.friendAcceptedSubscription?.unsubscribe();
    this.friendIsReadySubscription?.unsubscribe();
    this.friendRejectedSubscription?.unsubscribe();
  }
}