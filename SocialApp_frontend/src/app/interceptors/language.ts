import { Injectable } from '@angular/core';
import {
  HttpEvent, HttpInterceptor, HttpHandler, HttpRequest
} from '@angular/common/http';
import { Observable } from 'rxjs';

/** Pass untouched request through to the next request handler. */
@Injectable()
export class LanguageInterceptor implements HttpInterceptor {

  intercept(req: HttpRequest<any>, next: HttpHandler):
        Observable<HttpEvent<any>> {
    let lang = localStorage.getItem('lang');
    if (lang == null) {
        lang = 'en';
    };
    const langReq = req.clone({
        headers: req.headers.set('Accept-Language', lang + "-" + lang.toUpperCase())
    });
    return next.handle(langReq);
  }
}