import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { KeycloakService } from 'keycloak-angular';
import { catchError, Observable, throwError } from 'rxjs';
import { User } from '../models/user';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private user: User;
  private token = this.keycloakService.getKeycloakInstance().token;
  private baseUrl = "http://localhost:8090/api/app/";

  constructor(private http: HttpClient, private keycloakService: KeycloakService) { }

  public login(): Observable<any> {
    if (this.user == null) {
      this.user = new User();
      let payload = this.obtainPayload(this.token);
      this.createUserWithPayload(payload);
    }
    const httpHeaders = new HttpHeaders({'Content-Type': 'application/json', 'Authorization': 'Basic ' + this.keycloakService.getToken()});
    return this.http.post<any>(this.baseUrl + "login", this.user, {headers: httpHeaders});
  }

  public getUsername(): string {
    let payload = this.obtainPayload(this.token);
    return payload.preferred_username;
  }

  public hasRole(role: string): boolean {
    let payload = this.obtainPayload(this.token);
    return payload.resource_access.springback.roles.includes(role);
  }

  private obtainPayload(access_token:string): any {
    return JSON.parse(window.atob(access_token.split(".")[1]));
  }

  private createUserWithPayload(payload) {
    this.user.username = payload.preferred_username;
    this.user.name = payload.given_name;
    this.user.surname = payload.family_name;
  }
}
