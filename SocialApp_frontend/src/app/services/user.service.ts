import { HttpClient } from '@angular/common/http';
import { EventEmitter, Injectable, Output } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, map, Observable, throwError } from 'rxjs';
import { User } from '../models/user';
import { AuthService } from './auth.service';
import { CatchErrorService } from './catch-error.service';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private baseUrl: string = 'http://localhost:8090/api/user';
  @Output() userChanger: EventEmitter<any> = new EventEmitter();
  @Output() userConnectEvent: EventEmitter<any> = new EventEmitter();
  @Output() userDisconnectEvent: EventEmitter<any> = new EventEmitter();

  constructor(private http: HttpClient, private authService: AuthService, private router: Router,
    private catchErrorService: CatchErrorService) { }

  public getKeycloakUser(): Observable<User> {
    return this.http.get<User>(`${this.baseUrl}/get/keycloak/${this.authService.getUsername()}`).pipe(
      catchError(e => {
        this.catchErrorService.showErrorModal(e);
        return throwError(() => e);
      })
    );
  }

  public getUser(id: number): Observable<User> {
    return this.http.get<User>(`${this.baseUrl}/get/${id}`).pipe(
      catchError(e => {
        this.router.navigate(['/profile']);
        this.catchErrorService.showErrorModal(e);
        return throwError(() => e);
      })
    );
  }

  public filterUsers(name: string): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/get/autocomplete/${name}&${this.authService.getUsername()}`).pipe(
      map(response => response as User[]),
      catchError(e => {
        this.catchErrorService.showErrorModal(e);
        return throwError(() => e);
      })
    );
  }

  public filterUsersWithoutLimit(name: string): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/get/search/${name}&${this.authService.getUsername()}`).pipe(
      map(response => response as User[]),
      catchError(e => {
        this.catchErrorService.showErrorModal(e);
        return throwError(() => e);
      })
    );
  }

  public sendNewPhoto(file: File): Observable<any> {
    let formData = new FormData();
    formData.append("file", file);
    formData.append("username", this.authService.getUsername());
    return this.http.post<any>(`${this.baseUrl}/send-photo`, formData).pipe(
      catchError(e => {
        this.catchErrorService.showErrorModal(e);
        return throwError(() => e);
      })
    );
  }

  public updateDescription(userUpdated: User): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/edit`, userUpdated).pipe(
      catchError(e => {
        this.catchErrorService.showErrorModal(e);
        return throwError(() => e);
      })
    );
  }

  public getFriends(idUser: number): Observable<User[]> {
    return this.http.get<User[]>(`${this.baseUrl}/get/friends/${idUser}`).pipe(
      catchError(e => {
        this.catchErrorService.showErrorModal(e);
        return throwError(()=>e);
      })
    );
  }

  public getUsersYouMayKnow(idUser: number, idKeycloakUser: number): Observable<User[]> {
    return this.http.get<User[]>(`${this.baseUrl}/get/may-know/${idUser}&${idKeycloakUser}`).pipe(
      catchError(e => {
        this.catchErrorService.showErrorModal(e);
        return throwError(()=>e);
      })
    );
  }
}