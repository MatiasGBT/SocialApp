import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Notification } from 'src/app/models/notification';
import { AuthService } from 'src/app/services/auth.service';
import { FriendshipService } from 'src/app/services/friendship.service';
import { NotificationsService } from 'src/app/services/notifications.service';
import { TranslateExtensionService } from 'src/app/services/translate-extension.service';
import { WebsocketService } from 'src/app/services/websocket.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-notif',
  templateUrl: './notif.component.html',
  styleUrls: ['./notif.component.css']
})
export class NotifComponent implements OnInit {
  public areNotificationsEnabled: boolean;
  public notifications: Notification[] = [];

  constructor(private notificationsService: NotificationsService, private authService: AuthService,
    private friendshipService: FriendshipService, private router: Router,
    private translateExtensionService: TranslateExtensionService,
    private webSocketService: WebsocketService) { }

  ngOnInit(): void {
    this.areNotificationsEnabled = this.notificationsService.getNotificationsStatus();
    if (this.areNotificationsEnabled) {
      let user = this.authService.keycloakUser;
      this.notificationsService.getNotifications(user).subscribe(notifications => this.notifications = notifications);
    }

    this.notificationsService.notificationsEnabledEvent.subscribe(areNotificationsEnabled => this.areNotificationsEnabled = areNotificationsEnabled);

    this.notificationsService.newNotificationEvent.subscribe(() => {
      if (this.router.url.includes('notif')) {
        Swal.fire({
          title: this.translateExtensionService.getTranslatedStringByUrl('NOTIFICATIONS.NEW_NOTIF_TITLE'),
          text: this.translateExtensionService.getTranslatedStringByUrl('NOTIFICATIONS.NEW_NOTIF_TEXT'),
          position: 'bottom-end', timer: 1500, background: '#7f5af0', timerProgressBar: true,
          showConfirmButton: false, color: 'white', toast: true, icon: 'info',
          didOpen: (toast) => {
            toast.addEventListener('mouseenter', Swal.stopTimer)
            toast.addEventListener('mouseleave', Swal.resumeTimer)
          }
        })
      }
    });
  }

  public getOpacity(notification: Notification) {
    return notification.isViewed ? '50%' : '100%';
  }

  public deleteNotification(notification: Notification): void {
    this.notificationsService.delete(notification.idNotification).subscribe(response => {
      console.log(response.message);
      this.notifications = this.notifications.filter(n => n.idNotification !== response.id);
      this.notificationsService.notificationViewedOrDeletedEvent.emit(this.notifications);
      if (notification?.friendship) {
        this.friendshipService.deleteFriendship(notification.friendship.idFriendship).subscribe();
      }
    });
  }

  public deleteAllNotifications(): void {
    if (this.notifications.filter(n => !n.friendship).length > 0) {
      this.notificationsService.deleteAll().subscribe(response => {
        console.log(response.message);
        this.notifications = this.notifications.filter(n => n?.friendship);
        this.notificationsService.notificationViewedOrDeletedEvent.emit(this.notifications);
      });
    } else {
      this.showNoNotificationsModal();
    }
  }

  public acceptFriendRequest(notification: Notification): void {
    this.friendshipService.acceptFriendRequest(notification.friendship.idFriendship).subscribe(response => {
      this.webSocketService.newNotification(notification.friendship.userTransmitter);
      Swal.fire({
        title: response.message,
        icon: 'success', showConfirmButton: false, timer: 1250,
        background: '#7f5af0', color: 'white'
      }).then(() => {
        this.notificationsService.delete(notification.idNotification).subscribe(response => {
          console.log(response.message);
          this.notifications = this.notifications.filter(n => n?.idNotification != notification.idNotification);
          this.notificationsService.notificationViewedOrDeletedEvent.emit(this.notifications);
        })
      });
    });
  }

  public viewNotification(notification: Notification) {
    this.notificationsService.viewNotification(notification.idNotification).subscribe(response => console.log(response.message));
    if (notification.type != 'friendship_type') {
      this.notificationsService.notificationViewedOrDeletedEvent.emit(this.notifications.filter(n => n.idNotification !== notification.idNotification));
    }
    if (notification.type == 'friend_type') {
      this.router.navigate(['/profile', notification.friend.idUser]);
    }
    if (notification.type == 'post_type') {
      this.router.navigate(['/post', notification.post.idPost]);
    }
    if (notification.type == 'comment_type') {
      this.router.navigate(['/comment', notification.comment.idComment]);
    }
    if (notification.type == 'message_type') {
      this.router.navigate(['/profile/chat', notification.userTransmitter.idUser]);
    }
    if (notification.type == 'followership_type') {
      this.router.navigate(['/profile', notification.followership.userFollower.idUser]);
    }
  }

  private showNoNotificationsModal() {
    Swal.fire({
      icon: 'info',
      title: this.translateExtensionService.getTranslatedStringByUrl('NOTIFICATIONS.NO_NOTIFICATIONS_TO_DELETE_TITLE'),
      text: this.translateExtensionService.getTranslatedStringByUrl('NOTIFICATIONS.NO_NOTIFICATIONS_TO_DELETE_TEXT'),
      showConfirmButton: false, timer: 1750, background: '#7f5af0', color: 'white'
    });
  }
}
