import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
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

  constructor(private friendshipService: FriendshipService, public authService: AuthService,
    private router: Router) { }

  ngOnInit(): void {
  }

  public goToUser() {
    if (this.user.username == this.authService.getUsername()) {
      this.router.navigate(['/profile']);
    } else {
      this.router.navigate(['/profile', this.user.idUser]);
    }
  }

  public addFriend(): void {
    this.friendshipService.addFriend(this.user.idUser);
  }

  public deleteFriend() {
    this.friendshipService.askToDelete(this.user.idUser);
    this.friendshipService.friendshipDeletedEmitter.subscribe(() => this.user.isFriend = false);
  }
}