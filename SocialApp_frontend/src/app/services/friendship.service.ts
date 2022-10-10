import { HttpClient } from '@angular/common/http';
import { EventEmitter, Injectable, Output } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import Swal from 'sweetalert2';
import { Friendship } from '../models/friendship';
import { User } from '../models/user';
import { AuthService } from './auth.service';
import { CatchErrorService } from './catch-error.service';
import { TranslateExtensionService } from './translate-extension.service';

@Injectable({
  providedIn: 'root'
})
export class FriendshipService {
  private baseUrl: string = 'http://localhost:8090/api/friend';
  @Output() friendshipDeletedEmitter: EventEmitter<any> = new EventEmitter();

  constructor(private http: HttpClient, private authService: AuthService,
          private translateExtensionService: TranslateExtensionService,
          private catchErrorService: CatchErrorService) { }

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
    return this.http.post(`${this.baseUrl}/add-friend`, formData).pipe(
      catchError(e => {
        this.catchErrorService.showErrorModal(e);
        return throwError(() => new Error(e));
      })
    );
  }

  public getFriendship(idReceiver: number): Observable<Friendship> {
    return this.http.get<Friendship>(`${this.baseUrl}/get/${idReceiver}&${this.authService.getUsername()}`).pipe(
      catchError(e => {
        this.catchErrorService.showErrorModal(e);
        return throwError(()=>e);
      })
    );
  }

  public acceptFriendRequest(id: number): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/accept-request/${id}`, null).pipe(
      catchError(e => {
        this.catchErrorService.showErrorModal(e);
        return throwError(()=>e);
      })
    );
  }

  public rejectFriendRequest(id: number): Observable<any> {
    return this.http.delete<any>(`${this.baseUrl}/reject-request/${id}`).pipe(
      catchError(e => {
        this.catchErrorService.showErrorModal(e);
        return throwError(()=>e);
      })
    );
  }

  public getFriendsQuantity(idUser: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/get/friends/quantity/${idUser}`).pipe(
      catchError(e => throwError(()=>e))
    );
  }

  private deleteFriendship(idUserFriend: number): Observable<any> {
    return this.http.delete<any>(`${this.baseUrl}/delete/${idUserFriend}&${this.authService.getUsername()}`).pipe(
      catchError(e => {
        this.catchErrorService.showErrorModal(e);
        return throwError(()=>e);
      })
    );
  }

  public askToDelete(idUserFriend: number): void {
    Swal.fire({
      icon: 'question', showCancelButton: true,
      title: this.translateExtensionService.getTranslatedStringByUrl('USER.DELETE_MODAL_TITLE'),
      text: this.translateExtensionService.getTranslatedStringByUrl('USER.DELETE_MODAL_TEXT'),
      confirmButtonText: this.translateExtensionService.getTranslatedStringByUrl('USER.DELETE_MODAL_SAVE_BTN'),
      cancelButtonText: this.translateExtensionService.getTranslatedStringByUrl('USER.DELETE_MODAL_CANCEL_BTN'),
      background: '#7f5af0', color: 'white', confirmButtonColor: '#d33', cancelButtonColor: '#2cb67d'
    }).then((result) => {
      if (result.isConfirmed) {
        this.deleteFriendship(idUserFriend).subscribe(response => console.log(response.message));
        this.friendshipDeletedEmitter.emit();
      }
    });
  }

  public setIsFriend(users: User[]) {
    users.map(user => {
      this.getFriendship(user.idUser).subscribe(friendship => {
        friendship.status ? user.isFriend = true : user.isFriend = false;
      });
    });
  }
}
