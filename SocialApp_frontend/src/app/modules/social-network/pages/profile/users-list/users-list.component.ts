import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { User } from 'src/app/models/user';
import { AuthService } from 'src/app/services/auth.service';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-friends',
  templateUrl: './users-list.component.html',
  styleUrls: ['./users-list.component.css']
})
export class FriendsComponent implements OnInit {
  public users: User[] = [];
  public isTheKeycloakUser: boolean;
  public page: string;

  constructor(private activatedRoute: ActivatedRoute, private userService: UserService,
    private authService: AuthService) { }

  ngOnInit(): void {
    this.activatedRoute.params.subscribe(params => {
      this.page = params['page'];
      let id = params['id'];
      if (this.page && id) {
        this.userService.getUser(id).subscribe(user => {
          this.getUsers(user);
          this.getIsKeycloakUser(user);
        });
      }
    });
  }

  private getUsers(user: User) {
    if (this.page == 'friends') {
      this.userService.getFriends(user.idUser).subscribe(friends => this.users = friends);
    }
    if (this.page == 'followers') {
      this.userService.getUserFollowers(user.idUser).subscribe(followers => this.users = followers);
    }
    if (this.page == 'following') {
      this.userService.getUserFollowing(user.idUser).subscribe(following => this.users = following);
    }
  }

  private getIsKeycloakUser(user: User) {
    if (user.username == this.authService.getUsername()) {
      this.isTheKeycloakUser = true;
    }
  }
}