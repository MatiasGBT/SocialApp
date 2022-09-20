import { Component, Input, OnInit } from '@angular/core';
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
  private limit: number = 10;

  constructor(private postService: PostService) { }

  ngOnInit(): void {
    this.getPosts();

    this.postService.deletePostEmitter.subscribe(post => {
      this.posts = this.posts.filter(p => p.idPost != post.idPost);
      this.postService.reducePostsQuantityEmitter.emit();
    });
  }

  public getNewPosts() {
    this.limit += 10;
    this.getPosts();
  }

  private getPosts() {
    this.postService.getPosts(this.idUser, this.limit, this.page).subscribe(response => {
      this.posts = response.posts;
      this.isLastPage = response.isLastPage;
      if (this.isLastPage && this.page == "feed") {
        this.postService.isLastFeedPageEmitter.emit(this.isLastPage);
      }
    });
  }
}
