import { Component, OnInit } from '@angular/core';
import { Post } from 'src/app/models/post';
import { PostService } from 'src/app/services/post.service';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-index',
  templateUrl: './index.component.html',
  styleUrls: ['./index.component.css', '../../../assets/index_profile.css']
})
export class IndexComponent implements OnInit {
  public idUser: number;
  public showOldFeed: boolean = false;
  public isLastPage: boolean;
  public popularPost: Post;
  private noTodayFeed: boolean = false;
  public showNoFriendsFeed: boolean = false;
  public welcomePost: Post;

  constructor(private userService: UserService, private postService: PostService) { }

  ngOnInit(): void {
    this.userService.getKeycloakUser().subscribe(user => this.idUser = user.idUser);
    this.postService.isLastFeedPageEmitter.subscribe(isLastPage => {
      this.isLastPage = isLastPage;
      this.showOldFeed = true;
    });
    this.postService.getTheMostPopularPostFromToday().subscribe(popularPost => {
      this.popularPost = popularPost;
    });
  }

  public updateNoTodayFeed() {
    this.noTodayFeed = true;
  }

  public updateNoOldFeed() {
    if (this.noTodayFeed) {
      this.showNoFriendsFeed = true;
      this.postService.getPost(1).subscribe(post => this.welcomePost = post);
    }
  }
}