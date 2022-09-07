import { Component, Input, OnInit } from '@angular/core';
import { User } from 'src/app/models/user';
import { AuthService } from 'src/app/services/auth.service';
import { FriendshipService } from 'src/app/services/friendship.service';

@Component({
  selector: 'user-comp',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {
  @Input() public user: User;

  constructor(private friendshipService: FriendshipService, public authService: AuthService) { }

  ngOnInit(): void {
  }

  public addFriend(): void {
    this.friendshipService.addFriend(this.user.idUser);
  }

  public async deleteFriend() {
    if (await this.friendshipService.askToDelete(this.user.idUser)) {
      this.user.isFriend = false;
    }
  }
}