import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import { Report } from '../models/report';
import { CatchErrorService } from './catch-error.service';

@Injectable({
  providedIn: 'root'
})
export class ReportService {
  private baseUrl = "http://localhost:8090/api/report/";

  constructor(private http: HttpClient, private catchErrorService: CatchErrorService) { }

  public createReport(report: Report): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}post`, report).pipe(
      catchError(e => {
        this.catchErrorService.showErrorModal(e);
        return throwError(() => e);
      })
    );
  }
}
