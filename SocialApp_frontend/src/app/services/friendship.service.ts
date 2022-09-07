import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { catchError, Observable, throwError } from 'rxjs';
import Swal from 'sweetalert2';
import { Friendship } from '../models/friendship';
import { User } from '../models/user';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class FriendshipService {
  private baseUrl: string = 'http://localhost:8090/api';

  constructor(private http: HttpClient, private authService: AuthService,
          private translate: TranslateService) { }

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
        Swal.fire({
          icon: 'error', title: e.error.message, text: e.error.error, showConfirmButton: false,
          timer: 1250, background: '#7f5af0', color: 'white'
        });
        return throwError(() => new Error(e));
      })
    );
  }

  public getFriendship(idReceiver: number): Observable<Friendship> {
    return this.http.get<Friendship>(`${this.baseUrl}/profile/get-friendship/${idReceiver}&${this.authService.getUsername()}`).pipe(
      catchError(e => {
        Swal.fire({
          icon: 'error', title: e.error.message, text: e.error.error, showConfirmButton: false,
          timer: 1250, background: '#7f5af0', color: 'white'
        });
        return throwError(()=>e);
      })
    );
  }

  public acceptFriendRequest(id: number): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/notifications/accept-request/${id}`, null).pipe(
      catchError(e => {
        Swal.fire({
          icon: 'error', title: e.error.message, text: e.error.error, showConfirmButton: false,
          timer: 1250, background: '#7f5af0', color: 'white'
        });
        return throwError(()=>e);
      })
    );
  }

  public getFriendsQuantity(idUser: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/profile/get-friends-quantity/${idUser}`).pipe(
      catchError(e => throwError(()=>e))
    );
  }

  private deleteFriendship(idUserFriend: number): Observable<any> {
    return this.http.delete<any>(`${this.baseUrl}/friend/delete/${idUserFriend}&${this.authService.getUsername()}`).pipe(
      catchError(e => {
        Swal.fire({
          icon: 'error', title: e.error.message, text: e.error.error, showConfirmButton: false,
          timer: 1250, background: '#7f5af0', color: 'white'
        });
        return throwError(()=>e);
      })
    );
  }

  public async askToDelete(idUserFriend: number) {
    const lang = localStorage.getItem('lang');
    lang != null ? this.translate.use(lang) : this.translate.use('en');
    let modalTitle: string = this.translateModalText('USER.DELETE_MODAL_TITLE');
    let modalText: string = this.translateModalText('USER.DELETE_MODAL_TEXT');
    let modalBtnSave: string = this.translateModalText('USER.DELETE_MODAL_SAVE_BTN');
    let modalBtnCancel: string = this.translateModalText('USER.DELETE_MODAL_CANCEL_BTN');

    let isDeleted: boolean = false;
    await Swal.fire({
      icon: 'question', title: modalTitle, text: modalText,
      showCancelButton: true, confirmButtonText: modalBtnSave, cancelButtonText: modalBtnCancel,
      background: '#7f5af0', color: 'white', confirmButtonColor: '#d33', cancelButtonColor: '#2cb67d'
    }).then((result) => {
      if (result.isConfirmed) {
        isDeleted = true;
        this.deleteFriendship(idUserFriend).subscribe(response => console.log(response.message));
      }
    });
    return isDeleted;
  }

  private translateModalText(url: string): string {
    let text: string;
    this.translate.get(url).subscribe((res) => text = res);
    return text;
  }

  public getFriendships(idUser: number): Observable<Friendship[]> {
    return this.http.get<Friendship[]>(`${this.baseUrl}/friend/get-friends/${idUser}`).pipe(
      catchError(e => {
        Swal.fire({
          icon: 'error', title: e.error.message, text: e.error.error, showConfirmButton: false,
          timer: 1250, background: '#7f5af0', color: 'white'
        });
        return throwError(()=>e);
      })
    );
  }

  public setIsFriend(users: User[]) {
    users.map(user => {
      this.getFriendship(user.idUser).subscribe(friendship => {
        friendship.status ? user.isFriend = true : user.isFriend = false;
      });
    });
  }
}
