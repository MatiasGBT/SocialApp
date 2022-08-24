import { HttpClient } from '@angular/common/http';
import { EventEmitter, Injectable, Output } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, map, Observable, throwError } from 'rxjs';
import { User } from '../models/user';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private baseUrl: string = 'http://localhost:8090/api';
  @Output() userChanger: EventEmitter<any> = new EventEmitter();

  constructor(private http: HttpClient, private router: Router,
    private authService: AuthService) { }

  public getKeycloakUser(): Observable<User> {
    return this.http.get<User>(`${this.baseUrl}/app/get-user/keycloak/${this.authService.getUsername()}`).pipe(
      catchError(e => {
        window.location.reload();
        return throwError(()=>e);
      })
    );
  }

  public getUser(id: number): Observable<User> {
    return this.http.get<User>(`${this.baseUrl}/app/get-user/${id}`).pipe(
      catchError(e => {
        this.router.navigate(['/index']); //Need a 404 page
        return throwError(()=>e);
      })
    );
  }

  public filterUsers(name: string): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/navbar/autocomplete/${name}&${this.authService.getUsername()}`).pipe(
      map(response => response as User[]),
      catchError(e => {
        console.error(e.error);
        return throwError(() => new Error(e));
      })
    );
  }

  public filterUsersWithoutLimit(name: string): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/navbar/autocomplete/full/${name}&${this.authService.getUsername()}`).pipe(
      map(response => response as User[]),
      catchError(e => {
        console.error(e.error);
        return throwError(() => new Error(e));
      })
    );
  }

  uploadNewUser(file: File, user: User): Observable<any> {
    let formData = new FormData();
    formData.append("file", file);
    formData.append("username", user.username);
    formData.append("description", user.description);
    return this.http.post(`${this.baseUrl}/profile/edit/complete`, formData).pipe(
      catchError(e => {
        console.error(e.error);
        return throwError(() => new Error(e));
      })
    );
  }

  uploadNewUserWithoutFile(user: User): Observable<any> {
    let formData = new FormData();
    formData.append("username", user.username);
    formData.append("description", user.description);
    return this.http.post(`${this.baseUrl}/profile/edit/half`, formData);
  }
}
