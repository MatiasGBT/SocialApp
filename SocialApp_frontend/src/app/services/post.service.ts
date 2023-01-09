import { HttpClient } from '@angular/common/http';
import { EventEmitter, Injectable, Output } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, Observable, throwError } from 'rxjs';
import { Post } from '../models/post';
import { AuthService } from './auth.service';
import { CatchErrorService } from './catch-error.service';

@Injectable({
  providedIn: 'root'
})
export class PostService {
  private baseUrl: string = 'http://localhost:8090/api/posts';
  @Output() postDeletedEvent: EventEmitter<any> = new EventEmitter();
  @Output() reducePostsQuantityEvent: EventEmitter<any> = new EventEmitter();
  @Output() pinPostEvent: EventEmitter<any> = new EventEmitter();

  constructor(private http: HttpClient, private router: Router,
    private authService: AuthService, private catchErrorService: CatchErrorService) { }

  public getPost(idPost: number): Observable<Post> {
    return this.http.get<Post>(`${this.baseUrl}/get/${idPost}`).pipe(
      catchError(e => {
        this.router.navigate(['/index']);
        this.catchErrorService.showErrorModal(e);
        return throwError(() => e);
      })
    );
  }

  public getPosts(idUser: number, from: number, page: string): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/get/list/${idUser}&${from}&${page}`).pipe(
      catchError(e => {
        this.catchErrorService.showErrorModal(e);
        return throwError(() => e);
      })
    );
  }

  public getTheMostPopularPostFromToday(): Observable<Post> {
    return this.http.get<Post>(`${this.baseUrl}/get/popular`).pipe(
      catchError(e => {
        this.catchErrorService.showErrorModal(e);
        return throwError(() => e);
      })
    );
  }

  public countPostByUser(idUser: number): Observable<number> {
    return this.http.get<number>(`${this.baseUrl}/get/count/${idUser}`).pipe(
      catchError(e => {
        this.catchErrorService.showErrorModal(e);
        return throwError(() => e);
      })
    );
  }

  public createPost(postText: string, file: File): Observable<any> {
    let formData = new FormData();
    formData.append("file", file);
    formData.append("text", postText);
    formData.append("username", this.authService.getUsername());
    return this.http.post<any>(`${this.baseUrl}/post`, formData).pipe(
      catchError(e => {
        this.catchErrorService.showErrorModal(e);
        return throwError(() => e);
      })
    );
  }

  public deletePost(idPost: number): Observable<any> {
    return this.http.delete<any>(`${this.baseUrl}/delete/${idPost}`).pipe(
      catchError(e => {
        this.catchErrorService.showErrorModal(e);
        return throwError(() => e);
      })
    );
  }

  public getPinnedPost(idUser: number): Observable<Post> {
    return this.http.get<Post>(`${this.baseUrl}/get/pinned/${idUser}`).pipe(
      catchError(e => {
        this.catchErrorService.showErrorModal(e);
        return throwError(() => e);
      })
    );
  }

  public pinPost(idPost: number): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/put/pin/${idPost}`, null).pipe(
      catchError(e => {
        this.catchErrorService.showErrorModal(e);
        return throwError(() => e);
      })
    );
  }

  public unpinPost(idPost: number): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/put/unpin/${idPost}`, null).pipe(
      catchError(e => {
        this.catchErrorService.showErrorModal(e);
        return throwError(() => e);
      })
    );
  }
}