import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { KeycloakService } from 'keycloak-angular';
import { NotificationsService } from 'src/app/services/notifications.service';
import { WebsocketService } from 'src/app/services/websocket.service';

@Component({
  selector: 'app-config',
  templateUrl: './config.component.html',
  styleUrls: ['./config.component.css']
})
export class ConfigComponent implements OnInit {
  public inputCheck: boolean;
  public selectedLanguage: string;

  constructor(public translate: TranslateService, private keycloakService: KeycloakService,
    private notificationServide: NotificationsService, private webSocketService: WebsocketService) {}

  ngOnInit(): void {
    const notifications = localStorage.getItem('notifications');
    if (notifications == null || notifications == 'on') {
      this.inputCheck = true;
    } else {
      this.inputCheck = false;
    }

    this.selectedLanguage = localStorage.getItem('lang');
    if (this.selectedLanguage != null && this.translate.currentLang != this.selectedLanguage) {
      this.changeLanguage();
    } else if(this.selectedLanguage == null) {
      this.selectedLanguage = 'en';
    }
  }

  changeNotifications() {
    this.inputCheck = !this.inputCheck;
    var notif: string = 'on';
    if (!this.inputCheck) {
      notif = 'off';
    }
    localStorage.setItem('notifications', notif);
    this.notificationServide.notificationsEnabled.emit(this.inputCheck);
  }

  changeLanguage() {
    this.translate.use(this.selectedLanguage);
    localStorage.setItem('lang', this.selectedLanguage);
  }

  manageAccount() {
    this.keycloakService.getKeycloakInstance().accountManagement();
  }

  logout() {
    this.keycloakService.logout();
    this.webSocketService.endConnection();
  }
}
