import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, Observable, throwError } from 'rxjs';
import Swal from 'sweetalert2';
import { Post } from '../models/post';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class PostService {
  private baseUrl: string = 'http://localhost:8090/api/posts';

  constructor(private http: HttpClient, private router: Router,
    private authService: AuthService) { }

  public getPost(idPost: number): Observable<Post> {
    return this.http.get<Post>(`${this.baseUrl}/get/${idPost}`).pipe(
      catchError(e => {
        this.router.navigate(['/index']);
        Swal.fire({
          icon: 'error', title: e.error.message, text: e.error.error, showConfirmButton: false,
          timer: 1250, background: '#7f5af0', color: 'white'
        });
        return throwError(() => e);
      })
    );
  }

  public getPostsByUser(idUser: number): Observable<Post[]> {
    return this.http.get<Post[]>(`${this.baseUrl}/get/by-user/${idUser}`).pipe(
      catchError(e => {
        Swal.fire({
          icon: 'error', title: e.error.message, text: e.error.error, showConfirmButton: false,
          timer: 1250, background: '#7f5af0', color: 'white'
        });
        return throwError(() => e);
      })
    );
  }

  public likePost(idPost: number, idUser: number): Observable<any> {
    let formData = new FormData();
    formData.append("idPost", idPost.toString());
    formData.append("idUser", idUser.toString());
    return this.http.post<any>(`${this.baseUrl}/like`, formData).pipe(
      catchError(e => {
        Swal.fire({
          icon: 'error', title: e.error.message, text: e.error.error, showConfirmButton: false,
          timer: 1250, background: '#7f5af0', color: 'white'
        });
        return throwError(() => new Error(e));
      })
    );
  }

  public dislikePost(idPost: number, idUser: number): Observable<any> {
    return this.http.delete<any>(`${this.baseUrl}/dislike/${idPost}&${idUser}`).pipe(
      catchError(e => {
        Swal.fire({
          icon: 'error', title: e.error.message, text: e.error.error, showConfirmButton: false,
          timer: 1250, background: '#7f5af0', color: 'white'
        });
        return throwError(() => new Error(e));
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
        Swal.fire({
          icon: 'error', title: e.error.message, text: e.error.error, showConfirmButton: false,
          timer: 1250, background: '#7f5af0', color: 'white'
        });
        return throwError(() => e);
      })
    );
  }

  public createPostWithoutFile(postText: string): Observable<any> {
    let formData = new FormData();
    formData.append("text", postText);
    formData.append("username", this.authService.getUsername());
    return this.http.post<any>(`${this.baseUrl}/post/text`, formData).pipe(
      catchError(e => {
        Swal.fire({
          icon: 'error', title: e.error.message, text: e.error.error, showConfirmButton: false,
          timer: 1250, background: '#7f5af0', color: 'white'
        });
        return throwError(() => e);
      })
    );
  }

  public createPostWithoutText(file: File): Observable<any> {
    let formData = new FormData();
    formData.append("file", file);
    formData.append("username", this.authService.getUsername());
    return this.http.post<any>(`${this.baseUrl}/post/file`, formData).pipe(
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
