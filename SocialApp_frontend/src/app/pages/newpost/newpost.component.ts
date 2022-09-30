import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { PostService } from 'src/app/services/post.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-newpost',
  templateUrl: './newpost.component.html',
  styleUrls: ['./newpost.component.css']
})
export class NewpostComponent implements OnInit {
  files: File[] = [];
  public placeholder: string;
  public postText: string;
  public publishBtnText: string;

  constructor(private translate: TranslateService, private postService: PostService,
    private router: Router) { }

  ngOnInit(): void {
    this.translate.get('NEWPOST.PLACEHOLDER').subscribe((res: string) => this.placeholder = res);
  }

  onSelect(event) {
    this.files.push(...event.addedFiles);
    if(this.files.length > 1){
      this.replaceFile();
    }
  }
  
  onRemove(event) {
    this.files.splice(this.files.indexOf(event), 1);
  }

  replaceFile(){
    this.files.splice(0, 1);
  }

  public createPost() {
    if (this.postText == null && this.files[0] == null) {
      this.fireErrorModal();
    } else if (this.postText != null && this.files[0] != null) {
      this.createCompletePost();
    } else if (this.postText != null && this.files[0] == null) {
      this.createPostWithoutFile();
    } else if (this.postText == null && this.files[0] != null) {
      this.createPostWithoutText();
    }
  }

  private fireErrorModal() {
    let titleError: string, textError: string;
    this.translate.get("NEWPOST.MODAL_ERROR_TITLE").subscribe((res) => titleError = res);
    this.translate.get("NEWPOST.MODAL_ERROR_TEXT").subscribe((res) => textError = res);
    Swal.fire({
      icon: 'error', title: titleError, text: textError, showConfirmButton: false,
      timer: 1250, background: '#7f5af0', color: 'white'
    });
  }

  private createCompletePost() {
    this.postService.createPost(this.postText, this.files[0]).subscribe(response => {
      console.log(response.message);
      this.postText = "";
      this.files = [];
      this.router.navigate(['post/', response.idPost]);
    });
  }

  private createPostWithoutFile() {
    this.postService.createPostWithoutFile(this.postText).subscribe(response => {
      console.log(response.message);
      this.postText = "";
      this.router.navigate(['post/', response.idPost]);
    });
  }

  private createPostWithoutText() {
    this.postService.createPostWithoutText(this.files[0]).subscribe(response => {
      console.log(response.message);
      this.files = [];
      this.router.navigate(['post/', response.idPost]);
    });
  }
}
