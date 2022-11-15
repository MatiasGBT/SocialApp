import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './guards/auth.guard';
import { RoleGuard } from './guards/role.guard';

const routes: Routes = [
  {path: '', redirectTo: 'social', pathMatch: 'full'},
  {path: 'social', loadChildren: () => import('./modules/social-network/social-network.module').then( m => m.SocialNetworkModule ), canActivate: [AuthGuard]},
  {path: 'admin', loadChildren: () => import('./modules/admin-dashboard/admin-dashboard.module').then( m => m.AdminDashboardModule ), canActivate: [AuthGuard, RoleGuard], data: {role: 'admin'}}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
