import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Friendship } from 'src/app/models/friendship';
import { User } from 'src/app/models/user';
import { FriendshipService } from 'src/app/services/friendship.service';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css', '../../../assets/index_profile.css']
})
export class ProfileComponent implements OnInit {
  public user: User;
  public keycloakUserId: number;
  private id: number;
  public friendship: Friendship;
  public friendsQuantity: number;

  constructor(private userService: UserService, private activatedRoute: ActivatedRoute,
    private friendshipService: FriendshipService) { }

  ngOnInit(): void {
    this.userService.userChanger.subscribe(data => {
      this.user.photo = data.photo;
    });

    this.activatedRoute.params.subscribe(params => {
      this.id = params['id'];
      if (this.id) {
        this.userService.getUser(this.id).subscribe(user => {
          this.user = user;
          this.getFriendsQuantity();
        });
        this.friendshipService.getFriendship(this.id).subscribe(friendship => this.friendship = friendship);
      } else {
        this.userService.getKeycloakUser().subscribe(response => {
          this.user = response;
          this.keycloakUserId = response.idUser;
          this.getFriendsQuantity();
        });
      }
    });
  }

  public addFriend(): void {
    this.friendshipService.addFriend(this.id);
  }

  private getFriendsQuantity(): void {
    this.friendshipService.getFriendsQuantity(this.user.idUser).subscribe(friendsQuantity => {
      this.friendsQuantity = friendsQuantity;
    });
  }

  public async deleteFriend() {
    if (await this.friendshipService.askToDelete(this.id)) {
      this.friendship.status = false;
    }
  }
}
