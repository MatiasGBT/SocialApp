import { Component, Input, OnInit } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { Router } from '@angular/router';
import { LangChangeEvent, TranslateService } from '@ngx-translate/core';
import { map, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';
import { User } from 'src/app/models/user';
import { AuthService } from 'src/app/services/auth.service';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {
  public placeholder: string;
  public people: Observable<User[]>;
  control = new FormControl();

  constructor(private translate:TranslateService, private userService: UserService,
    private router: Router, public authService: AuthService) {}

  ngOnInit(): void {
    this.getPlaceholder();

    this.translate.onLangChange.subscribe((event: LangChangeEvent) => {
      this.translate.use(event.lang);
      this.getPlaceholder();
    });
    
    this.people = this.control.valueChanges.pipe(
      map(value => typeof value === 'string' && value.length >= 3 ? value : value.name),
      mergeMap(value => value ? this._filter(value) : [])
    );
  }

  private _filter(value: string): Observable<User[]> {
    return this.userService.filterUsers(value);
  }

  public goToProfile(event: MatAutocompleteSelectedEvent) {
    let user = event.option.value as User;
    this.router.navigate(['/profile', user.idUser])
    this.control.setValue('');
    event.option.focus();
    event.option.deselect();
  }

  public search(event) {
    if ((event.key === 'Enter' || event.keyCode == 13) && this.control.value && this.control.value.length >= 3) {
      this.router.navigate(['/search', this.control.value]);
    }
  }

  private getPlaceholder() {
    this.translate.get('NAVBAR.SEARCH_PEOPLE').subscribe((res: string) => this.placeholder = res);
  }
}
