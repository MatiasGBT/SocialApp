import { Component, OnInit } from '@angular/core';
import { User } from 'src/app/models/user';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css', '../../../assets/index_profile.css']
})
export class ProfileComponent implements OnInit {
  public user: User;

  constructor(private authService: AuthService) { }

  ngOnInit(): void {
    this.user = this.authService.user;
  }

}
