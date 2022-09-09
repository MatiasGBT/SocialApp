import { HttpClient } from '@angular/common/http';
import { EventEmitter, Injectable, Output } from '@angular/core';
import { catchError, Observable, retry, throwError } from 'rxjs';
import Swal from 'sweetalert2';
import { Notification } from '../models/notification';
import { User } from '../models/user';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class NotificationsService {
  private baseUrl: string = 'http://localhost:8090/api/notifications';
  @Output() notificationsChanger: EventEmitter<any> = new EventEmitter();
  @Output() notificationsEnabled: EventEmitter<any> = new EventEmitter();

  constructor(private authService: AuthService, private http: HttpClient) { }

  getNotificationsStatus() {
    const notificationsStatus: string = localStorage.getItem('notifications');
    return notificationsStatus == null || notificationsStatus == 'on';
  }

  public getNotifications(user: User): Observable<Notification[]> {
    return this.http.get<Notification[]>(`${this.baseUrl}/get/${user.username}`).pipe(
      catchError(e => {
        Swal.fire({
          icon: 'error', title: e.error.message, text: e.error.error, showConfirmButton: false,
          timer: 1250, background: '#7f5af0', color: 'white'
        });
        return throwError(() => e);
      })
    );
  }

  public delete(id: number): Observable<any> {
    return this.http.delete<any>(`${this.baseUrl}/delete/${id}`).pipe(
      catchError(e => {
        Swal.fire({
          icon: 'error', title: e.error.message, text: e.error.error, showConfirmButton: false,
          timer: 1250, background: '#7f5af0', color: 'white'
        });
        return throwError(() => e);
      })
    );
  }

  public deleteAll(): Observable<any> {
    return this.http.delete<any>(`${this.baseUrl}/delete-all/${this.authService.getUsername()}`).pipe(
      catchError(e => {
        Swal.fire({
          icon: 'error', title: e.error.message, text: e.error.error, showConfirmButton: false,
          timer: 1250, background: '#7f5af0', color: 'white'
        });
        return throwError(() => e);
      })
    );
  }

  public viewNotification(id: number): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/view/${id}`, null).pipe(
      catchError(e => {
        Swal.fire({
          icon: 'error', title: e.error.message, text: e.error.error, showConfirmButton: false,
          timer: 1250, background: '#7f5af0', color: 'white'
        });
        return throwError(() => e);
      })
    );
  }
}
