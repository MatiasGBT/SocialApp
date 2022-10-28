import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { filter, Subscription } from 'rxjs';
import { Friendship } from 'src/app/models/friendship';
import { User } from 'src/app/models/user';
import { CallService } from 'src/app/services/call.service';
import { FriendshipService } from 'src/app/services/friendship.service';
import { PostService } from 'src/app/services/post.service';
import { UserService } from 'src/app/services/user.service';
import { WebsocketService } from 'src/app/services/websocket.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css', '../../../assets/index_profile.css']
})
export class ProfileComponent implements OnInit, OnDestroy {
  public user: User;
  private id: number;
  public friendship: Friendship;
  public friendsQuantity: number;
  public userPostQuantity: number;
  public usersYouMayKnow: User[] = [];
  private subscriber: Subscription;

  constructor(private userService: UserService, private activatedRoute: ActivatedRoute,
    private friendshipService: FriendshipService, private router: Router,
    private postService: PostService, private webSocketService: WebsocketService,
    private callService: CallService) { }

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

    this.postService.reducePostsQuantityEmitter.subscribe(() => this.userPostQuantity--);

    /*
    This subscription serves to fix a bug that arises when we enter a user profile that is
    not ours and, without changing page, we enter another user profile (from the navbar),
    which caused the second user profile to show the posts of the first profile searched
    because the user instance had already been obtained and initialising the component sets
    the view first (which passed the previous user id to the list of posts) before the new
    user instance is initialised.
    */
    this.subscriber = this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe(() =>  this.user = null);
  }

  //#region Friendship
  public addFriend(): void {
    this.friendshipService.addFriend(this.id);
    this.webSocketService.newNotification(this.user);
  }

  private getFriendsQuantity(): void {
    this.friendshipService.getFriendsQuantity(this.user.idUser).subscribe(friendsQuantity => {
      this.friendsQuantity = friendsQuantity;
    });
  }

  public deleteFriend() {
    this.friendshipService.askToDelete(this.id);
    this.friendshipService.friendshipDeletedEmitter.subscribe(() => this.friendship.status = false);
  }

  public goToFriendsPage(): void {
    if (this.id) {
      this.router.navigate(['profile/friends', this.id]);
    } else {
      this.router.navigate(['profile/friends', this.user.idUser]);
    }
  }
  //#endregion

  //#region User and posts
  private getUser() {
    this.userService.getUser(this.id).subscribe(user => {
      this.user = user;
      this.getProfileHeaderData();
      this.getUsersYouMayKnow();
      this.callService.subscribeToEvents();
    });
  }

  private getKeycloakUser() {
    this.userService.getKeycloakUser().subscribe(user => {
      this.user = user;
      this.getProfileHeaderData();
    });
  }

  private getProfileHeaderData() {
    this.getFriendsQuantity();
    this.postService.countPostByUser(this.user.idUser).subscribe(count => this.userPostQuantity = count);
  }

  private getUsersYouMayKnow() {
    this.userService.getKeycloakUser().subscribe(user => {
      let keycloakUserId = user.idUser;
      this.userService.getUsersYouMayKnow(this.user.idUser, keycloakUserId).subscribe(usersYouMayKnow => this.usersYouMayKnow = usersYouMayKnow);
    });
  }

  public goToProfile(userYouMayKnow: User) {
    this.router.navigate(['/profile', userYouMayKnow.idUser]);
  }
  //#endregion

  public moveToPosts(el: HTMLElement) {
    el.scrollIntoView({behavior: 'smooth'});
  }

  public callFriend() {
    this.callService.callFriend(this.user);
  }

  ngOnDestroy () {
    this.subscriber?.unsubscribe();
    this.callService.unsubscribeFromEvents();
  }
}