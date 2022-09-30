import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import { ReportReason } from '../models/report-reason';
import { CatchErrorService } from './catch-error.service';

@Injectable({
  providedIn: 'root'
})
export class ReportReasonService {
  private baseUrl = "http://localhost:8090/api/report-reason/";

  constructor(private http: HttpClient, private catchErrorService: CatchErrorService) { }

  public getReasons(): Observable<ReportReason[]> {
    return this.http.get<ReportReason[]>(`${this.baseUrl}get`).pipe(
      catchError(e => {
        this.catchErrorService.showErrorModal(e);
        return throwError(() => e);
      })
    );
  }
}
