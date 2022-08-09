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

  uploadPhoto(file: File, user: User): Observable<User> {
    let formData = new FormData();
    formData.append("file", file);
    formData.append("username", user.username);
    formData.append("description", user.description);
    return this.http.post(`${this.urlEndPoint}profile/upload/`, formData).pipe(
      map((response: any) => response.user as User),
      catchError(e => {
        console.error(e.error);
        return throwError(() => new Error(e));
      })
    );
  }
}
