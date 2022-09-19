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
  public posts: Post[] = [];
  private limit: number = 10;
  private idUser: number;
  public isLastPage: boolean;

  constructor(private userService: UserService, private postService: PostService) { }

  ngOnInit(): void {
    this.userService.getKeycloakUser().subscribe(user => {
      this.idUser = user.idUser;
      this.getPosts();
    });
  }

  public getNewPosts() {
    this.limit *= 2;
    this.getPosts();
  }

  private getPosts() {
    this.postService.getFeedByUser(this.idUser, this.limit).subscribe(response => {
      this.posts = response.posts;
      this.isLastPage = response.isLastPage;
    });
  }
}
