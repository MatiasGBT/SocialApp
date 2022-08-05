import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { KeycloakService } from 'keycloak-angular';

@Component({
  selector: 'app-config',
  templateUrl: './config.component.html',
  styleUrls: ['./config.component.css']
})
export class ConfigComponent implements OnInit {
  public inputCheck: boolean;
  public select: string;

  constructor(public translate: TranslateService, private keycloakService: KeycloakService) {}

  ngOnInit(): void {
    const notifications = localStorage.getItem('notifications');
    if (notifications == null || notifications == 'on') {
      this.inputCheck = true;
    } else {
      this.inputCheck = false;
    }

    this.select = localStorage.getItem('lang');
    if (this.select != null && this.translate.currentLang != this.select) {
      this.changeLanguage();
    } else if(this.select == null) {
      this.select = 'en';
    }
  }

  changeNotifications() {
    this.inputCheck = !this.inputCheck;
    var notif: string = 'on';
    if (!this.inputCheck) {
      notif = 'off';
    }
    localStorage.setItem('notifications', notif);
    window.location.reload();
  }

  changeLanguage() {
    this.translate.use(this.select);
    localStorage.setItem('lang', this.select);
  }

  manageAccount() {
    this.keycloakService.getKeycloakInstance().accountManagement();
  }

  logout() {
    this.keycloakService.logout();
  }
}
