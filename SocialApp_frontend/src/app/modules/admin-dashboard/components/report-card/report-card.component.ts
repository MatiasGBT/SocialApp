import { Component, Input, OnInit } from '@angular/core';
import { Report } from 'src/app/models/report';
import { PostService } from 'src/app/services/post.service';
import { ReportService } from 'src/app/services/report.service';
import { TranslateExtensionService } from 'src/app/services/translate-extension.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'report-card-comp',
  templateUrl: './report-card.component.html',
  styleUrls: ['./report-card.component.css']
})
export class ReportCardComponent implements OnInit {
  @Input() public report: Report;
  public canBeRemoved: boolean = true;

  constructor(private postService: PostService, private reportService: ReportService,
    private translateExtensionService: TranslateExtensionService) { }

  ngOnInit(): void {
  }

  public deletePost(report: Report) {
    Swal.fire({
      title: this.translateExtensionService.getTranslatedStringByUrl('ADMIN.REPORTS_DELETE_POST_TITLE'),
      text: this.translateExtensionService.getTranslatedStringByUrl('ADMIN.REPORTS_DELETE_TEXT'),
      icon: 'warning', showCancelButton: true, background: '#7f5af0', color: 'white',
      confirmButtonColor: '#d33', cancelButtonColor: '#2cb67d',
      confirmButtonText: this.translateExtensionService.getTranslatedStringByUrl('ADMIN.REPORTS_DELETE_MODAL_BTN_DELETE'),
      cancelButtonText: this.translateExtensionService.getTranslatedStringByUrl('ADMIN.REPORTS_DELETE_MODAL_BTN_CANCEL')
    }).then((result) => {
      if (result.isConfirmed) {
        this.postService.deletePost(report.post.idPost).subscribe(response => console.log(response.message));
        this.canBeRemoved = false;
        //notif
      }
    })
  }

  public deleteReport(report: Report) {
    Swal.fire({
      title: this.translateExtensionService.getTranslatedStringByUrl('ADMIN.REPORTS_DELETE_REPORT_TITLE'),
      text: this.translateExtensionService.getTranslatedStringByUrl('ADMIN.REPORTS_DELETE_TEXT'),
      icon: 'warning', showCancelButton: true, background: '#7f5af0', color: 'white',
      confirmButtonColor: '#d33', cancelButtonColor: '#2cb67d',
      confirmButtonText: this.translateExtensionService.getTranslatedStringByUrl('ADMIN.REPORTS_DELETE_MODAL_BTN_DELETE'),
      cancelButtonText: this.translateExtensionService.getTranslatedStringByUrl('ADMIN.REPORTS_DELETE_MODAL_BTN_CANCEL')
    }).then((result) => {
      if (result.isConfirmed) {
        this.reportService.deleteReport(report.idReport).subscribe(response => console.log(response.message));
        this.canBeRemoved = false;
      }
    })
  }
}
