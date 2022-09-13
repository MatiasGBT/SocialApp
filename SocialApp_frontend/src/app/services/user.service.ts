import { HttpClient, HttpParams } from '@angular/common/http';
import { EventEmitter, Injectable, Output } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, map, Observable, throwError } from 'rxjs';
import Swal from 'sweetalert2';
import { User } from '../models/user';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private baseUrl: string = 'http://localhost:8090/api/user';
  @Output() userChanger: EventEmitter<any> = new EventEmitter();

  constructor(private http: HttpClient, private authService: AuthService, private router: Router) { }

  public getKeycloakUser(): Observable<User> {
    return this.http.get<User>(`${this.baseUrl}/get/keycloak/${this.authService.getUsername()}`).pipe(
      catchError(e => {
        Swal.fire({
          icon: 'error', title: e.error.message, text: e.error.error, showConfirmButton: false,
          timer: 1250, background: '#7f5af0', color: 'white'
        });
        return throwError(() => e);
      })
    );
  }

  public getUser(id: number): Observable<User> {
    return this.http.get<User>(`${this.baseUrl}/get/${id}`).pipe(
      catchError(e => {
        this.router.navigate(['/profile']);
        Swal.fire({
          icon: 'error', title: e.error.message, text: e.error.error, showConfirmButton: false,
          timer: 1250, background: '#7f5af0', color: 'white'
        });
        return throwError(() => e);
      })
    );
  }

  public filterUsers(name: string): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/get/autocomplete/${name}&${this.authService.getUsername()}`).pipe(
      map(response => response as User[]),
      catchError(e => {
        Swal.fire({
          icon: 'error', title: e.error.message, text: e.error.error, showConfirmButton: false,
          timer: 1250, background: '#7f5af0', color: 'white'
        });
        return throwError(() => e);
      })
    );
  }

  public filterUsersWithoutLimit(name: string): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/get/search/${name}&${this.authService.getUsername()}`).pipe(
      map(response => response as User[]),
      catchError(e => {
        Swal.fire({
          icon: 'error', title: e.error.message, text: e.error.error, showConfirmButton: false,
          timer: 1250, background: '#7f5af0', color: 'white'
        });
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
        Swal.fire({
          icon: 'error', title: e.error.message, text: e.error.error, showConfirmButton: false,
          timer: 1250, background: '#7f5af0', color: 'white'
        });
        return throwError(() => e);
      })
    );
  }

  public updateDescription(userUpdated: User): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/edit`, userUpdated).pipe(
      catchError(e => {
        Swal.fire({
          icon: 'error', title: e.error.message, text: e.error.error, showConfirmButton: false,
          timer: 1250, background: '#7f5af0', color: 'white'
        });
        return throwError(() => e);
      })
    );
  }

  public getFriends(idUser: number): Observable<User[]> {
    return this.http.get<User[]>(`${this.baseUrl}/get/friends/${idUser}`).pipe(
      catchError(e => {
        Swal.fire({
          icon: 'error', title: e.error.message, text: e.error.error, showConfirmButton: false,
          timer: 1250, background: '#7f5af0', color: 'white'
        });
        return throwError(()=>e);
      })
    );
  }
}
