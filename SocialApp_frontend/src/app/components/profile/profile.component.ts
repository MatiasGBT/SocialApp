import { Component, OnInit } from '@angular/core';
import { User } from 'src/app/models/user';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css', '../../../assets/index_profile.css']
})
export class ProfileComponent implements OnInit {
  public user: User;

  constructor(private userService: UserService) { }

  ngOnInit(): void {
    this.userService.getKeycloakUser().subscribe(response => {
      this.user = response;
    });

    this.userService.userChanger.subscribe(data => {
      this.user = data.user;
    })
  }

}
