import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import { Comment } from '../models/comment';
import { CatchErrorService } from './catch-error.service';

@Injectable({
  providedIn: 'root'
})
export class CommentService {
  private baseUrl = "http://localhost:8090/api/comments/";

  constructor(private http: HttpClient, private catchErrorService: CatchErrorService) { }

  public getComments(idPost: number): Observable<Comment[]> {
    return this.http.get<Comment[]>(`${this.baseUrl}get/${idPost}`).pipe(
      catchError(e => {
        this.catchErrorService.showErrorModal(e);
        return throwError(() => e);
      })
    );
  }

  public createComment(comment: Comment): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}post`, comment).pipe(
      catchError(e => {
        this.catchErrorService.showErrorModal(e);
        return throwError(() => e);
      })
    );
  }
}
