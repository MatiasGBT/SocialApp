import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import { Notification } from '../models/notification';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class NotificationsService {
  private baseUrl: string = 'http://localhost:8090/api';

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
}
