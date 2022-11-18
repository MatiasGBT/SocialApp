import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UsersComponent } from './pages/users/users.component';
import { AuthGuard } from '../../guards/auth.guard';
import { RoleGuard } from '../../guards/role.guard';
import { ReportsComponent } from './pages/reports/reports.component';

const routes: Routes = [
  {path: '', redirectTo: 'users', pathMatch: 'full'},
  {path: 'users', component: UsersComponent, canActivate: [AuthGuard, RoleGuard], data: {role: 'admin'}},
  {path: 'users/:name/:page', component: UsersComponent, canActivate: [AuthGuard, RoleGuard], data: {role: 'admin'}},
  {path: 'reports/:page', component: ReportsComponent, canActivate: [AuthGuard, RoleGuard], data: {role: 'admin'}}
]

@NgModule({
  declarations: [],
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AdminDashboardRoutingModule { }
