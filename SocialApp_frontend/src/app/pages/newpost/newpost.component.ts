import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { PostService } from 'src/app/services/post.service';
import { TranslateExtensionService } from 'src/app/services/translate-extension.service';
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
    private router: Router, private translateExtensionService: TranslateExtensionService) { }

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
    } else {
      this.sendPost();
    }
  }

  private fireErrorModal() {
    Swal.fire({
      title: this.translateExtensionService.getTranslatedStringByUrl("NEWPOST.MODAL_ERROR_TITLE"),
      text: this.translateExtensionService.getTranslatedStringByUrl("NEWPOST.MODAL_ERROR_TEXT"),
      icon: 'error', showConfirmButton: false, timer: 1250, background: '#7f5af0', color: 'white'
    });
  }

  private sendPost() {
    if (!this.postText) {
      this.postText = "";
    }
    this.postService.createPost(this.postText, this.files[0]).subscribe(response => {
      console.log(response.message);
      this.postText = "";
      this.files = [];
      this.router.navigate(['post/', response.idPost]);
    });
  }
}
