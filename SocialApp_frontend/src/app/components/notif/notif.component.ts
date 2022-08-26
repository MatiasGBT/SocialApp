import { Component, OnInit } from '@angular/core';
import { Notification } from 'src/app/models/notification';
import { FriendshipService } from 'src/app/services/friendship.service';
import { NotificationsService } from 'src/app/services/notifications.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-notif',
  templateUrl: './notif.component.html',
  styleUrls: ['./notif.component.css']
})
export class NotifComponent implements OnInit {
  public isNotificationsEnabled: boolean;
  public notifications: Notification[] = [];

  constructor(private notificationsService: NotificationsService,
    private friendshipService: FriendshipService) { }

  ngOnInit(): void {
    this.isNotificationsEnabled = this.notificationsService.getNotificationsStatus();

    if (this.isNotificationsEnabled) {
      this.notificationsService.getNotifications().subscribe(notifications => this.notifications = notifications);
    }

    this.notificationsService.notificationsChanger.subscribe(isNotificationsEnabled => this.isNotificationsEnabled = isNotificationsEnabled);
  }

  public returnOpacity(notification: Notification) {
    return notification.isViewed ? '50%' : '100%';
  }

  public deleteNotification(id: number): void {
    this.notificationsService.delete(id).subscribe(response => {
      console.log(response.message);
      this.notifications = this.notifications.filter(n => n.idNotification !== response.id);
      this.notificationsService.notificationsChanger.emit(this.notifications.filter(n => !n.isViewed));
    });
  }

  public deleteAllNotifications(): void {
    this.notificationsService.deleteAll().subscribe(response => {
      console.log(response.message);
      this.notifications = this.notifications.filter(n => n?.friendship);
      this.notificationsService.notificationsChanger.emit(this.notifications.filter(n => !n.isViewed));
    });
  }

  public acceptFriendRequest(notification: Notification): void {
    this.friendshipService.acceptFriendRequest(notification.friendship.idFriendship).subscribe(response => {
      Swal.fire({
        icon: 'success',
        title: response.message,
        showConfirmButton: false,
        timer: 1250,
        background: '#7f5af0',
        color: 'white'
      }).then(() => {
        this.notificationsService.delete(notification.idNotification).subscribe(response => {
          console.log(response.message);
          this.notifications = this.notifications.filter(n => n?.friendship.idFriendship != notification.friendship.idFriendship);
          this.notificationsService.notificationsChanger.emit(this.notifications.filter(n => !n.isViewed));
        })
      });
    });
  }
}
