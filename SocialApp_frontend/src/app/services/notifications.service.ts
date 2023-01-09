import { HttpClient } from '@angular/common/http';
import { EventEmitter, Injectable, Output } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import { Notification } from '../models/notification';
import { User } from '../models/user';
import { AuthService } from './auth.service';
import { CatchErrorService } from './catch-error.service';

@Injectable({
  providedIn: 'root'
})
export class NotificationsService {
  private baseUrl: string = 'http://localhost:8090/api/notifications';
  @Output() notificationViewedOrDeletedEvent: EventEmitter<any> = new EventEmitter();
  @Output() notificationsEnabledEvent: EventEmitter<any> = new EventEmitter();
  @Output() newNotificationEvent: EventEmitter<any> = new EventEmitter();

  constructor(private authService: AuthService, private http: HttpClient,
    private catchErrorService: CatchErrorService) { }

  getNotificationsStatus() {
    const notificationsStatus: string = localStorage.getItem('notifications');
    return notificationsStatus == 'on' ? true : false;
  }

  public getNotifications(user: User): Observable<Notification[]> {
    return this.http.get<Notification[]>(`${this.baseUrl}/get/list/${user.username}`).pipe(
      catchError(e => {
        this.catchErrorService.showErrorModal(e);
        return throwError(() => e);
      })
    );
  }

  public delete(id: number): Observable<any> {
    return this.http.delete<any>(`${this.baseUrl}/delete/${id}`).pipe(
      catchError(e => {
        this.catchErrorService.showErrorModal(e);
        return throwError(() => e);
      })
    );
  }

  public deleteAll(): Observable<any> {
    return this.http.delete<any>(`${this.baseUrl}/delete-all/${this.authService.getUsername()}`).pipe(
      catchError(e => {
        this.catchErrorService.showErrorModal(e);
        return throwError(() => e);
      })
    );
  }

  public viewNotification(id: number): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/put/view/${id}`, null).pipe(
      catchError(e => {
        this.catchErrorService.showErrorModal(e);
        return throwError(() => e);
      })
    );
  }
}
