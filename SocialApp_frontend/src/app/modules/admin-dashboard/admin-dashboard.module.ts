import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UsersComponent } from './pages/users/users.component';
import { AdminDashboardRoutingModule } from './admin-dashboard-routing.module';
import { SharedModule } from '../shared/shared.module';
import { ReportsComponent } from './pages/reports/reports.component';
import { PaginatorComponent } from './components/paginator/paginator.component';
import { ReportCardComponent } from './components/report-card/report-card.component';

@NgModule({
  declarations: [
    UsersComponent,
    ReportsComponent,
    PaginatorComponent,
    ReportCardComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    AdminDashboardRoutingModule
  ]
})
export class AdminDashboardModule { }
