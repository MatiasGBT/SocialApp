import { Component, OnInit } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { Router } from '@angular/router';
import { LangChangeEvent, TranslateService } from '@ngx-translate/core';
import { map, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';
import { User } from 'src/app/models/user';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {
  public placeholder: string;
  public user: User;
  public people: Observable<User[]>;
  control = new FormControl();

  constructor(private translate:TranslateService, private userService: UserService,
    private router: Router) {}

  ngOnInit(): void {
    const lang = localStorage.getItem('lang');
    if (lang != null) {
      this.translate.use(lang);
    } else {
      this.translate.use('en');
    }
    
    this.translate.onLangChange.subscribe((event: LangChangeEvent) => {
      this.translate.use(event.lang);
      this.translate.get('NAVBAR.SEARCH_PEOPLE').subscribe((res: string) => {
        this.placeholder = res;
      });
    });

    this.userService.getKeycloakUser().subscribe(response => {
      this.user = response;
    });

    this.userService.userChanger.subscribe(data => {
      this.user.photo = data.user.photo;
    });
    
    this.people = this.control.valueChanges.pipe(
      map(value => typeof value === 'string' && value.length >= 3 ? value : value.name),
      mergeMap(value =>  value ? this._filter(value) : [])
    );
  }

  private _filter(value: string): Observable<User[]> {
    return this.userService.filterUsers(value);
  }

  goToProfile(event: MatAutocompleteSelectedEvent) {
    let user = event.option.value as User;
    this.router.navigate(['/profile', user.idUser])
    this.control.setValue('');
    event.option.focus();
    event.option.deselect();
  }
}
