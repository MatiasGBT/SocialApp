import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import { CatchErrorService } from './catch-error.service';

@Injectable({
  providedIn: 'root'
})
export class LikeService {
  private baseUrl: string = 'http://localhost:8090/api/likes';

  constructor(private http: HttpClient, private catchErrorService: CatchErrorService) { }

  public likePost(idPost: number, idUser: number): Observable<any> {
    let formData = new FormData();
    formData.append("idPost", idPost.toString());
    formData.append("idUser", idUser.toString());
    return this.http.post<any>(`${this.baseUrl}/post`, formData).pipe(
      catchError(e => {
        this.catchErrorService.showErrorModal(e);
        return throwError(() => new Error(e));
      })
    );
  }

  public dislikePost(idPost: number, idUser: number): Observable<any> {
    return this.http.delete<any>(`${this.baseUrl}/delete/${idPost}&${idUser}`).pipe(
      catchError(e => {
        this.catchErrorService.showErrorModal(e);
        return throwError(() => new Error(e));
      })
    );
  }
}