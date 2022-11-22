import { Component, Input, OnInit } from '@angular/core';
import { Notification } from 'src/app/models/notification';
import { AuthService } from 'src/app/services/auth.service';
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

  constructor(private notificationsService: NotificationsService, public authService: AuthService) { }

  ngOnInit(): void {
    this.isNotificationsEnabled = this.notificationsService.getNotificationsStatus();
    this.obtainNotifications();

    //This code detects when the user activates/deactivates notifications.
    this.notificationsService.notificationsEnabled.subscribe(areEnabled => {
      this.isNotificationsEnabled = areEnabled;
      this.obtainNotifications();
    });

    //This code detects when a user sees one of his notifications, which causes the badge number to be reduced.
    this.notificationsService.notificationsChanger.subscribe(notifications => {
      this.notifications = notifications.filter(n => !n.isViewed);
      this.notificationsNumber = this.notifications.length;
    });

    this.notificationsService.newNotification.subscribe(() => this.notificationsNumber++);
  }

  ngOnChanges() {
    this.obtainNotifications();
  }

  private obtainNotifications(): void {
    if (this.isNotificationsEnabled) {
      this.notificationsService.getNotifications(this.authService.keycloakUser).subscribe(notifications => {
        this.notifications = notifications;
        this.notificationsNumber = this.notifications.filter(n => !n.isViewed).length;
      });
    }
  }
}