import { Component, ElementRef, OnDestroy, OnInit, Renderer2, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Peer from 'peerjs';
import { Subscription } from 'rxjs';
import { Friendship } from 'src/app/models/friendship';
import { User } from 'src/app/models/user';
import { AuthService } from 'src/app/services/auth.service';
import { FriendshipService } from 'src/app/services/friendship.service';
import { WebsocketService } from 'src/app/services/websocket.service';

@Component({
  selector: 'app-call',
  templateUrl: './call.component.html',
  styleUrls: ['./call.component.css']
})
export class CallComponent implements OnInit, OnDestroy {
  public keycloakUser: User;
  public friend: User;
  @ViewChild("keycloakUserVideo") keycloakUserVideo: ElementRef;
  public localStream: MediaStream;
  @ViewChild("friendVideo") friendVideo: ElementRef;
  public remoteStream: MediaStream;
  private peer: Peer;
  private peerList: Array<any> = [];
  public videoEnabled = true;
  public friendVideoEnabled = true;
  public audioEnabled = true;
  private receivePeerId: Subscription;
  private friendChangedCamera: Subscription;

  constructor(private friendshipService: FriendshipService, private activatedRoute: ActivatedRoute,
    private router: Router, private authService: AuthService,
    private webSocketService: WebsocketService, private renderer2: Renderer2) {
      this.peer = new Peer();
  }

  ngOnInit(): void {
    this.activatedRoute.params.subscribe(params => {
      let id = params['id'];
      if (id) {
        this.getInitContent(id).then(() => {
          this.receivePeerId = this.webSocketService.receivePeerIdEvent.subscribe(peerId => {
            this.callPeer(peerId);
          });
          //When one user deactivates the camera, it reaches the other user so that it is not displayed on their screen.
          this.friendChangedCamera = this.webSocketService.friendChangedCameraStatusEvent.subscribe(cameraStatus => {
            this.friendVideoEnabled = (cameraStatus === 'true');
            if (this.friendVideoEnabled) {
              this.renderer2.setStyle(this.friendVideo.nativeElement, 'opacity', '100');
            } else {
              this.renderer2.setStyle(this.friendVideo.nativeElement, 'opacity', '0');
            }
          });
        });
      } else {
        this.router.navigate(['/index']);
      }
    });
  }

  private async getInitContent(idFriend: number) {
    this.getFriendship(idFriend);
    this.getLocalStream();
  }

  private getFriendship(id: number) {
    this.friendshipService.getFriendship(id).subscribe(friendship => {
      friendship.status ? this.setUsers(friendship) : this.router.navigate(['/profile']);
    });
  }

  private setUsers(friendship: Friendship) {
    if (friendship?.userTransmitter.username == this.authService.getUsername()) {
      this.keycloakUser = friendship.userTransmitter;
      this.friend = friendship.userReceiver;
    } else {
      this.keycloakUser = friendship.userReceiver;
      this.friend = friendship.userTransmitter;
    }
    this.webSocketService.subscribeToCall(this.friend);
  }

  private getLocalStream() {
    navigator.mediaDevices.getUserMedia({
      video: true,
      audio: true
    }).then((stream) => {
      this.keycloakUserVideo.nativeElement.srcObject = stream;
      this.localStream = stream;
      this.getPeerId();
    }).catch(error => console.error(error));
  }

  private getPeerId = () => {
    this.peer.on('open', (peerId) => {
      /*
      If the user is the creator of the call, it sends the other user its peer ID so that
      he/she can join the call. If the user is not the creator of the call, it notifies
      the creator that it is ready to make the call, so he/she can join and send its peer ID.
      */
      if (this.webSocketService?.callCreatorUsername == this.keycloakUser.username) {
        this.sendPeerId(peerId);
      } else {
        this.webSocketService.sendFriendIsReady(this.friend);
      }
    });

    this.peer.on('call', (call) => {
      call.answer(this.localStream);
      call.on('stream', (remoteStream) => {
        if (!this.peerList.includes(call.peer)) {
          this.streamRemoteVideo(remoteStream);
          this.peerList.push(call.peer);
        }
      });
    });
  }

  public sendPeerId(peerId: string) {
    this.webSocketService.sendPeerId(this.friend, peerId);
  }

  private streamRemoteVideo(stream: any): void {
    this.friendVideo.nativeElement.srcObject = stream;
    this.remoteStream = stream;
  }

  private callPeer(id: string): void {
    const call = this.peer.call(id, this.localStream);
    call.on('stream', (remoteStream) => {
      if (!this.peerList.includes(call.peer)) {
        this.streamRemoteVideo(remoteStream);
        this.peerList.push(call.peer);
      }
    });
  }

  public changeVideo() {
    if (this.localStream) {
      this.videoEnabled = !this.videoEnabled;
      this.localStream.getVideoTracks().forEach(track => {
        track.enabled = this.videoEnabled;
      });
      if (this.videoEnabled) {
        this.renderer2.setStyle(this.keycloakUserVideo.nativeElement, 'opacity', '100');
      } else {
        this.renderer2.setStyle(this.keycloakUserVideo.nativeElement, 'opacity', '0');
      }
      this.webSocketService.notifyCameraChange(this.friend, this.videoEnabled);
    }
  }

  public changeAudio() {
    if (this.localStream) {
      this.audioEnabled = !this.audioEnabled;
      this.localStream.getAudioTracks().forEach(track => {
        track.enabled = this.audioEnabled;
      });
    }
  }

  public endCall() {
    this.router.navigate(['/profile', this.friend.idUser]);
  }

  ngOnDestroy(): void {
    this.stopStreams();
    this.webSocketService.endCall(this.friend);
    this.receivePeerId.unsubscribe();
    this.friendChangedCamera.unsubscribe();
  }

  private stopStreams() {
    this.localStream?.getTracks().forEach(track => {
      track.stop();
    });
    this.remoteStream?.getTracks().forEach(track => {
      track.stop();
    });
  }
}