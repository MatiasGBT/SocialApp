import { HttpClient } from '@angular/common/http';
import { EventEmitter, Injectable, Output } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import { Notification } from '../models/notification';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class NotificationsService {
  private baseUrl: string = 'http://localhost:8090/api';
  @Output() notificationsChanger: EventEmitter<any> = new EventEmitter();

  constructor(private authService: AuthService, private http: HttpClient) { }

  getNotificationsStatus() {
    const notificationsStatus: string = localStorage.getItem('notifications');
    return notificationsStatus == null || notificationsStatus == 'on';
  }

  public getNotifications(): Observable<Notification[]> {
    return this.http.get<Notification[]>(`${this.baseUrl}/notifications/get/${this.authService.getUsername()}`).pipe(
      catchError(e => {
        return throwError(() => e);
      })
    );
  }

  public delete(id: number): Observable<any> {
    return this.http.delete<any>(`${this.baseUrl}/notifications/delete/${id}`).pipe(
      catchError(e => {
        return throwError(() => e);
      })
    );
  }

  public deleteAll(): Observable<any> {
    return this.http.delete<any>(`${this.baseUrl}/notifications/delete-all/${this.authService.getUsername()}`).pipe(
      catchError(e => {
        return throwError(() => e);
      })
    );
  }
}
