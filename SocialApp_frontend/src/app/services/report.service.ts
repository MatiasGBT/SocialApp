import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import { Report } from '../models/report';
import { CatchErrorService } from './catch-error.service';

@Injectable({
  providedIn: 'root'
})
export class ReportService {
  private baseUrl = "http://localhost:8090/api/reports";

  constructor(private http: HttpClient, private catchErrorService: CatchErrorService) { }

  public getReports(page: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/get/list/${page}`).pipe(
      catchError(e => {
        this.catchErrorService.showErrorModal(e);
        return throwError(() => e);
      })
    );
  }

  public createReport(report: Report): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/post`, report).pipe(
      catchError(e => {
        this.catchErrorService.showErrorModal(e);
        return throwError(() => e);
      })
    );
  }

  public deleteReport(idReport: number): Observable<any> {
    return this.http.delete<any>(`${this.baseUrl}/delete/${idReport}`).pipe(
      catchError(e => {
        this.catchErrorService.showErrorModal(e);
        return throwError(() => e);
      })
    );
  }
}