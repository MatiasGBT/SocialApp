import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Followership } from 'src/app/models/followership';
import { Friendship } from 'src/app/models/friendship';
import { User } from 'src/app/models/user';
import { AuthService } from 'src/app/services/auth.service';
import { CallService } from 'src/app/services/call.service';
import { FollowershipService } from 'src/app/services/followership.service';
import { FriendshipService } from 'src/app/services/friendship.service';

@Component({
  selector: 'user-comp',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {
  @Input() public user: User;
  public friendship: Friendship;
  public followership: Followership

  constructor(private friendshipService: FriendshipService, public authService: AuthService,
    private router: Router, private callService: CallService,
    private followershipService: FollowershipService) { }

  ngOnInit(): void {
    this.callService.subscribeToEvents();
    if (this.user.isChecked) {
      this.followershipService.getFollowership(this.user.idUser).subscribe(followership => {
        this.followership = followership;
      })
    } else {
      this.friendshipService.getFriendship(this.user.idUser).subscribe(friendship => {
        this.friendship = friendship;
      });
    }
  }

  public goToUser() {
    if (this.user.username == this.authService.getUsername()) {
      this.router.navigate(['/profile']);
    } else {
      this.router.navigate(['/profile', this.user.idUser]);
    }
  }

  public addFriend(): void {
    this.friendshipService.addFriend(this.user);
  }

  public deleteFriend() {
    this.friendshipService.askToDelete(this.friendship.idFriendship);
    this.friendshipService.friendshipDeletedEvent.subscribe(() => this.friendship.status = false);
  }

  public callFriend() {
    this.callService.callFriend(this.user);
  }

  public followUser(): void {
    this.followershipService.followUser(this.user).subscribe(followership => {
      this.followership = followership;
    });
  }

  public unfollowUser() {
    this.followershipService.unfollowUser(this.followership.idFollowership).subscribe(response => {
      console.log(response.message);
      this.followership = null;
    });
  }
}