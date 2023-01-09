import { HttpClient } from '@angular/common/http';
import { EventEmitter, Injectable, Output } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import Swal from 'sweetalert2';
import { Friendship } from '../models/friendship';
import { User } from '../models/user';
import { AuthService } from './auth.service';
import { CatchErrorService } from './catch-error.service';
import { TranslateExtensionService } from './translate-extension.service';
import { WebsocketService } from './websocket.service';

@Injectable({
  providedIn: 'root'
})
export class FriendshipService {
  private baseUrl: string = 'http://localhost:8090/api/friendships';
  @Output() friendshipDeletedEvent: EventEmitter<any> = new EventEmitter();

  constructor(private http: HttpClient, private authService: AuthService,
          private translateExtensionService: TranslateExtensionService,
          private catchErrorService: CatchErrorService, private webSocketService: WebsocketService) { }

  public addFriend(userReceiver: User) {
    this.sendFriendRequest(userReceiver.idUser).subscribe(response => {
      Swal.fire({
        icon: response.send ? 'success' : 'info',
        title: response.message,
        showConfirmButton: false,
        timer: 1250,
        background: '#7f5af0',
        color: 'white'
      });
      if (response.send) {
        this.webSocketService.newNotification(userReceiver);
      }
    });
  }

  public sendFriendRequest(idReceiver: number): Observable<any> {
    let formData = new FormData();
    formData.append("idReceiver", idReceiver.toString());
    formData.append("usernameTransmitter", this.authService.getUsername());
    return this.http.post(`${this.baseUrl}/post/send-request`, formData).pipe(
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

  public acceptFriendRequest(idFriendship: number): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/put/accept-request/${idFriendship}`, null).pipe(
      catchError(e => {
        this.catchErrorService.showErrorModal(e);
        return throwError(()=>e);
      })
    );
  }

  public deleteFriendship(idFriendship: number): Observable<any> {
    return this.http.delete<any>(`${this.baseUrl}/delete/${idFriendship}`).pipe(
      catchError(e => {
        this.catchErrorService.showErrorModal(e);
        return throwError(()=>e);
      })
    );
  }

  public getFriendsQuantity(idUser: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/get/friends-count/${idUser}`).pipe(
      catchError(e => throwError(()=>e))
    );
  }

  public askToDelete(idFriendship: number): void {
    Swal.fire({
      icon: 'question', showCancelButton: true,
      title: this.translateExtensionService.getTranslatedStringByUrl('USER.DELETE_MODAL_TITLE'),
      text: this.translateExtensionService.getTranslatedStringByUrl('USER.DELETE_MODAL_TEXT'),
      confirmButtonText: this.translateExtensionService.getTranslatedStringByUrl('USER.DELETE_MODAL_SAVE_BTN'),
      cancelButtonText: this.translateExtensionService.getTranslatedStringByUrl('USER.DELETE_MODAL_CANCEL_BTN'),
      background: '#7f5af0', color: 'white', confirmButtonColor: '#d33', cancelButtonColor: '#2cb67d'
    }).then((result) => {
      if (result.isConfirmed) {
        this.deleteFriendship(idFriendship).subscribe(response => console.log(response.message));
        this.friendshipDeletedEvent.emit();
      }
    });
  }
}