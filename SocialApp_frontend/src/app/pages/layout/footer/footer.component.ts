import { Component, Input, OnInit } from '@angular/core';
import { Notification } from 'src/app/models/notification';
import { User } from 'src/app/models/user';
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
  @Input() public user: User;

  constructor(private notificationsService: NotificationsService) { }

  ngOnInit(): void {
    this.isNotificationsEnabled = this.notificationsService.getNotificationsStatus();

    this.notificationsService.notificationsEnabled.subscribe(areEnabled => {
      this.isNotificationsEnabled = areEnabled;
      if (this.isNotificationsEnabled) {
        this.obtainNotifications();
      }
    });

    //This code detects when a user sees one of his notifications, which causes the badge number to be reduced.
    this.notificationsService.notificationsChanger.subscribe(notifications => {
      this.notifications = notifications.filter(n => !n.isViewed);
      this.notificationsNumber = this.notifications.length;
    });

    this.notificationsService.newNotification.subscribe(() => this.notificationsNumber++);
  }

  ngOnChanges() {
    if (this.isNotificationsEnabled && this.user) {
      this.obtainNotifications();
    }
  }

  private obtainNotifications(): void {
    this.notificationsService.getNotifications(this.user).subscribe(notifications => {
      this.notifications = notifications;
      this.notificationsNumber = this.notifications.filter(n => !n.isViewed).length;
    });
  }
}
