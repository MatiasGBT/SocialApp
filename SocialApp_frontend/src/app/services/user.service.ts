import { HttpClient, HttpParams } from '@angular/common/http';
import { EventEmitter, Injectable, Output } from '@angular/core';
import { catchError, map, Observable, throwError } from 'rxjs';
import Swal from 'sweetalert2';
import { User } from '../models/user';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private baseUrl: string = 'http://localhost:8090/api';
  @Output() userChanger: EventEmitter<any> = new EventEmitter();

  constructor(private http: HttpClient, private authService: AuthService) { }

  public getKeycloakUser(): Observable<User> {
    return this.http.get<User>(`${this.baseUrl}/app/get-user/keycloak/${this.authService.getUsername()}`).pipe(
      catchError(e => {
        Swal.fire({
          icon: 'error', title: e.message, text: e.error, showConfirmButton: false,
          timer: 1250, background: '#7f5af0', color: 'white'
        });
        return throwError(() => e);
      })
    );
  }

  public getUser(id: number): Observable<User> {
    return this.http.get<User>(`${this.baseUrl}/app/get-user/${id}`).pipe(
      catchError(e => {
        Swal.fire({
          icon: 'error', title: e.message, text: e.error, showConfirmButton: false,
          timer: 1250, background: '#7f5af0', color: 'white'
        });
        return throwError(() => e);
      })
    );
  }

  public filterUsers(name: string): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/navbar/autocomplete/${name}&${this.authService.getUsername()}`).pipe(
      map(response => response as User[]),
      catchError(e => {
        Swal.fire({
          icon: 'error', title: e.message, text: e.error, showConfirmButton: false,
          timer: 1250, background: '#7f5af0', color: 'white'
        });
        return throwError(() => e);
      })
    );
  }

  public filterUsersWithoutLimit(name: string): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/navbar/autocomplete/full/${name}&${this.authService.getUsername()}`).pipe(
      map(response => response as User[]),
      catchError(e => {
        Swal.fire({
          icon: 'error', title: e.message, text: e.error, showConfirmButton: false,
          timer: 1250, background: '#7f5af0', color: 'white'
        });
        return throwError(() => e);
      })
    );
  }

  public sendNewPhoto(file: File): Observable<any> {
    let formData = new FormData();
    formData.append("file", file);
    formData.append("username", this.authService.getUsername())
    return this.http.post<any>(`${this.baseUrl}/profile/send-photo`, formData).pipe(
      catchError(e => {
        Swal.fire({
          icon: 'error', title: e.message, text: e.error, showConfirmButton: false,
          timer: 1250, background: '#7f5af0', color: 'white'
        });
        return throwError(() => e);
      })
    );
  }

  public updateDescription(userUpdated: User): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/profile/edit`, userUpdated).pipe(
      catchError(e => {
        Swal.fire({
          icon: 'error', title: e.message, text: e.error, showConfirmButton: false,
          timer: 1250, background: '#7f5af0', color: 'white'
        });
        return throwError(() => e);
      })
    );
  }
}
