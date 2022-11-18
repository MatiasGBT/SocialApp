import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Report } from 'src/app/models/report';
import { PostService } from 'src/app/services/post.service';
import { ReportService } from 'src/app/services/report.service';
import { TranslateExtensionService } from 'src/app/services/translate-extension.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-reports',
  templateUrl: './reports.component.html',
  styleUrls: ['./reports.component.css']
})
export class ReportsComponent implements OnInit {
  public paginator: any;
  public reports: Report[] = [];
  public noReportsFound: string;

  constructor(private reportService: ReportService, private activatedRoute: ActivatedRoute,
    private postService: PostService, private translateExtensionService: TranslateExtensionService) { }

  ngOnInit(): void {
    this.noReportsFound = this.translateExtensionService.getTranslatedStringByUrl('ADMIN.NO_REPORTS_FOUND');
    
    this.activatedRoute.params.subscribe(params => {
      let page = params['page'];
      if (page) {
        this.getReports(page);
      }
    });
  }

  private getReports(page: number) {
    this.reportService.getReports(page).subscribe(response => {
      this.paginator = response;
      this.reports = response.content;
    });
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
        //inhabilitar report
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
        //inhabilitar report
      }
    })
  }
}