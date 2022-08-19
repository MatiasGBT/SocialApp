import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import Swal from 'sweetalert2';
import { Friendship } from '../models/friendship';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class FriendshipService {
  private baseUrl: string = 'http://localhost:8090/api';

  constructor(private http: HttpClient, private authService: AuthService) { }

  public addFriend(id: number) {
    this.sendFriendRequest(id).subscribe(response => {
      Swal.fire({
        icon: response.send ? 'success' : 'info',
        title: response.message,
        showConfirmButton: false,
        timer: 1250,
        background: '#7f5af0',
        color: 'white'
      })
    });
  }

  public sendFriendRequest(idReceiver: number): Observable<any> {
    let formData = new FormData();
    formData.append("idReceiver", idReceiver.toString());
    formData.append("usernameTransmitter", this.authService.getUsername());
    return this.http.post(`${this.baseUrl}/profile/add-friend`, formData).pipe(
      catchError(e => {
        return throwError(() => new Error(e));
      })
    );
  }

  public getFriendship(idReceiver: number): Observable<Friendship> {
    return this.http.get<Friendship>(`${this.baseUrl}/profile/get-friendship/${idReceiver}&${this.authService.getUsername()}`).pipe(
      catchError(e => {
        return throwError(()=>e);
      })
    );
  }
}
