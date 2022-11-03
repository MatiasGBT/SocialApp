import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Friendship } from 'src/app/models/friendship';
import { User } from 'src/app/models/user';
import { AuthService } from 'src/app/services/auth.service';
import { CallService } from 'src/app/services/call.service';
import { FriendshipService } from 'src/app/services/friendship.service';

@Component({
  selector: 'user-comp',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {
  @Input() public user: User;
  public friendship: Friendship;

  constructor(private friendshipService: FriendshipService, public authService: AuthService,
    private router: Router, private callService: CallService) { }

  ngOnInit(): void {
    this.callService.subscribeToEvents();
    this.friendshipService.getFriendship(this.user.idUser).subscribe(friendship => {
      this.friendship = friendship;
    });
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
    this.friendshipService.friendshipDeletedEmitter.subscribe(() => this.friendship.status = false);
  }

  public callFriend() {
    this.callService.callFriend(this.user);
  }
}