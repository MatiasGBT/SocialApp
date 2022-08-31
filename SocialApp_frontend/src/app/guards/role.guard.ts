import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import Swal from 'sweetalert2';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class RoleGuard implements CanActivate {

  constructor(private authService: AuthService, private router: Router,
    private translate: TranslateService) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
      let role = route.data['role'] as string;
      if (this.authService.hasRole(role)) {
        return true;
      } else {
        let title: string;
        let text: string;
        this.translate.get('ERROR.ROLE_TITLE').subscribe((res: string) => title = res);
        this.translate.get('ERROR.ROLE_TEXT').subscribe((res: string) => text = res);
        Swal.fire({
          icon: 'error', title: title, text: text, showConfirmButton: false,
          timer: 1250, background: '#7f5af0', color: 'white'
        });
        this.router.navigate(['/index']);
        return false;
      }
  }
  
}
