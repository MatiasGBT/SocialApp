import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { NgxCroppedEvent, NgxPhotoEditorService } from 'ngx-photo-editor';
import { User } from 'src/app/models/user';
import { AuthService } from 'src/app/services/auth.service';
import { UserService } from 'src/app/services/user.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-edit-profile',
  templateUrl: './edit-profile.component.html',
  styleUrls: ['./edit-profile.component.css']
})
export class EditProfileComponent implements OnInit {
  public user: User;
  public file: File;
  public placeholder: string;
  private applyBtn: string;
  private cancelBtn: string;
  output?: NgxCroppedEvent;

  constructor(private authService: AuthService, private translate: TranslateService,
    private userService: UserService, private router: Router,
    private fileEditorService: NgxPhotoEditorService) { }

  ngOnInit(): void {
    let lang = localStorage.getItem("lang");
    if (lang == null) {
      lang = "en";
    }
    this.translate.get('PROFILE.EDIT.DESCRIPTION_PLACEHOLDER').subscribe((res: string) => {
      this.placeholder = res;
    });
    this.translate.get('PROFILE.EDIT.APPLY_BUTTON').subscribe((res: string) => {
      this.applyBtn = res;
    });
    this.translate.get('PROFILE.EDIT.CANCEL_BUTTON').subscribe((res: string) => {
      this.cancelBtn = res;
    });

    this.user = this.authService.user;
  }

  fileChangeHandler(event: any) {
    this.fileEditorService.open(event, {
      aspectRatio: 4 / 4,
      autoCropArea: 1,
      resizeToWidth: 500,
      resizeToHeight: 500,
      viewMode: 1,
      hideModalHeader: true,
      applyBtnText: this.applyBtn,
      closeBtnText: this.cancelBtn
    }).subscribe(data => {
      this.output = data;
      this.file = this.output.file;
    });
  }

  editProfile() {
    if (!this.file) {
      console.error("No file selected");
    } else {
      if (this.user.description == null) {
        this.user.description = "";
      }
      this.userService.uploadNewUser(this.file, this.user).subscribe(user => {
        this.user = user;
        window.location.reload();
      });
    }
  }
}
