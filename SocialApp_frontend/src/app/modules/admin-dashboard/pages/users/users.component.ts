import { Component, OnInit } from '@angular/core';
import { AuthService } from 'src/app/services/auth.service';
import { TranslateExtensionService } from 'src/app/services/translate-extension.service';

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.css']
})
export class UsersComponent implements OnInit {
  public placeholder: string;

  constructor(private authService: AuthService, private translateExtensionService: TranslateExtensionService) { }

  ngOnInit(): void {
    this.authService.userIsOnAdminModule = true;
    this.placeholder = this.translateExtensionService.getTranslatedStringByUrl('ADMIN.USERS_PLACEHOLDER');
  }
}