import { Component, Input, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { Post } from 'src/app/models/post';
import { AuthService } from 'src/app/services/auth.service';
import { PostService } from 'src/app/services/post.service';
import { UserService } from 'src/app/services/user.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'post-comp',
  templateUrl: './post.component.html',
  styleUrls: ['./post.component.css']
})
export class PostComponent implements OnInit {
  @Input() public post: Post;
  public isLiked: boolean;

  constructor(public authService: AuthService, private postService: PostService,
    private userService: UserService, private translate: TranslateService) { }

  ngOnInit(): void {
    /*This is done to find out if the logged in user has liked this post so that the heart icon can be changed.*/
    this.post.likes.find(l => l.user.username == this.authService.getUsername()) != undefined 
    ? this.isLiked = true
    : this.isLiked = false;
  }

  public likePost() {
    if (!this.isLiked) {
      this.userService.getKeycloakUser().subscribe(user => {
        this.postService.likePost(this.post.idPost, user.idUser).subscribe(response => {
          console.log(response.message);
          this.isLiked = true;
          this.post.likes.length += 1;
        });
      });
    }
  }

  public dislikePost() {
    if (this.isLiked) {
      this.userService.getKeycloakUser().subscribe(user => {
        this.postService.dislikePost(this.post.idPost, user.idUser).subscribe(response => {
          console.log(response.message);
          this.isLiked = false;
          this.post.likes.length -= 1;
        });
      });
    }
  }

  public deletePost() {
    const lang = localStorage.getItem('lang');
    lang != null ? this.translate.use(lang) : this.translate.use('en');
    let modalTitle: string, modalText: string, modalBtnDelete: string, modalBtnCancel: string;
    this.translate.get("POST.MODAL_TITLE").subscribe((res) => modalTitle = res);
    this.translate.get("POST.MODAL_TEXT").subscribe((res) => modalText = res);
    this.translate.get("POST.MODAL_BUTTON_DELETE").subscribe((res) => modalBtnDelete = res);
    this.translate.get("POST.MODAL_BUTTON_CANCEL").subscribe((res) => modalBtnCancel = res);
    Swal.fire({
      icon: 'warning', title: modalTitle, text: modalText,
      showCancelButton: true, confirmButtonText: modalBtnDelete, cancelButtonText: modalBtnCancel,
      background: '#7f5af0', color: 'white', confirmButtonColor: '#d33', cancelButtonColor: '#2cb67d'
    }).then((result) => {
      if (result.isConfirmed) {
        this.postService.deletePost(this.post.idPost).subscribe(response => console.log(response.message));
        this.postService.deletePostEmitter.emit(this.post);
      }
    });
  }
}
