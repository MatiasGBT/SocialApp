import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { User } from './models/user';
import { AuthService } from './services/auth.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  private user: User;

  constructor(private translate: TranslateService, private authService: AuthService) {
    translate.addLangs(['en', 'es', 'pt']);
  }
  
  ngOnInit(): void {
    this.user = this.authService.user;
  }
}