import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, Observable, throwError } from 'rxjs';
import Swal from 'sweetalert2';
import { Post } from '../models/post';

@Injectable({
  providedIn: 'root'
})
export class PostService {
  private baseUrl: string = 'http://localhost:8090/api';

  constructor(private http: HttpClient, private router: Router) { }

  public getPost(idPost: number): Observable<Post> {
    return this.http.get<Post>(`${this.baseUrl}/posts/get-post/${idPost}`).pipe(
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

  public likePost(idPost: number, idUser: number): Observable<any> {
    let formData = new FormData();
    formData.append("idPost", idPost.toString());
    formData.append("idUser", idUser.toString());
    return this.http.post<any>(`${this.baseUrl}/posts/like`, formData).pipe(
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
    return this.http.delete<any>(`${this.baseUrl}/posts/like/${idPost}&${idUser}`).pipe(
      catchError(e => {
        Swal.fire({
          icon: 'error', title: e.error.message, text: e.error.error, showConfirmButton: false,
          timer: 1250, background: '#7f5af0', color: 'white'
        });
        return throwError(() => new Error(e));
      })
    );
  }
}
