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
  public description: string;
  public placeholder: string;
  private applyBtn: string;
  private cancelBtn: string;
  private changesSaved: string;
  output?: NgxCroppedEvent;
  public noChanges: boolean = true;

  constructor(private translate: TranslateService, private userService: UserService,
    private fileEditorService: NgxPhotoEditorService, private router: Router,
    private authService: AuthService) { }

  ngOnInit(): void {
    this.translate.get('PROFILE.EDIT.DESCRIPTION_PLACEHOLDER').subscribe((res: string) => this.placeholder = res);
    this.translate.get('PROFILE.EDIT.APPLY_BUTTON').subscribe((res: string) => this.applyBtn = res);
    this.translate.get('PROFILE.EDIT.CANCEL_BUTTON').subscribe((res: string) => this.cancelBtn = res);
    this.translate.get('PROFILE.EDIT.CHANGES_SAVED').subscribe((res: string) => this.changesSaved = res);


    this.user = this.authService.keycloakUser;
    this.description = this.user.description;
  }

  public fileChangeHandler(event: any) {
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
      this.noChanges = false;
    });
  }

  public editProfile() {
    if (this.description == null || this.description == undefined) {
      this.description = "";
    }
    this.updateProfile();
    this.fireSuccessModal();
  }

  private updateProfile() {
    this.userService.updateProfile(this.file, this.description).subscribe(response => {
      console.log(response.message);
      this.userService.changePhotoOrDescriptionEvent.emit({photo: response.photo, description: response.description});
    });
  }

  private fireSuccessModal() {
    Swal.fire({
      icon: 'success', title: this.changesSaved, showConfirmButton: false,
      timer: 1250, background: '#7f5af0', color: 'white'
    }).then(() => {
      this.router.navigate(['/profile']);
    });
  }

  public checkChanges(): void {
    if (this.description != undefined && this.description != this.user.description) {
      this.noChanges = false;
    }
}
}