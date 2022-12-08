import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { Report } from 'src/app/models/report';
import { ReportService } from 'src/app/services/report.service';

@Component({
  selector: 'app-reports',
  templateUrl: './reports.component.html',
  styleUrls: ['./reports.component.css']
})
export class ReportsComponent implements OnInit {
  public paginator: any;
  public reports: Report[] = [];

  constructor(private reportService: ReportService, private activatedRoute: ActivatedRoute,
    private translate: TranslateService) { }

  ngOnInit(): void {
    const lang = localStorage.getItem('lang');
    lang ? this.translate.use(lang) : this.translate.use('en');
    
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
}