import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Friendship } from 'src/app/models/friendship';
import { Post } from 'src/app/models/post';
import { User } from 'src/app/models/user';
import { FriendshipService } from 'src/app/services/friendship.service';
import { PostService } from 'src/app/services/post.service';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css', '../../../assets/index_profile.css']
})
export class ProfileComponent implements OnInit {
  public user: User;
  public posts: Post[] = [];
  private id: number;
  public friendship: Friendship;
  public friendsQuantity: number;

  constructor(private userService: UserService, private activatedRoute: ActivatedRoute,
    private friendshipService: FriendshipService, private router: Router,
    private postService: PostService) { }

  ngOnInit(): void {
    this.userService.userChanger.subscribe(data => {
      this.user.photo = data.photo;
    });

    this.activatedRoute.params.subscribe(params => {
      this.id = params['id'];
      if (this.id) {
        this.getUser();
        this.friendshipService.getFriendship(this.id).subscribe(friendship => this.friendship = friendship);
      } else {
        this.getKeycloakUser();
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

  public goToFriendsPage(): void {
    if (this.id) {
      this.router.navigate(['profile/friends', this.id]);
    } else {
      this.router.navigate(['profile/friends', this.user.idUser]);
    }
  }

  private getUser() {
    this.userService.getUser(this.id).subscribe(user => {
      this.user = user;
      this.getFriendsQuantity();
      this.getPosts(this.user.idUser);
    });
  }

  private getKeycloakUser() {
    this.userService.getKeycloakUser().subscribe(user => {
      this.user = user;
      this.getFriendsQuantity();
      this.getPosts(this.user.idUser);
    });
  }

  private getPosts(idUser: number) {
    this.postService.getPostsByUser(idUser).subscribe(posts => this.posts = posts);
  }
}
