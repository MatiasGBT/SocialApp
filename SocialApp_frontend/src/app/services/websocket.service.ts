import { Injectable } from '@angular/core';
import { Client } from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';
import { User } from '../models/user';
import { AuthService } from './auth.service';
import { NotificationsService } from './notifications.service';

@Injectable({
  providedIn: 'root'
})
export class WebsocketService {
  private stopmJsClient: Client;

  constructor(private notificationsService: NotificationsService, private authService: AuthService) { }

  public startConnection() {
    this.stopmJsClient = new Client;
    this.stopmJsClient.webSocketFactory = () => {
      return new SockJS("http://localhost:8090/ws");
    }

    this.stopmJsClient.onConnect = (frame) => {
      this.stopmJsClient.subscribe(`/ws/receiving/${this.authService.getUsername()}`, e => {
        console.log(e.body);
        this.notificationsService.newNotification.emit();
      });
    }

    this.stopmJsClient.activate();
  }

  public newNotification(userReceiver: User) {
    let lang = localStorage.getItem('lang');
    if (lang == null) {
        lang = 'en';
    };
    this.stopmJsClient.publish({ destination: `/ws-app/notifications/receiving/${userReceiver.username}`, body: lang});
  }
}
