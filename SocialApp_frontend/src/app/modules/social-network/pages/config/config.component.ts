import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { KeycloakService } from 'keycloak-angular';
import { AuthService } from 'src/app/services/auth.service';
import { LocaleService } from 'src/app/services/locale.service';
import { NotificationsService } from 'src/app/services/notifications.service';
import { TranslateExtensionService } from 'src/app/services/translate-extension.service';
import { UserService } from 'src/app/services/user.service';
import { WebsocketService } from 'src/app/services/websocket.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-config',
  templateUrl: './config.component.html',
  styleUrls: ['./config.component.css']
})
export class ConfigComponent implements OnInit {
  public inputCheck: boolean;
  public selectedLanguage: string;

  constructor(public translate: TranslateService, private keycloakService: KeycloakService,
    private notificationService: NotificationsService, private webSocketService: WebsocketService,
    public authService: AuthService, private localeService: LocaleService,
    private userService: UserService,
    private translateExtensionService: TranslateExtensionService) {}

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
    let notif: string;
    this.inputCheck ? notif = 'on' : notif = 'off';
    localStorage.setItem('notifications', notif);
    this.notificationService.notificationsEnabledEvent.emit(this.inputCheck);
  }

  changeLanguage() {
    this.localeService.setLocale(this.selectedLanguage);
    localStorage.setItem('lang', this.selectedLanguage);
  }

  manageAccount() {
    this.keycloakService.getKeycloakInstance().accountManagement();
  }

  logout() {
    this.keycloakService.logout();
    this.webSocketService.endConnection();
  }

  startDeletionProcess() {
    Swal.fire({
      title: this.translateExtensionService.getTranslatedStringByUrl('SETTINGS.DELETE_ACCOUNT.MODAL_TITLE'),
      text: this.translateExtensionService.getTranslatedStringByUrl('SETTINGS.DELETE_ACCOUNT.MODAL_TEXT'),
      confirmButtonText: this.translateExtensionService.getTranslatedStringByUrl('SETTINGS.DELETE_ACCOUNT.MODAL_CONFIRM_BUTTON'),
      cancelButtonText: this.translateExtensionService.getTranslatedStringByUrl('SETTINGS.DELETE_ACCOUNT.MODAL_CANCEL_BUTTON'),
      background: '#7f5af0', color: 'white', confirmButtonColor: '#d33', 
      showCancelButton: true, cancelButtonColor: '#2cb67d',
    }).then((result) => {
      if (result.isConfirmed) {
        this.authService.keycloakUser.deletionDate = new Date();
        this.userService.update(this.authService.keycloakUser).subscribe(response => console.log(response.message));
      }
    });
  }
}