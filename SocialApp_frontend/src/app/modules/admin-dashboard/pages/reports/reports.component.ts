import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Report } from 'src/app/models/report';
import { ReportService } from 'src/app/services/report.service';
import { TranslateExtensionService } from 'src/app/services/translate-extension.service';

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
    private translateExtensionService: TranslateExtensionService) { }

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
}