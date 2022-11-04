import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Comment } from 'src/app/models/comment';
import { User } from 'src/app/models/user';
import { CommentService } from 'src/app/services/comment.service';
import { TranslateExtensionService } from 'src/app/services/translate-extension.service';
import { WebsocketService } from 'src/app/services/websocket.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'comment-comp',
  templateUrl: './comment.component.html',
  styleUrls: ['./comment.component.css']
})
export class CommentComponent implements OnInit {
  @Input() public comment: Comment;
  @Input() public keycloakUser: User;
  @Input() public commentLevel: number;
  public replies: Comment[];

  constructor(private router: Router, private commentService: CommentService,
    private translateExtensionService: TranslateExtensionService,
    private webSocketService: WebsocketService) { }

  ngOnInit(): void {
  }

  public goToProfile() {
    if (this.keycloakUser.idUser == this.comment.user.idUser) {
      this.router.navigate(['/profile']);
    } else {
      this.router.navigate(['/profile', this.comment.user.idUser]);
    }
  }

  public async showReplyModal() {
    const { value: commentText } = await Swal.fire({
      title: this.translateExtensionService.getTranslatedStringByUrl("COMMENT.REPLY_MODAL_TITLE"),
      input: 'textarea',
      inputLabel: this.translateExtensionService.getTranslatedStringByUrl("COMMENT.REPLY_MODAL_TEXT"),
      inputAttributes: {maxlength: "100"},
      showCancelButton: true,
      background: '#7f5af0', color: 'white',
      confirmButtonColor: '#2cb67d', cancelButtonColor: '#d33',
      confirmButtonText: this.translateExtensionService.getTranslatedStringByUrl('COMMENT.REPLY_MODAL_REPLY_BUTTON'),
      cancelButtonText: this.translateExtensionService.getTranslatedStringByUrl('COMMENT.REPLY_MODAL_CANCEL_BUTTON')
    });
    
    if (commentText) {
      this.createComment(commentText);
    }
  }

  private createComment(commentText: string) {
    if (commentText?.length <= 100) {
      let comment: Comment = new Comment;
      comment.text = commentText;
      comment.user = this.keycloakUser;
      comment.post = this.comment.post;
      this.commentService.createComment(comment, this.comment.idComment).subscribe(response => {
        console.log(response.message);
        this.webSocketService.newNotification(this.comment.user);
        this.showReplies();
      });
    }
  }

  public showReplies() {
    //If the level of the comment is 7, the system cannot display his replies due to UI/UX issues.
    if (this.commentLevel < 7) {
      this.commentService.getReplies(this.comment.idComment).subscribe(comments => {
        this.replies = comments;
        this.comment.hasReplies = false;
      });
    } else {
      this.router.navigate(['/comment/', this.comment.idComment]);
    }
  }
}