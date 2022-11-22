import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { filter, Subscription } from 'rxjs';
import { Comment } from 'src/app/models/comment';
import { User } from 'src/app/models/user';
import { AuthService } from 'src/app/services/auth.service';
import { CommentService } from 'src/app/services/comment.service';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-comment-page',
  templateUrl: './comment-page.component.html',
  styleUrls: ['./comment-page.component.css']
})
export class CommentPageComponent implements OnInit, OnDestroy {
  public comment: Comment;
  public keycloakUser: User;
  public commentLevel: number = 0;
  public subscriber: Subscription;

  constructor(private activatedRoute: ActivatedRoute, private router: Router,
    private commentService: CommentService, private authService: AuthService) { }

  ngOnInit(): void {
    this.activatedRoute.params.subscribe(params => {
      let id = params['id'];
      if (id) {
        this.commentService.getComment(id).subscribe(comment => this.comment = comment);
        this.keycloakUser = this.authService.keycloakUser;
      } else {
        this.router.navigate(['/index']);
      }
    });

    /*If the url/path changes (another comment goes to level 7 and has replies),
    it detects the change and restarts the page so that the ngOnInit is run again.*/
    this.subscriber = this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event) => {
       window.location.reload();
    });
  }

  ngOnDestroy(): void {
    this.subscriber?.unsubscribe();
  }
}