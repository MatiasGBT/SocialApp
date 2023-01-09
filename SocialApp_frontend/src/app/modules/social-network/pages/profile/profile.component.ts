import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { filter, Subscription } from 'rxjs';
import { Followership } from 'src/app/models/followership';
import { Friendship } from 'src/app/models/friendship';
import { Post } from 'src/app/models/post';
import { User } from 'src/app/models/user';
import { AuthService } from 'src/app/services/auth.service';
import { CallService } from 'src/app/services/call.service';
import { FollowershipService } from 'src/app/services/followership.service';
import { FriendshipService } from 'src/app/services/friendship.service';
import { PostService } from 'src/app/services/post.service';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css', '../../../../../assets/index_profile.css']
})
export class ProfileComponent implements OnInit, OnDestroy {
  public user: User;
  private id: number;
  public isKeycloakUserPage: boolean;
  public friendship: Friendship;
  public followership: Followership;
  public friendsQuantity: number;
  public userPostQuantity: number;
  public followersQuantity: number;
  public followingQuantity: number;
  public usersYouMayKnow: User[] = [];
  private subscriber: Subscription;
  public pinnedPost: Post;
  public daysUntilElimination: number;
  public daysUntilEliminationParam: any;

  constructor(private userService: UserService, private activatedRoute: ActivatedRoute,
    private friendshipService: FriendshipService, private router: Router,
    private postService: PostService, private callService: CallService,
    private followershipService: FollowershipService,
    public authService: AuthService) { }

  ngOnInit(): void {
    this.userService.changePhotoOrDescriptionEvent.subscribe(data => {
      if (data?.photo) {
        this.user.photo = data.photo;
      }
      if (data?.description) {
        this.user.description = data.description;
      }
    });

    this.activatedRoute.params.subscribe(params => {
      this.id = params['id'];
      if (this.id) {
        this.getUser();
      } else {
        this.isKeycloakUserPage = true;
        this.getKeycloakUser();
      }
    });

    this.postService.reducePostsQuantityEvent.subscribe(() => this.userPostQuantity--);

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
    ).subscribe(() =>  {
      this.user = null;
      this.friendship = null;
      this.followership = null;
    });
  }

  //#region User
  private getUser() {
    this.userService.getUser(this.id).subscribe(user => {
      this.user = user;
      this.getHighlightedPost();
      this.getRelationship();
      this.callService.subscribeToEvents();
      this.getDaysUntilElimination();
    });
  }

  private getKeycloakUser() {
    this.user = this.authService.keycloakUser;
    this.getProfileHeaderData();
    this.getHighlightedPost();
    this.getDaysUntilElimination();
  }

  private getHighlightedPost() {
    this.postService.getPinnedPost(this.user.idUser).subscribe(pinnedPost => {
      this.pinnedPost = pinnedPost;
    });
  }

  private getDaysUntilElimination() {
    if (this.user?.deletionDate) {
      let deletionDate = new Date(this.user.deletionDate);
      let todayDate = new Date();
      let daysDifferenceInMilliseconds = todayDate.getTime() - deletionDate.getTime();
      let dayInMilliseconds = 86400000;
      this.daysUntilElimination = 14 - daysDifferenceInMilliseconds / dayInMilliseconds | 0;
      this.daysUntilEliminationParam = {value: this.daysUntilElimination};
    }
  }

  private getRelationship() {
    this.user.isChecked ? this.getFollowership() : this.getFriendship();
  }

  private getProfileHeaderData() {
    this.getFriendsQuantity();
    this.getPostQuantity();
    this.getFollowingQuantity();
    if (this.user.isChecked) {
      this.getFollowersQuantity();
    }
  }

  private getPostQuantity() {
    this.postService.countPostByUser(this.user.idUser).subscribe(count => this.userPostQuantity = count);
  }

  private getFollowingQuantity() {
    this.followershipService.getFollowingQuantity(this.user.idUser).subscribe(followingQuantity => this.followingQuantity = followingQuantity);
  }

  public goToProfile(userYouMayKnow: User) {
    this.router.navigate(['/profile', userYouMayKnow.idUser]);
  }
  //#endregion

  //#region Friendship
  private getFriendship() {
    this.friendshipService.getFriendship(this.id).subscribe(friendship => {
      this.friendship = friendship;
      this.getFriendshipProfileHeaderData();
      this.getUsersYouMayKnow();
    });
  }

  private getFriendshipProfileHeaderData() {
    this.getFriendsQuantity();
    this.getPostQuantity();
    this.getFollowingQuantity();
  }

  private getUsersYouMayKnow() {
    let keycloakUserId = this.authService.keycloakUser.idUser;
    this.userService.getUsersYouMayKnow(this.user.idUser, keycloakUserId).subscribe(usersYouMayKnow => this.usersYouMayKnow = usersYouMayKnow);
  }

  public addFriend(): void {
    this.friendshipService.addFriend(this.user);
  }

  private getFriendsQuantity(): void {
    this.friendshipService.getFriendsQuantity(this.user.idUser).subscribe(friendsQuantity => {
      this.friendsQuantity = friendsQuantity;
    });
  }

  public deleteFriend() {
    this.friendshipService.askToDelete(this.friendship.idFriendship);
    this.friendshipService.friendshipDeletedEvent.subscribe(() => {
      this.friendship.status = false;
      this.friendsQuantity--;
    });
  }

  public goToFriendsPage(): void {
    if (this.user.idUser) {
      this.router.navigate(['profile/lists/friends', this.user.idUser]);
    }
  }
  //#endregion

  //#region Followership
  private getFollowership() {
    this.followershipService.getFollowership(this.id).subscribe(followership => {
      this.followership = followership;
      this.getFollowershipProfileHeaderData();
    });
  }

  private getFollowershipProfileHeaderData() {
    this.getPostQuantity();
    this.getFollowersQuantity();
    this.getFollowingQuantity();
  }

  private getFollowersQuantity() {
    this.followershipService.getFollowersQuantity(this.user.idUser).subscribe(followersQuantity => this.followersQuantity = followersQuantity);
  }

  public followUser() {
    this.followershipService.followUser(this.user).subscribe(followership => {
      this.followership = followership;
      this.followersQuantity++;
    });
  }

  public unfollowUser() {
    this.followershipService.unfollowUser(this.followership.idFollowership).subscribe(response => {
      console.log(response.message);
      this.followership = null;
      this.followersQuantity--;
    });
  }

  public goToFollowersPage(): void {
    if (this.user.idUser) {
      this.router.navigate(['profile/lists/followers', this.user.idUser]);
    }
  }

  public goToFollowingPage(): void {
    if (this.user.idUser) {
      this.router.navigate(['profile/lists/following', this.user.idUser]);
    }
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