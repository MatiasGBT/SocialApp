import { Component, Input, OnInit } from '@angular/core';
import { Post } from 'src/app/models/post';
import { AuthService } from 'src/app/services/auth.service';
import { PostService } from 'src/app/services/post.service';

@Component({
  selector: 'post-comp',
  templateUrl: './post.component.html',
  styleUrls: ['./post.component.css']
})
export class PostComponent implements OnInit {
  @Input() public post: Post;
  public isLiked: boolean;

  constructor(private authService: AuthService, private postService: PostService) { }

  ngOnInit(): void {
    /*This is done to find out if the logged in user has liked this post so that the heart icon can be changed.*/
    this.post.likes.find(l => l.user.username == this.authService.getUsername()) != undefined 
    ? this.isLiked = true
    : this.isLiked = false;
  }

  public likePost() {
    if (!this.isLiked) {
      this.postService.likePost(this.post.idPost, this.post.user.idUser).subscribe(response => {
        console.log(response.message);
        this.isLiked = true;
        this.post.likes.length += 1;
      });
    }
  }

  public dislikePost() {
    if (this.isLiked) {
      this.postService.dislikePost(this.post.idPost, this.post.user.idUser).subscribe(response => {
        console.log(response.message);
        this.isLiked = false;
        this.post.likes.length -= 1;
      });
    }
  }

}
