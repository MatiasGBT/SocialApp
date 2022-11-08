import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, Observable, throwError } from 'rxjs';
import { Comment } from '../models/comment';
import { CatchErrorService } from './catch-error.service';

@Injectable({
  providedIn: 'root'
})
export class CommentService {
  private baseUrl = "http://localhost:8090/api/comments";

  constructor(private http: HttpClient, private catchErrorService: CatchErrorService,
    private router: Router) { }

  public getComments(idPost: number): Observable<Comment[]> {
    return this.http.get<Comment[]>(`${this.baseUrl}/get/list/by-post/${idPost}`).pipe(
      catchError(e => {
        this.catchErrorService.showErrorModal(e);
        return throwError(() => e);
      })
    );
  }

  public createComment(comment: Comment, idSourceComment: number): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/post/${idSourceComment}`, comment).pipe(
      catchError(e => {
        this.catchErrorService.showErrorModal(e);
        return throwError(() => e);
      })
    );
  }

  public getComment(idComment: number): Observable<Comment> {
    return this.http.get<Comment>(`${this.baseUrl}/get/${idComment}`).pipe(
      catchError(e => {
        this.router.navigate(['/index']);
        this.catchErrorService.showErrorModal(e);
        return throwError(() => e);
      })
    );
  }

  public getReplies(idComment: number): Observable<Comment[]> {
    return this.http.get<Comment[]>(`${this.baseUrl}/get/list/replies/${idComment}`).pipe(
      catchError(e => {
        this.catchErrorService.showErrorModal(e);
        return throwError(() => e);
      })
    );
  }
}