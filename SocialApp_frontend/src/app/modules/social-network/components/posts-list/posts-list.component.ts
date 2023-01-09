import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Post } from 'src/app/models/post';
import { PostService } from 'src/app/services/post.service';

@Component({
  selector: 'posts-list-comp',
  templateUrl: './posts-list.component.html',
  styleUrls: ['./posts-list.component.css']
})
export class PostsListComponent implements OnInit {
  @Input() public idUser: number;
  @Input() public page: string;
  public posts: Post[] = [];
  public isLastPage: boolean;
  private from: number = 0;
  @Output() noTodayFeedEvent = new EventEmitter<string>();
  @Output() noOldFeedEvent = new EventEmitter<string>();

  constructor(private postService: PostService) { }

  ngOnInit(): void {
    this.getPosts();
    this.postService.postDeletedEvent.subscribe(post => this.posts = this.posts.filter(p => p.idPost != post.idPost));
  }

  public getNewPosts() {
    this.from += 10;
    this.getPosts();
  }

  private getPosts() {
    this.postService.getPosts(this.idUser, this.from, this.page).subscribe(response => {
      this.posts = this.posts.concat(response.posts);
      this.isLastPage = response.isLastPage;
      if (this.isLastPage && (this.page == "friendsFeed" || this.page=='followingFeed')) {
        this.noTodayFeedEvent.emit();
      }
      if ((this.page == "friendsFeedOld" || this.page == "followingFeedOld") && this.posts.length == 0) {
        this.noOldFeedEvent.emit();
      }
    });
  }
}