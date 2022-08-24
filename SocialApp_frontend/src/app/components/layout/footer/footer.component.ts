import { Component, OnInit } from '@angular/core';
import { Notification } from 'src/app/models/notification';
import { NotificationsService } from 'src/app/services/notifications.service';

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.css']
})
export class FooterComponent implements OnInit {
  public isNotificationsEnabled: boolean;
  public notificationsNumber: number;
  private notifications: Notification[] = [];

  constructor(private notificationsService: NotificationsService) { }

  ngOnInit(): void {
    this.isNotificationsEnabled = this.notificationsService.getNotificationsStatus();
    if (this.isNotificationsEnabled) {
      this.notificationsService.getNotifications().subscribe(notifications => {
        this.notifications = notifications;
        this.notificationsNumber = this.notifications.filter(n => !n.isViewed).length;
      });
    }
  }
}
