import { Component, OnInit } from '@angular/core';
import { Notification } from 'src/app/models/notification';
import { NotificationsService } from 'src/app/services/notifications.service';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-notif',
  templateUrl: './notif.component.html',
  styleUrls: ['./notif.component.css']
})
export class NotifComponent implements OnInit {
  public isNotificationsEnabled: boolean;
  public notifications: Notification[] = [];

  constructor(private notificationsService: NotificationsService) { }

  ngOnInit(): void {
    this.isNotificationsEnabled = this.notificationsService.getNotificationsStatus();

    if (this.isNotificationsEnabled) {
      this.notificationsService.getNotifications().subscribe(notifications => this.notifications = notifications);
    }
  }

  public returnOpacity(notification: Notification) {
    return notification.isViewed ? '50%' : '100%';
  }
}
