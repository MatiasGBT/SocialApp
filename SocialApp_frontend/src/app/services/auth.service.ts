import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { KeycloakService } from 'keycloak-angular';
import { Observable } from 'rxjs';
import { User } from '../models/user';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private _user: User;
  private token = this.keycloakService.getKeycloakInstance().token;

  constructor(private http: HttpClient, private keycloakService: KeycloakService,
    private router: Router) { }

  public get user(): User {
    if (this._user == null) {
      this._user = new User();
      let payload = this.obtainPayload(this.token);
      this.createUserWithPayload(payload);
    }
    this.login(this._user).subscribe(response => {
      this._user = response.user as User;
      if (response.status != undefined && response.status == 201) {
        this.router.navigate(["/profile/edit"]);
      }
    });
    return this._user;
  }

  private login(user: User): Observable<any> {
    let url = "http://localhost:8090/api/index/login";
    const httpHeaders = new HttpHeaders({'Content-Type': 'application/json', 'Authorization': 'Basic ' + this.keycloakService.getToken()});
    return this.http.post<any>(url, user, {headers: httpHeaders});
  }

  private obtainPayload(access_token:string): any {
    return JSON.parse(window.atob(access_token.split(".")[1]));
  }

  private createUserWithPayload(payload) {
    this._user.username = payload.preferred_username;
    this._user.name = payload.given_name;
    this._user.surname = payload.family_name;
  }
}
