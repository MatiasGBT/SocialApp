import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { User } from 'src/app/models/user';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css', '../../../assets/index_profile.css']
})
export class ProfileComponent implements OnInit {
  public user: User;
  public keycloakUserId: number;

  constructor(private userService: UserService, private activatedRout: ActivatedRoute) { }

  ngOnInit(): void {
    this.userService.userChanger.subscribe(data => {
      this.user = data.user;
    });

    this.activatedRout.params.subscribe(params => {
      let id = params['id'];
      if (id) {
        this.userService.getUser(id).subscribe(user => {
          this.user = user
        });
      } else {
        this.userService.getKeycloakUser().subscribe(response => {
          this.user = response;
          this.keycloakUserId = response.idUser;
        });
      }
    });
  }

}
