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

  constructor(private activatedRoute: ActivatedRoute, private userService: UserService,
    private friendshipService: FriendshipService) { }

  ngOnInit(): void {
    this.activatedRoute.params.subscribe(params => {
      let id = params['id'];
      if (id) {
        this.userService.getUser(id).subscribe(user => {
          this.user = user;
          this.setFriends();
        });
      } else {
        this.userService.getKeycloakUser().subscribe(user => {
          this.user = user;
          this.setFriends();
        });
      }
    });
  }

  /*
    This method fetches all users who are friends of the selected user and adds them to the
    list that will then be passed to the User component.
    The problem is that the HTTP request brings a friendship relationship, so the system have
    to check which user in the relationship is NOT the selected user in order to add him/her
    to the list.
  */
  private setFriends(): void {
    this.friendshipService.getFriendships(this.user.idUser).subscribe(friendships => {
      friendships.forEach(f => {
        if (f.userReceiver.idUser != this.user.idUser) {
          this.friends.push(f.userReceiver);
        } else {
          this.friends.push(f.userTransmitter);
        }
        this.friendshipService.setIsFriend(this.friends);
      })
    });
  }
}
