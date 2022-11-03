import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { User } from 'src/app/models/user';
import { FriendshipService } from 'src/app/services/friendship.service';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-friends',
  templateUrl: './friends.component.html',
  styleUrls: ['./friends.component.css']
})
export class FriendsComponent implements OnInit {
  public user: User;
  public friends: User[] = [];
  public isTheKeycloakUser: boolean;

  constructor(private activatedRoute: ActivatedRoute, private userService: UserService,
    private friendshipService: FriendshipService) { }

  ngOnInit(): void {
    this.activatedRoute.params.subscribe(params => {
      let id = params['id'];
      this.userService.getUser(id).subscribe(user => this.user = user);
      this.userService.getFriends(id).subscribe(friends => this.friends = friends);
      this.userService.getKeycloakUser().subscribe(user => {
        id == user.idUser ? this.isTheKeycloakUser = true : this.isTheKeycloakUser = false
      });
    });
  }
}
