import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { User } from 'src/app/models/user';
import { AuthService } from 'src/app/services/auth.service';
import { TranslateExtensionService } from 'src/app/services/translate-extension.service';
import { UserService } from 'src/app/services/user.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.css']
})
export class UsersComponent implements OnInit {
  public placeholder: string;
  public name: string;
  public searchName: string;
  public page: number;
  public paginator: any;
  public users: User[] = [];

  constructor(private authService: AuthService, private userService: UserService,
    private translateExtensionService: TranslateExtensionService,
    private activatedRoute: ActivatedRoute, private router: Router,
    private translate: TranslateService) { }

  ngOnInit(): void {
    const lang = localStorage.getItem('lang');
    lang ? this.translate.use(lang) : this.translate.use('en');

    this.authService.userIsOnAdminModule = true;
    this.placeholder = this.translateExtensionService.getTranslatedStringByUrl('ADMIN.USERS_PLACEHOLDER');

    this.activatedRoute.params.subscribe(params => {
      if (params['name'] && params['page']) {
        this.name = params['name'];
        this.searchName = params['name'];
        this.page = params['page'];
        this.searchUsers();
      }
    });
  }

  public search(event) {
    if (event.key === 'Enter' || event.keyCode == 13) {
      this.searchUsers();
    }
  }

  public searchUsers() {
    if (this.searchName && this.searchName.length >= 3) {
      if (!this.page || this.searchName != this.name) {
        this.router.navigate(['/admin/users/' + this.searchName + '/0']);
      } else {
        this.getUsers();
      }
    }
  }

  private getUsers() {
    this.userService.getUsersByNames(this.name, this.page).subscribe(response => {
      this.users = response.content;
      this.paginator = response;
    });
  }

  public changeUserIsChecked(user: User) {
    user.isChecked = !user.isChecked;
    this.userService.update(user).subscribe(response => {
      console.log(response.message);
    });
  }

  public deleteOrRestoreUser(user: User) {
    user.deletionDate ? user.deletionDate = null : user.deletionDate = new Date();
    this.userService.update(user).subscribe(response => {
      console.log(response.message);
    });
  }

  public deleteUsersWhoseDeletionDateIsNotNull() {
    Swal.fire({
      title: this.translateExtensionService.getTranslatedStringByUrl('ADMIN.DELETE_PROCESS.MODAL_TITLE'),
      text: this.translateExtensionService.getTranslatedStringByUrl('ADMIN.DELETE_PROCESS.MODAL_TEXT'),
      showCancelButton: true, showLoaderOnConfirm: true, background: '#7f5af0', color: 'white',
      confirmButtonColor: '#2cb67d', cancelButtonColor: '#d33', backdrop: true,
      preConfirm: () => {
        return this.userService.deleteUsersWithDeletionDate().subscribe(response => console.log(response.message));
      },
      allowOutsideClick: () => !Swal.isLoading()
    }).then((result) => {
      if (result.isConfirmed) {
        this.router.navigate(['/admin/users']);
        Swal.fire({
          title: this.translateExtensionService.getTranslatedStringByUrl('ADMIN.DELETE_PROCESS.COMPLETED_MODAL_TITLE'),
          background: '#7f5af0', color: 'white', confirmButtonColor: '#2cb67d'
        });
      }
    })
  }
}