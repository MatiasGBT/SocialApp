import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { User } from 'src/app/models/user';
import { FriendshipService } from 'src/app/services/friendship.service';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-search-users',
  templateUrl: './search-users.component.html',
  styleUrls: ['./search-users.component.css']
})
export class SearchUsersComponent implements OnInit {
  public users: User[] = [];

  constructor(private userService: UserService, private activatedRoute: ActivatedRoute,
    private friendshipService: FriendshipService) { }

  ngOnInit(): void {
    this.activatedRoute.params.subscribe(params => {
      let name: string = params['name'];
      if (name) {
        this.userService.filterUsersWithoutLimit(name).subscribe(response => {
          this.users = response;
          this.users.map(user => {
            this.friendshipService.getFriendship(user.idUser).subscribe(friendship => {
              friendship.status ? user.isFriend = true : user.isFriend = false;
            });
          })
        });
      }
    });
  }

  public addFriend(id: number): void {
    this.friendshipService.addFriend(id);
  }

  public deleteFriend(id: number): void {
    this.friendshipService.deleteFriendship(id).subscribe(response => {
      console.log(response.message);
      this.ngOnInit();
    });
  }
}
