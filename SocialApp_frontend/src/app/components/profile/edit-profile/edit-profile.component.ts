import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { NgxCroppedEvent, NgxPhotoEditorService } from 'ngx-photo-editor';
import { User } from 'src/app/models/user';
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
  output?: NgxCroppedEvent;
  public noChanges: boolean = true;

  constructor(private translate: TranslateService, private userService: UserService,
    private fileEditorService: NgxPhotoEditorService, private router: Router) { }

  ngOnInit(): void {
    let lang = localStorage.getItem("lang");
    if (lang == null) {
      lang = "en";
    }
    this.translate.get('PROFILE.EDIT.DESCRIPTION_PLACEHOLDER').subscribe((res: string) => this.placeholder = res);
    this.translate.get('PROFILE.EDIT.APPLY_BUTTON').subscribe((res: string) => this.applyBtn = res);
    this.translate.get('PROFILE.EDIT.CANCEL_BUTTON').subscribe((res: string) => this.cancelBtn = res);

    this.userService.getKeycloakUser().subscribe(response => {
      this.user = response;
      this.description = this.user.description;
    });

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
      this.user.photo = null; //This is done in order to remove the user's image from the view and add the uploaded from the input.
      this.noChanges = false;
    });
  }

  editProfile() {
    if (this.description == null || this.description == undefined) {
      this.description = "";
    }
    this.user.description = this.description;

    Swal.fire({
      icon: 'success',
      title: 'Changes saved',
      showConfirmButton: false,
      timer: 1250,
      background: '#7f5af0',
      color: 'white'
    }).then(() => {
      this.userService.updateDescription(this.user).subscribe(response => {
        this.user = response.user as User;
        console.log(response.message);
        this.router.navigate(['/profile']);

        if (this.file) {
          this.userService.sendNewPhoto(this.file).subscribe(response => {
            this.user = response.user as User;
            console.log(response.message);
            this.userService.userChanger.emit({user: this.user});
            this.router.navigate(['/profile']);
          })
        }
      });
    });
  }

  public checkChanges(): void {
      if (this.description != undefined && this.description != this.user.description) {
        this.noChanges = false;
      }
  }
}
