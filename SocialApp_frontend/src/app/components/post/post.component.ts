import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Post } from 'src/app/models/post';
import { Report } from 'src/app/models/report';
import { ReportReason } from 'src/app/models/report-reason';
import { AuthService } from 'src/app/services/auth.service';
import { PostService } from 'src/app/services/post.service';
import { ReportReasonService } from 'src/app/services/report-reason.service';
import { ReportService } from 'src/app/services/report.service';
import { TranslateExtensionService } from 'src/app/services/translate-extension.service';
import { UserService } from 'src/app/services/user.service';
import { WebsocketService } from 'src/app/services/websocket.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'post-comp',
  templateUrl: './post.component.html',
  styleUrls: ['./post.component.css']
})
export class PostComponent implements OnInit {
  @Input() public post: Post;
  public isLiked: boolean;
  public isReported: boolean;

  constructor(public authService: AuthService, private postService: PostService,
    private userService: UserService, private reportService: ReportService,
    private router: Router, private reportReasonService: ReportReasonService,
    private translateExtensionService: TranslateExtensionService,
    private webSocketService: WebsocketService) { }

  ngOnInit(): void {
    /*This is done to find out if the logged in user has liked this post so that the heart icon can be changed.*/
    this.post.likes.find(l => l.user.username == this.authService.getUsername()) != undefined 
    ? this.isLiked = true
    : this.isLiked = false;

    this.post.reports.find(r => r.user.username == this.authService.getUsername()) != undefined 
    ? this.isReported = true
    : this.isReported = false;
  }

  public likePost() {
    if (!this.isLiked) {
      this.userService.getKeycloakUser().subscribe(user => {
        this.postService.likePost(this.post.idPost, user.idUser).subscribe(response => {
          console.log(response.message);
          this.isLiked = true;
          this.post.likes.length += 1;
          this.webSocketService.newNotification(this.post.user);
        });
      });
    }
  }

  public dislikePost() {
    if (this.isLiked) {
      this.userService.getKeycloakUser().subscribe(user => {
        this.postService.dislikePost(this.post.idPost, user.idUser).subscribe(response => {
          console.log(response.message);
          this.isLiked = false;
          this.post.likes.length -= 1;
        });
      });
    }
  }

  public deletePost() {
    Swal.fire({
      icon: 'warning', showCancelButton: true,
      title: this.translateExtensionService.getTranslatedStringByUrl('POST.DELETE_MODAL_TITLE'),
      text: this.translateExtensionService.getTranslatedStringByUrl('POST.DELETE_MODAL_TEXT'),
      confirmButtonText: this.translateExtensionService.getTranslatedStringByUrl('POST.DELETE_MODAL_BUTTON_DELETE'),
      cancelButtonText: this.translateExtensionService.getTranslatedStringByUrl('POST.DELETE_MODAL_BUTTON_CANCEL'),
      background: '#7f5af0', color: 'white', confirmButtonColor: '#d33', cancelButtonColor: '#2cb67d'
    }).then((result) => {
      if (result.isConfirmed) {
        this.postService.deletePost(this.post.idPost).subscribe(response => console.log(response.message));
        this.postService.deletePostEmitter.emit(this.post);
        this.postService.reducePostsQuantityEmitter.emit();
        if (this.router.url.includes("post")) {
          this.router.navigate(['/index']);
        }
      }
    });
  }

  public goToProfile() {
    this.userService.getKeycloakUser().subscribe(user => {
      if (user.idUser == this.post.user.idUser) {
        this.router.navigate(['/profile']);
      } else {
        this.router.navigate(['/profile', this.post.user.idUser]);
      }
    });
  }

  //#region Report post
  public reportPost() {
    this.reportReasonService.getReasons().subscribe(reasons => {
      this.showReportModal(reasons);
    })
  }

  private async showReportModal(reasons: ReportReason[]) {
    const { value: reason } = await Swal.fire({
      title: this.translateExtensionService.getTranslatedStringByUrl('POST.REPORT_MODAL_TITLE'),
      input: 'select',
      inputOptions: reasons.map(r => r.reason),
      inputPlaceholder: this.translateExtensionService.getTranslatedStringByUrl('POST.REPORT_MODAL_PLACEHOLDER'),
      showCancelButton: true,
      confirmButtonText: this.translateExtensionService.getTranslatedStringByUrl('POST.REPORT_MODAL_BUTTON_REPORT'),
      cancelButtonText: this.translateExtensionService.getTranslatedStringByUrl('POST.REPORT_MODAL_BUTTON_CANCEL'),
      inputValidator: (value) => {
        return new Promise((resolve) => {
          if (value === '') {
            resolve(this.translateExtensionService.getTranslatedStringByUrl('POST.REPORT_MODAL_ERROR'));
          } else {
            let report = new Report();
            report.reportReason = reasons.find(r => r.idReportReason == parseInt(value) + 1); //+1 needed because in the database the values start with 1 and not 0
            Swal.close();
            this.showReportExtraInformationModal(report);
          }
        });
      }
    });
  }

  private async showReportExtraInformationModal(report: Report) {
    const { value: extraInformation } = await Swal.fire({
      input: 'textarea',
      inputLabel: this.translateExtensionService.getTranslatedStringByUrl('POST.REPORT_MODAL_EXTRAINFORMATION_TITLE'),
      inputPlaceholder: this.translateExtensionService.getTranslatedStringByUrl('POST.REPORT_MODAL_EXTRAINFORMATION_PLACEHOLDER'),
      inputAttributes: {
        'aria-label': this.translateExtensionService.getTranslatedStringByUrl('POST.REPORT_MODAL_EXTRAINFORMATION_PLACEHOLDER'),
        maxlength: "200"
      }
    });

    if (extraInformation && extraInformation.length <= 50) {
      report.extraInformation = extraInformation;
    }
    this.completeReport(report);
  }

  private completeReport(report: Report) {
    this.userService.getKeycloakUser().subscribe(user => {
      report.user = user;
      report.post = this.post;
      this.reportService.createReport(report).subscribe(response => console.log(response.message));
    });
    Swal.fire(this.translateExtensionService.getTranslatedStringByUrl('POST.REPORT_MODAL_SUCCESS'));
    this.isReported = true;
  }
  //#endregion
}
