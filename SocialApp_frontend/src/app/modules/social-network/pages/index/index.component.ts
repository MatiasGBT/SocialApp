import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Post } from 'src/app/models/post';
import { User } from 'src/app/models/user';
import { AuthService } from 'src/app/services/auth.service';
import { PostService } from 'src/app/services/post.service';

@Component({
  selector: 'app-index',
  templateUrl: './index.component.html',
  styleUrls: ['./index.component.css', '../../../../../assets/index_profile.css']
})
export class IndexComponent implements OnInit {
  public user: User;
  public showOldFeed: boolean = false;
  public isLastPage: boolean;
  public popularPost: Post;
  public showNoFeed: boolean = false;
  public welcomePost: Post;
  public page: string;

  constructor(private authService: AuthService, private postService: PostService,
    private activatedRoute: ActivatedRoute, private router: Router) { }

  ngOnInit(): void {
    this.activatedRoute.params.subscribe(params => {
      this.page = params['page'];
      if (this.page != 'friends' && this.page != 'following' && this.page != 'trend') {
        this.router.navigate(['index']);
      }
      //When this check is performed, the user may not yet be defined (especially the first time).
      if (this.page == 'friends' && (this.user?.isChecked || !this.user)) {
        this.router.navigate(['index/following']);
      }
    });

    this.user = this.authService.keycloakUser;

    this.postService.getTheMostPopularPostFromToday().subscribe(popularPost => {
      this.popularPost = popularPost;
    });
  }

  public updateNoTodayFeed() {
    this.showOldFeed = true;
    this.isLastPage = true;
  }

  public updateNoOldFeed() {
    if (this.showOldFeed) {
      this.showNoFeed = true;
      if (this.page == 'friends') {
        this.postService.getPost(1).subscribe(post => this.welcomePost = post);
      }
    }
  }

  /*
  It is necessary to reset the showOldFeed and showNoFriendsFeed booleans so that they do not interfere
  with the other lists (if a user sees all the posts in the Friends feed, the system should not show all
  the posts in the Following feed if they did not see them, for example)
  */
  public goToFriendsFeed() {
    this.showOldFeed = false;
    this.showNoFeed = false;
    this.router.navigate(['/index/friends']);
  }

  public goToFollowingFeed() {
    this.showOldFeed = false;
    this.showNoFeed = false;
    this.router.navigate(['/index/following']);
  }

  public goToTrendFeed() {
    this.showOldFeed = false;
    this.showNoFeed = false;
    this.router.navigate(['/index/trend']);
  }
}