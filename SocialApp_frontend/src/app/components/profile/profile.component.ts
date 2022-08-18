import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Friendship } from 'src/app/models/friendship';
import { User } from 'src/app/models/user';
import { FriendshipService } from 'src/app/services/friendship.service';
import { UserService } from 'src/app/services/user.service';
import Swal from 'sweetalert2';

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

  constructor(private userService: UserService, private activatedRout: ActivatedRoute,
    private friendshipService: FriendshipService) { }

  ngOnInit(): void {
    this.userService.userChanger.subscribe(data => {
      this.user = data.user;
    });

    this.activatedRout.params.subscribe(params => {
      this.id = params['id'];
      if (this.id) {
        this.userService.getUser(this.id).subscribe(user => this.user = user);
        this.friendshipService.getFriendship(this.id).subscribe(friendship => this.friendship = friendship);
      } else {
        this.userService.getKeycloakUser().subscribe(response => {
          this.user = response;
          this.keycloakUserId = response.idUser;
        });
      }
    });
  }

  addFriend() {
    this.friendshipService.sendFriendRequest(this.id).subscribe(response => {
      Swal.fire({
        icon: response.send ? 'success' : 'info',
        title: response.message,
        showConfirmButton: false,
        timer: 1250,
        background: '#7f5af0',
        color: 'white'
      })
    });
  }
}
