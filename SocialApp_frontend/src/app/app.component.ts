import { Component, HostListener, OnInit } from '@angular/core';
import { ThemePalette } from '@angular/material/core';
import { NavigationEnd, Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { filter } from 'rxjs';
import { User } from './models/user';
import { AuthService } from './services/auth.service';
import { WebsocketService } from './services/websocket.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  constructor(private translate: TranslateService, public authService: AuthService,
              private router: Router, private webSocketService: WebsocketService) {
    translate.addLangs(['en', 'es', 'pt']);
  }
  
  ngOnInit(): void {
    this.login();
    this.getAndSetLanguage();
    this.webSocketService.startConnection();
    this.subscribeToRoutes();
  }

  private login() {
    this.authService.login().subscribe(response => {
      console.log(response.message);
      this.authService.keycloakUser = response.user as User;
      if (response?.status && response.status == 201) {
        this.router.navigate(['/profile/edit'])
      }
    });
  }

  private getAndSetLanguage() {
    const lang = localStorage.getItem('lang');
    if (lang) {
      this.translate.use(lang);
    } else {
      this.translate.use('en');
      localStorage.setItem('lang', 'en');
    }
  }

  private subscribeToRoutes() {
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event) => {
      event['url'].includes('admin') ? this.authService.userIsOnAdminModule = true : this.authService.userIsOnAdminModule = false;
    });
  }

  @HostListener('window:beforeunload')
  onUnload() {
    this.webSocketService.endConnection();
  }
}