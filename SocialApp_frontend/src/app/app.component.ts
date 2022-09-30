import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { User } from './models/user';
import { AuthService } from './services/auth.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  public user: User;

  constructor(private translate: TranslateService, private authService: AuthService,
    private router: Router) {
    translate.addLangs(['en', 'es', 'pt']);
  }
  
  ngOnInit(): void {
    this.authService.login().subscribe(response => {
      console.log(response.message);
      this.user = response.user as User;
      if (response.status != undefined && response.status == 201) {
        this.router.navigate(['/profile/edit'])
      }
    });

    const lang = localStorage.getItem('lang');
    if (lang != null) {
      this.translate.use(lang);
    } else {
      this.translate.use('en');
      localStorage.setItem('lang', 'en');
    }
  }
}