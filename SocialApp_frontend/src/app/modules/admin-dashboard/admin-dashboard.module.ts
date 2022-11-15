import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UsersComponent } from './pages/users/users.component';
import { AdminDashboardRoutingModule } from './admin-dashboard-routing.module';
import { SharedModule } from '../shared/shared.module';
import { ReportsComponent } from './pages/reports/reports.component';

@NgModule({
  declarations: [
    UsersComponent,
    ReportsComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    AdminDashboardRoutingModule
  ]
})
export class AdminDashboardModule { }
