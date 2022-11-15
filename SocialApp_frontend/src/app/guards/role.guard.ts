import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import Swal from 'sweetalert2';
import { AuthService } from '../services/auth.service';
import { TranslateExtensionService } from '../services/translate-extension.service';

@Injectable({
  providedIn: 'root'
})
export class RoleGuard implements CanActivate {

  constructor(private authService: AuthService, private router: Router,
    private translateExtensionService: TranslateExtensionService) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
      let role = route.data['role'] as string;
      if (this.authService.hasRole(role)) {
        return true;
      } else {
        this.router.navigate(['/index']);
        this.fireModal();
        return false;
      }
  }

  private fireModal() {
    Swal.fire({
      title: this.translateExtensionService.getTranslatedStringByUrl('ERROR.ROLE_TITLE'),
      text: this.translateExtensionService.getTranslatedStringByUrl('ERROR.ROLE_TEXT'),
      icon: 'error', showConfirmButton: false,
      timer: 1500, background: '#7f5af0', color: 'white'
    });
  }
}