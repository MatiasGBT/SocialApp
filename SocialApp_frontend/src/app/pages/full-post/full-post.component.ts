import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { Comment } from 'src/app/models/comment';
import { Post } from 'src/app/models/post';
import { User } from 'src/app/models/user';
import { CommentService } from 'src/app/services/comment.service';
import { PostService } from 'src/app/services/post.service';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-full-post',
  templateUrl: './full-post.component.html',
  styleUrls: ['./full-post.component.css']
})
export class FullPostComponent implements OnInit {
  public post: Post;
  public keycloakUser: User;
  public comments: Comment[] = [];
  public createdComment: Comment = new Comment;
  public commentPlaceholder: string;

  constructor(private activatedRoute: ActivatedRoute, private postService: PostService,
    private userService: UserService, private router: Router,
    private commentService: CommentService, private translate: TranslateService) { }

  ngOnInit(): void {
    this.activatedRoute.params.subscribe(params => {
      let id = params['id'];
      if (id) {
        this.postService.getPost(id).subscribe(post => this.post = post);
        this.userService.getKeycloakUser().subscribe(user => this.keycloakUser = user);
        this.commentService.getComments(id).subscribe(comments => this.comments = comments);
        this.translateCreateCommentPlaceholder();
      } else {
        this.router.navigate(['/index']);
      }
    });
  }

  public goToProfile(idUserComment: number) {
    if (this.keycloakUser.idUser == idUserComment) {
      this.router.navigate(['/profile']);
    } else {
      this.router.navigate(['/profile', idUserComment]);
    }
  }

  public createComment() {
    if (this.createdComment.text?.length >= 1 && this.createdComment.text?.length <= 100) {
      this.createdComment.user = this.keycloakUser;
      this.createdComment.post = this.post;
      this.createdComment.date = new Date();
      this.commentService.createComment(this.createdComment).subscribe(response => {
        console.log(response.message);
        this.comments.unshift(this.createdComment);
        this.createdComment = new Comment;
      });
    }
  }

  private translateCreateCommentPlaceholder() {
    this.translate.get('FULL_POST.CREATE_COMMENT_PLACEHOLDER').subscribe((res: string) => this.commentPlaceholder = res);
  }
}
