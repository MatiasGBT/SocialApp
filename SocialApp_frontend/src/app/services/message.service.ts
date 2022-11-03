import { HttpClient } from '@angular/common/http';
import { EventEmitter, Injectable, Output } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import { Message } from '../models/message';
import { CatchErrorService } from './catch-error.service';

@Injectable({
  providedIn: 'root'
})
export class MessageService {
  private baseUrl = "http://localhost:8090/api/messages";
  @Output() newMessageEvent: EventEmitter<any> = new EventEmitter();
  @Output() deleteMessageEvent: EventEmitter<any> = new EventEmitter();

  constructor(private http: HttpClient, private catchErrorService: CatchErrorService) { }

  public getMessagesByUsers(idKeycloakUser: number, idFriend: number, page: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/get/list/${idKeycloakUser}&${idFriend}&${page}`).pipe(
      catchError(e => {
        this.catchErrorService.showErrorModal(e);
        return throwError(() => e);
      })
    );
  }

  public createMessage(message: Message, file: File): Observable<any> {
    let formData = new FormData();
    formData.append("message", JSON.stringify(message));
    formData.append("file", file);
    return this.http.post<any>(`${this.baseUrl}/post`, formData).pipe(
      catchError(e => {
        this.catchErrorService.showErrorModal(e);
        return throwError(() => e);
      })
    );
  }

  public deleteMessage(idMessage: number): Observable<any> {
    return this.http.delete<any>(`${this.baseUrl}/delete/${idMessage}`).pipe(
      catchError(e => {
        this.catchErrorService.showErrorModal(e);
        return throwError(() => e);
      })
    );
  }
}