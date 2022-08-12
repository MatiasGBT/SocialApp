import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, map, Observable, throwError } from 'rxjs';
import { User } from '../models/user';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private urlEndPoint: string = 'http://localhost:8090/api/';

  constructor(private http: HttpClient) { }

  uploadNewUser(file: File, user: User): Observable<any> {
    let formData = new FormData();
    formData.append("file", file);
    formData.append("username", user.username);
    formData.append("description", user.description);
    return this.http.post(`${this.urlEndPoint}profile/edit/complete`, formData).pipe(
      catchError(e => {
        console.error(e.error);
        return throwError(() => new Error(e));
      })
    );
  }

  uploadNewUserWithoutFile(user: User): Observable<any> {
    let formData = new FormData();
    formData.append("username", user.username);
    formData.append("description", user.description);
    return this.http.post(`${this.urlEndPoint}profile/edit/half`, formData);
  }
}
