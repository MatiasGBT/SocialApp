import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Notification } from 'src/app/models/notification';
import { FriendshipService } from 'src/app/services/friendship.service';
import { NotificationsService } from 'src/app/services/notifications.service';
import { TranslateExtensionService } from 'src/app/services/translate-extension.service';
import { UserService } from 'src/app/services/user.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-notif',
  templateUrl: './notif.component.html',
  styleUrls: ['./notif.component.css']
})
export class NotifComponent implements OnInit {
  public isNotificationsEnabled: boolean;
  public notifications: Notification[] = [];

  constructor(private notificationsService: NotificationsService, private userService: UserService,
    private friendshipService: FriendshipService, private router: Router,
    private translateExtensionService: TranslateExtensionService) { }

  ngOnInit(): void {
    this.isNotificationsEnabled = this.notificationsService.getNotificationsStatus();
    if (this.isNotificationsEnabled) {
      this.userService.getKeycloakUser().subscribe(keycloakUser => {
        let user = keycloakUser;
        this.notificationsService.getNotifications(user).subscribe(notifications => this.notifications = notifications);
      });
    }

    this.notificationsService.notificationsChanger.subscribe(isNotificationsEnabled => this.isNotificationsEnabled = isNotificationsEnabled);
  }

  public returnOpacity(notification: Notification) {
    return notification.isViewed ? '50%' : '100%';
  }

  public deleteNotification(notification: Notification): void {
    this.notificationsService.delete(notification.idNotification).subscribe(response => {
      console.log(response.message);
      this.notifications = this.notifications.filter(n => n.idNotification !== response.id);
      this.notificationsService.notificationsChanger.emit(this.notifications);
      if (notification?.friendship) {
        this.friendshipService.rejectFriendRequest(notification.friendship.idFriendship).subscribe(response => {
          console.log(response.message)
        });
      }
    });
  }

  public deleteAllNotifications(): void {
    if (this.notifications.filter(n => !n.friendship).length > 0) {
      this.notificationsService.deleteAll().subscribe(response => {
        console.log(response.message);
        this.notifications = this.notifications.filter(n => n?.friendship);
        this.notificationsService.notificationsChanger.emit(this.notifications);
      });
    } else {
      this.showNoNotificationsModal();
    }
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
          this.notifications = this.notifications.filter(n => n?.idNotification != notification.idNotification);
          this.notificationsService.notificationsChanger.emit(this.notifications);
        })
      });
    });
  }

  public viewNotification(notification: Notification) {
    this.notificationsService.viewNotification(notification.idNotification).subscribe(response => console.log(response.message));
    if (!notification?.friendship) {
      this.notificationsService.notificationsChanger.emit(this.notifications.filter(n => n.idNotification !== notification.idNotification));
    }
    if (notification?.friend) {
      this.router.navigate(['/profile', notification.friend.idUser]);
    }
    if (notification?.post) {
      this.router.navigate(['/post', notification.post.idPost]);
    }
  }

  private showNoNotificationsModal() {
    Swal.fire({
      icon: 'info',
      title: this.translateExtensionService.translateModalText('NOTIFICATIONS.NO_NOTIFICATIONS_TO_DELETE_TITLE'),
      text: this.translateExtensionService.translateModalText('NOTIFICATIONS.NO_NOTIFICATIONS_TO_DELETE_TEXT'),
      showConfirmButton: false, timer: 1750, background: '#7f5af0', color: 'white'
    });
  }
}
