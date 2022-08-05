import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class NotificationsService {

  constructor() { }

  getNotificationsStatus() {
    const notificationsStatus: string = localStorage.getItem('notifications');
    return notificationsStatus == null || notificationsStatus == 'on';
  }
}
