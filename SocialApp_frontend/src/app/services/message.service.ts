import { HttpClient } from '@angular/common/http';
import { EventEmitter, Injectable, Output } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import { Message } from '../models/message';
import { CatchErrorService } from './catch-error.service';

@Injectable({
  providedIn: 'root'
})
export class MessageService {
  private baseUrl = "http://localhost:8090/api/messages/";
  @Output() newMessageEvent: EventEmitter<any> = new EventEmitter();

  constructor(private http: HttpClient, private catchErrorService: CatchErrorService) { }

  public getMessagesByUsers(idKeycloakUser: number, idFriend: number, page: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}get/by-users/${idKeycloakUser}&${idFriend}&${page}`).pipe(
      catchError(e => {
        this.catchErrorService.showErrorModal(e);
        return throwError(() => e);
      })
    );
  }
}