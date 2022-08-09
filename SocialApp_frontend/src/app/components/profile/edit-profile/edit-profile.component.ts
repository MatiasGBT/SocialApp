import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { User } from 'src/app/models/user';
import { AuthService } from 'src/app/services/auth.service';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-edit-profile',
  templateUrl: './edit-profile.component.html',
  styleUrls: ['./edit-profile.component.css']
})
export class EditProfileComponent implements OnInit {
  public user: User;
  public files: File[] = [];
  public placeholder: string;

  constructor(private authService: AuthService, private translate: TranslateService,
    private userService: UserService, private router: Router) { }

  ngOnInit(): void {
    let lang = localStorage.getItem("lang");
    if (lang == null) {
      lang = "en";
    }
    this.translate.get('PROFILE.EDIT.DESCRIPTION_PLACEHOLDER').subscribe((res: string) => {
      this.placeholder = res;
    });

    this.user = this.authService.user;
    if (this.user.photo != null) {
      this.files.push();
    }
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

  editProfile() {
    if (this.files.length == 0) {
      console.error("No file selected");
    } else {
      this.userService.uploadPhoto(this.files[0], this.user).subscribe(user => {
        this.user = user;
        this.router.navigate(['/index']);
      });
    }
  }
}
